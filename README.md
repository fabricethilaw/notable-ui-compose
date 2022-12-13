# Notable UI - Ui Kits for Compose
<img src="readme/samples_montage.gif" alt="Notable UI - More Compose Samples" width="824" />

This repository contains a set of individual Android Studio projects to help you build common ui patterns with Jetpack Compose toolkit in Android. Each sample demonstrates different use cases, complexity levels and APIs.

For more information, please [read the documentation](https://notableui.com)

💻 Requirements
------------
To try out these sample apps, you need to use the lastest version of [Android Studio](https://developer.android.com/studio).
You can clone this repository or import the
project from Android Studio following the steps
[here](https://notableui.com/getstarted.md/#setup).

The branch `compose-latest` is targeting the latest alpha versions of Compose. 

## Formatting

To automatically format all samples: Run `./scripts/format.sh`
To check one sample for errors: Navigate to the sample folder and run `./gradlew --init-script buildscripts/init.gradle.kts spotlessCheck`
To format one sample: Navigate to the sample folder and run `./gradlew --init-script buildscripts/init.gradle.kts spotlessApply`

## Updates

To update dependencies to their new stable versions, run:

```
./scripts/updateDeps.sh
```

To make any other manual updates to dependencies (ie add a new dependency or set an alpha version), update the `/scripts/libs.versions.toml` file with changes, and then run `duplicate_version_config.sh` to propogate the changes to all other samples. You can also update the  `toml-updater-config.gradle` file with changes that need to propogate to each sample. 


## License
```
Copyright 2022 Notable UI

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
