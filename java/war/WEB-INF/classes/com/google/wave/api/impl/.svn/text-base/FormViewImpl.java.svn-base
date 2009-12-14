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

import com.google.wave.api.Element;
import com.google.wave.api.FormElement;
import com.google.wave.api.FormView;
import com.google.wave.api.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link FormView} implementation.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
public class FormViewImpl implements FormView {

  final TextView textView;
  
  public FormViewImpl(TextView textView) {
    this.textView = textView;
  }

  @Override
  public void append(FormElement formElement) {
    textView.appendElement(formElement);
  }

  @Override
  public void delete(String name) {
    FormElement formElement = getFormElement(name);
    if (formElement != null) {
      textView.deleteElement(textView.getPosition(formElement));
    }
  }

  @Override
  public FormElement getFormElement(String name) {
    if (textView.getElements() != null) {
      for (Element element : textView.getElements()) {
        if (element.getProperties().containsKey("name") &&
            name.equals(element.getProperty("name"))) {
          return (FormElement) element;
        }
      }
    }
    return null;
  }

  @Override
  public List<FormElement> getFormElements() {
    List<FormElement> formElements = new ArrayList<FormElement>();
    for (Element element : textView.getElements()) {
      if (element.isFormElement()) {
        formElements.add((FormElement) element);
      }
    }
    return formElements;
  }

  @Override
  public void insertAfter(String name, FormElement formElement) {
    textView.insertElement(textView.getPosition(getFormElement(name)) + 1, formElement);
  }

  @Override
  public void insertBefore(String name, FormElement formElement) {
    textView.insertElement(textView.getPosition(getFormElement(name)), formElement);
  }

  @Override
  public void replace(String name, FormElement formElement) {
    textView.replaceElement(textView.getPosition(getFormElement(name)), formElement);
  }

  @Override
  public void delete(FormElement formElement) {
    textView.deleteElement(textView.getPosition(formElement));
  }

  @Override
  public void insertAfter(FormElement after, FormElement formElement) {
    textView.insertElement(textView.getPosition(after) + 1, formElement);
  }

  @Override
  public void insertBefore(FormElement before, FormElement formElement) {
    textView.insertElement(textView.getPosition(before), formElement);
  }

  @Override
  public void replace(FormElement formElement) {
    textView.replaceElement(-1, formElement);
  }

  @Override
  public void replace(FormElement toReplace, FormElement formElement) {
    textView.replaceElement(textView.getPosition(toReplace), formElement);
  }
}
