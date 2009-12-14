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

import java.util.ArrayList;
import java.util.List;

/**
 * StyledText represents a styled text region, that provides API to set or unset
 * {@link StyleType}. The StyledText can then be used within the TextView to
 * modify the document content.
 * 
 * This object is meant to be used for convenience. The underlying text
 * representation is disjoint from the document. Therefore, once the text is
 * appended / inserted into the document, modifying this object will have no
 * impact on the document.
 * 
 * @author mprasetya@google.com (Marcel Prasetya)
 * @author scovitz@google.com (Seth Covitz)
 * @see StyleType
 */
public class StyledText {

  /**
   * The text being styled.
   */
  private String text;
  
  /**
   * The list of styles to apply to the text.
   */
  private List<StyleType> styles;
  
  /**
   * Constucts an empty StyledText object.
   */
  public StyledText() {
    this("");
  }
  
  /**
   * Constructs a StyledText object, initializing it with the given text.
   */
  public StyledText(String text) {
    this.text = text;
    this.styles = new ArrayList<StyleType>();
  }
  
  /**
   * Constructs a StyledText object, initializing it with a string of text and
   * an initial style.
   * 
   * @param text The text to styled.
   * @param style The style to be applied to the text.
   */
  public StyledText(String text, StyleType style) {
    this(text);
    styles.add(style);
  }

  /**
   * Adds an additional style to this text.
   * 
   * @param style The style to be added.
   */
  public void addStyle(StyleType style) {
    styles.add(style);
  }

  /**
   * Appends text to the end of the styled text range. All current styles will
   * apply to the resulting text.
   * 
   * @param text The text to be appended.
   */
  public void appendText(String text) {
    this.text += text;
  }

  /**
   * Returns the list of styles applied to this object.
   * 
   * @return the list of styles.
   */
  public List<StyleType> getStyles() {
    return styles;
  }

  /**
   * Returns the text managed by this object.
   * 
   * @return the text managed by this object.
   */
  public String getText() {
    return text;
  }

  /**
   * Inserts text at the starting index provided. Styles applying to the current
   * text will apply to the combined text. If the index does not exist,
   * the text is appended to the end of the document.
   * 
   * @param start index into current text with which to perform the insertion.
   * @param text the text to be inserted.
   */
  public void insertText(int start, String text) {
    StringBuilder sb = new StringBuilder(this.text);
    sb.insert(start, text);
    this.text = sb.toString();
  }

  /**
   * Removes all styles from this text object.
   */
  public void removeAllStyles() {
    styles.clear();
  }

  /**
   * Removes the requested style from the list of styles for this text.
   * 
   * @param style the style to be removed.
   */
  public void removeStyle(StyleType style) {
    while (styles.remove(style)) {
    }
  }

  /**
   * Sets/replaces the text of this object. The resulting text continues to be
   * styled by the styles added previously to this object. 
   */
  public void setText(String text) {
    this.text = text;
  }
}
