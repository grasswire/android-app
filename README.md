# GWreader
 
This repository contains the Java source code for GWreader, an Android application that allows a user to read current news stories from the Grasswire web site without the use of a web browser.  The app implements a user interface that allows 'swiping' left and right between stories.

The tweets, web links, and videos that are associated with each news story can be tapped, and the app will use Android 'intents' to launch the appropriate app in the device to process the tapped item.  By default, the Twitter app is launched for tweets, the device browser is launched for web links, and the YouTube app is launched to play videos.  These defaults can be via Android configuration external to the GWreader app.

# Building #

After acquiring the source code of this repository, simply import the build.gradle file into Android Studio and kick off a build.
