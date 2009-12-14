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
 * Gadgets are external code that can be executed within a protected
 * environment within a Wave. Gadgets are indentified by the url that points to
 * their gadget specification. Gadgets can also maintain state that both they
 * and Robots can modify.  
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public class Gadget extends Element {

  /**
   * Constructs an empty gadget.
   */
  public Gadget() {
    super(ElementType.GADGET);
    setUrl("");
  }
  
  /**
   * Constructs a gadget for the specified url.
   *  
   * @param url the url of the gadget specification.
   */
  public Gadget(String url) {
    super(ElementType.GADGET);
    setUrl(url);
  }

  /**
   * Returns the URL for the gadget.
   * 
   * @return the URL for the gadget.
   */
  public String getUrl() {
    return (String) getProperty("url");
  }
  
  /**
   * Changes the URL for the gadget to the given url. This will cause the new
   * gadget to be initialized and loaded.
   * 
   * @param url the new gadget url.
   */
  public void setUrl(String url) {
    setProperty("url", url);
  }
  
  /**
   * Deletes the field represented by the given key from the gadget's state.
   * 
   * @param key The key that identifies the field.
   */
  public void deleteField(String key) {
    getProperties().remove(key);
  }
  
  /**
   * Returns the value of the field with the given key.
   * 
   * @param key The key that identifies the field.
   * @return The value of the field.
   */
  public String getField(String key) {
    return (String) getProperty(key);
  }

  /**
   * Creates or replaces a field matching the key with the given value. Only
   * a single field with a given key is allowed.
   */
  public void setField(String key, String value) {
    setProperty(key, value);
  }
}
