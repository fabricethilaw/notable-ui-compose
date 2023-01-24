# Notable UI

![Notable-ui](https://user-images.githubusercontent.com/13453007/214191039-a9531b83-4eb8-4540-97d2-11496ec70635.svg)

Android UI boilerplate designed to accelerate the UI development and launch of your projects.

For more information, please [visit official website](https://notableui.com)

💻 Requirements
------------
To try out these components and demos, you need to use the latest version of [Android Studio](https://developer.android.com/studio).
Clone the repository, the Android project is in *components* folder.

* The components are implemented in `notable-ui` module.
* Samples and demos are in `app` module.

💄 Components
------------

* **ResponsiveText** : extends the Compose built-in Text component by adding a new property `textScale: TextScale`.
This property allows for uniform scaling of text size and fine-tuning of the font auto-sizing feature. ([Source folder](https://github.com/fabricethilaw/notable-ui-compose/tree/develop/components/notable-ui/src/main/java/com/notable/ui/text))

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
