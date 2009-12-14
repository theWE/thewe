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
 * Represents an image within a Wave. The image can either refer to an external
 * resource or a Wave attachment. An external image is defined by the 'url' 
 * property, while the Wave attachment is defined by the 'attachmentId'
 * property.
 * 
 * @author scovitz@google.com (Seth Covitz)
 * @author mprasetya@google.com (Marcel Prasetya)
 */
public class Image extends Element {

  /**
   * Constructs an empty image.
   */
  public Image() {
    super(ElementType.IMAGE);
  }
  
  /**
   * Constructs a Wave image given an attachment id and a caption.
   * 
   * @param attachmentId the attachment id of the wave image.
   * @param caption the captopm for the image.
   */
  public Image(String attachmentId, String caption) {
    this();
    setAttachmentId(attachmentId);
    setCaption(caption);
  }
  
  /**
   * Constructs an external image given a url, image dimensions, and a caption.
   * 
   * @param url the url for the external image.
   * @param width the width of the image.
   * @param height the height of the image.
   * @param caption the caption for the image.
   */
  public Image(String url, int width, int height, String caption) {
    this();
    setUrl(url);
    setWidth(width);
    setHeight(height);
    setCaption(caption);
  }
  
  /**
   * Returns the URL for the image.
   * 
   * @return the URL for the image.
   */
  public String getUrl() {
    return (String) getProperty("url");
  }
  
  /**
   * Changes the URL for the image to the given url. This will cause the new
   * image to be initialized and loaded.
   * 
   * @param url the new image url.
   */
  public void setUrl(String url) {
    setProperty("url", url);
  }
  
  /**
   * Sets the fixed width of the image to be displayed.
   * 
   * @param width the fixed width of the image.
   */
  public void setWidth(int width) {
    setProperty("width", Integer.toString(width));
  }
  
  /**
   * Returns the fixed width of the image.
   * 
   * @return the fixed width of the image or -1 if no width was specified.
   */
  public int getWidth() {
    return getProperty("width") == null ? -1 : Integer.parseInt((String) getProperty("width"));
  }
  
  /**
   * Sets the fixed height of the image to be displayed.
   * 
   * @param height the fixed height of the image. 
   */
  public void setHeight(int height) {
    setProperty("height", Integer.toString(height));
  }
  
  /**
   * Returns the fixed height of the image.
   * 
   * @return the fixed height of the image or -1 if no height was specified.
   */
  public int getHeight() {
    return getProperty("height") == null ? -1 : Integer.parseInt((String) getProperty("height"));
  }
  
  /**
   * Sets the attacmentId for the Wave image.
   * 
   * @param attachmentId the attachment id for the image.
   */
  public void setAttachmentId(String attachmentId) {
    setProperty("attachmentId", attachmentId);
  }
  
  /**
   * Returns the attachmentId for the image.
   * 
   * @return the attachmentId for the image.
   */
  public String getAttachmentId() {
    return (String) getProperty("attachmentId"); 
  }
  
  /**
   * Sets the caption for the image.
   * 
   * @param caption the caption to display for the image.
   */
  public void setCaption(String caption) {
    setProperty("caption", caption);
  }
  
  /**
   * Returns the caption for the image.
   * 
   * @return the caption for the image.
   */
  public String getCaption() {
    return (String) getProperty("caption");
  }
}
