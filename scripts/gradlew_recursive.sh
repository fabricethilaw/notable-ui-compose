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
# Allows running of ./gradlew commands across all projects in
# folders of the current directory.
#
# Example: To run build over all projects run:
#     ./scripts/gradlew_recursive.sh build
#
########################################################################

set -xe

# Crawl all gradlew files which indicate an Android project
# You may edit this if your repo has a different project structure
for GRADLEW in `find . -name "gradlew"` ; do
    SAMPLE=$(dirname "${GRADLEW}")
    bash "$GRADLEW" -p "$SAMPLE" --stacktrace $@
done