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

"""Support for operations that can be applied to the server.

Contains classes and utilities for creating operations that are to be
applied on the server.
"""

import errors
import logging
import util

PROTOCOL_VERSION = '0.2'

# Operation Types
WAVELET_APPEND_BLIP = 'wavelet.appendBlip'
WAVELET_CREATE = 'wavelet.create'
WAVELET_REMOVE_SELF = 'wavelet.removeSelf'
WAVELET_SET_TITLE = 'wavelet.setTitle'
WAVELET_ADD_PARTICIPANT = 'wavelet.participant.add'
WAVELET_REMOVE_PARTICIPANT = 'wavelet.participant.remove'
WAVELET_DATADOC_APPEND = 'wavelet.datadoc.append'
WAVELET_DATADOC_SET = 'wavelet.datadoc.set'

BLIP_CREATE_CHILD = 'blip.createChild'
BLIP_DELETE = 'blip.delete'
BLIP_SET_AUTHOR = 'blip.setAuthor'
BLIP_SET_CREATION_TIME = 'blip.setCreationTime'

DOCUMENT_ANNOTATION_DELETE = 'document.annotation.delete'
DOCUMENT_ANNOTATION_SET = 'document.annotation.set'
DOCUMENT_ANNOTATION_SET_NORANGE = 'document.annotation.setNoRange'
DOCUMENT_APPEND = 'document.append'
DOCUMENT_APPEND_MARKUP = 'document.appendMarkup'
DOCUMENT_APPEND_STYLED_TEXT = 'document.appendStyledText'
DOCUMENT_INSERT = 'document.insert'
DOCUMENT_DELETE = 'document.delete'
DOCUMENT_REPLACE = 'document.replace'
DOCUMENT_ELEMENT_APPEND = 'document.element.append'
DOCUMENT_ELEMENT_DELETE = 'document.element.delete',
DOCUMENT_ELEMENT_INSERT = 'document.element.insert'
DOCUMENT_ELEMENT_INSERT_AFTER = 'document.element.insertAfter'
DOCUMENT_ELEMENT_INSERT_BEFORE = 'document.element.insertBefore'
DOCUMENT_ELEMENT_MODIFY_ATTRS = 'document.element.modifyAttrs'
DOCUMENT_ELEMENT_REPLACE = 'document.element.replace'
DOCUMENT_INLINE_BLIP_APPEND = 'document.inlineBlip.append'
DOCUMENT_INLINE_BLIP_DELETE = 'document.inlineBlip.delete'
DOCUMENT_INLINE_BLIP_INSERT = 'document.inlineBlip.insert'
DOCUMENT_INLINE_BLIP_INSERT_AFTER_ELEMENT = (
    'document.inlineBlip.insertAfterElement')
DOCUMENT_MODIFY = 'document.modify'

ROBOT_NOTIFY_CAPABILITIES_HASH = 'robot.notifyCapabilitiesHash'


class OpsRange(object):
  """Represents a start and end range with integers.

  Ranges are used in the json format and on the server but don't not in the
  python API.
  """

  def __init__(self, start, end):
    self.start = start
    self.end = end


class OpsAnnotation(object):
  """Represents an annotation on a document as used in the json format.

  The python API itself uses a more controled construct for Annotations
  found in the blip module.
  """

  def __init__(self, name, value, r):
    self.name = name
    self.value = value
    self.range = r


class Operation(object):
  """Represents a generic operation applied on the server.

  This operation class contains data that is filled in depending on the
  operation type.

  It can be used directly, but doing so will not result
  in local, transient reflection of state on the blips. In other words,
  creating a 'delete blip' operation will not remove the blip from the local
  context for the duration of this session. It is better to use the OpBased
  model classes directly instead.
  """

  def __init__(self, method, opid, params):
    """Initializes this operation with contextual data.

    Args:
      method: Method to call or type of operation.
      opid: The id of the operation. Any callbacks will refer to these.
      params: An operation type dependent dictionary
    """
    self.method = method
    self.id = opid
    self.params = params

  def __str__(self):
    return '%s[%s]%s' % (self.method, self.id, str(self.params))

  def set_param(self, param, value):
    self.params[param] = value

  def serialize(self, method_prefix=''):
    """Serialize the operation.

    method_prefix is prefixed for each method name to allow for specifying
    a namespace.
    """
    if method_prefix and not method_prefix.endswith('.'):
      method_prefix += '.'
    return {'method': method_prefix + self.method,
            'id': self.id,
            'params': util.serialize(self.params)}


