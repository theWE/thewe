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
 * The GadgetView is a view of a document that simplifies management of gadgets
 * and gadget state.
 * 
 * All modifications to a document via the GadgetView generate operations. These
 * operations are transmitted back to the Wave Robot Proxy and applied 
 * just as operations generated in a client would be.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public interface GadgetView {

  /**
   * Appends the given gadget to the end of the document.
   * 
   * @param gadget The gadget to be appended.
   */
  public void append(Gadget gadget);
  
  /**
   * Deletes the gadget matching the one specified. If it is not found in the
   * document, then this method has no effect.
   * 
   * @param gadget The gadget to be deleted.
   */
  public void delete(Gadget gadget);
  
  /**
   * Searches the document for a gadget matching the given url. If found, it is
   * deleted, otherwise this method has no effect.
   * 
   * @param url the url of the gadget to be deleted.
   */
  public void delete(String url);
  
  /**
   * Returns the gadget matching the specified url. If no gadget is found, null
   * is returned.
   * 
   * @param url the url of the gadget to be matched.
   * @return the gadget if found, null otherwise.
   */
  public Gadget getGadget(String url);
  
  /**
   * Returns all gadgets found in the document.
   * 
   * @return the list of gadgets.
   */
  public List<Gadget> getGadgets();
  
  /**
   * Inserts a new gadget immediately after the given gadget's position in the
   * document. If the 'after' gadget is not found, this method has no effect.
   * 
   * @param after the gadget after which the new gadget is to be inserted.
   * @param gadget the new gadget to be inserted.
   */
  public void insertAfter(Gadget after, Gadget gadget);
  
  /**
   * Inserts a new gadget immediately after the gadget with the given url. If
   * a gadget with the url is not found, this method has no effect.
   * 
   * @param url the url of the gadget to be found.
   * @param gadget the new gadget to be inserted.
   */
  public void insertAfter(String url, Gadget gadget);
  
  /**
   * Inserts a new gadget immediately before the given gadget's position in the
   * document. If the 'before' gadget is not found, this method has no effect.
   * 
   * @param before the gadget before which the new gadget is to be inserted.
   * @param gadget the new gadget to be inserted.
   */
  public void insertBefore(Gadget before, Gadget gadget);
  
  /**
   * Inserts a new gadget immediately before the gadget with the given url. If
   * a gadget with the url is not found, this method has no effect.
   * 
   * @param url the url of the gadget to be found.
   * @param gadget the new gadget to be inserted.
   */
  public void insertBefore(String url, Gadget gadget);
  
  /**
   * Replaces the contents of the gadget matching the given gadget's url with
   * the contents of the given gadget. If a gadget matching this url is not
   * found, this method has no effect.
   * 
   * @param gadget the gadget to be found and replaced.
   */
  public void replace(Gadget gadget);
  
  /**
   * Replaces the 'toReplace' gadget with the 'replaceWith' gadget. If the
   * 'toReplace' gadget cannot be found, this method has no effect.
   * 
   * @param toReplace the gadget to be replaced.
   * @param replaceWith the replacement gadget.
   */
  public void replace(Gadget toReplace, Gadget replaceWith);
  
  /**
   * Replaces the gadget matching the given url with the given gadget. If the
   * gadget cannot be found, this method has no effect.
   * 
   * @param url the url of the gadget to be replaced.
   * @param gadget the replacement gadget.
   */
  public void replace(String url, Gadget gadget);
}
