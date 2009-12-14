#!/usr/bin/python2.4
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

"""Defines classes that represent parts of the common wave model.

Defines the core data structures for the common wave model. At this level,
models are read-only but can be modified through operations.

Note that model attributes break the typical style by providing lower
camel-cased characters to match the wire protocol format.
"""

import logging
import blip

ROOT_WAVELET_ID_SUFFIX = '!conv+root'

class DataDocs(object):
  """Class modeling a bunch of data documents in pythonic way."""

  def __init__(self, init_docs, wave_id, wavelet_id, operation_queue):
    self._docs = init_docs
    self._wave_id = wave_id
    self._wavelet_id = wavelet_id
    self._operation_queue = operation_queue

  def __getitem__(self, key):
    return self._docs[key]

  def __setitem__(self, key, value):
    self._operation_queue.WaveletSetDataDoc(
        self._wave_id, self._wavelet_id, key, value)
    if value is None and key in self._docs:
      del self._docs[key]
    else:
      self._docs[key] = value

  def __len__(self):
    return len(self._docs)

  def serialize(self):
    return self._docs


class Participants(object):
  """Class modelling a set of participants in pythonic way."""
  def __init__(self, participants, wave_id, wavelet_id, operation_queue):
    self._participants = set(participants)
    self._wave_id = wave_id
    self._wavelet_id = wavelet_id
    self._operation_queue = operation_queue

  def __contains__(self, participant):
    return participant in self._participants

  def __len__(self):
    return len(self._participants)

  def __iter__(self):
    return self._participants.__iter__()

  def add(self, participant_id):
    self._operation_queue.WaveletAddParticipant(
        self._wave_id, self._wavelet_id, participant_id)
    self._participants.add(participant_id)

  def serialize(self):
    return list(self._participants)


