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
 * An interface for processing Robot events.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public interface RobotServlet {

  /**
   * The main event loop for a Robot. The robot will process the set of events
   * as a whole or one by one. Each event contains the Wavelet, Blips and
   * Documents that are affected. More context and content are provided for an
   * event depending on the settings in the Capabilities XML configuration.
   * 
   * Modifications to the Wavelet, Blips and Documents attached to an event
   * will generate operations that are transmitted back to the Wave Robot Proxy
   * and applied to the original Wave as intended.
   * 
   * @param events The set of events to be processed.
   */
  public void processEvents(RobotMessageBundle events);
}
