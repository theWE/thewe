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

import com.google.wave.api.EventType;

import com.metaparadigm.jsonrpc.AbstractSerializer;
import com.metaparadigm.jsonrpc.MarshallException;
import com.metaparadigm.jsonrpc.ObjectMatch;
import com.metaparadigm.jsonrpc.SerializerState;
import com.metaparadigm.jsonrpc.UnmarshallException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * JSON Serializer for EventData.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
@SuppressWarnings("unchecked")
public class EventDataSerializer extends AbstractSerializer {

  private static final Class[] SERIALIZABLE_CLASSES = new Class[] { EventData.class };
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
    if (!(o instanceof EventData)) {
      throw new MarshallException("Object is not of type Event.");
    }
    
    JSONObject json = new JSONObject();
    EventData event = (EventData) o;
    try {
      json.put("javaClass", EventData.class.getName());
      json.put("modifiedBy", ser.marshall(state, event.getModifiedBy()));
      json.put("timestamp", ser.marshall(state, event.getTimestamp()));
      json.put("type", event.getType().toString());
      json.put("properties", ser.marshall(state, event.getProperties()));
    } catch (JSONException jsonx) {
      throw new MarshallException("Cannot marshall Event.");
    }
    
    return json;
  }

  @SuppressWarnings("unused")
  @Override
  public ObjectMatch tryUnmarshall(SerializerState state, Class clazz, Object json)
      throws UnmarshallException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Object unmarshall(SerializerState state, Class clazz, Object json)
      throws UnmarshallException {
    if (!EventData.class.isAssignableFrom(clazz)) {
      throw new UnmarshallException(clazz.getName() + " is not assignable from EventType");
    }
    
    JSONObject jsonObject = (JSONObject) json;
    EventData event = null;
    try {
      String modifiedBy = (String) ser.unmarshall(
          state, String.class, jsonObject.get("modifiedBy"));
      Long timestamp = (Long) ser.unmarshall(
          state, Long.class, jsonObject.get("timestamp"));
      EventType eventType = EventType.valueOf(jsonObject.getString("type"));
      Map<String, Object> properties = (Map<String, Object>) ser.unmarshall(
          state, Map.class, jsonObject.getJSONObject("properties"));
      event = new EventData(eventType, modifiedBy, timestamp);
      event.setProperties(properties);
    } catch (JSONException jsonx) {
      throw new UnmarshallException("Event could not be unmarshalled.");
    }
    return event;
  }
}
