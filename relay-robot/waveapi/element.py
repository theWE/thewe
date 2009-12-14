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

"""Elements are non-text bits living in blips like images, gadgets etc.

This module defines the Element class and the derived classes.
"""


import logging

import util
import sys

class Element(object):
  """Elements are non-text content within a document.

  These are generally abstracted from the Robot. Although a Robot can query the
  properties of an element it can only interact with the specific types that
  the element represents.

  Properties of elements are both accessible directly (image.url) and through
  the properties dictionary (image.properties['url']). In general Element
  should not be instantiated by robots, but rather rely on the derived classes.
  """

  def __init__(self, element_type, **properties):
    """Initializes self with the specified type and any properties.

    Args:
      element_type: string typed member of ELEMENT_TYPE
      properties: either a dictionary of initial properties, or a dictionary
          with just one member properties that is itself a dictionary of
          properties. This allows us to both use
          e = Element(atype, prop1=val1, prop2=prop2...)
          and
          e = Element(atype, properties={prop1:val1, prop2:prop2..})
    """
    if len(properties) == 1 and 'properties' in properties:
      properties = properties['properties']
    self.type = element_type
    # as long as the operation_queue of an element in None, it is
    # unattached. After an element is acquired by a blip, the blip
    # will set the operation_queue to make sure all changes to the
    # element are properly send to the server.
    self._operation_queue = None
    for key, val in properties.items():
      setattr(self, key, val)

  @classmethod
  def from_json(cls, json):
    """Class method to instantiate an Element based on a json string."""
    etype = json['type']
    logging.info('constructing: ' + str(json))
    props = json['properties'].copy()

    element_class = ALL.get(etype)
    if not element_class:
      # Unknown type. Server could be newer than we are
      return Element(element_type=etype, properties=props)

    return element_class.from_props(props)

  def get(self, key, default=None):
    """Standard get interface"""
    return getattr(self, key, default)

  def serialize(self):
    """Custom serializer for Elements.

    Element need their non standard attributes returned in a dict named
    properties.
    """
    props = {}
    data = {}
    for attr in dir(self):
      if attr.startswith('_'):
        continue
      val = getattr(self, attr)
      if val is None or callable(val):
        continue
      val = util.serialize(val)
      if attr == 'type':
        data[attr] = val
      else:
        props[attr] = val
    data['properties'] = util.serialize(props)
    return data


class Input(Element):
  """an input box."""

  type = 'INPUT'

  def __init__(self, name, value='', label=''):
    super(Input, self).__init__(Input.type,
          name=name, value=value, default_value=value, label=label)

  @classmethod
  def from_props(cls, props):
    return Input(name=props['name'], value=props['value'], label=props['label'])


class Check(Element):
  """a checkbox."""

  type = 'CHECK'

  def __init__(self, name, value=''):
    super(Check, self).__init__(Check.type,
          name=name, value=value, default_value=value)

  @classmethod
  def from_props(cls, props):
    return Check(name=props['name'], value=props['value'], label=props['label'])


class Button(Element):
  """a button on a form."""

  type = 'BUTTON'

  def __init__(self, name, caption):
    super(Button, self).__init__(Button.type,
          name=name, value=caption)

  @classmethod
  def from_props(cls, props):
    return Button(name=props['name'], value=props['value'])


class Label(Element):
  """a label element."""

  type = 'LABEL'

  def __init__(self, label_for, caption):
    super(Label, self).__init__(Label.type,
          name=label_for, value=caption)

  @classmethod
  def from_props(cls, props):
    return Label(label_for=props['name'], caption=props['value'])


class RadioButton(Element):
  """a radio button element."""

  type = 'RADIO_BUTTON'

  def __init__(self, name, group):
    super(RadioButton, self).__init__(RadioButton.type,
          name=name, value=group)

  @classmethod
  def from_props(cls, props):
    return RadioButton(name=props['name'], group=props['value'])


class RadioButtonGroup(Element):
  """a group of radio buttons."""

  type = 'RADIO_BUTTON_GROUP'

  def __init__(self, name, value):
    super(RadioButtonGroup, self).__init__(RadioButtonGroup.type,
          name=name, value=value)

  @classmethod
  def from_props(cls, props):
    return RadioButtonGroup(name=props['name'], value=props['value'])


class Password(Element):
  """a password element."""

  type = 'PASSWORD'

  def __init__(self, name, value):
    super(Password, self).__init__(Password.type,
          name=name, value=value)

  @classmethod
  def from_props(cls, props):
    return Password(name=props['name'], value=props['value'])


class TextArea(Element):
  """a text area element."""

  type = 'TEXTAREA'

  def __init__(self, name, value):
    super(TextArea, self).__init__(TextArea.type,
          name=name, value=value)

  @classmethod
  def from_props(cls, props):
    return TextArea(name=props['name'], value=props['value'])


class Gadget(Element):
  """a Gadget element within the content of a document."""
  
  type = 'GADGET'

  def __init__(self, url, props=None):
    if props is None:
      props = {}
    props['url'] = url
    super(Gadget, self).__init__(Gadget.type, properties=props)

  @classmethod
  def from_props(cls, props):
    return Gadget(props.get('url'), props)


class Image(Element):
  """Represents an Image element within the context of a document."""

  type = 'IMAGE'

  def __init__(self, url='', width=None, height=None,
      attachmentId=None, caption=None):
    super(Image, self).__init__(Image.type, url=url, width=width,
        height=height, attachmentId=attachmentId, caption=caption)

  @classmethod
  def from_props(cls, props):
    props = dict([(key.encode('utf-8'), value)
                  for key, value in props.items()])
    logging.info('from_props=' + str(props))
    return apply(Image, [], props)


def is_element(cls):
  """Returns whether the passed class is an element."""
  try:
    if not issubclass(cls, Element):
      return False
    return hasattr(cls, 'type')
  except TypeError:
    return False

cur_module = sys.modules[__name__]
ALL = [getattr(cur_module, item) for item in dir(cur_module)]
ALL = dict([(item.type, item) for item in ALL if is_element(item)])
