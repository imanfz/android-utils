# utils

[![](https://jitpack.io/v/imanfz/utils.svg)](https://jitpack.io/#imanfz/utils)

## Feature
- View Extension
- Context Extension
- String Extension
- Date Extension
- Image Extension
- EditText Extension
- RecyclerView Extension
- Date Utils
- Device Utils
- Network Utils
- Validation From Utils

## How to use

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```
and:

```gradle
dependencies {
    implementation 'com.github.imanfz:utils:{latest version}'
}
```

## Adding the maven plugin

To enable installing into local maven repository and JitPack you need to add the [android-maven](https://github.com/dcendents/android-maven-gradle-plugin) plugin:

1. Add `classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1'` to root build.gradle under `buildscript { dependencies {`
2. Add `com.github.dcendents.android-maven` to the library/build.gradle

After these changes you should be able to run:

    ./gradlew install
    
from the root of your project. If install works and you have added a GitHub release it should work on jitpack.io
