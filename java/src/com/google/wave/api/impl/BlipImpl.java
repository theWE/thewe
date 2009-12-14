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

import com.google.wave.api.Blip;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of a Blip. A Blip is the object representation used to
 * wrap the underlying {@link BlipData} object passed during serialization. It
 * provides navigation through the wavelet/blip hierarchy as well as
 * convenience methods for manipulating a {@link Blip}. The message bundle is
 * used to refer to other wavelet and blip data that was sent with the events.
 * 
 * @author scovitz@google.com (Seth Covitz)
 */
public class BlipImpl implements Blip {
  
  private final RobotMessageBundleImpl events;
  
  private final BlipData blipData;
  
  private final TextView textView;
  
  /**
   * BlipImpl constructor.
   * 
   * @param blipData the underlying blip data for this blip.
   * @param events a reference to other wavelet and blip data.
   */
  public BlipImpl(BlipData blipData, RobotMessageBundleImpl events) {
    this.blipData = blipData;
    this.events = events;
    this.textView = new TextViewImpl(blipData, events);
  }
  
  @Override
  public String getBlipId() {
    return blipData.getBlipId();
  }

  @Override
  public Wavelet getWavelet() {
    if (events.getWavelet().getWaveId().equals(blipData.getWaveId()) &&
        events.getWavelet().getWaveletId().equals(blipData.getWaveletId())) {
      return events.getWavelet();
    } else {
      WaveletData waveletData = new WaveletData();
      waveletData.setWaveId(blipData.getWaveId());
      waveletData.setWaveletId(blipData.getWaveletId());
      return new WaveletImpl(waveletData, events);
    }
  }

  @Override
  public TextView getDocument() {
    return textView;
  }

  private Blip createChildBlip(String blipId) {
    BlipData data = events.getBlipData().get(blipId);
    if (data == null) {
      data = new BlipData();
      data.setBlipId(blipId);
      data.setWaveletId(blipData.getWaveletId());
      data.setWaveId(blipData.getWaveId());
      data.setParentBlipId(blipData.getBlipId());
    }
    return new BlipImpl(data, events);
  }

  @Override
  public List<Blip> getChildren() {
    List<Blip> children = new ArrayList<Blip>();
    for (String blipId : blipData.getChildBlipIds()) {
      children.add(createChildBlip(blipId));
    }
    return children;
  }

  @Override
  public Blip getParent() {
    return new BlipImpl(events.getBlipData().get(blipData.getParentBlipId()), events);
  }

  @Override
  public boolean hasChildren() {
    return !blipData.getChildBlipIds().isEmpty();
  }

  @Override
  public Blip getChild(int index) {
    return createChildBlip(blipData.getChildBlipIds().get(index));
  }

  @Override
  public void delete() {
    events.addOperation(new OperationImpl(OperationType.BLIP_DELETE, blipData.getWaveId(),
        blipData.getWaveletId(), blipData.getBlipId(), -1, null));
    events.getBlipData().remove(blipData.getBlipId());
    events.getBlipData().get(blipData.getParentBlipId()).removeChildBlipId(blipData.getBlipId());
  }

  @Override
  public List<String> getContributors() {
    return blipData.getContributors();
  }

  @Override
  public Blip createChild() {
    BlipData childBlip = new BlipData();
    childBlip.setWaveId(blipData.getWaveId());
    childBlip.setWaveletId(blipData.getWaveletId());
    childBlip.setBlipId("TBD" + Math.random());
    events.addOperation(new OperationImpl(OperationType.BLIP_CREATE_CHILD, blipData.getWaveId(),
        blipData.getWaveletId(), blipData.getBlipId(), -1, childBlip));
    blipData.getChildBlipIds().add(childBlip.getBlipId());
    return new BlipImpl(childBlip, events);
  }

  @Override
  public long getLastModifiedTime() {
    return blipData.getLastModifiedTime();
  }

  @Override
  public long getVersion() {
    return blipData.getVersion();
  }

  @Override
  public String getCreator() {
    return blipData.getCreator();
  }

  @Override
  public boolean isChildAvailable(int index) {
    return events.getBlipData().containsKey(blipData.getChildBlipIds().get(index));
  }

  @Override
  public boolean isDocumentAvailable() {
    return blipData.getContent() != null && !blipData.getContent().isEmpty();
  }

  @Override
  public boolean isParentAvailable() {
    return events.getBlipData().containsKey(blipData.getParentBlipId());
  }

  @Override
  public List<String> getChildBlipIds() {
    return blipData.getChildBlipIds();
  }

  @Override
  public String getParentBlipId() {
    return blipData.getParentBlipId();
  }

  @Override
  public void deleteInlineBlip(Blip child) {
    events.addOperation(new OperationImpl(OperationType.DOCUMENT_INLINE_BLIP_DELETE,
        blipData.getWaveId(),
        blipData.getWaveletId(), blipData.getBlipId(), -1, child.getBlipId()));
    child.delete();
  }
}
