## Description

This repository contains the Android Java source code for a sample browser app that uses the MAM SDKs for endpoint management.  It does not contain the [MAM SDK](https://docs.citrix.com/en-us/mdx-toolkit/mam-sdk-overview.html) libraries themselves, which are downloaded separately.

## Download

The MAM SDK libraries can be downloaded from [MAM SDKs and Toolkit](https://www.citrix.com/downloads/citrix-endpoint-management/product-software/mdx-toolkit.html).

Getting Started
---------------

### Build the sample app
#### Step 1
Open mamsdk.properties file and update the below two properties. *mamSdkVersion* can have the latest version of MAM SDK. Current latest version is 20.10.5.1. *appPackageName* should have a unique package name. Since the APK needs to be published to Managed Google Play, the package name has to be globally unique.

```
mamSdkVersion=20.10.5.1
appPackageName=com.citrix.mvpntestapp
```

#### Step 2
Open keystore.properties file and update the below properties. This file contains APK signing keystore details. *keyStorePath* should have the complete path for the signing keystore. *keystorePassword* should have keystore password. *keyAlias* should have keystore alias. *keyPassword* should have key password.

```
keyStorePath=
keystorePassword=
keyAlias=
keyPassword=
```

#### Step 3
Run gradle build using the below command.
```
./gradlew clean build
```

#### Step 4
After the gradle build completes successfully, the signed release APK and MDX files will be generated under this location <app module>/build/outputs/apk/release

#### Step 5
After generating the APK file and MDX file, you will need to upload the app to the Managed Google Play Store and configure MDX on the CEM server. For details refer [Distribute Apps](https://developer.cloud.com/citrixworkspace/mobile-application-integration/android-native/docs/distribute-apps) link.

## Notes

This sample browser app is designed to make it easy for you to test the Micro VPN functionality of the Android MAM SDK. Note that by default it only allows HTTPS connections, and if you'd like to connect to an HTTP web page then you will need to modify `android:usesCleartextTraffic="false"` in the AndroidManifest.xml file.

## Documentation

For detailed documentation on how to use this sample app, and compile it, see the [MAM SDK for Android Native Overview](https://developer.cloud.com/citrixworkspace/mobile-application-integration/android-native/docs/overview).  Documentation is also included in the MAM SDK download package.

## Questions

For questions and support:

-  [StackOverflow [citrix] [mdm] tags](https://stackoverflow.com/questions/tagged/mdm+citrix)
-  [Citrix Discussion Forum](https://discussions.citrix.com/forum/1797-mobile-app-management-mam/)
-  [Troubleshooting](https://developer.cloud.com/citrixworkspace/mobile-application-integration/android-native/docs/troubleshooting)

## License

[MIT License](./LICENSE)
