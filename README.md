# GWreader
 
This repository contains the Java source code for GWreader, an Android application that allows a
user to read current news stories from the Grasswire web site without the use of a web browser.
The app implements a user interface that allows 'swiping' left and right between stories.

The tweets, web links, and videos that are associated with each news story can be tapped, and the
app will use Android 'intents' to launch the appropriate app in the device to process the tapped
item.  By default, the Twitter app is launched for tweets, the device browser is launched for web
links, and the YouTube app is launched to play videos.  These defaults can be via Android
configuration external to the GWreader app.

### Building

After acquiring the source code of this repository, simply import the build.gradle file into Android
Studio and kick off a build.  The generated APK file will be found in the app/build/outputs/apk/
directory, with the name app-debug.apk.  The 'minSdkVersion' in the build.gradle file is set to 8,
which means this app should run on Android versions as far back as 4.1.

### News feed via JSON vs HTML

The original implementation of the app queried the Grasswire API at
https://api-prod.grasswire.com/v1/digests/current for a JSON formatted digest of news items.  That
digest was parsed and the appropriate screen elements dictated by the digest were then rendered.

The JSON digest generation was not done in real-time, as updates to the Grasswire web site were made
by contributors.  It was done as a manual, infrequent operation, and therefore the news displayed by
the Android app was often out of date as compared to the Grasswire web site.

The most recent enhancement to the GWreader app was to add an 'HTML mode', which is currently its
default operation.  In this mode, the main Grasswire web site at https://www.grasswire.com is
queried, and the HTML that is returned is parsed (using the JSoup library) and the appropriate
screen elements dictated by the HTML are then rendered.

The HTML mode of operation is currently the default, however, the JSON mode can be easily
reenabled, should enhancements be made to the Grasswire platform that allow the JSON digest to be
kept up to date with the web site.  The compile configuration flag DOING_JSON in the file
MainActivity.java simply needs to update to have a TRUE value.

JSON mode is the preferred mode of operation should it become possible.  HTML mode is 'brittle' as
any significant changes to the rendered HTML of the Grasswire web site are likely to cause its
parsing to fail.

### Things to do 

The next major item to be done was the support of push notifications when the JSON digest changed.
In addition to the Android app changes, that feature will require understanding the implmentation of
how push notifications are being sent to the iOS Grasswire app and determining what need to be done
to support pushing to Android.
