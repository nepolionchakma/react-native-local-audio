### 📖 README.md

````markdown
# react-native-local-audio

A lightweight React Native native module to fetch **local audio files** (with metadata like title, artist, album, and cover image) directly from the device storage.

✅ Works on **Android 6+**
⚠️ iOS support not yet implemented (coming soon).

---

## ✨ Features

- Fetch all audio files from device storage
- Optional parameters:
  - `sortBy` → `TITLE | ARTIST | ALBUM | DATE_ADDED`
  - `orderBy` → `ASC | DESC`
  - `limit` → number of items
  - `offset` → pagination offset
  - `coverQuality` → album art quality (0–100)
- Fetch songs by album
- Search songs by title
- Returns metadata: `id`, `path`, `title`, `artist`, `album`, `cover`

---

## 📦 Installation

```sh
# install via npm
npm install react-native-local-audio

# or via yarn
yarn add react-native-local-audio
```
````

```tsx
// add in the android/settings.gradle
include ':react-native-local-audio'
project(':react-native-local-audio').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-local-audio/android')

// dependencies add in the android/app/build.gradle
implementation project(':react-native-local-audio')
```

---

## 🔗 Linking

If you’re using React Native **0.60+**, autolinking works automatically.
For React Native **<0.60**, manually link:

```sh
react-native link react-native-local-audio
```

---

## ⚙️ Android Setup

1. Open `android/build.gradle` and make sure `minSdkVersion` is **23 or higher**:

```gradle
ext {
    minSdkVersion = 23
    targetSdkVersion = 34
    compileSdkVersion = 34
}
```

2. Add storage permissions in your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="29"/>
```

3. For Android 11+ (API 30+), add **scoped storage** permission:

```xml
<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" tools:ignore="ScopedStorage"/>
```

⚠️ You must request permissions at runtime using `PermissionsAndroid`.

---

## 🚀 Usage

```tsx
import AudioModule from "react-native-local-audio";

// Example: get all audio
const songs = await AudioModule.getAllAudio({
  sortBy: "TITLE",
  orderBy: "ASC",
  limit: 20,
  offset: 0,
  coverQuality: 50,
});
console.log(songs);

// Example: get songs by album
const albums = await AudioModule.getSongsByAlbum();
console.log(albums);

// Example: search songs by title
const searchResult = await AudioModule.searchSongsByTitle({ title: "ale" });
console.log(searchResult);
```

---

## 🛠 Development (for contributors)

Clone the repo and install dependencies:

```sh
git clone https://github.com/nepolionchakma/react-native-local-audio.git
cd react-native-local-audio
yarn install
```

Build TypeScript:

```sh
yarn build
```

Run example project:

```sh
cd example
yarn install
yarn android
```

---

## 📤 Publishing to NPM

1. Build the package:

```sh
yarn build
```

2. Login to npm:

```sh
npm login
```

3. Publish:

```sh
npm publish --access public
```

4. Install in any app:

```sh
yarn add react-native-local-audio
# or
npm install react-native-local-audio
```

---

## 📌 Roadmap

- [x] Android support
- [ ] iOS support
- [ ] Playlist management
- [ ] Audio player integration

---

## 📄 License

MIT © 2025
[License](https://github.com/nepolionchakma/react-native-local-audio/blob/main/LICENSE)
