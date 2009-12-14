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

import java.util.HashMap;
import java.util.Map;

/**
 * Elements are non-text content within a document. What the represent is
 * generally abstracted from the Robot. Although a Robot can query the
 * properties of an element it can only interact with the specific types that
 * the element represents.  
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public class Element {

  /**
   * The type of an element.
   */
  private ElementType type;
  
  /**
   * A map of properties representing details of the element.
   */
  private Map<String, Object> properties;

  /**
   * Constructs an empty Element.
   */
  public Element() {
    this.type = null;
    this.properties = new HashMap<String, Object>();
  }
  
  /**
   * Creates a copy of an element.
   * 
   * @param element the element to copy.
   */
  public Element(Element element) {
    this.type = element.getType();
    this.properties = new HashMap<String, Object>(element.getProperties());
  }

  /**
   * Returns the type of the element.
   * 
   * @return the type of the element.
   */
  public ElementType getType() {
    return type;
  }
  
  /**
   * Returns the map of properties for this element.
   * 
   * @return the map of properties for this element.
   */
  public Map<String, Object> getProperties() {
    return properties;
  }
  
  /**
   * Constructs an Element of the given type.
   * 
   * @param type the type of elment to construct.
   */
  public Element(ElementType type) {
    this.type = type;
    this.properties = new HashMap<String, Object>();
  }
  
  /**
   * Constructs an Element of the given type with an initial set of properties.
   * 
   * @param type the type of the element.
   * @param properties the properties of the element.
   */
  public Element(ElementType type, Map<String, Object> properties) {
    this.type = type;
    this.properties = properties;
  }
  
  /**
   * Replaces the properties of this element with a new set of properties.
   * 
   * @param properties the properties to be set on this element.
   */
  public void setProperties(Map<String, Object> properties) {
    this.properties = properties;
  }
  
  /**
   * Creates/replaces a property with the given to a new value.
   * 
   * @param name the name of the property to create/replace.
   * @param value the value to be set on this property.
   */
  public void setProperty(String name, String value) {
    this.properties.put(name, value);
  }
  
  /**
   * Returns the named property of this element.
   * 
   * @param name the name of the property.
   * @return the value of the property or null if the property was not found.
   */
  public Object getProperty(String name) {
    return this.properties.get(name);
  }
  
  /**
   * Sets the type of this Element.
   * 
   * @param type the type of the element.
   */
  public void setType(ElementType type) {
    this.type = type;
  }

  /**
   * Returns whether this element is a form element.
   * 
   * @return true if the element is a form element, false otherwise.
   */
  public boolean isFormElement() {
    return
        type == ElementType.BUTTON ||
        type == ElementType.CHECK ||
        type == ElementType.INPUT ||
        type == ElementType.PASSWORD ||
        type == ElementType.LABEL ||
        type == ElementType.RADIO_BUTTON ||
        type == ElementType.RADIO_BUTTON_GROUP ||
        type == ElementType.TEXTAREA;
  }
  
  /**
   * Returns whether this element is a gadget.
   * 
   * @return true if the element is a gadget, false otherwise.
   */
  public boolean isGadget() {
    return type == ElementType.GADGET;
  }
  
  /**
   * Returns whether this element is an inline blip.
   * 
   * @return true if the element is an inline blip, false otherwise.
   */
  public boolean isInlineBlip() {
    return type == ElementType.INLINE_BLIP;
  }
  
  /**
   * Returns whether this element is an image.
   * 
   * @return true if the element is an image, false otherwise.
   */
  public boolean isImage() {
    return type == ElementType.IMAGE;
  }
}
