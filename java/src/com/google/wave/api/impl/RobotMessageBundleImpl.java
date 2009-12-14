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

import com.google.wave.api.Blip;
import com.google.wave.api.Event;
import com.google.wave.api.EventType;
import com.google.wave.api.RobotMessageBundle;
import com.google.wave.api.Wavelet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link RobotMessageBundle} implementation.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
public class RobotMessageBundleImpl implements RobotMessageBundle {

  private final OperationMessageBundle operationMessageBundle;
  private final EventMessageBundle eventMessageBundle;
  private final String robotAddress;
  private final Map<Tuple<String>, Blip> blips;
  private final Map<Tuple<String>, Wavelet> wavelets;
  private List<Event> events;
  private Wavelet wavelet;

  public RobotMessageBundleImpl(EventMessageBundle eventMessageBundle,
      String robotAddress) {
    this.eventMessageBundle = eventMessageBundle;
    this.operationMessageBundle = new OperationMessageBundle();
    this.robotAddress = robotAddress;
    this.blips = new HashMap<Tuple<String>, Blip>();
    this.wavelets = new HashMap<Tuple<String>, Wavelet>();
  }

  @Override
  public boolean wasParticipantAddedToNewWave(String participantId) {
    return wasParticipantAddedToWave(participantId) && isNewWave();
  }

  @Override
  public boolean wasSelfAdded() {
    return !filterEventsByType(EventType.WAVELET_SELF_ADDED).isEmpty();
  }

  @Override
  public boolean wasSelfRemoved() {
    return !filterEventsByType(EventType.WAVELET_SELF_REMOVED).isEmpty();
  }
  
  @Override
  public boolean isNewWave() {
    return !getWavelet().getRootBlip().hasChildren();
  }
  
  @Override
  public boolean wasParticipantAddedToWave(String participantId) {
    for (Event event : getParticipantsChangedEvents()) {
      if (event.getAddedParticipants().contains(participantId)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public List<Event> getParticipantsChangedEvents() {
    return filterEventsByType(EventType.WAVELET_PARTICIPANTS_CHANGED);
  }
  
  @Override
  public List<Event> getBlipSubmittedEvents() {
    return filterEventsByType(EventType.BLIP_SUBMITTED);
  }

  @Override
  public List<Event> filterEventsByType(EventType eventType) {
    List<Event> filteredEvents = new ArrayList<Event>();
    for (Event event : getEvents()) {
      if (eventType == event.getType()) {
        filteredEvents.add(event);
      }
    }
    return filteredEvents;
  }
  
  public void addOperation(Operation operation) {
    operationMessageBundle.add(operation);
  }
  
  public OperationMessageBundle getOperations() {
    return operationMessageBundle;
  }
  
  @Override
  public Wavelet getWavelet() {
    if (wavelet == null) {
      WaveletData waveletData = eventMessageBundle.getWaveletData();
      if (waveletData == null) {
        waveletData = new WaveletData();
      }
      wavelet = new WaveletImpl(waveletData, this);
    }
    return wavelet;
  }
  
  public Map<String, BlipData> getBlipData() {
   return eventMessageBundle.getBlipData(); 
  }

  @Override
  public boolean blipHasChanged(Blip blip) {
    if (blip != null) {
      for (EventData event : eventMessageBundle.getEvents()) {
        if (event.getType().equals(EventType.DOCUMENT_CHANGED)) {
          if (event.getProperties().get("blipId").equals(blip.getBlipId())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  @Override
  public List<Event> getEvents() {
    if (events == null) {
      events = new ArrayList<Event>();
      for (EventData event : eventMessageBundle.getEvents()) {
        events.add(new EventImpl(event, this));
      }
    }
    return events;
  }

  @Override
  public Wavelet createWavelet(List<String> participants) {
    return createWavelet(participants, null);
  }

  @Override
  @Deprecated
  public Wavelet createWavelet(List<String> participants, String annotationWriteBack) {
    return getWavelet().createWavelet(participants, annotationWriteBack);
  }

  @Override
  public Wavelet getWavelet(String waveId, String waveletId) {
    Tuple<String> key = Tuple.of(waveId, waveletId);
    Wavelet result = wavelets.get(key);
    if (result == null) {
      Wavelet wavelet = getWavelet();
      if (wavelet != null && waveId.equals(wavelet.getWaveId()) &&
          waveletId.equals(wavelet.getWaveletId())) {
        result = wavelet;
      } else {
        WaveletData waveletData = new WaveletData();
        waveletData.setWaveId(waveId);
        waveletData.setWaveletId(waveletId);
        result = new WaveletImpl(waveletData, this);
      }
      wavelets.put(key, result);
    }
    return result;
  }

  @Override
  public Blip getBlip(String waveId, String waveletId, String blipId) {
    Tuple<String> key = Tuple.of(waveId, waveletId, blipId);
    Blip blip = blips.get(key);
    if (blip == null) {
      BlipData blipData = eventMessageBundle.getBlipData().get(blipId);
      if (blipData == null) {
        blipData = new BlipData();
        blipData.setWaveId(waveId);
        blipData.setWaveletId(waveletId);
        blipData.setBlipId(blipId);
      }
      blip = new BlipImpl(blipData, this);
      blips.put(key, blip);
    }
    return blip;
  }
  
  @Override
  public String getRobotAddress() {
    return robotAddress;
  }
}
