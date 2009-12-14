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
 * An annotation is metadata that augments a range of text in a Document.
 * Example uses of annotations include styling text, supplying spelling
 * corrections, and links to refer that area of text to another document or
 * web site.
 * 
 * The size of an annotation range must be positive and non-zero.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public class Annotation {

  /**
   * The range of the document that this annotation affects.
   */
  private Range range;
  
  /**
   * The name of the annotation used for quick retrieval and identification.
   */
  private String name;
  
  /**
   * The value/data associated with this annotation. 
   */
  private String value;
  
  /**
   * Constructs a default annotation with "annotationName" as the name,
   * "annotationValue" as the value, and a default range from 0 to 1.
   */
  public Annotation() {
    this("annotationName", "annotationValue", new Range(0, 1));
  }
  
  /**
   * Constructs an annotation over a range of the document given a (name,value)
   * pair.
   * 
   * @param name the name of the annotation
   * @param value the data needed for this annotation
   * @param range the range over the document which this annotation affects
   */
  public Annotation(String name, String value, Range range) {
    this.name = name;
    this.value = value;
    this.range = range;
    validate();
  }

  /**
   * Validates the members of the annotation.
   */
  private void validate() {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("The annotation name must not be null or empty.");
    }
    
    if (value == null) {
      throw new IllegalArgumentException("The annotation value must not be null.");
    }
  }
  
  /**
   * Returns the name of the annotation.
   * 
   * @return the name of the annotation
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the annotation.
   * 
   * @param name the new name of the annotation
   */
  public void setName(String name) {
    this.name = name;    
  }

  /**
   * Returns the range of the document which the annotation affects.
   * 
   * @return the range of the document
   */
  public Range getRange() {
    return range;
  }

  /**
   * Sets the range of the annotation.
   * 
   * @param range the new range of the annotation
   */
  public void setRange(Range range) {
    this.range = range;
  }

  /**
   * Returns the value of the annotation.
   * 
   * @return the value of the annotation.
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value of the annotation.
   * 
   * @param value the nwe value of the annotation
   */
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((range == null) ? 0 : range.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    
    if (obj == null) {
      return false;
    }
    
    if (getClass() != obj.getClass()) {
      return false;
    }
    
    Annotation other = (Annotation) obj;
    if ((name == null && other.name != null) ||
        (value == null && other.value != null) ||
        (range == null && other.range != null)) {
      return false;
    }

    return name.equals(other.name) && range.equals(other.range) && value.equals(other.value);
  }
}
