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

package com.google.wave.api.impl;

import com.google.wave.api.Annotation;
import com.google.wave.api.Blip;
import com.google.wave.api.Element;
import com.google.wave.api.ElementType;
import com.google.wave.api.FormElement;
import com.google.wave.api.FormView;
import com.google.wave.api.GadgetView;
import com.google.wave.api.Range;
import com.google.wave.api.StyleType;
import com.google.wave.api.StyledText;
import com.google.wave.api.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * {@link TextView} implementation.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public class TextViewImpl implements TextView {

  static private abstract class AnnotationMatcher {
    public boolean match(Annotation annotation) {
      return matchRange(annotation) && matchContent(annotation);
    }
    
    public boolean matchRange(Annotation annotation) {
      return true;
    }
    
    public abstract boolean matchContent(Annotation annotation);
  }
  
  static private abstract class RangedAnnotationMatcher extends AnnotationMatcher {
    private final Range range;

    public RangedAnnotationMatcher(Range range) {
      this.range = range;
    }
    
    @Override
    public boolean matchRange(Annotation ann) {
      return ann.getRange().getStart() <= range.getStart() &&
          range.getEnd() <= ann.getRange().getEnd();
    }
  }

  private static final Pattern htmlTagPattern = Pattern.compile("</?[a-zA-Z][^>]*>");

  private BlipData blipData;

  private RobotMessageBundleImpl events;

  @SuppressWarnings("unchecked")
  public TextViewImpl(BlipData blipData, RobotMessageBundleImpl events) {
    this.blipData = blipData;
    this.events = events;
  }
  
  @Override
  public void insert(int start, String text) {
    // Validate the starting index.
    if (start < 0 || start > blipData.getContent().length()) {
      throw new IndexOutOfBoundsException("Invalid start index " + start);
    }
    
    // Ensure that insertion to the end of the content ends with "\n".
    if (start == blipData.getContent().length() && !text.endsWith("\n")) {
      text += "\n";
    }

    // Generate operation if this is a modification to an existing blip.
    if (blipData.getBlipId() != null) {
      Operation operation = new OperationImpl(OperationType.DOCUMENT_INSERT, blipData.getWaveId(),
          blipData.getWaveletId(), blipData.getBlipId(), start, text);
      events.addOperation(operation);
    }
    
    // Insert the text to the underlying blip data.
    StringBuilder sb = new StringBuilder(blipData.getContent());
    sb.insert(start, text);
    blipData.setContent(sb.toString());
    expandOrShiftAnnotations(start, text.length());
  }

  @Override
  public void append(String text) {
    if (blipData.getBlipId() != null) {
      // It's a modification to an existing blip, queue up operation.
      Operation operation = new OperationImpl(OperationType.DOCUMENT_APPEND, blipData.getWaveId(),
          blipData.getWaveletId(), blipData.getBlipId(), 0, text);
      events.addOperation(operation);
    }
    blipData.setContent(blipData.getContent().concat(text));
  }
  
  @Override
  public void appendMarkup(String content) {
    if (blipData.getBlipId() != null) {
      Operation operation = new OperationImpl(OperationType.DOCUMENT_APPEND_MARKUP,
          blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(), 0, content);
      events.addOperation(operation);
    }

    String stripped = htmlTagPattern.matcher(content).replaceAll("");
    blipData.setContent(blipData.getContent() + stripped);
  }
  
  @Override
  public void delete(Range range) {
    int start = range.getStart();
    int end = range.getEnd();
    
    if (start < 0 || end >= blipData.getContent().length() || start >= end) {
      throw new IndexOutOfBoundsException("Invalid range " + start + " - " + end);
    }
    
    if (blipData.getBlipId() != null) {
      // It's a modification to an existing blip, queue up operation.
      Operation operation = new OperationImpl(OperationType.DOCUMENT_DELETE, blipData.getWaveId(),
          blipData.getWaveletId(), blipData.getBlipId(), -1, range);
      events.addOperation(operation);
    }

    // Delete the text from the underlying blip data.
    StringBuilder sb = new StringBuilder(blipData.getContent());
    sb.delete(range.getStart(), range.getEnd());
    blipData.setContent(sb.toString());
    shrinkOrShiftAnnotations(range.getStart(), range.getEnd() - range.getStart());
  }

  @Override
  public void delete() {
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_DELETE, blipData.getWaveId(),
        blipData.getWaveletId(), blipData.getBlipId(), -1, null));
    blipData.getAnnotations().clear();
    blipData.setContent("");
    blipData.getElements().clear();
  }

  @Override
  public String getText() {
    return blipData.getContent();
  }

  @Override
  public String getText(Range range) {
    int start = range.getStart();
    int end = range.getEnd();
    if (start < 0 || end > blipData.getContent().length()) {
      throw new IndexOutOfBoundsException("Invalid range " + start + " - " + end);
    }
    return blipData.getContent().substring(start, end);
  }

  @Override
  public void replace(String text) {
    delete();
    insert(0, text);
  }

  @Override
  public void replace(Range range, String text) {
    delete(range);
    insert(range.getStart(), text);
  }

  @Override
  public void appendStyledText(StyledText styledText) {
    if (blipData.getBlipId() != null) {
      Operation operation = new OperationImpl(OperationType.DOCUMENT_APPEND_STYLED_TEXT,
          blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
          styledText.getStyles().get(0).ordinal(), styledText.getText());
      events.addOperation(operation);
    }
    
    int start = blipData.getContent().length();
    int end = start + styledText.getText().length();

    String text = styledText.getText();
    if (!text.endsWith("\n")) {
      text = text.concat("\n");
    }
    blipData.setContent(blipData.getContent().concat(text));
    for (StyleType type : styledText.getStyles()) {
      blipData.addAnnotation(new Annotation("styled-text", type.toString(),
          new Range(start, end)));
    }
  }

  @Override
  public void insertStyledText(int start, StyledText styledText) {
    insert(start, styledText.getText());
    Range range = new Range(start, start + styledText.getText().length());
    for (StyleType style : styledText.getStyles()) {
      setAnnotation(range, "styled-text", style.toString());
    }
  }

  @Override
  public void replaceStyledText(Range range, StyledText styledText) {
    delete(range);
    insertStyledText(range.getStart(), styledText);
  }

  @Override
  public void replaceStyledText(StyledText styledText) {
    delete();
    insertStyledText(0, styledText);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public List<Annotation> getAnnotations() {
    return blipData.getAnnotations();
  }

  @Override
  public List<Annotation> getStyles() {
    return getAnnotations(new AnnotationMatcher() {
      @Override
      public boolean matchContent(Annotation annotation) {
        return annotation.getName().equals("styled-text");
      } 
    });
  }

  @Override
  public void setAnnotation(String name, String value) {
    setAnnotation(null, name, value);
  }

  @Override
  public void setStyle(StyleType style) {
    setAnnotation("styled-text", style.toString());
  }
  
  @Override
  public Blip appendInlineBlip() {
    BlipData inlineBlipData = new BlipData();
    inlineBlipData.setWaveId(blipData.getWaveId());
    inlineBlipData.setWaveletId(blipData.getWaveletId());
    inlineBlipData.setBlipId("TBD" + Math.random());
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_INLINE_BLIP_APPEND, 
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        0, inlineBlipData));
    return new BlipImpl(inlineBlipData, events);
  }

  @Override
  public void deleteInlineBlip(Blip blip) {
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_INLINE_BLIP_DELETE, 
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        0, blip.getBlipId()));
    blipData.getChildBlipIds().remove(blip.getBlipId());
    events.getBlipData().remove(blip.getBlipId());
    for (String index : blipData.getElements().keySet()) {
      Element element = getElement(Integer.parseInt(index));
      if (element.isInlineBlip() && element.getProperty("blipId").equals(blip.getBlipId())) {
        deleteElement(Integer.parseInt(index));
      }
    }
  }

  @Override
  public List<Blip> getInlineBlips() {
    List<Blip> inlineBlips = new ArrayList<Blip>();
    for (Element element : blipData.getElements().values()) {
      if (element.isInlineBlip() &&
          element.getProperty("blipId") != null &&
          events.getBlipData().containsKey(element.getProperty("blipId"))) {
        inlineBlips.add(new BlipImpl(events.getBlipData().get(element.getProperty("blipId")),
            events));
      }
    }
    return inlineBlips;
  }

  @Override
  public Blip insertInlineBlip(int start) {
    BlipData inlineBlipData = new BlipData();
    inlineBlipData.setWaveId(blipData.getWaveId());
    inlineBlipData.setWaveletId(blipData.getWaveletId());
    inlineBlipData.setBlipId("TBD" + Math.random());
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_INLINE_BLIP_INSERT, 
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        start, inlineBlipData));
    return new BlipImpl(inlineBlipData, events);
  }

  @Override
  public List<Annotation> getAnnotations(final String name) {
    return getAnnotations(new AnnotationMatcher() {
      @Override
      public boolean matchContent(Annotation annotation) {
        return annotation.getName().equals(name);
      } 
    });
  }

  @Override
  public List<Annotation> getStyles(final StyleType style) {
    return getAnnotations(new AnnotationMatcher() {
      @Override
      public boolean matchContent(Annotation annotation) {
        return annotation.getName().equals("styled-text") &&
            annotation.getValue().equals(style.toString());
      } 
    });
  }

  @Override
  public List<Annotation> getAnnotations(Range range) {
    return getAnnotations(new RangedAnnotationMatcher(range) {
      @Override
      public boolean matchContent(Annotation annotation) {
        return true;
      } 
    });
  }

  @Override
  public List<Annotation> getAnnotations(Range range, final String name) {
    return getAnnotations(new RangedAnnotationMatcher(range) {
      @Override
      public boolean matchContent(Annotation annotation) {
        return annotation.getName().equals(name);
      } 
    });
  }

  @Override
  public List<Annotation> getStyles(Range range) {
    return getAnnotations(new RangedAnnotationMatcher(range) {
      @Override
      public boolean matchContent(Annotation annotation) {
        return annotation.getName().equals("styled-text");
      } 
    });
  }

  @Override
  public List<Annotation> getStyles(Range range, final StyleType style) {
    return getAnnotations(new RangedAnnotationMatcher(range) {
      @Override
      public boolean matchContent(Annotation annotation) {
        return annotation.getName().equals("styled-text") &&
            annotation.getValue().equals(style.toString());
      } 
    });
  }

  @Override
  public void setAnnotation(Range range, String name, String value) {
    if (range == null) {
      events.addOperation(new OperationImpl(OperationType.DOCUMENT_ANNOTATION_SET_NORANGE,
          blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
          0, new Annotation(name, value, range)));
      range = new Range(0, blipData.getContent().length());
    } else {
      events.addOperation(new OperationImpl(OperationType.DOCUMENT_ANNOTATION_SET,
          blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
          0, new Annotation(name, value, range)));
    }
    blipData.getAnnotations().add(new Annotation(name, value, range));
  }

  @Override
  public void setStyle(Range range, StyleType style) {
    setAnnotation(range, "styled-text", style.toString());
  }

  @Override
  public void appendElement(Element element) {
    int length = blipData.getContent().length();
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_ELEMENT_APPEND,
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        length, element));
    
    blipData.addElement(length, element);
    if (element.getType().equals(ElementType.INPUT)) {
      blipData.setContent(blipData.getContent().concat(" "));
      expandOrShiftAnnotations(length - 1, 1);
    } else {
      blipData.setContent(blipData.getContent().concat(" \n"));      
      expandOrShiftAnnotations(length - 1, 2);
    }
  }

  @Override
  public List<Element> getElements() {
    if (blipData != null && blipData.getElements() != null) {
      return new ArrayList<Element>(blipData.getElements().values());
    } else {
      return null;
    }
  }

  @Override
  public List<Element> getElements(Range range) {
    List<Element> elements = new ArrayList<Element>();
    for (Iterator<String> iterator = blipData.getElements().keySet().iterator();
        iterator.hasNext(); ) {
      Integer index = Integer.parseInt(iterator.next());
      if (index >= range.getStart() && index < range.getEnd()) {
        elements.add(blipData.getElements().get(index));
      }
    }
    return elements;
  }

  @Override
  public FormView getFormView() {
    return new FormViewImpl(this);
  }

  private List<Annotation> getAnnotations(AnnotationMatcher matcher) {
    List<Annotation> result = new ArrayList<Annotation>();
    for (Annotation annotation : blipData.getAnnotations()) {
      if (matcher.match(annotation)) {
        result.add(annotation);
      }
    }
    return result;
  }

  /**
   * Helper method to expand all annotations that cover <code>point</code>,
   * and shift the ones that start after.
   * 
   * @param point the starting point of that determines which annotations
   *     should be shifted.
   * @param length the length of the entity that is about to be inserted,
   *     which determines the shift amount.
   */
  private void expandOrShiftAnnotations(int point, int length) {
    for (Annotation annotation : blipData.getAnnotations()) {
      // Shift the starting index of the annotation.
      int start = annotation.getRange().getStart();
      if (start >= point) {
        annotation.getRange().setStart(start + length);
      }

      // Shift the end index of the annotation.
      int end = annotation.getRange().getEnd();
      if (end >= point) {
        annotation.getRange().setEnd(end + length);
      }
    }
  }
  
  /**
   * Helper method to subtract annotations that covers <code>point</code> to
   * <code>length</code> range, and shift the ones that start after.
   * 
   * @param point the starting point of that determines which annotations
   *     should be shifted.
   * @param length the length of the entity that is about to be deleted
   */
  private void shrinkOrShiftAnnotations(int point, int length) {
    ListIterator<Annotation> iterator = blipData.getAnnotations().listIterator();
    while (iterator.hasNext()) {
      Range range = iterator.next().getRange();
      int start = range.getStart();
      int end = range.getEnd();
      
      // There are six cases to consider, since start and end can each be in three different
      // states: before, in, or after the "deleted" range. However, we ignore the case where
      // both start and end are before the "deleted" range. We also assume that start < end.
      if (start < point) {
        if (end >= point + length) {
          // Case 1: Start is before, end is after the deleted range.
          range.setEnd(end - length);
        } else if (end >= point) {
          // Case 2: Start is before, end is in the deleted range.
          range.setEnd(point);
        } else {
          // Case 3: Start and end are before the deleted range. Do nothing.
        }
      } else if (start >= point + length) {
        // Case 4: Start and end are after the deleted range.
        range.setStart(start - length);
        range.setEnd(end - length);
      } else {
        if (end > point + length) {
          // Case 5: Start is inside, end is after the deleted range.
          range.setStart(point);
          range.setEnd(end - length);
        } else {
          // Case 6: Start is inside, end is inside the deleted range. Delete the annotation.
          iterator.remove();
        }
      }
    }
  }

  @Override
  public boolean hasAnnotation(String name) {
    if (blipData != null && blipData.getAnnotations() != null) {
      for (Annotation annotation : blipData.getAnnotations()) {
        if (name.equals(annotation.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public Blip insertInlineBlipAfterFormElement(FormElement formElement) {
    BlipData inlineBlipData = new BlipData();
    inlineBlipData.setWaveId(blipData.getWaveId());
    inlineBlipData.setWaveletId(blipData.getWaveletId());
    inlineBlipData.setBlipId("TBD" + Math.random() + "|" + formElement.getType().toString() + "|"
        + formElement.getName());
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_INLINE_BLIP_INSERT_AFTER_ELEMENT, 
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        0, inlineBlipData));
    return new BlipImpl(inlineBlipData, events);
  }

  @Override
  public void deleteAnnotations(String name) {
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_ANNOTATION_DELETE, 
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        0, name));
    
    ListIterator<Annotation> iterator = blipData.getAnnotations().listIterator();
    while (iterator.hasNext()) {
      Annotation annotation = iterator.next();
      if (annotation.getName().equals(name)) {
        iterator.remove();
      }
    }
  }

  @Override
  public void deleteAnnotations(Range range) {
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_ANNOTATION_DELETE, 
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        0, range));
    
    ListIterator<Annotation> iterator = blipData.getAnnotations().listIterator();
    while (iterator.hasNext()) {
      Annotation annotation = iterator.next();
      if (range.getStart() <= annotation.getRange().getStart() &&
          annotation.getRange().getEnd() <= range.getEnd()) {
        iterator.remove();
      }
    }
  }

  @Override
  public void deleteElement(int index) {
    Element element = getElement(index);
    if (element != null) {
      events.addOperation(new OperationImpl(OperationType.DOCUMENT_ELEMENT_DELETE, 
          blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
          -1, element));
      
    } else {
      events.addOperation(new OperationImpl(OperationType.DOCUMENT_ELEMENT_DELETE, 
          blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
          index, null));
    }
    // adjust annotations and other element positions
    blipData.getElements().remove(Integer.toString(index));
    if (index >= 0 && index < blipData.getContent().length()) {
      // Insert the text to the underlying blip data.
      StringBuilder sb = new StringBuilder(blipData.getContent());
      sb.deleteCharAt(index);
      blipData.setContent(sb.toString());
      shrinkOrShiftAnnotations(index, 1);
      shiftElementsUp(index, 1);
    }
  }

  @Override
  public boolean elementExists(int index) {
    return blipData.getElements().containsKey(index);
  }

  @Override
  public Element getElement(int index) {
    return blipData.getElements().get(Integer.toString(index));
  }

  @Override
  public List<Element> getElements(ElementType type) {
    List<Element> elements = new ArrayList<Element>();
    for (Element element : blipData.getElements().values()) {
      if (element.getType() == type) {
        elements.add(element);
      }
    }
    return elements;
  }

  @Override
  public GadgetView getGadgetView() {
    return new GadgetViewImpl(this);
  }

  @Override
  public int getPosition(Element element) {
    if (!blipData.getElements().containsValue(element)) {
      return -1;
    }
    for (String index : blipData.getElements().keySet()) {
      if (blipData.getElements().get(index).equals(element)) {
        return Integer.parseInt(index);
      }
    }
    return -1;
  }

  @Override
  public void insertElement(int index, Element element) {
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_ELEMENT_INSERT, 
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        index, element));
    this.expandOrShiftAnnotations(index, 1);
    shiftElementsDown(index, 1);
    blipData.getElements().put(Integer.toString(index), element);
    // Insert the text to the underlying blip data.
    StringBuilder sb = new StringBuilder(blipData.getContent());
    sb.insert(index, " ");
    blipData.setContent(sb.toString());
  }

  private void shiftElementsDown(int start, int length) {
    Set<String> keys = new HashSet<String>();
    for (String key : blipData.getElements().keySet()) {
      if (Integer.parseInt(key) >= start) {
        keys.add(key);
      }
    }
    
    for (String keyValue : keys) {
      int key = Integer.parseInt(keyValue);
      Element element = blipData.getElements().get(Integer.toString(key));
      blipData.getElements().remove(Integer.toString(key));
      blipData.getElements().put(Integer.toString(key + length), element);
    }
  }

  private void shiftElementsUp(int start, int length) {
    Set<String> keys = new HashSet<String>();
    for (String key : blipData.getElements().keySet()) {
      if (Integer.parseInt(key) >= start) {
        keys.add(key);
      }
    }
    
    for (String keyValue : keys) {
      int key = Integer.parseInt(keyValue);
      Element element = blipData.getElements().get(Integer.toString(key));
      blipData.getElements().remove(Integer.toString(key));
      blipData.getElements().put(Integer.toString(key - length), element);
    }
  }

  @Override
  public void replaceElement(int index, Element element) {
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_ELEMENT_REPLACE, 
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        index, element));
    blipData.getElements().put(Integer.toString(index), element);
  }

  @Override
  public void setAuthor(String author) {
    events.addOperation(new OperationImpl(OperationType.BLIP_SET_AUTHOR,
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        -1, author));
  }

  @Override
  public String getAuthor() {
    List<Annotation> annotations = getAnnotations("relayedfrom");
    if (!annotations.isEmpty()) {
      return annotations.get(0).getValue();
    }
    return null;
  }

  @Override
  public void setCreationTime(long creationTime) {
    events.addOperation(new OperationImpl(OperationType.BLIP_SET_CREATION_TIME,
        blipData.getWaveId(), blipData.getWaveletId(), blipData.getBlipId(),
        -1, creationTime));
  }
}
