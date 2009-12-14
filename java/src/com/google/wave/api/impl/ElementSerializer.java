/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.wave.api.impl;

import com.google.wave.api.Element;
import com.google.wave.api.ElementType;
import com.google.wave.api.FormElement;
import com.google.wave.api.Gadget;
import com.google.wave.api.Image;

import com.metaparadigm.jsonrpc.AbstractSerializer;
import com.metaparadigm.jsonrpc.MarshallException;
import com.metaparadigm.jsonrpc.ObjectMatch;
import com.metaparadigm.jsonrpc.SerializerState;
import com.metaparadigm.jsonrpc.UnmarshallException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * {@link Element} serialization/deserialization.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
@SuppressWarnings("unchecked")
public class ElementSerializer extends AbstractSerializer {

  private static final Class[] SERIALIZABLE_CLASSES =
      new Class[] { Element.class, FormElement.class, Gadget.class, Image.class };
  private static final Class[] JSON_CLASSES = new Class[] { JSONObject.class };

  @Override
  public Class[] getJSONClasses() {
    return JSON_CLASSES;
  }

  @Override
  public Class[] getSerializableClasses() {
    return SERIALIZABLE_CLASSES;
  }

  @Override
  public Object marshall(SerializerState state, Object o) throws MarshallException {
    if (!(o instanceof Element)) {
      throw new MarshallException("Object is not of type Element.");
    }
    
    JSONObject json = new JSONObject();
    Element element = (Element) o;
    try {
      json.put("javaClass", o.getClass().getName());
      json.put("type", element.getType().toString());
      json.put("properties", ser.marshall(state, element.getProperties()));
    } catch (JSONException jsonx) {
      throw new MarshallException("Cannot marshall Element.");
    }
    
    return json;
  }

  @Override
  public ObjectMatch tryUnmarshall(SerializerState state, Class clazz, Object json) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object unmarshall(SerializerState state, Class clazz, Object json)
      throws UnmarshallException {
    if (!Element.class.isAssignableFrom(clazz)) {
      throw new UnmarshallException(clazz.getName() + " is not assignable from Element");
    }
    
    JSONObject jsonObject = (JSONObject) json;
    Element element = null;
    try {
      String javaname = jsonObject.isNull("name") ? "" : jsonObject.getString("name");
      element = (Element) clazz.newInstance();
      element.setType(ElementType.valueOf(jsonObject.getString("type")));
      element.setProperties((Map<String, Object>) ser.unmarshall(state, Map.class,
          jsonObject.getJSONObject("properties")));
 
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (JSONException jsonx) {
      jsonx.printStackTrace();
    }
    
    return element;
  }
}
