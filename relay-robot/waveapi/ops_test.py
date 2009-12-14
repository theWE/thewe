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

"""Unit tests for the ops module."""


import unittest

import ops


class TestOperation(unittest.TestCase):
  """Test case for Operation class."""

  def testFields(self):
    op = ops.Operation(ops.DOCUMENT_INSERT, 'opid02',
                       {'waveId': 'wavelet-id',
                        'blipId': 'blip-id'})
    self.assertEquals(ops.DOCUMENT_INSERT, op.method)
    self.assertEquals('opid02', op.id)
    self.assertEquals(2, len(op.params))


if __name__ == '__main__':
  unittest.main()