class Wavelet(object):
  """Models a single wavelet instance.

  A single wavelet is composed of metadata, participants and the blips it
  contains.

  properties:
    id: the id of this wavelet
    wave_id: the id of the wave this wavelet is associated with
    creator: participant id of the creator of this wavelet
    creation_time: time this wavelet was created on the server.
    last_modified_time: time this wavelet was last modified.
    data_documents: data documents associated with this wavelet
    title: title of this wavelet
    root_blip: the root blip of this wavelet
    blips: the blips in this wavelet
  """

  def __init__(self, json, blips, robot, operation_queue):
    """Inits this wavelet with JSON data.

    Args:
      json: JSON data dictionary from Wave server.
    """
    self._robot = robot
    self._operation_queue = operation_queue
    self._wave_id = json.get('waveId')
    self._wavelet_id = json.get('waveletId')
    self._creator = json.get('creator')
    self._creation_time = json.get('creationTime', 0)
    self._data_documents = DataDocs(json.get('dataDocuments', {}),
                                    self._wave_id,
                                    self._wavelet_id,
                                    operation_queue)
    self._last_modified_time = json.get('lastModifiedTime')
    self._participants = Participants(json.get('participants', []),
                                      self._wave_id,
                                      self._wavelet_id,
                                      operation_queue)
    self._title = json.get('title', '')
    self._raw_data = json
    self._blips = blip.Blips(blips)
    self._root_blip_id = json.get('rootBlipId')
    if self._root_blip_id and self._root_blip_id in self._blips:
      self._root_blip = self._blips[self._root_blip_id]
    else:
      self._root_blip = None
    self._robot_address = None

  @property
  def wavelet_id(self):
    """Returns this wavelet's id."""
    return self._wavelet_id

  @property
  def wave_id(self):
    """Returns this wavelet's parent wave id."""
    return self._wave_id

  @property
  def creator(self):
    """Returns the participant id of the creator of this wavelet."""
    return self._creator

  @property
  def creation_time(self):
    """Returns the time that this wavelet was first created in milliseconds."""
    return self._creation_time

  @property
  def data_documents(self):
    """Returns the data documents for this wavelet based on key name."""
    return self._data_documents

  @property
  def domain(self):
    """Return the domain that wavelet belongs to."""
    p = self._wave_id.find('!')
    if p == -1:
      return None
    else:
      return self._wave_id[:p]

  @property
  def last_modified_time(self):
    """Returns the time that this wavelet was last modified in ms."""
    return self._last_modified_time

  @property
  def participants(self):
    """Returns a set of participants on this wavelet."""
    return self._participants

  @property
  def robot(self):
    """The robot that owns this wavelet."""
    return self._robot

  def _get_title(self):
    return self._title

  def _set_title(self, title):
    self._operation_queue.WaveletSetTitle(self.wave_id, self.wavelet_id,
                                          title)
    self._title = title

  """The title of the wavelet."""
  title = property(_get_title, _set_title)

  def _get_robot_address(self):
    return self._robot_address

  def _set_robot_address(self, address):
    if self._robot_address:
      raise errors.Error('robot address already set')
    self._robot_address = address

  """The address of the current robot."""
  robot_address = property(_get_robot_address, _set_robot_address)

  @property
  def root_blip(self):
    """Returns this wavelet's root blip."""
    return self._root_blip

  @property
  def blips(self):
    """Returns the blips for this wavelet."""
    return self._blips

  def get_operation_queue(self):
    return self._operation_queue

  def serialize(self):
    """Return a dictionary representation of the wavelet ready for json."""
    return {'waveId': self._wave_id,
            'waveletId': self._wavelet_id,
            'creator': self._creator,
            'creationTime': self._creation_time,
            'dataDocuments': self._data_documents.serialize(),
            'lastModifiedTime': self._last_modified_time,
            'participants': self._participants.serialize(),
            'title': self._title,
            'blips': self._blips.serialize(),
            'rootBlipId': self._root_blip_id
           }

  def proxy_for(self, proxy_for_id):
    """Return a view on this wavelet that will proxy for the specified id.

    A shallow copy of the current wavelet is returned with the proxy_for_id
    set. Any modifications made to this copy will be done using the
    proxy_for_id, i.e. the robot+<proxy_for_id>@appspot.com address will
    be used.
    """
    self.add_proxying_participant(proxy_for_id)
    operation_queue = self.get_operation_queue().proxy_for(proxy_for_id)
    res = Wavelet(json={},
                  blips={},
                  robot=self.robot,
                  operation_queue=operation_queue)
    res._wave_id = self._wave_id
    res._wavelet_id = self._wavelet_id
    res._creator = self._creator
    res._creation_time = self._creation_time
    res._data_documents = self._data_documents
    res._last_modified_time = self._last_modified_time
    res._participants = self._participants
    res._title = self._title
    res._raw_data = self._raw_data
    res._blips = self._blips
    res._root_blip = self._root_blip
    return res

  def add_proxying_participant(self, id):
    """Ads a proxying participant to the wave.

    Proxying participants are of the form robot+proxy@domain.com. This
    convenience method constructs this id and then calls participants.add.
    """
    robotid, domain = self.robot_address.split('@', 1)
    if '#' in robotid:
      robotid, version = robotid.split('#')
    else:
      version = None
    if '+' in robotid:
      newid = robotid.split('+', 1) + '+' + id
    else:
      newid = robotid + '+' + id
    if version:
      newid += '#' + version
    newid += '@' + domain
    self.participants.add(newid)

  def submit_with(self, other_wavelet):
    """Submit this wavelet when the passed other wavelet is submited.

    wavelets constructed outside of the event callback need to
    be either explicitly submited using robot.submit(wavelet) or be
    associated with a different wavelet that will be submited or
    is part of the event callback.
    """
    other_wavelet._operation_queue.copy_operations(self._operation_queue)
    self._operation_queue = other_wavelet._operation_queue

  def reply(self, initial_content=None):
    """Replies to the conversation in this wavelet.

    Args:
      initial_contents: if set, start with this content.

    Returns:
      A transient version of the blip that contains the reply.
    """
    if not initial_content:
      initial_content = '\n'
    blip_data = self._operation_queue.WaveletAppendBlip(
       self.wave_id, self.wavelet_id, initial_content)

    instance = blip.Blip(blip_data, self._blips, self._operation_queue)
    self._blips._add(instance)
    return instance

  def delete(self, todelete):
    """Remove a blip from this wavelet.

    Args:
      todelete: either a blip or a blip id to be removed.
    """
    if isinstance(todelete, blip.Blip):
      blip_id = todelete.blip_id
    else:
      blip_id = todelete
    self._operation_queue.BlipDelete(self.wave_id, self.wavelet_id, blip_id)
    self._blips._remove_with_id(blip_id)
