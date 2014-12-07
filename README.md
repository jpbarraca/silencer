silencer
========

Experimental encrypted PushBullet (Android/Chrome Extension only)

# Setup
## Chrome Extension
Download the [Chrome extension](https://github.com/hauckwill/silencer/raw/master/chrome-extension/PushBullet.crx) and drag it onto the [Chrome extensions](chrome://extensions/) window (developer mode may need to be enabled). Alternatively, patch the existing PushBullet files (located in the Chrome extension folder) as shown [in this commit](https://github.com/hauckwill/silencer/commit/339096cc86995fb948b347fb5769d5c2cdaf5d92). Next, open PushBullet options from the toolbar and enter a password in the encryption key field under the newly added encryption tab. This can be anything, but must also be entered on the Android application.

## Android Application
Download the [signed APK](https://github.com/hauckwill/silencer/raw/master/android-application/app-release.apk) or compile one from source, then follow the setup instructions on your device.

##Verifying Setup
Once the above steps are completed, setup can be verified by sending a notification to your device. It will be mirrored to Chrome and appear exactly the same as it would without silencer, including dismiss support. On the Chrome extensions page with developer mode enabled, click the "Inspect views" button under PushBullet. In the window that appears, click console. In the console, there will be messages that begin with "Encrypted" followed by an encrypted string. This string contains notification data that would normally be sent in plain text.

#Notes
This is *very* experimental and may not work as intended, or at all. It was created solely as a challenge, but has been released because of demand. It was put together quickly, so expect some bugs.

To the PushBullet team: if you want this removed, let me know.
