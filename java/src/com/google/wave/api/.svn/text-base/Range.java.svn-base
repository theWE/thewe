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
 * Defines a range for an annotation.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public class Range {
  
  /**
   * Start of the range.
   */
  private int start;
  
  /**
   * End of the range.
   */
  private int end;
  
  /**
   * Constructs a default range object, that starts at 0, and ends at 1.
   */
  public Range() {
    this(0, 1);
  }
  
  /**
   * Constructs a range object given a start and end index into the document.
   * 
   * @param start Start of the range.
   * @param end End of the range.
   */
  public Range(int start, int end) {
    // TODO(scovitz): Figure out why Spelly generates zero length range here.
//    if (end - start <= 0) {
//      throw new RuntimeException("Range length cannot be zero or negative.");
//    } else {
      this.start = start;
      this.end = end;
//    }
  }

  /**
   * Returns the starting index of the range.
   * 
   * @return the starting index of the range.
   */
  public int getStart() {
    return start;
  }
  
  /**
   * Returns the ending index of the range.
   * 
   * @return the ending index of the range.
   */
  public int getEnd() {
    return end;
  }
  
  /**
   * Sets the starting index of the range.
   * 
   * @param start the starting index.
   */
  public void setStart(int start) {
    this.start = start;
  }
  
  /**
   * Sets the ending index of the range.
   * 
   * @param end the ending index.
   */
  public void setEnd(int end) {
    this.end = end;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + end;
    result = prime * result + start;
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
    
    Range other = (Range) obj;
    return start == other.start && end == other.end;
  }
}