class BlipData(dict):
  """Temporary class for storing ephemeral blip data.

  This should be removed once the Java API no longer requires javaClass
  objects, at which point, this method should just return a dict.
  """
  def __init__(self, wave_id, wavelet_id, blip_id, initial_content):
    super(BlipData, self).__init__()
    self.waveId = wave_id
    self.waveletId = wavelet_id
    self.blipId = blip_id
    if initial_content:
      self.content = initial_content
    self['waveId'] = wave_id
    self['waveletId'] = wavelet_id
    self['blipId'] = blip_id
    self['content'] = initial_content


class WaveletData(dict):
  """Temporary class for storing ephemeral blip data.

  This should be removed once the Java API no longer requires javaClass
  objects, at which point, this method should just return a dict.
  """

  def __init__(self, wave_id, wavelet_id, rootblip_id, participants):
    super(WaveletData, self).__init__()
    self.waveId = wave_id
    self.waveletId = wavelet_id
    self.rootBlipId = rootblip_id
    self.participants = participants
    self['waveId'] = wave_id
    self['waveletId'] = wavelet_id
    self['rootBlipId'] = rootblip_id
    self['participants'] = participants


class OperationQueue(object):
  """Wraps the queuing of operations using easily callable functions.

  The operation queue wraps single operations as functions and queues the
  resulting operations in-order. Typically there shouldn't be a need to
  call this directly unless operations are needed on entities outside
  of the scope of the robot. For example, to modify a blip that
  does not exist in the current context, you might specify the wave, wavelet
  and blip id to generate an operation.

  Any calls to this will not be reflected in the robot in any way.
  For example, calling WaveletAppendBlip will not result in a new blip
  being added to the robot, only an operation to be applied on the
  server.
  """

  # Some class global counters:
  __nextBlipId = 1
  __nextWaveId = 1
  __nextOperationId = 1

  def __init__(self):
    self.clear()

  def __CreateNewBlipData(self, wave_id, wavelet_id, initial_content=''):
    """Creates JSON of the blip used for this session."""
    temp_blip_id = 'TBD_%s_%s' % (wavelet_id, OperationQueue.__nextBlipId)
    OperationQueue.__nextBlipId += 1
    return BlipData(wave_id, wavelet_id, temp_blip_id, initial_content)

  def CreateNewWaveletData(self, domain, participants):
    """Creates an ephemeral WaveletData instance used for this session.

    Args:
      domain: the domain to create the data for.
      participants initially on the wavelet
    Returns:
      Blipdata (for the rootblip), WaveletData."""
    wave_id = domain + '!TBD_%s' % OperationQueue.__nextWaveId
    OperationQueue.__nextWaveId += 1
    wavelet_id = domain + '!conv+root'
    root_blip_data = self.__CreateNewBlipData(wave_id, wavelet_id)
    participants = set(participants)
    wavelet_data = WaveletData(
        wave_id, wavelet_id, root_blip_data.blipId, participants)
    return root_blip_data, wavelet_data

  def __len__(self):
    return len(self.__pending)

  def __iter__(self):
    return self.__pending.__iter__()

  def clear(self):
    self.__pending = []
    self._capability_hash = 0
    self._proxy_for_id = None

  def proxy_for(self, id):
    """Return a view of this operation queue with the proxying for set to id.

    This method returns a new instance of an operation queue that shares the
    operation list, but has a different proxying_for_id set so the robot using
    this new queue will send out operations with the proxying_for field set.
    """
    res = OperationQueue()
    res.__pending = self.__pending
    res._capability_hash = self._capability_hash
    res._proxy_for_id = id
    return res

  def set_capability_hash(self, capability_hash):
    self._capability_hash = capability_hash

  def serialize(self):
    first = Operation(ROBOT_NOTIFY_CAPABILITIES_HASH,
                      '0',
                      {'capabilitiesHash': self._capability_hash})
    operations = [first] + self.__pending
    res = util.serialize(operations)
    logging.info('>>>>>' + str(res))
    return res

  def copy_operations(self, other_queue):
    """Copy the pending operations from other_queue into this one."""
    for op in other_queue:
      self.__pending.append(op)

  def new_operation(self, method, wave_id, wavelet_id, props=None, **kwprops):
    """Creates and adds a new operation to the operation list."""
    if props is None:
      props = {}
    props.update(kwprops)
    props['waveId'] = wave_id
    props['waveletId'] = wavelet_id
    if self._proxy_for_id:
      props['proxyingFor'] = self._proxy_for_id
    operation = Operation(method,
                          'op%s' % OperationQueue.__nextOperationId,
                          props)
    self.__pending.append(operation)
    OperationQueue.__nextOperationId += 1
    return operation

  def WaveletAppendBlip(self, wave_id, wavelet_id, initial_content=''):
    """Requests to append a blip to a wavelet.

    Args:
      wave_id: The wave id owning the containing wavelet.
      wavelet_id: The wavelet id that this blip should be appended to.
      initial_content: optionally the content to start with

    Returns:
      JSON representing the information of the new blip.
    """
    blip_data = self.__CreateNewBlipData(wave_id, wavelet_id, initial_content)
    self.new_operation(WAVELET_APPEND_BLIP, wave_id,
                       wavelet_id, blipData=blip_data)
    return blip_data

  def WaveletAddParticipant(self, wave_id, wavelet_id, participant_id):
    """Requests to add a participant to a wavelet.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      participant_id: Id of the participant to add.

    Returns:
      data for the root_blip, wavelet
    """
    return self.new_operation(WAVELET_ADD_PARTICIPANT, wave_id, wavelet_id,
                              participantId=participant_id)

  def WaveletCreate(self, domain, wave_id=None, wavelet_id=None,
                    blip_id=None, participants=None, message=''):
    """Requests to create a wavelet in a wave.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: blip_id of the document to receive the new ids
      participants: initial participants on this wavelet or None if none

    Returns:
      data for the root_blip, wavelet
    """
    if participants is None:
      participants = []
    blip_data, wavelet_data = self.CreateNewWaveletData(domain, participants)
    self.new_operation(WAVELET_CREATE, wave_id, wavelet_id,
                       waveletData=wavelet_data, datadocWriteback=blip_id,
                       message=message)
    return blip_data, wavelet_data

  def WaveletSetDataDoc(self, wave_id, wavelet_id, name, data):
    """Requests set a key/value pair on the data document of a wavelet.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      name: The key name for this data.
      data: The value of the data to set.
    Returns:
      The operation created.
    """
    return self.new_operation(WAVELET_DATADOC_SET, wave_id, wavelet_id,
                              datadocName=name, datadocValue=data)

  def WaveletSetTitle(self, wave_id, wavelet_id, title):
    """Requests to set the title of a wavelet.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      title: The title to set.
    Returns:
      The operation created.
    """
    return self.new_operation(WAVELET_SET_TITLE, wave_id, wavelet_id,
                             waveletTitle=title)

  def BlipCreateChild(self, wave_id, wavelet_id, blip_id):
    """Requests to create a child blip of another blip.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.

    Returns:
      JSON of blip for which further operations can be applied.
    """
    blip_data = self.__CreateNewBlipData(wave_id, wavelet_id)
    self.new_operation(BLIP_CREATE_CHILD, wave_id, wavelet_id,
                       blipId=blip_id,
                       blipData=blip_data)
    return blip_data

  def BlipDelete(self, wave_id, wavelet_id, blip_id):
    """Requests to delete (tombstone) a blip.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
    Returns:
      The operation created.
    """
    return self.new_operation(BLIP_DELETE, wave_id, wavelet_id, blipId=blip_id)

  def DocumentAnnotationDelete(self, wave_id, wavelet_id, blip_id, start, end,
                               name):
    """Deletes a specified annotation of a given range with a specific key.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      start: Start position of the range.
      end: End position of the range.
      name: Annotation key name to clear.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_ANNOTATION_DELETE, wave_id, wavelet_id,
                              blipId=blip_id, range=OpsRange(start, end))

  def DocumentAnnotationSet(self, wave_id, wavelet_id, blip_id, start, end,
                            name, value):
    """Set a specified annotation of a given range with a specific key.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      start: Start position of the range.
      end: End position of the range.
      name: Annotation key name to clear.
      value: The value of the annotation across this range.
    Returns:
      The operation created.
    """
    annotation = OpsAnnotation(name, value, OpsRange(start, end))
    return self.new_operation(DOCUMENT_ANNOTATION_SET, wave_id, wavelet_id,
                              blipId=blip_id, annotation=annotation)

  def DocumentAnnotationSetNoOpsRange(self, wave_id, wavelet_id, blip_id,
                                   name, value):
    """Requests to set an annotation on an entire document.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      name: Annotation key name to clear.
      value: The value of the annotation.
    Returns:
      The operation created.
    """
    annotation = OpsAnnotation(name, value, None)
    return self.new_operation(DOCUMENT_ANNOTATION_SET_NORANGE, wave_id,
                              wavelet_id, blipId=blip_id, annotation=annotation)

  def DocumentAppend(self, wave_id, wavelet_id, blip_id, content):
    """Requests to append content to a document.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      content: The content to append.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_APPEND, wave_id, wavelet_id,
                              blipId=blip_id, content=content)

  def DocumentAppendMarkup(self, wave_id, wavelet_id, blip_id, content):
    """Requests to append content with markup to a document.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      content: The markup content to append.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_APPEND_MARKUP, wave_id, wavelet_id,
                              blipId=blip_id, content=content)

  def DocumentAppendStyledText(self, wave_id, wavelet_id, blip_id, text, style):
    """Requests to append styled text to the document.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      text: The text ot append..
      style: The style to apply.
    """
    self.new_operation(DOCUMENT_APPEND_MARKUP, wave_id, wavelet_id,
                       blipId=blip_id,
                       content=text,
                       styleType=style)

  def DocumentDelete(self, wave_id, wavelet_id, blip_id, start=None, end=None):
    """Requests to delete content in a given range.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      start: Start of the range.
      end: End of the range.
    Returns:
      The operation created.
    """
    if start is None or end is None:
      range = None
    else:
      range = OpsRange(start, end)
    return self.new_operation(DOCUMENT_DELETE, wave_id, wavelet_id,
                              blipId=blip_id, range=range)

  def DocumentInsert(self, wave_id, wavelet_id, blip_id, content, index=0):
    """Requests to insert content into a document at a specific location.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      content: The content to insert.
      index: The position insert the content at in ths document.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_INSERT, wave_id, wavelet_id,
                              blipId=blip_id, index=index, content=content)

  def DocumentElementAppend(self, wave_id, wavelet_id, blip_id, element):
    """Requests to append an element to the document.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      element: Element instance to append.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_ELEMENT_APPEND, wave_id, wavelet_id,
                              blipId=blip_id, element=element)

  def DocumentElementDelete(self, wave_id, wavelet_id, blip_id, position):
    """Requests to delete an element from the document at a specific position.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      position: Position of the element to delete.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_ELEMENT_DELETE, wave_id, wavelet_id,
                              blipId=blip_id, index=position)

  def DocumentElementInsert(self, wave_id, wavelet_id, blip_id, position,
                            element):
    """Requests to insert an element to the document at a specific position.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      position: Position of the element to delete.
      element: Element instance to insert.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_ELEMENT_INSERT, wave_id, wavelet_id,
                              blipId=blip_id, index=position, element=element)

  def DocumentModify(self, wave_id, wavelet_id, blip_id):
    """Requests to insert an element to the document at a specific position.

    The returned operation still needs to be filled with details before
    it makes sense.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_MODIFY,
                              wave_id,
                              wavelet_id,
                              blipId=blip_id)

  def DocumentElementReplace(self, wave_id, wavelet_id, blip_id, position,
                             element):
    """Requests to replace an element.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      position: Position of the element to replace.
      element: Element instance to replace.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_ELEMENT_REPLACE, wave_id, wavelet_id,
                              blipId=blip_id,
                              index=position,
                              element=element)

  def DocumentModifyAttributes(self, wave_id, wavelet_id, blip_id,
                               element):
    """Modifies the attributes of an element.

    This is done by passing the a new element that is matched against
    existing elements and the attributes are copied without the element
    actually being deleted and reinserted. This is especially useful for
    gadgets.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      element: Element instance to take the attributes from an to
               match.
    Returns:
      The operation created.
    """
    return self.new_operation(DOCUMENT_ELEMENT_MODIFY_ATTRS, wave_id, wavelet_id,
                              blipId=blip_id, index=-1, element=element)

  def DocumentInlineBlipAppend(self, wave_id, wavelet_id, blip_id):
    """Requests to create and append a new inline blip to another blip.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.

    Returns:
      JSON of blip containing the id information.
    """
    inline_blip_data = self.__CreateNewBlipData(wave_id, wavelet_id)
    self.new_operation(DOCUMENT_INLINE_BLIP_APPEND, wave_id, wavelet_id,
                       blipId=blip_id,
                       blipData=inline_blip_data)
    inline_blip_data['parentBlipId'] = blip_id
    return inline_blip_data

  def DocumentInlineBlipDelete(self, wave_id, wavelet_id, blip_id,
                               inline_blip_id):
    """Requests to delete an inline blip from its parent.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      inline_blip_id: The blip to be deleted.
    """
    self.new_operation(DOCUMENT_INLINE_BLIP_DELETE, wave_id, wavelet_id,
                       blipId=blip_id,
                       childBlipId=inline_blip_id)

  def DocumentInlineBlipInsert(self, wave_id, wavelet_id, blip_id, position):
    """Requests to insert an inline blip at a specific location.

    Args:
      wave_id: The wave id owning that this operation is applied to.
      wavelet_id: The wavelet id that this operation is applied to.
      blip_id: The blip id that this operation is applied to.
      position: The position in the document to insert the blip.

    Returns:
      JSON data for the blip that was created for further operations.
    """
    inline_blip_data = self.__CreateNewBlipData(wave_id, wavelet_id)
    inline_blip_data['parentBlipId'] = blip_id
    self.new_operation(DOCUMENT_INLINE_BLIP_INSERT, wave_id, wavelet_id,
                       blipId=blip_id,
                       index=position,
                       blipData=inline_blip_data)
    return inline_blip_data

  def DocumentInlineBlipInsertAfterElement(self):
    """Requests to insert an inline blip after an element.

    Raises:
      NotImplementedError: Function not yet implemented.
    """
    raise NotImplementedError()
