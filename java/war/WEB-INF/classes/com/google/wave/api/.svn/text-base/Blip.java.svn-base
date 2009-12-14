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
 * A blip is a unit of conversation in a Wave. It is a node in a tree of other
 * nodes and may have a parent and children. It contains metadata to keep
 * track of contributors and versioning.
 * 
 * Changes to this blip will generate Operations that will be applied to the
 * wave once the response has been received by the Robot proxy.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public interface Blip {
  
  /**
   * Returns the Wavelet that contains this blip.
   * 
   * @return the wavelet that contains this blip
   */
  public Wavelet getWavelet();

  /**
   * Returns the Blip ID corresponding to this blip. This along with the Wave
   * ID and Wavelet ID can uniquely identify this blip. This information can
   * enable the Robot to send future changes to this blip without receiving an
   * event first.
   * 
   * @return the blipId for this blip.
   */
  public String getBlipId();
  
  /**
   * Returns the email address corresponding to the Wave user that created this
   * blip.
   * 
   * @return the creator of the blip
   */
  public String getCreator();
  
  /**
   * Returns the list of contributors to this blip.
   * 
   * @return the list of contributors to this blip
   */
  public List<String> getContributors();
  
  /**
   * Returns the time measured in milliseconds since the UNIX epoch when this
   * blip was last modified.
   * 
   * @return the last modified time for this blip
   */
  public long getLastModifiedTime();
  
  /**
   * Returns the latest version of the blip. 
   * 
   * @return the version of the blip
   */
  public long getVersion();

  /**
   * Creates a new blip as a child of the this blip. The child will be appended
   * to the end of the list of children.
   * 
   * @return the newly added child blip
   */
  public Blip createChild();
  
  /**
   * Deletes the current blip and all of its descendants.
   */
  public void delete();

  /**
   * Returns the Nth child blip of this blip (if available). Use the 
   * isChildAvailable() method to check for availability of the child blip.
   * 
   * Note: The behavior of this method is dependent on the 'context' settings
   * in the Capabilities XML configuration. Child blips may not have been sent
   * with this event.
   * 
   * @param index the index of the child to be returned
   * @return returns the Nth child blip or null if the blip is not available.
   */
  public Blip getChild(int index);

  /**
   * Checks whether the Nth child blip of this blip is available to be
   * returned.
   * 
   * Note: The behavior of this method is dependent on the 'context' settings
   * in the Capabilities XML configuration. Child blips may not have been sent
   * with this event.
   * 
   * @param index the index of the child blip to check
   * @return true if the child blip is available, false otherwise.
   */
  public boolean isChildAvailable(int index);
  
  /**
   * Returns the children blips of this blip.
   * 
   * Note: The behavior of this method is dependent on the 'context' settings
   * in the Capabilities XML configuration. Child blips may not have been sent
   * with this event.
   * 
   * @return a list of children blips for this blip.
   */
  public List<Blip> getChildren();
  
  /**
   * Returns the list of child blips ids for this blip. This call operates on
   * blip metadata and is not dependent on the 'context' setting in the
   * Capabilities XML configuration.
   * 
   * @return the list of child blip ids.
   */
  public List<String> getChildBlipIds();
  
  /**
   * Returns the parent blip for this blip. Use the isParentAvaialble() method
   * to check if the parent is available for this blip.
   * 
   * Note: The behavior of this method is dependent on the 'context' settings
   * in the Capabilities XML configuration. The parent blip may not have been
   * sent with this event. 
   * 
   * @return the parent blip for this blip or null if it is not available.
   */
  public Blip getParent();

  /**
   * Returns the parent blip id for this blip. This call operates on blip
   * metadata and is not dependent on the 'context' setting in the Capabilities
   * XML configuration.
   * 
   * @return the parent blip id for this blip.
   */
  public String getParentBlipId();
  
  /**
   * Returns whether the parent blip is available.
   * 
   * Note: The behavior of this method is dependent on the 'context' settings
   * in the Capabilities XML configuration. The parent blip may not have been
   * sent with this event.
   * 
   * @return true if the parent is available, false otherwise.
   */
  public boolean isParentAvailable();

  /**
   * Determines whether this blip has children. This call operates on blip
   * metadata and is not dependent on the 'context' setting in the Capabilities
   * XML configuration.
   * 
   * @return true if this blip has children, false otherwise.
   */
  public boolean hasChildren();
  
  /**
   * Returns the document content associated with this blip. Use the
   * isDocumentAvailable() method to check for document availability. 
   * 
   * Note: The behavior of this method is dependent on the 'content' setting in
   * the Capabilities XML configuration.
   * 
   * @return the text view interface to the document content or null if it is
   *     not available.
   */
  public TextView getDocument();
  
  /**
   * Returns whether the document content is available in the blip.
   * 
   * Note: The behavior of this method is dependent on the 'content' setting
   * in the Capabilities XML configuration.
   * 
   * @return true if the document content is available, false otherwise.
   */
  public boolean isDocumentAvailable();

  /**
   * Removes an inline blip from this blip.
   * 
   * @param child the child blip to be deleted.
   */
  public void deleteInlineBlip(Blip child);
}
