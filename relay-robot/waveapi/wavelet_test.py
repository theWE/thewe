#!/usr/bin/python2.4
#
# Copyright (C) 2009 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Unit tests for the model module."""



import unittest

import blip
import ops
import wavelet

TEST_WAVELET_DATA = {
    'creator': 'creator@google.com',
    'creationTime': 100,
    'lastModifiedTime': 101,
    'participants': ['robot@google.com'],
    'rootBlipId': 'blip-1',
    'title': 'Title',
    'waveId': 'test.com!w+g3h3im',
    'waveletId': 'test.com!root+conv',
}

TEST_BLIP_DATA = {
    'blipId': TEST_WAVELET_DATA['rootBlipId'],
    'childBlipIds': [],
    'content': '<p>testing</p>',
    'contributors': [TEST_WAVELET_DATA['creator'], 'robot@google.com'],
    'creator': TEST_WAVELET_DATA['creator'],
    'lastModifiedTime': TEST_WAVELET_DATA['lastModifiedTime'],
    'parentBlipId': None,
    'waveId': TEST_WAVELET_DATA['waveId'],
    'elements': {},
    'waveletId': TEST_WAVELET_DATA['waveletId'],
}


class TestWavelet(unittest.TestCase):
  """Tests the wavelet class."""

  def setUp(self):
    self.operation_queue = ops.OperationQueue()
    self.all_blips = {}
    self.blip = blip.Blip(TEST_BLIP_DATA,
                          self.all_blips,
                          self.operation_queue)
    self.all_blips[self.blip.blip_id] = self.blip
    self.wavelet = wavelet.Wavelet(TEST_WAVELET_DATA,
                                   self.all_blips,
                                   None,
                                   self.operation_queue)

  def testWaveletProperties(self):
    w = self.wavelet
    self.assertEquals(TEST_WAVELET_DATA['creator'], w.creator)
    self.assertEquals(TEST_WAVELET_DATA['creationTime'], w.creation_time)
    self.assertEquals(TEST_WAVELET_DATA['lastModifiedTime'],
                      w.last_modified_time)
    self.assertEquals(len(TEST_WAVELET_DATA['participants']),
                      len(w.participants))
    self.assertTrue(TEST_WAVELET_DATA['participants'][0] in w.participants)
    self.assertEquals(TEST_WAVELET_DATA['rootBlipId'], w.root_blip.blip_id)
    self.assertEquals(TEST_WAVELET_DATA['title'], w.title)
    self.assertEquals(TEST_WAVELET_DATA['waveId'], w.wave_id)
    self.assertEquals(TEST_WAVELET_DATA['waveletId'], w.wavelet_id)
    self.assertEquals('test.com', w.domain)

  def testWaveletMethods(self):
    w = self.wavelet
    blip = w.reply()
    self.assertEquals(2, len(self.wavelet.blips))
    w.delete(blip)
    self.assertEquals(1, len(self.wavelet.blips))
    self.assertEquals(0, len(self.wavelet.data_documents))
    self.wavelet.data_documents['key'] = 'value'
    self.assertEquals(1, len(self.wavelet.data_documents))
    self.wavelet.data_documents['key'] = None
    self.assertEquals(0, len(self.wavelet.data_documents))

if __name__ == '__main__':
  unittest.main()
