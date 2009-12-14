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

import com.metaparadigm.jsonrpc.AbstractSerializer;
import com.metaparadigm.jsonrpc.MarshallException;
import com.metaparadigm.jsonrpc.ObjectMatch;
import com.metaparadigm.jsonrpc.SerializerState;
import com.metaparadigm.jsonrpc.UnmarshallException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * EventMessageBundle serialization/deserialization.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
@SuppressWarnings("unchecked")
public class EventMessageBundleSerializer extends AbstractSerializer {

  private static final Class[] SERIALIZABLE_CLASSES = new Class[] { EventMessageBundle.class };
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
    if (!(o instanceof EventMessageBundle)) {
      throw new MarshallException("Object is not of type EventMessageBundle.");
    }
    
    JSONObject json = new JSONObject();
    EventMessageBundle bundle = (EventMessageBundle) o;
    try {
      json.put("events", ser.marshall(state, bundle.getEvents()));
      json.put("wavelet", ser.marshall(state, bundle.getWaveletData()));
      json.put("blips", ser.marshall(state, bundle.getBlipData()));
    } catch (JSONException jsonx) {
      throw new MarshallException("Cannot marshall EventMessageBundle.");
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
    if (!EventMessageBundle.class.isAssignableFrom(clazz)) {
      throw new UnmarshallException(clazz.getName() + " is not assignable from EventMessageBundle");
    }
    
    JSONObject jsonObject = (JSONObject) json;
    EventMessageBundle bundle = new EventMessageBundle();
    try {
      bundle.setEvents((List<EventData>) ser.unmarshall(
          state, List.class, jsonObject.getJSONObject("events")));
      bundle.setWaveletData((WaveletData) ser.unmarshall(
            state, WaveletData.class, jsonObject.getJSONObject("wavelet")));
      bundle.setBlipData((Map<String, BlipData>) ser.unmarshall(
            state, Map.class, jsonObject.get("blips")));
    } catch (JSONException jsonx) {
      jsonx.printStackTrace();
    }
    
    return bundle;
  }
}
