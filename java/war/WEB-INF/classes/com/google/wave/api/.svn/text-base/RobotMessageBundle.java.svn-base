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

import java.util.List;

/**
 * Interface to a Robot Message Bundle used to process incoming Wave events. 
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public interface RobotMessageBundle {

  /**
   * Returns a list of events in this bundle.
   * 
   * @return a list of events.
   */
  public List<Event> getEvents();

  /**
   * Returns a list of events filtered by the specified EventType.
   * 
   * @param eventType The EventType to filter by.
   * @return a list of filtered events.
   */
  public List<Event> filterEventsByType(EventType eventType);

  /**
   * Returns a list of ParticipantChanged events included in the event bundle.
   *  
   * @return a list of ParticipantChanged events.
   */
  public List<Event> getParticipantsChangedEvents();

  /**
   * Returns a list of BlipSubmitted events included in the event bundle.
   * 
   * @return a list of BlipSubmitted events.
   */
  public List<Event> getBlipSubmittedEvents();
  
  /**
   * Returns the Wavelet associated with all of the events in the bundle.
   * 
   * @return the Wavelet.
   */
  public Wavelet getWavelet();
  
  /**
   * Convenience routine that tests if this robot was just added to the wave.
   *
   * @return true if the robot was just added to the wave, false otherwise.
   */
  public boolean wasSelfAdded();

  /**
   * Convenience routine that tests if this robot was just removed from the
   * wave.
   *
   * @return true if the robot was just removed from the wave, false otherwise.
   */
  public boolean wasSelfRemoved();

  /**
   * Convenience routine that tests if there is only a single root blip in this
   * wavelet. Typically this is the case for a new wave.
   * 
   * @return true if there is only a single blip in the wave, false otherwise.
   */
  public boolean isNewWave();
  
  /**
   * Convenience routine that tests if a participant was just added to a new
   * wave. The same logic to determine isNewWave() is used here.
   * 
   * @param participantId Participant to check for inclusion on the wave.
   * @return true if the participant was just added and the wave is new, false
   *     otherwise.
   */
  public boolean wasParticipantAddedToNewWave(String participantId);

  /**
   * Convenience routine that tests if a participant was just added to the wave.
   * 
   * @param participantId Participant to check for inclusion on the wave.
   * @return true if the participant was just added to the wave, false
   *     otherwise.
   */
  public boolean wasParticipantAddedToWave(String participantId);

  /**
   * Convenience routine that checks the event bundle to see if the given blip
   * has changed.
   * 
   * @param blip Blip to check for change events.
   * @return true if the Blip has changed, false otherwise.
   */
  public boolean blipHasChanged(Blip blip);

  /**
   * Creates a new wavelet with an initial list of participants. The robot
   * participant will be added by default.
   * 
   * @param participants the list of participants to add to the wave.
   * @return the newly created wavelet.
   */
  public Wavelet createWavelet(List<String> participants);

  /**
   * Creates a new wavelet with an initial list of participants. The robot
   * participant will be added by default. The new wave and wavelet ids will be
   * written to a data document on the wavelet whose event was being processed
   * at the time the wavelet was created.
   * 
   * @param participants the list of participants to add to the wave.
   * @param dataDocumentWriteBack the name of a data document to write the newly
   *     created waveId and waveletId once created.
   * @return the newly created wavelet.
   * @deprecated Replaced by {@link Wavelet#createWavelet(List<String>, String)
   */
  @Deprecated
  public Wavelet createWavelet(List<String> participants, String dataDocumentWriteBack);

  /**
   * Creates a reference wavelet for the given wave id and wavelet id. If the
   * wavelet is not currently on the robot, the metadata may not be complete.
   * Still the wavelet may be used to perform operations where this metadata is
   * not needed.
   * 
   * If the wavelet does not exist in the Wave system, no operations will be
   * performed. A new wavelet must be created with the createWavelet() method.
   * 
   * @param waveId the wave id of the wavelet
   * @param waveletId the wavelet id of the wavelet
   * @return a wavelet referred to by the wave id / wavelet id combination.
   */
  public Wavelet getWavelet(String waveId, String waveletId);
  
  /**
   * Creates a reference blip for the given wave id, wavelet id and blip id. If
   * the blip is not currently on the robot, the metadata may not be complete.
   * Still the blip may be used to perform operations where this metadata is
   * not needed.
   * 
   * If the blip does not exist in the Wave system, no operations will be
   * performed. A new blip must be created with the appendBlip(),
   * createChild(), or insertInlineBlip() methods.
   * 
   * @param waveId the wave id of the wavelet
   * @param waveletId the wavelet id of the wavelet
   * @param blipId the blip id of the blip
   * @return a blip referred to by the wave id / wavelet id / blip id triple. 
   */
  public Blip getBlip(String waveId, String waveletId, String blipId);

  /**
   * Returns the address of the robot.
   *
   * @return the address of the robot. 
   */
  public String getRobotAddress();
}

