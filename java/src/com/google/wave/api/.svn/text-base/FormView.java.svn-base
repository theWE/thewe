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
 * The FormView is a view of a document that simplifies management of form
 * elements.
 * 
 * All modifications to a document via the FormView generate operations. These
 * operations are transmitted back to the Wave Robot Proxy and applied 
 * just as operations generated in a client would be.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public interface FormView {

  /**
   * Appends a form element to the end of the document.
   * 
   * @param formElement The form element to be appended.
   */
  void append(FormElement formElement);

  /**
   * Deletes a form element.
   * 
   * @param formElement The form element to be deleted.
   */
  void delete(FormElement formElement);
  
  /**
   * Deletes a form element by name.
   * 
   * @param name The name of the form element to delete.
   */
  void delete(String name);
  
  /**
   * Returns a form element by name.
   * 
   * @param name the name of the form element to be returned.
   * @return the requested FormElement or null if an element with the given
   *     name cannot be found.
   */
  FormElement getFormElement(String name);

  /**
   * Returns the list of form elements contained within this document.
   * 
   * @return the list of form elements.
   */
  List<FormElement> getFormElements();

  /**
   * Insert a new form element after the given form element. This will insert
   * the new form element into the text document immediately after the given
   * form element. If the form element cannot be found, this method has no
   * effect.
   * 
   * @param after the form element after which to insert the new element.
   * @param formElement the new form element to be inserted.
   */
  void insertAfter(FormElement after, FormElement formElement);

  /**
   * Insert a new form element after the named form element. This will insert
   * the new form element in the list and into the text document immediately
   * after the form element. If the named element cannot be found, this method
   * has no effect.
   * 
   * @param name the named form element after which to insert the new element.
   * @param formElement the form element to be inserted.
   */
  void insertAfter(String name, FormElement formElement);

  /**
   * Insert a new form element before the given form element. This will insert
   * the new form element into the text document immediately before the form
   * element. If the specified element cannot be found, this method has no
   * effect.
   * 
   * @param before the form element before which to insert the new element.
   * @param formElement the form element to be inserted.
   */
  void insertBefore(FormElement before, FormElement formElement);

  /**
   * Insert a new form element before the named form element. This will insert
   * the new form element in the list and into the text document immediately
   * before the form element. If the named element cannot be found, this method
   * has no effect.
   * 
   * @param name the named form element before which to insert the new element.
   * @param formElement the form element to be inserted.
   */
  void insertBefore(String name, FormElement formElement);
  
  /**
   * Searches for the form element matching the given form element's name. If
   * found, the contents of the form element are replaced with the contents of
   * the new form element.
   * 
   * @param formElement the form element to be replaced.
   */

  void replace(FormElement formElement);
  /**
   * Replaces the specified form element with the given form element. This will
   * replace the element both in the text document (at the same position). If
   * the specified form element cannot be found, this method has no effect.
   * 
   * @param toReplace the form element which is being replaced.
   * @param formElement the new form element that is replacing it.
   */
  void replace(FormElement toReplace, FormElement formElement);
  
  /**
   * Replaces the named form element with the given form element. This will
   * replace the element both in the text document (at the same position). If
   * the named form element cannot be found, this method has no effect.
   * 
   * @param name the named form element which is being replaced.
   * @param formElement the new form element that is being replaced.
   */
  void replace(String name, FormElement formElement);
}
