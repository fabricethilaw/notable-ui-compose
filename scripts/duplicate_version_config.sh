#!/bin/bash

# Copyright (C) 2022 Notable UI for Compose
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
# Duplicates libs.versions.toml into each project from master copy.
#
# Example: To run build over all projects run:
#     ./scripts/duplicate_version_config.sh
#
########################################################################

set -xe

# cp scripts/libs.versions.toml ui-templates/avatar-compose/gradle/libs.versions.toml

# cp scripts/toml-updater-config.gradle ui-templates/avatar-compose/buildscripts/toml-updater-config.gradle

# cp scripts/init.gradle.kts ui-templates/avatar-compose/buildscripts/init.gradle.kts