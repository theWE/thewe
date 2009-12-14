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

import com.google.wave.api.Context;
import com.google.wave.api.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A container for a bundle of messages to be sent to a robot.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
public class EventMessageBundle {

  protected List<EventData> events;
  protected WaveletData waveletData;
  protected Map<String, BlipData> blipData;
  private Map<String, Set<Context>> requiredBlips;
  
  public EventMessageBundle() {
    events = new ArrayList<EventData>();
    waveletData = null;
    blipData = new HashMap<String, BlipData>();
    requiredBlips = new HashMap<String, Set<Context>>();
  }
  
  public Map<String, Set<Context>> getRequiredBlips() {
    return requiredBlips;
  }

  /**
   * Require the availability of the specified blipId for this bundle.
   * 
   * @param blipId the id of the blip that is required.
   * @param contexts we need for this blip.
   */
  public void requireBlip(String blipId, List<Context> contexts) {
    Set<Context> contextSet = requiredBlips.get(blipId);
    if (contextSet == null) {
      contextSet = new HashSet<Context>();
      requiredBlips.put(blipId, contextSet);
    }
    for (Context context : contexts) {
      contextSet.add(context);
    }
  }

  /**
   * Add an event to the events that are tracked.
   * @param event to add.
   */
  public void addEvent(EventData event) {
    events.add(event);
  }

  public boolean hasMessages() {
    return !events.isEmpty();
  }

  public List<EventData> getEvents() {
    return events;
  }
  
  public WaveletData getWaveletData() {
    return waveletData;
  }

  public Map<String, BlipData> getBlipData() {
    return blipData;
  }

  public void setEvents(List<EventData> events) {
    this.events = events;
  }

  public void setWaveletData(WaveletData waveletData) {
    this.waveletData = waveletData;
  }

  public void setBlipData(Map<String, BlipData> blipData) {
    this.blipData = blipData;
  }

  public void clear() {
    events.clear();
    blipData.clear();
    requiredBlips.clear();
    waveletData = null;
  }

  /**
   * Return whether a blip is already in the blipdata
   * 
   * @param id of the blip
   * @return whether it is in blipData
   */
  public boolean hasBlipId(String id) {
    return blipData.containsKey(id);
  }

  /**
   * Add a blip to the blipdata
   * 
   * @param id
   * @param blip
   */
  public void addBlip(String id, BlipData blip) {
    blipData.put(id, blip);
  }

  /**
   * Return whether the lookingFor event is contained in this bundle.
   */
  public boolean hasEvent(EventType lookingFor) {
    for (EventData event : events) {
      if (event.getType().equals(lookingFor)) {
        return true;
      }
    }
    return false;
  }
}
