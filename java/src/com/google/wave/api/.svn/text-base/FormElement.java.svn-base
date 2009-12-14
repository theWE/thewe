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
 * Form Elements allow users and robots to build forms for other users to
 * interact with. For each element you can specify its type, name, a default
 * value and a label. The current value of the element is stored with in.  
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public class FormElement extends Element {
  
  /**
   * Default Constructor
   */
  public FormElement() {
  }
  
  /**
   * Constructs a form element of the given type.
   */
  public FormElement(ElementType type) {
    this(type, "", "", "", "");
  }
  
  /**
   * Constructs a form element given a type and name.
   */
  public FormElement(ElementType type, String name) {
    this(type, "", name, "", "");
  }
  
  /**
   * Constructs a form element given a type, name and default value.
   */
  public FormElement(ElementType type, String name, String defaultValue) {
    this(type, "", name, defaultValue, defaultValue);
  }
  
  /**
   * Constructs a form element given a type, label, name and default value.
   */
  public FormElement(ElementType type, String label, String name, String defaultValue) {
    this(type, label, name, defaultValue, defaultValue);
  }
  
  /**
   * Creates a copy of an existing form element.
   */
  public FormElement(FormElement formElement) {
    this(formElement.getType(),
        (String) formElement.getProperty("label"),
        (String) formElement.getProperty("name"),
        (String) formElement.getProperty("defaultValue"),
        (String) formElement.getProperty("value"));
  }

  /**
   * Constructs a form element specifying all fields.
   */
  public FormElement(ElementType type, String label, String name, String defaultValue,
      String value) {
    super(type);
    setProperty("label", label);
    setProperty("name", name);
    setProperty("defaultValue", defaultValue);
    setProperty("value", value);
  }

  /**
   * Returns the label for the form element.
   * 
   * @return the label for the form element.
   */
  public String getLabel() {
    return getPropertyNullCheck("label");
  }

  /**
   * Sets the label text for the form element.
   * 
   * @param label the new label for the form element.
   */
  public void setLabel(String label) {
    setProperty("label", label);
  }

  /**
   * Returns the name of the form element.
   * 
   * @return the name of the form element.
   */
  public String getName() {
    return getPropertyNullCheck("name");
  }

  private String getPropertyNullCheck(String name) {
    String property = (String) getProperty(name);
    return property == null ? "" : property;
  }

  /**
   * Sets the name of the form element.
   * 
   * @param name the new name of the form element.
   */
  public void setName(String name) {
    setProperty("name", name);
  }

  /**
   * Returns the default value of the form element.
   * 
   * @return the default value.
   */
  public String getDefaultValue() {
    return getPropertyNullCheck("defaultValue");
  }

  /**
   * Sets the default value of the form element. The default value is used
   * to initialize the form element and to test whether or not it has been
   * modified.
   * 
   * @param defaultValue the new default value of the form element.
   */
  public void setDefaultValue(String defaultValue) {
    setProperty("defaultValue", defaultValue);
  }

  /**
   * Returns the current value of the form element.
   * 
   * @return the current value of the form element.
   */
  public String getValue() {
    return getPropertyNullCheck("value");
  }

  /**
   * Sets the value of the form element.
   * 
   * @param value the new value of the form element.
   */
  public void setValue(String value) {
    setProperty("value", value);
  }  
}
