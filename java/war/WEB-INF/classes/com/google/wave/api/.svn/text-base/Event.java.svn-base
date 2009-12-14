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

package com.google.wave.api;

import java.util.Collection;

/**
 * An event captures changes made to a Wavelet, Blip, or Document in the Wave
 * system. 
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public interface Event {
  
  /**
   * Returns the Wavelet affected by this event.
   * 
   * @return the Wavelet affected by this event.
   */
  public Wavelet getWavelet();

  /**
   * Returns the Blip affected by this event (if applicable).
   * 
   * @return the Blip affected by this event, if applicable. Null otherwise.
   */
  public Blip getBlip();
  
  /**
   * Returns the type of event this instance represents.
   * 
   * @return the type of event this instance represents.
   */
  public EventType getType();
  
  /**
   * Returns the actor/contributor responsible for triggering this event.
   * 
   * @return the actor/contributor responsible for triggering this event.
   */
  public String getModifiedBy();
  
  /**
   * Returns the timestamp measured in milliseconds since the UNIX epoch, when
   * the event was triggered.
   * 
   * @return the timestamp of the event.
   */
  public Long getTimestamp();
  
  /**
   * Returns a list of participants added to the Wavelet (if applicable).
   * 
   * @return a list of participants.
   */
  public Collection<String> getAddedParticipants();

  /**
   * Returns a list of participants removed from the Wavelet (if applicable).
   * 
   * @return a list of participants.
   */
  public Collection<String> getRemovedParticipants();
  
  /**
   * Returns the id of the removed blip (if applicable).
   * 
   * @return the id of the removed blip.
   */
  public String getRemovedBlipId();
  
  /**
   * Returns the id of the newly created blip (if applicable).
   * 
   * @return the id of the newly created blip.
   */
  public String getCreatedBlipId();
  
  /**
   * Returns the title of the wave that changed during the event (if
   *     applicable).
   * 
   * @return the title of the wave.
   */
  public String getChangedTitle();
  
  /**
   * Returns the changed version of the wave or blip (if applicable).
   * 
   * @return the changed version number.
   */
  public Long getChangedVersion();
  
  /**
   * Returns the name of the button that is associated with this event (if
   * applicable).
   */
  public String getButtonName();
}
