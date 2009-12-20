#!/usr/bin/python
#
# Copyright (C) 2009 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Defines the generic robot classes.

This module provides the Robot class and RobotListener interface,
as well as some helper functions for web requests and responses.
"""

from google.appengine.api import urlfetch
import base64
import logging
import sys
import urllib

try:
  __import__("google3") # setup internal test environment
except ImportError:
  pass

import simplejson

import blip
import events
import ops
import util
import wavelet

# We only import oauth when we need it
oauth = None

DEFAULT_PROFILE_URL = (
    'http://code.google.com/apis/wave/extensions/robots/python-tutorial.html')

class Robot(object):
  """Robot metadata class.

  This class holds on to basic robot information like the name and profile.
  It also maintains the list of event handlers and cron jobs and
  dispatches events to the appropriate handlers.
  """

  def __init__(self, name, image_url='', profile_url=DEFAULT_PROFILE_URL):
    """Initializes self with robot information.

    Args:
      name: The name of the robot
      image_url: (optional) url of an image that should be used as the avatar
          for this robot.
      profile_url: (optional) url of a webpage with more information about
          this robot.
    """
    self._handlers = {}
    self._name = name
    self._verification_token = None
    self._st = None
    self._consumer_key = None
    self._consumer_secret = None
    self._server_rpc_base = None
    self._profile_handler = None
    self._image_url = image_url
    self._profile_url = profile_url
    self._capability_hash = 0

  @property
  def name(self):
    return self._name

  @property
  def image_url(self):
    return self._image_url

  @property
  def profile_url(self):
    return self._profile_url


  def http_post(self, url, data, headers):
    """Execute an http post.

    Monkey patch this method to use something other than
    the default urllib.
    Args:
        url: to post to
        body: post body
        headers: extra headers to pass along
    Returns:
        response_code, returned_page
    """
    import urllib2
    req = urllib2.Request(url,
                          data=data,
                          headers=headers)
    try:
      f = urllib2.urlopen(req)
      result = f.read()
      return 200, result
    except urllib2.URLError, e:
      return e.code, e.read()

  def get_verification_token_info(self):
    return self._verification_token, self._st

  def capabilities_hash(self):
    """Return the capabilities hash as a hex string."""
    return hex(self._capability_hash)

  def register_handler(self, event_class, handler, context=None, filter=None):
    """Registers a handler on a specific event type.

    Multiple handlers may be registered on a single event type and are
    guaranteed to be called in order.

    The handler takes two arguments, the robot from this session and
    the event. For example:

      def OnParticipantsAdded(robot, event):
        pass

    We could then register this like:
      myrobot.RegisterHandler(events.ParticipantsAdded,
                              event.Context.SIBLING + event.Context.PARENT,
                              "user@googlewave.com")

    Args:
      event_class: An event to listen for from the classes defined in the
          events module.
      handler: A function handler which takes two arguments, event properties
          and the Context of this session.
      context: (optional) the context to provide for this handler; pick something
          from event.Context
      filter: depending on the event, a filter can be specified that restricts
          for which values the event handler will be called from the server.
          Valuable to restrict the amount of traffic send to the robot.
    """
    payload = (handler, event_class, context, filter)
    self._handlers.setdefault(event_class.type, []).append(payload)
    self._capability_hash = (
        self._capability_hash * 13 + hash(event_class.type)) & 0xfffffff

  def set_verification_token_info(self, token, st=None):
    """Set the verification token used in the ownership verification.

    /wave/robot/register starts this process up and will produce this token.

    Args:
      token: the token provided by /wave/robot/register
      st: optional parameter to verify the request for the token came from
          the wave server.
    """
    self._verification_token = token
    self._st = st

  def setup_oauth(self, consumer_key, consumer_secret,
                 server_rpc_base='http://gmodules.com/api/rpc'):
    """Configure this robot to use the oauth'd json rpc.

    Args:
      consumer_key: consumer key received from the verification process.
      consumer_secret: secret received from the verification process.
      server_rpc_base: url of the rpc gateway to use. Specify None for default.
          For wave preview, http://gmodules.com/api/rpc should be used.
          For wave sandbox, http://sandbox.gmodules.com/api/rpc should be used.
    """
    # Import oauth inline and using __import__ for pyexe compatibility
    # when oauth is not installed.
    global oauth
    __import__('waveapi.oauth')
    oauth = sys.modules['waveapi.oauth']

    self._server_rpc_base = server_rpc_base
    self._consumer_key = consumer_key
    self._consumer_secret = consumer_secret
    self._oauth_signature_method = oauth.OAuthSignatureMethod_HMAC_SHA1()
    self._oauth_consumer = oauth.OAuthConsumer(self._consumer_key,
                                               self._consumer_secret)

  def register_profile_handler(self, handler):
    """Sets the profile handler for this robot.

    The profile handler will be called when a profile is needed. The handler
    gets passed the name for which a profile is needed or None for the
    robot itself. A dictionary with keys for name, imageUrl and
    profileUrl should be returned.
    """
    this._profile_handler = handler

  def _hash(self, value):
    """return b64encoded sha1 hash of value."""
    try:
      hashlib = __import__('hashlib') # 2.5
      hashed = hashlib.sha1(value)
    except ImportError:
      import sha # deprecated
      hashed = sha.sha(value)
    return base64.b64encode(hashed.digest())


  def make_rpc(self, operations):
    """Make an rpc call, submitting the specified operations."""

    if not oauth or not self._oauth_consumer.key:
      raise errors.Error('OAuth has not been configured')
    if (not type(operations) == list and
        not isinstance(operations, ops.OperationQueue)):
      operations = [operations]

    rpcs = [op.serialize(method_prefix='wave') for op in operations]

    post_body = simplejson.dumps(rpcs)
    body_hash = self._hash(post_body)
    params = {
      'oauth_consumer_key': 'google.com:' + self._oauth_consumer.key,
      'oauth_timestamp': oauth.generate_timestamp(),
      'oauth_nonce': oauth.generate_nonce(),
      'oauth_version': oauth.OAuthRequest.version,
      'oauth_body_hash': body_hash,
    }
    oauth_request = oauth.OAuthRequest.from_request('POST',
                                                    self._server_rpc_base,
                                                    parameters=params)
    oauth_request.sign_request(self._oauth_signature_method,
                               self._oauth_consumer,
                               None)
    code, content = self.http_post(
        url=oauth_request.to_url(),
        data=post_body,
        headers={'Content-Type': 'application/json'})
    if code != 200:
      logging.info(oauth_request.to_url())
      logging.info(content)
      raise IOError('HttpError ' + str(code))
    return simplejson.loads(content)

  def capabilities_xml(self):
    """Return this robot's capabilities as an XML string."""
    lines = []
    for capability, payloads in self._handlers.items():
      for payload in payloads:
        handler, event_class, context, filter = payload
        line = '  <w:capability name="%s"' % capability
        if context:
          line += ' context="%s"' % context
        if filter:
          line += ' filter="%s"' % filter
        line += '/>\n'
        lines.append(line)
    return ('<?xml version="1.0"?>\n'
            '<w:robot xmlns:w="http://wave.google.com/extensions/robots/1.0">\n'
            '<w:version>%s</w:version>\n'
            '<w:protocolversion>%s</w:protocolversion>\n'
            '<w:capabilities>\n'
            '%s'
            '</w:capabilities>\n'
            '</w:robot>\n') % (self.capabilities_hash(),
                             ops.PROTOCOL_VERSION,
                             '\n'.join(lines))

  def profile_json(self, name=None):
    """Json representation of the profile.

    This method is called both for the basic profile of the robot and to
    get a proxying for profile, in which case name is set. By default
    the information supplied at registration is returned.

    use register_profile_handler to override this default behavior.
    """
    if self._profile_handler:
      data = self._profile_handler(name)
    else:
      data = {'name': self.name,
              'imageUrl': self.image_url,
              'profileUrl': self.profile_url}
    return simplejson.dumps(data)

  def _wavelet_from_json(self, json, pending_ops):
    """Construct a wavelet from the passed json.

    The json should either contain a wavelet and a blips record that
    define those respective object. The returned wavelet
    will be constructed using the passed pending_ops
    OperationQueue.
    Alternatively the json can be the result of a previous
    wavelet.serialize() call. In that case the blips will
    be contaned in the wavelet record.
    """
    if isinstance(json, basestring):
      json = simplejson.loads(json)

    blips = {}
    for blip_id, raw_blip_data in json['blips'].items():
      blips[blip_id] = blip.Blip(raw_blip_data, blips, pending_ops)

    if 'wavelet' in json:
      raw_wavelet_data = json['wavelet']
    else:
      raw_wavelet_data = json
    wavelet_blips = {}
    wavelet_id = raw_wavelet_data['waveletId']
    wave_id = raw_wavelet_data['waveId']
    for blip_id, instance in blips.items():
      if instance.wavelet_id == wavelet_id and instance.wave_id == wave_id:
        wavelet_blips[blip_id] = instance
    result = wavelet.Wavelet(raw_wavelet_data, wavelet_blips, self, pending_ops)
    robot_address = json.get('robotAddress')
    if robot_address:
      result.robot_address = robot_address
    return result

  def process_events(self, json):
    """Process an incoming set of events encoded as json."""
    parsed = simplejson.loads(json)

    proxying_for = parsed['proxyingFor']
    logging.info(proxying_for)
    port = simplejson.loads(proxying_for)['port']
    response = urlfetch.fetch(url=('http://jem.thewe.net/%s/wave' % port),
                              payload=urllib.urlencode({'events': json}),
                              method=urlfetch.POST,
                              deadline=10,
                              headers={'Content-Type': 'application/x-www-form-urlencoded'}).content

    logging.info(response)
    first = ops.Operation(ops.ROBOT_NOTIFY_CAPABILITIES_HASH,
                          '0',
                          {'capabilitiesHash': self._capability_hash})
    operations = [util.serialize(first)] + simplejson.loads(response)

    return simplejson.dumps(operations)

  def new_wave(self, domain, participants=None, message=''):
    """Create a new wave with the initial participants on it.

    A new wave is returned with its own operation queue. It the
    responsibility of the caller to make sure this wave gets
    submitted to the server, either by calling robot.submit() or
    by calling .submit_with() on the returned wave.

    Args:
      domain: the domain to create the wavelet on. This should
          in general correspond to the domain of the incoming
          wavelet. (wavelet.domain). Exceptions are situations
          where the robot is calling new_wave outside of an
          event or when the server is handling multiple domains.
      participants: initial participants on the wave. The robot
          as the creator of the wave is always added.
      message: a string that will be passed back to the robot
          when the WAVELET_CREATOR event is fired. This is a
          lightweight way to pass around state.

    """
    operation_queue = ops.OperationQueue()
    if not isinstance(message, basestring):
      message = simplejson.dumps(message)

    blip_data, wavelet_data = operation_queue.WaveletCreate(
        domain,
        participants=participants,
        message=message)

    blips = {}
    root_blip = blip.Blip(blip_data, blips, operation_queue)
    blips[root_blip.blip_id] = root_blip
    return wavelet.Wavelet(wavelet_data,
                           blips=blips,
                           robot=self,
                           operation_queue=operation_queue)

  def open_wavelet(self, wave_id, wavelet_id, context=None):
    """Use the REST interface to fetch a wave and return it.

    The returned wavelet contains a snapshot of the state of the
    wavelet at that point. It can be used to modify the wavelet,
    but the wavelet might change in between, so treat carefully.

    Also note that the wavelet returned has its own operation
    queue. It the responsibility of the caller to make sure this
    wavelet gets submited to the server, either by calling
    robot.submit() or by calling .submit_with() on the returned
    wavelet.
    """
    pass

  def blind_wavelet(self, json):
    """Construct a blind wave from a json string.

    Call this method if you have a snapshot of a wave that you
    want to operate on outside of an event. Since the wave might
    have changed since you last saw it, you should take care to
    submit operations that are as safe as possible.

    Args:
      json: a json object or string containing at least a key
        wavelet defining the wavelet and a key blips defining the
        blips in the view.

    A new wavelet is returned with its own operation queue. It the
    responsibility of the caller to make sure this wavelet gets
    submited to the server, either by calling robot.submit() or
    by calling .submit_with() on the returned wavelet.
    """
    return self._wavelet_from_json(json, ops.OperationQueue())

  def submit(self, wavelet):
    """Submit the pending operations associated with this wavelet.

    Typically the wavelet will be the result of open_wavelet, blind_wavelet
    or new_wavelet.
    """
    pending = wavelet.get_operation_queue()
    res = self.make_rpc(pending)
    pending.clear()
    logging.info('submit returned:%s' % res)
    return res
