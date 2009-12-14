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
 * Enumeration that represents the context that the robot needs to provide when
 * calling the Robot's event handler. This is specified in the Robot's
 * capabilities.xml.
 * 
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public enum Context {
  PARENT,
  SIBLINGS,
  CHILDREN;
  
  public static Context valueOfIgnoreCase(String name) {
    return valueOf(name.toUpperCase());
  }
}
