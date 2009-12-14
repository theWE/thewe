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

/**
 * Implementation class for an Operation.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
public class OperationImpl implements Operation {

  private String waveId;
  private String waveletId;
  private String blipId;
  private OperationType type;
  private int index;
  private Object property;
  
  public OperationImpl(OperationType type, String waveId, String waveletId, String blipId,
      int index, Object property) {
    this.type = type;
    this.waveId = waveId;
    this.waveletId = waveletId;
    this.blipId = blipId;
    this.index = index;
    this.property = property;
  }
    
  /* (non-Javadoc)
   * @see com.google.wave.api.impl.Operation#getBlipId()
   */
  public String getBlipId() {
    return blipId;
  }

  /* (non-Javadoc)
   * @see com.google.wave.api.impl.Operation#getType()
   */
  public OperationType getType() {
    return type;
  }

  /* (non-Javadoc)
   * @see com.google.wave.api.impl.Operation#getWaveId()
   */
  public String getWaveId() {
    return waveId;
  }

  /* (non-Javadoc)
   * @see com.google.wave.api.impl.Operation#getWaveletId()
   */
  public String getWaveletId() {
    return waveletId;
  }

  /* (non-Javadoc)
   * @see com.google.wave.api.impl.Operation#setBlipId(java.lang.String)
   */
  public void setBlipId(String blipId) {
    this.blipId = blipId;
  }

  /* (non-Javadoc)
   * @see com.google.wave.api.impl.Operation#setType(com.google.wave.api.OperationType)
   */
  public void setType(OperationType type) {
    this.type = type;
  }

  /* (non-Javadoc)
   * @see com.google.wave.api.impl.Operation#setWaveId(java.lang.String)
   */
  public void setWaveId(String waveId) {
    this.waveId = waveId;
  }

  /* (non-Javadoc)
   * @see com.google.wave.api.impl.Operation#setWaveletId(java.lang.String)
   */
  public void setWaveletId(String waveletId) {
    this.waveletId = waveletId;
  }

  @Override
  public int getIndex() {
    return index;
  }

  @Override
  public Object getProperty() {
    return property;
  }

  @Override
  public void setIndex(int index) {
    this.index = index;
  }

  @Override
  public void setProperty(Object property) {
    this.property = property;
  }
}
