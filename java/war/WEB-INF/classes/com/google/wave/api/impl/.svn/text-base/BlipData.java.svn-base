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

import com.google.wave.api.Annotation;
import com.google.wave.api.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * BlipData is the serializable data representation of a Blip. It contains
 * metadata, a text-only representation of the document content, and a list of
 * annotations.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public class BlipData {

  /**
   * The list of annotations for the document content. 
   */
  private List<Annotation> annotations;
  
  /**
   * The list of elements embedded within the document.
   */
  private Map<String, Element> elements;

  /**
   * The blip id for this blip.
   */
  private String blipId;

  /**
   * A list of child blip ids for this blip.
   */
  private List<String> childBlipIds;

  /**
   * A list of contributors to this blip.
   */
  private List<String> contributors;

  /**
   * The creator of this blip.
   */
  private String creator;

  /**
   * The text document content for this blip.
   */
  private String content;

  /**
   * The time this blip was last modified.
   */
  private long lastModifiedTime;

  /**
   * The parent blip id for this blip.
   */
  private String parentBlipId;

  /**
   * The latest version number for this blip.
   */
  private long version;

  /**
   * The Wave ID for the wave containing this blip.
   */
  private String waveId;

  /**
   * The Wavelet ID for the wavelet containing this blip.
   */
  private String waveletId;
  
  /**
   * Constructs an empty BlipData object.
   */
  public BlipData() {
    annotations = new ArrayList<Annotation>();
    elements = new HashMap<String, Element>();
    creator = null;
    childBlipIds = new ArrayList<String>();
    content = "\n";
    contributors = new ArrayList<String>();
    blipId = null;
    lastModifiedTime = -1L;
    version = -1L;
    parentBlipId = null;
    waveId = null;
    waveletId = null;
  }
  
  /**
   * Creates a deep copy/clone of a blip's data.
   * 
   * @param blip The original blip to be copied. 
   */
  public BlipData(BlipData blip) {
    // Deep copy annotations.
    annotations = new ArrayList<Annotation>();
    for (Annotation annotation : blip.getAnnotations()) {
      annotations.add(new Annotation(annotation.getName(), annotation.getValue(),
          annotation.getRange()));
    }
    
    // Deep copy form elements.
    elements = new HashMap<String, Element>();
    for (Entry<String, Element> entry : blip.getElements().entrySet()) {
      elements.put(entry.getKey(), new Element(entry.getValue()));
    }
    
    creator = blip.getCreator();
    childBlipIds = blip.getChildBlipIds();
    content = blip.getContent();
    contributors = blip.getContributors();
    blipId = blip.getBlipId();
    lastModifiedTime = blip.getLastModifiedTime();
    version = blip.getVersion();
    parentBlipId = blip.getParentBlipId();
    waveId = blip.getWaveId();
    waveletId = blip.getWaveletId();
  }
  
  /**
   * Adds an annotation to the end of the list of annotations.
   * 
   * @param annotation the annotation to be added.
   */
  public void addAnnotation(Annotation annotation) {
    annotations.add(annotation);
  }

  /**
   * Returns the list of annotations modifying this document's content.
   * 
   * @return a list of annotations.
   */
  public List<Annotation> getAnnotations() {
    return annotations == null ? new ArrayList<Annotation>() : annotations;
  }
  
  /**
   * Adds an element to the blip at a given index into the text document.
   * 
   * @param position The character position / index into the document to insert
   *     the form element.
   * @param element The form element to be added.
   */
  public void addElement(int position, Element element) {
    elements.put(Integer.toString(position), element);
  }
  
  /**
   * Returns a map of the elements in the blip and the positions where
   * they have been inserted.
   * 
   * @return the map of form elements to document positions.
   */
  public Map<String, Element> getElements() {
    return elements;
  }
  
  /**
   * Returns the Blip ID for this blip.
   * 
   * @return the blip id for this blip.
   */
  public String getBlipId() {
    return blipId;
  }

  /**
   * Returns a list of child Blip IDs for this blip.
   * 
   * @return a list of child Blip IDs.
   */
  public List<String> getChildBlipIds() {
    return childBlipIds;
  }

  /**
   * Returns the list of email addresses corresponding to the contributors who
   * have modified this blip's content.
   * 
   * @return the list of contributors.
   */
  public List<String> getContributors() {
    return contributors;
  }

  /**
   * Returns the email address corresponding to the creator of this blip.
   * 
   * @return the creator of this blip.
   */
  public String getCreator() {
    return creator;
  }

  /**
   * Returns the text document content for this blip.
   * 
   * @return the text document content for this blip.
   */
  public String getContent() {
    return content;
  }

  /**
   * Returns the time in milliseconds since the UNIX epoch when this blip was
   * last modified.
   * 
   * @return the last modified time for this blip.
   */
  public long getLastModifiedTime() {
    return lastModifiedTime;
  }

  /**
   * Returns the parent Blip ID for this blip. 
   * 
   * @return the parent Blip ID for this blip.
   */
  public String getParentBlipId() {
    return parentBlipId;
  }

  /**
   * Returns the version number for this blip.
   * 
   * @return the version number for this blip.
   */
  public long getVersion() {
    return version;
  }
  
  /**
   * Returns the Wave ID for the wave containing this blip.
   * 
   * @return the Wave ID for the wave containing this blip.
   */
  public String getWaveId() {
    return waveId;
  }

  /**
   * Returns the Wavelet ID for the wavelet containing this blip.
   * 
   * @return the Wavelet ID for the wavelet containing this blip.
   */
  public String getWaveletId() {
    return waveletId;
  }

  /**
   * Replaces the blip's list of annotations with a new list of annotations.
   * 
   * @param annotations the new list of annotations.
   */
  public void setAnnotations(List<Annotation> annotations) {
    this.annotations = annotations;
  }

  /**
   * Replaces the blip's list of elements with a new list of elements.
   * 
   * @param elements the new list of elements.
   */
  public void setElements(Map<String, Element> elements) {
    this.elements = elements;
  }
  
  /**
   * Returns the Blip ID for this blip.
   * 
   * @param blipId the Blip ID for this blip.
   */
  public void setBlipId(String blipId) {
    this.blipId = blipId;
  }

  /**
   * Replaces the blip's list of child Blip IDs with a new list. 
   * 
   * @param childBlipIds the new list of child Blip IDs.
   */
  public void setChildBlipIds(List<String> childBlipIds) {
    this.childBlipIds = childBlipIds;
  }
  
  /**
   * Adds a new child blip id to this blip's list of child id's.
   * 
   * @param blipId the Blip ID to be added.
   */
  public void addChildBlipId(String blipId) {
    this.childBlipIds.add(blipId);
  }

  /**
   * Replaces the blip's list of contributors with a new list.
   * 
   * @param contributors the new list of contributors.
   */
  public void setContributors(List<String> contributors) {
    this.contributors = contributors;
  }
  
  /**
   * Adds a contributor to this blip's list of contributors.
   * 
   * @param contributor a new contributor to the blip.
   */
  public void addContributor(String contributor) {
    this.contributors.add(contributor);
  }

  /**
   * Sets the creator of the blip.
   * 
   * @param creator the creator of the blip.
   */
  public void setCreator(String creator) {
    this.creator = creator;
  }

  /**
   * Replaces the blip's text document content.
   * 
   * @param content the new text content for the blip.
   */
  public void setContent(String content) {
    this.content = content;
  }

  /**
   * Sets the last modified time measured in milliseconds since the UNIX epoch
   * when the blip was last modified.
   * 
   * @param lastModifiedTime the last modified time of the blip.
   */
  public void setLastModifiedTime(long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  /**
   * Set's the parent Blip ID for the blip.
   * 
   * @param parentBlipId the parent blip id.
   */
  public void setParentBlipId(String parentBlipId) {
    this.parentBlipId = parentBlipId;
  }

  /**
   * Sets the version of the blip.
   * 
   * @param version the version of the blip.
   */
  public void setVersion(long version) {
    this.version = version;
  }
  
  /**
   * Sets the Wave ID of the blip.
   * 
   * @param waveId the Wave ID of the blip. 
   */
  public void setWaveId(String waveId) {
    this.waveId = waveId;
  }

  /**
   * Sets the Wavelet ID of the blip.
   * 
   * @param waveletId the Wavelet ID of the blip.
   */
  public void setWaveletId(String waveletId) {
    this.waveletId = waveletId;
  }

  public void removeChildBlipId(String blipId) {
    childBlipIds.remove(blipId);
  }
}
