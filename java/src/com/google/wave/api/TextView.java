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

import java.util.List;

/**
 * The TextView is a view of a document that simplifies management of the text
 * content that it contains. It is also a basis for accessing other embedded
 * types such as gadgets and forms.
 * 
 * All modifications to a document via the TextView generate operations. These
 * operations are transmitted back to the Wave Robot Proxy and applied just as
 * operations generated in a client would be.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public interface TextView {
  
  /**
   * Appends text to the end of document. Any annotations that extend to the end
   * of the document will not be extended to include this text.
   * 
   * @param text the text to be appended.
   */
  public void append(String text);
  
  /**
   * Appends text containing XHTML markup to the end of the document. Any
   * white-listed and supported HTML tags will be converted into the proper
   * annotations and conform to the wave XML schema. Anything unsupported
   * will be ignored. For example, <b><f>foo</f></b> will result in "foo" to be
   * set as bold but the "f" tag to be ignored.
   * 
   * NB: The local state annotations and elements will not be reflected locally
   * and only text is set. The state will be correct upon the next incoming
   * event bundle. 
   *
   * @param content Text string containing markup and text.
   */
  public void appendMarkup(String content);
  
  /**
   * Deletes all text and annotations for this document.
   * 
   * Deleting text will also delete any entities that have been inserted
   * between characters in the text. 
   */
  public void delete();
  
  /**
   * Deletes the text bounded by the specified range.
   * 
   * All annotations and entities that are wholly contained within this range
   * will also be deleted. Annotations that overlap this range will be truncated
   * accordingly.
   * 
   * @param range the range over the document affected by this operation.
   */
  public void delete(Range range);

  /**
   * Returns the text content for this document.
   * 
   * @return the text content for this document.
   */
  public String getText();
  
  /**
   * Returns the text content bounded by the specified range.
   * 
   * @param range The range over the document affected by this operation.
   * @return the text content.
   */
  public String getText(Range range);
  
  /**
   * Inserts text at the given starting position within the document.
   * 
   * If the text is inserted into a range that is surrounded completely by an
   * annotation, the annotation will be extended to include the inserted text.
   * 
   * @param start The starting index in the text document.
   * @param text The text to be inserted.
   */
  public void insert(int start, String text);
  
  /**
   * Replaces the text content bounded by the specified range with the given
   * text. This is equivalent to the two operations, delete(Range) and 
   * insert(start) and affects existing annotations accordingly to these
   * operations. 
   * 
   * @param range The range over the document affected by this operation.
   * @param text The new text that will replace the previous text. 
   */
  public void replace(Range range, String text);
  
  /**
   * Replaces all of the text and annotations in the document with the given
   * text. This is equivalent to the two operations, deleteText() and
   * appendText() and affects existing annotations and entities accordingly.
   * 
   * @param text The new text to be added to the document.
   */
  public void replace(String text);
  
  /**
   * Appends the given styled text as text and annotations to the end of the
   * document.
   * 
   * @param styledText the styled text to be appended.
   */
  public void appendStyledText(StyledText styledText);
  
  /**
   * Inserts the styled text as text and annotations at the given position
   * within the text document. Existing annotations that surround the insertion
   * point will be extended to include the new text.
   * 
   * @param start the position within the document to insert the new text.
   * @param styledText the styled text to be inserted.
   */
  public void insertStyledText(int start, StyledText styledText);

  /**
   * Replaces the text bounded by the specified range with the styled text as
   * text and annotations. This is the equivalent to the two operations,
   * deleteText(Range) and insertStyledText(start) and affects existing
   * annotations and entities accordingly.
   * 
   * @param range The range over the document which is affected by this
   *     operation.
   * @param styledText The text to be inserted into the document.
   */
  public void replaceStyledText(Range range, StyledText styledText);
  
  /**
   * Replaces all of the text in the document with the given styled text as
   * text and annotations. This is equivalent to the two operations,
   * deleteText() and appendStyledText() and affects existing annotations and
   * entities accordingly.
   * 
   * @param styledText The text to become the content for the document.
   */
  public void replaceStyledText(StyledText styledText);  

  /**
   * Returns the list of annotations affecting the text document.
   * 
   * @return the list of annotations.
   */
  public List<Annotation> getAnnotations();
  
  /**
   * Returns the list of annotations that overlap the given range.
   * 
   * @param range The range over the document to query for annotations.
   * @return the list of annotations.
   */
  public List<Annotation> getAnnotations(Range range);
  
  /**
   * Returns the list of all annotations affecting the text document that match
   * the given name.
   * 
   * @param name The annotation name to match.
   * @return The list of matching annotations.
   */
  public List<Annotation> getAnnotations(String name);

  /**
   * Returns the list of all annotations overlapping the specified range in the
   * text document that match the given name.
   * 
   * @param range The range over the document to query for annotations.
   * @param name The annotation name to match.
   * @return The list of matching annotations.
   */
  public List<Annotation> getAnnotations(Range range, String name);

  /**
   * Checks for the existence of a named annotation affecting the text
   * document.
   * 
   * @param name name of the annotation to search.
   * @return true if the annotation exists, false otherwise.
   */
  public boolean hasAnnotation(String name);

  /**
   * Sets an annotation affecting the entire document.
   * 
   * @param name The name of the annotation.
   * @param value The value of the annotation.
   */
  public void setAnnotation(String name, String value);

  /**
   * Sets an annotation affecting the specified range of the document.
   * 
   * @param range The range over the document affected by this operation.
   * @param name The name of the annotation.
   * @param value The value of the annotation.
   */
  public void setAnnotation(Range range, String name, String value);

  /**
   * Deletes all annotations that match the given name across the entire
   * document.
   * 
   * @param name the name of the annotations to delete.
   */
  public void deleteAnnotations(String name);
  
  /**
   * Deletes all annotations from the given range. Any annotation wholly
   * contained within this range will be deleted. Annotations that overlap
   * this range will be truncated at the edges of the range.
   * 
   * @param range the range over which all annotations should be deleted.
   */
  public void deleteAnnotations(Range range);

  /**
   * Returns the list of style annotations affecting the text document.
   * 
   * @return The list of style annotations affecting the text document.
   */
  public List<Annotation> getStyles();
  
  /**
   * Returns the list of style annotations affecting the specified range of the
   * document.
   * 
   * @param range The range over the document with which to query.
   * @return The list of style annotations.
   */
  public List<Annotation> getStyles(Range range);
  
  /**
   * Returns the list of style annotations affecting the text document that
   * match the specified StyleType.
   * 
   * @param style The type of style to match against.
   * @return The list of annotations.
   */
  public List<Annotation> getStyles(StyleType style);
  
  /**
   * Returns the list of style annotations affecting the specified range of the
   * document and matching the given StyleType. 
   * 
   * @param range The range over the document with which to query.
   * @param style The type of style to match against.
   * @return The list of annotations.
   */
  public List<Annotation> getStyles(Range range, StyleType style);
  
  /**
   * Applies the given style to the text document. 
   * 
   * @param style The StyleType to be applied.
   */
  public void setStyle(StyleType style);

  /**
   * Applies the given style over the specified range of the text document.
   * 
   * @param range The range over the document with which this operation affects.
   * @param style The StyleType to be applied.
   */
  public void setStyle(Range range, StyleType style);
  
  /**
   * Returns the list of non-text elements that exist within the document.
   * 
   * @return the list of elements.
   */
  public List<Element> getElements();
  
  /**
   * Returns the list of non-text elements that exist within the specified
   * range of the document.
   * 
   * @param range the range over which to search for elements.
   * @return the list of elements.
   */
  public List<Element> getElements(Range range);
  
  /**
   * Returns the list of non-text elements that match the type specified.
   * 
   * @param type the type of elements to select from the document.
   * @return the list of elements.
   */
  public List<Element> getElements(ElementType type);
  
  /**
   * Appends an element to the end of the document.
   * 
   * @param element The element to be appended.
   */
  public void appendElement(Element element);
  
  /**
   * Deletes the element at the specified position. If the specified position
   * does not match an element, this method has no effect.
   * 
   * @param index the position of the element to delete.
   */
  public void deleteElement(int index);
  
  /**
   * Checks whether an element exists at the specified position.
   * 
   * @param index the position to check for an element.
   * @return true if an element exists at this position, false otherwise.
   */
  public boolean elementExists(int index);
  
  /**
   * Returns the element at the specified position. If the specified position
   * does not match an element, null is returned.
   * 
   * @param index the position of the element to retrieve.
   * @return the requested element or null if no element exists at the
   *     requested position.
   */
  public Element getElement(int index);
  
  /**
   * Returns the position of given element.
   * 
   * @param element the element to query the position.
   * @return the position of the element or -1 if the element was not found
   *     within the document.
   */
  public int getPosition(Element element);
  
  /**
   * Inserts the given element at position of specified.
   * 
   * @param index the position to insert the element.
   * @param element the element to be inserted.
   */
  public void insertElement(int index, Element element);

  /**
   * Replaces the element at specified position with the given element. If
   * there is no element currently at the given position, this method has no
   * effect.
   * 
   * @param index the position in which to replace an existing element.
   * @param element the element to replace the old one.
   */
  public void replaceElement(int index, Element element);

  /**
   * Returns the FormView associated with the form elements in this document.
   * 
   * @return the FormView.
   */
  public FormView getFormView();

  /**
   * returns the GadgetView associated with the gadgets in this document.
   * 
   * @return the GadgetView.
   */
  public GadgetView getGadgetView();
  
  /**
   * Appends an inline blip within the document at the end of the existing text.
   * 
   * @return The newly appended blip.
   */
  public Blip appendInlineBlip();
  
  /**
   * Deletes the inline blip specified by the blip parameter.
   * 
   * @param blip The blip to be deleted.
   */
  public void deleteInlineBlip(Blip blip);
  
  /**
   * Returns all inline Blips embedded within the current document.
   * 
   * @return The list of inline blips.
   */
  public List<Blip> getInlineBlips();

  /**
   * Inserts an inline blip at the given position.
   * 
   * @param start The starting index within the text document to insert the
   *     blip;
   * @return The newly inserted blip.
   */
  public Blip insertInlineBlip(int start);

  /**
   * Inserts an inline blip immediately after an existing form element.
   * 
   * @param formElement The form element after which to insert the inline blip.
   * @return the newly inserted blip.
   */
  public Blip insertInlineBlipAfterFormElement(FormElement formElement);
  
  /**
   * Sets the author of the content that will be rendered as the contributor
   * of the blip.
   * 
   * @param author The author of the content.
   */
  public void setAuthor(String author);

  /**
   * Returns the author of the content.
   * 
   * @return The author of the content.
   */
  public String getAuthor();
  
  /**
   * Sets the content creation time that will be rendered on the blip.
   * 
   * @param creationTime The creation time of the content, since epoch.
   */
  public void setCreationTime(long creationTime);
}
