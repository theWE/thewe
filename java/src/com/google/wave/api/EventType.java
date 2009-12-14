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

/**
 * The types of events that Robots can process.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public enum EventType {
  WAVELET_BLIP_CREATED("WAVELET_BLIP_CREATED"),
  WAVELET_BLIP_REMOVED("WAVELET_BLIP_REMOVED"),
  WAVELET_PARTICIPANTS_CHANGED("WAVELET_PARTICIPANTS_CHANGED"),
  WAVELET_SELF_ADDED("WAVELET_SELF_ADDED"),
  WAVELET_SELF_REMOVED("WAVELET_SELF_REMOVED"),
  WAVELET_TIMESTAMP_CHANGED("WAVELET_TIMESTAMP_CHANGED"),
  WAVELET_TITLE_CHANGED("WAVELET_TITLE_CHANGED"),
  WAVELET_VERSION_CHANGED("WAVELET_VERSION_CHANGED"),
  BLIP_CONTRIBUTORS_CHANGED("BLIP_CONTRIBUTORS_CHANGED"),
  BLIP_DELETED("BLIP_DELETED"),
  BLIP_SUBMITTED("BLIP_SUBMITTED"),
  BLIP_TIMESTAMP_CHANGED("BLIP_TIMESTAMP_CHANGED"),
  BLIP_VERSION_CHANGED("BLIP_VERSION_CHANGED"),
  DOCUMENT_CHANGED("DOCUMENT_CHANGED"),
  FORM_BUTTON_CLICKED("FORM_BUTTON_CLICKED");
  
  private final String text;

  private EventType(String text) {
    this.text = text;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return text;
  }
  
  /**
   * Converts a string into an EventType ignoring case in the process. This is
   * used primarily for serialization from JSON.
   * 
   * @param name the name of the event type.
   * @return the converted event type.
   */
  public static EventType valueOfIgnoreCase(String name) {
    return valueOf(name.toUpperCase());
  }
}
