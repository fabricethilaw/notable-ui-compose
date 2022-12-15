#!/bin/bash

# Copyright (C) 2022 Notable UI
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

########################################################################
#
# Verifies samples build, lint error free and are formatted correctly.
#
# Example: ./scripts/verify_samples.sh
#
########################################################################

set -xe

./scripts/gradlew_recursive.sh assembleDebug
./scripts/gradlew_recursive.sh lintDebug
if ! ./scripts/gradlew_recursive.sh --init-script buildscripts/init.gradle.kts spotlessCheck ; then
    echo "Formatting error. Try running scripts/format.sh"
fi