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
import com.google.wave.api.Gadget;
import com.google.wave.api.GadgetView;
import com.google.wave.api.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * GadgetView implementation.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
public class GadgetViewImpl implements GadgetView {

  private final TextView textView;
  
  public GadgetViewImpl(TextView textView) {
    this.textView = textView;
  }

  @Override
  public void append(Gadget gadget) {
    textView.appendElement(gadget);
  }

  @Override
  public void delete(Gadget gadget) {
    textView.deleteElement(
        textView.getPosition(gadget));
  }

  @Override
  public void delete(String url) {
    Gadget gadget = getGadget(url);
    if (gadget != null) {
      delete(gadget);
    }
  }

  @Override
  public Gadget getGadget(String url) {
    for (Element element : textView.getElements()) {
      if (element.isGadget() &&
          element.getProperty("url") != null &&
          element.getProperty("url").equals(url)) {
        return (Gadget) element;
      }
    }
    return null;
  }

  @Override
  public List<Gadget> getGadgets() {
    List<Gadget> gadgets = new ArrayList<Gadget>();
    for (Element element : textView.getElements()) {
      if (element.isGadget()) {
        gadgets.add((Gadget) element);
      }
    }
    return gadgets;
  }

  @Override
  public void insertAfter(Gadget after, Gadget gadget) {
    textView.insertElement(textView.getPosition(after) + 1, gadget);
  }

  @Override
  public void insertAfter(String url, Gadget gadget) {
    Gadget after = getGadget(url);
    if (after != null) {
      insertAfter(after, gadget);
    }
  }

  @Override
  public void insertBefore(Gadget before, Gadget gadget) {
    textView.insertElement(textView.getPosition(before), gadget);
  }

  @Override
  public void insertBefore(String url, Gadget gadget) {
    textView.insertElement(textView.getPosition(getGadget(url)), gadget);
  }

  @Override
  public void replace(Gadget gadget) {
    textView.replaceElement(textView.getPosition(getGadget(gadget.getUrl())), gadget);
  }

  @Override
  public void replace(Gadget toReplace, Gadget replaceWith) {
    textView.replaceElement(textView.getPosition(toReplace), replaceWith);
  }

  @Override
  public void replace(String url, Gadget gadget) {
    textView.replaceElement(textView.getPosition(getGadget(url)), gadget);
  }
}
