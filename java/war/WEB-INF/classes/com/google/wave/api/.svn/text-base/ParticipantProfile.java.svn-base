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
 * ParticipantProfile represents participant information. It contains display
 * name, avatar's URL, and an external URL to view the participant's profile
 * page. This is a data transfer object that is being sent from Robot when Rusty
 * queries a profile. A participant can be the Robot itself, or a user in the
 * Robot's domain.
 * 
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public final class ParticipantProfile {

  private final String name;
  private final String imageUrl;
  private final String profileUrl;

  /**
   * Constructs an empty profile.
   */
  public ParticipantProfile() {
    this("", "", "");
  }
  
  /**
   * Constructs a profile.
   * 
   * @param name The name of the participant.
   * @param imageUrl The URL of the participant's avatar.
   * @param profileUrl The URL of the participant's external profile page.
   */
  public ParticipantProfile(String name, String imageUrl, String profileUrl) {
    this.name = name;
    this.imageUrl = imageUrl;
    this.profileUrl = profileUrl;
  }
  
  /**
   * Returns the name of the participant.
   * 
   * @return the name of the participant.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the URL of the participant's avatar.
   * 
   * @return The URL of the participant's avatar.
   */
  public String getImageUrl() {
    return imageUrl;
  }

  /**
   * Returns the URL of the participant's external profile page.
   * 
   * @return The URL of the profile page.
   */
  public String getProfileUrl() {
    return profileUrl;
  }
}
