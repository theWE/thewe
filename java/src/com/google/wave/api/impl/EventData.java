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

import java.util.HashMap;
import java.util.Map;

/**
 * EventData is the object used to serialize event data between the proxy and
 * the robot.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
public class EventData {

  final protected String modifiedBy;
  
  final protected long timestamp;
  
  final protected EventType type;
  
  protected Map<String, Object> properties;

  public EventData(EventType type, String modifiedBy, long timestamp) {
    this.modifiedBy = modifiedBy;
    this.timestamp = timestamp;
    this.type = type;
    properties = new HashMap<String, Object>();
  }
  
  /**
   * EventData copy constructor used to initialize classes that extend
   * EventData.
   * 
   * @param event the event. 
   */
  public EventData(EventData event) {
    timestamp = event.timestamp;
    modifiedBy = event.modifiedBy;
    type = event.type;
    properties = event.properties;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public EventType getType() {
    return type;
  }

  public boolean hasType() {
    return type != null;
  }

  public Map<String, Object> getProperties() {
    return properties;
  }
  
  public void put(String key, Object value) {
    properties.put(key, value);
  }

  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
}
