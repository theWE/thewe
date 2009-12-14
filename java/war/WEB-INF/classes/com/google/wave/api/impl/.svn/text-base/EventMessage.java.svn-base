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


import java.util.ArrayList;
import java.util.List;

/**
 * EventMessage class used to represent a message sent to the Robot.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
public class EventMessage {

  private EventData event;
  
  private final List<BlipData> blips;
  
  private WaveletData wavelet;

  public EventMessage() {
    event = null;
    blips = new ArrayList<BlipData>();
    wavelet = null;
  }
  
  public void addBlip(BlipData blip) {
    this.blips.add(blip);
  }

  public void addWavelet(WaveletData wavelet) {
    this.wavelet = wavelet;
  }

  public EventData getEvent() {
    return event;
  }

  public boolean hasBlip() {
    return !blips.isEmpty();
  }
  
  public boolean hasEvent() {
    return event != null;
  }

  public boolean hasWavelet() {
    return wavelet != null;
  }

  public void setEvent(EventData event) {
    this.event = event;
  }

  public WaveletData getWavelet() {
    return wavelet;
  }

  public List<BlipData> getBlips() {
    return blips;
  }
}
