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
import java.util.Map;

/**
 * A Wavelet represents a part of a threaded conversation and is the unit of
 * access control for a wave. Wavelets contain blips (conversation/replies) as
 * well as data documents which can store free form non-visible metadata about
 * a wave.
 * 
 * All modifications to a Wavelet generate operations. These operations are
 * transmitted back to the Wave Robot Proxy and applied just as operations
 * generated in a Wave client would be.
* 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public interface Wavelet {

  /**
   * Returns the unique id that represents the containing wave for this
   * wavelet.
   *  
   * @return the id for the wave
   */
  public String getWaveId();

  /**
   * Returns the id that represents this wavelet within the containing wave. It
   * is guaranteed to be unique only amongst the wavelets in this wave.
   * 
   * @return the id for the wavelet
   */
  public String getWaveletId();
  
  /**
   * Returns the time when the wavelet was created.
   * 
   * @return the UNIX epoch time in milliseconds.
   */
  public long getCreationTime();
  
  /**
   * Returns the time when the wavelet was last modified.
   * 
   * @return the UNIX epoch time in milliseconds.
   */
  public long getLastModifiedTime();
  
  /**
   * Returns the version of the wavelet.
   * 
   * @return the version of the wavelet.
   */
  public long getVersion();

  /**
   * Creates a new blip and appends it to the end of the wavelet.
   * 
   * @return the newly created blip.
   */
  public Blip appendBlip();

  /**
   * Creates a new blip and appends it to the end of the wavelet. The resulting
   * blip id of the new blip will be stored in a data document on the wavelet
   * whose event was being processed at the time the blip was created.
   * 
   * @param writeBackDataDocument The name of the data document to create.
   * @return the newly created blip.
   */
  public Blip appendBlip(String writeBackDataDocument);

  /**
   * Returns the root blip associated with this wavelet.
   * 
   * @return the root blip.
   */
  public Blip getRootBlip();

  /**
   * Returns the root blip id associated with this wavelet.
   * 
   * @return the id for the root blip.
   */
  public String getRootBlipId();

  /**
   * Sets the title of the wavelet.
   * 
   * @param title The new title of the wavelet.
   */
  public void setTitle(String title);

  /**
   * Sets the title of the wavelet using StyledText.
   * 
   * @param styledText The new formatted title of the wavelet.
   */
  public void setTitle(StyledText styledText);

  /**
   * Returns the title of the wavelet.
   * 
   * @return the title of the wavelet.
   */
  public String getTitle();
  
  /**
   * Returns the list of participant addresses for each participant on
   * the wavelet.
   * 
   * @return list of participant addresses.
   */
  public List<String> getParticipants();
  
  /**
   * Adds a participant to the wavelet.
   * 
   * @param participant The address of the participant to be added.
   */
  public void addParticipant(String participant);
  
  /**
   * Returns the named data document.
   * 
   * @param name The name of the data document to retrieve.
   * @return The string representation of the data document, or null if not
   *     found.
   */
  public String getDataDocument(String name);
  
  /**
   * Creates/replaces the named data document with the data provided. 
   * 
   * @param name The name of the data document.
   */
  public void setDataDocument(String name, String data);
  
  /**
   * Returns a map of data documents for this wavelet.
   * 
   * @return a map of data documents.
   */
  public Map<String, String> getDataDocuments();

  /**
   * Checks whether a data document with the given name is attached to this
   * wavelet.
   * 
   * @param name The name of the data document.
   * @return true if the data document is available, false otherwise.
   */
  public boolean hasDataDocument(String name);

  /**
   * Appends data to the named data document.
   * 
   * @param name The name of the data document.
   * @param data The data to append.
   */
  public void appendDataDocument(String name, String data);
  
  /**
   * Returns the address of the creator of the wavelet.
   * @return the address of the creator of the wavelet.
   */
  public String getCreator();

  /**
   * Removes a participant from the wavelet.
   * 
   * @param participant The address of the participant to be removed.
   */
  public void removeParticipant(String participant);
  
  /**
   * Creates a new wavelet with an initial list of participants. The robot
   * participant will be added by default. The new wave and wavelet ids will be
   * written to a data document on this wavelet.
   * 
   * @param participants the list of participants to add to the wave.
   * @param dataDocumentWriteBack the name of a data document to write the newly
   *     created waveId and waveletId once created.
   * @return the newly created wavelet.
   */
  public Wavelet createWavelet(List<String> participants, String dataDocumentWriteBack);
}
