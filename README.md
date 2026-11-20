# FMM27 Scout — Android project

This project wraps the supplied standalone FMM27 Scout HTML app in a lightweight Android WebView.
The HTML/JavaScript player-save parser and scouting UI are kept intact. The download routine is adapted to save exported CSV and edited-save ZIP files into `Downloads/FMM27 Scout` on Android. The `.fms` import uses Android's system document picker.

## Build an APK without a computer (GitHub Actions)

A workflow is included at `.github/workflows/build-apk.yml`. It builds the debug APK in GitHub's cloud and attaches it to the workflow run.

1. Create a GitHub account if you do not already have one.
2. Create a **new public or private repository** on GitHub. A name such as `FMM27-Scout-Android` is fine. Do not add a README, license, or `.gitignore` when creating it.
3. Upload the contents of this project folder to the repository, keeping the folder structure. In particular, `.github/workflows/build-apk.yml` must be uploaded; GitHub's mobile website may hide the `.github` folder, so use the GitHub app/desktop view or the GitHub connector-assisted route if needed.
4. Open the repository's **Actions** tab. Select **Build FMM27 Scout APK** and tap **Run workflow** if it has not started automatically after the upload.
5. Wait for the run to finish successfully. Open that run, scroll to **Artifacts**, and download `FMM27-Scout-debug-apk`.
6. Extract the downloaded artifact ZIP. Open `app-debug.apk` on your Android phone and allow installation if Android asks.

The workflow uses GitHub-hosted runners to install Java, Android SDK Platform 35, and Gradle 8.9, then runs `assembleDebug`. GitHub documents this general Java/Gradle workflow and artifact download process: https://docs.github.com/en/actions/tutorials/build-and-test-code/java-with-gradle

## Build locally (if Android Studio is available)

1. Install Android Studio with Android SDK Platform 35 and a JDK 17 runtime.
2. Open this folder (`FMM27-Scout-Android`) in Android Studio.
3. Allow Gradle sync to download the Android Gradle Plugin and dependencies.
4. Select **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
5. The generated APK is normally at `app/build/outputs/apk/debug/app-debug.apk`.

## App behaviour

- No account, server, or internet connection is required by this HTML app after installation.
- Selected `.fms` files are read locally by the app.
- Saved filter sets use WebView local storage and remain on that device.
- CSV and edited-save ZIP exports are written to the Android Downloads folder under `FMM27 Scout`.
- Minimum Android version: Android 10 (API 29).

## Notes

This is a build-ready Android Studio project with a cloud-build workflow, not a compiled APK. The APK is generated only after the GitHub Actions workflow succeeds. The workflow produces an unsigned debug APK for personal installation; it is not a Play Store release build.
