# Notable UI
<img src="readme/samples_montage.gif" alt="Notable UI - UI boilerplate for Android with Jetpack Compose" width="824" />

Android boilerplate designed to accelerate the development and launch of your projects.

For more information, please [read the documentation](https://notableui.com)

💻 Requirements
------------
To try out these components and demos, you need to use the latest version of [Android Studio](https://developer.android.com/studio).
Clone the repository, the Android project is in *components* folder.

* The components are implemented in `notable-ui` module.
* Samples and demos are in `app` module.

## Updates

To update dependencies to their new stable versions, run the following at the root folder of the repository:

```
./scripts/updateDeps.sh
```

To make any other manual updates to dependencies (ie add a new dependency or set an alpha version), update the `/scripts/libs.versions.toml` file with changes, and then run `duplicate_version_config.sh` to propogate the changes to all modules.

## License
```
Copyright 2022 Fabrice Thilaw KIKI

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
