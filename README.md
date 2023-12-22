# Xlightweight-Store-Android

[![CI Status](https://img.shields.io/travis/SnowGirls/Xlightweight-Store-Android.svg?style=flat)](https://travis-ci.org/SnowGirls/Xlightweight-Store-Android)
[![Version](https://img.shields.io/cocoapods/v/Xlightweight-Store-Android.svg?style=flat)](https://cocoapods.org/pods/Xlightweight-Store-Android)
[![License](https://img.shields.io/cocoapods/l/Xlightweight-Store-Android.svg?style=flat)](https://cocoapods.org/pods/Xlightweight-Store-Android)
[![Platform](https://img.shields.io/cocoapods/p/Xlightweight-Store-Android.svg?style=flat)](https://cocoapods.org/pods/Xlightweight-Store-Android)

## Publish to jitpack.io
create a [jitpack.yml](jitpack.yml) specified a jdk version, push the tag is ok enough, no need to create a new release on github.com.
the go to [jitpack.io](https://jitpack.io) and click `Look up` button to gererate the `aar`

    ````
    env JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home/  ./gradlew task --all
    env JAVA_HOME=$HOME/Library/Java/JavaVirtualMachines/openjdk-18.0.2/Contents/Home  ./gradlew task --all
    ````

    OTAG=v0.0.1.beta.1
    NTAG=v0.0.1.beta.2
    git add . && git commit --amend && git push origin master --force && git tag --delete $OTAG && git push origin --delete $OTAG && git tag $NTAG && git push origin --tags

## Publish to local .m2

    publish to local ~/.m2/repository
    env JAVA_HOME=/Applications/Android\ Studio.app/Contents/jbr/Contents/Home/ ./gradlew :module:publishDebugPublicationToMavenLocal

## Simple to use

Just use the `TslStoreProxy` class as the API.

If you want to change the implementation for store, i.e you want to use Files to save Key-Value data instead of `SharedPreferences`, just change the property `myProxy` of your `TslStoreProxy` instance.

[SharedPreferencesImpl.java](https://cs.android.com/android/platform/superproject/main/+/main:frameworks/base/core/java/android/app/SharedPreferencesImpl.java;bpv=0;bpt=1)

## Author

SnowGirls, june@Tesla.com

## License

Xlightweight-Store-Android is available under the MIT license. See the LICENSE file for more info.
