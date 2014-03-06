![jSona screenshot](https://dl.dropboxusercontent.com/u/3669658/github/jSona/logo.png "jSona logo")

jSona is a configuration file(JSON), [vlcj](https://github.com/caprica/vlcj) and [JavaFx](http://www.oracle.com/technetwork/java/javafx/overview/index.html) based music and media player. The aim of jSona is to always keep your playlists in synch with your music folders. For fast fulltext search jSona uses [Apache Lucene](http://lucene.apache.org/core/). The follwing features are fully supported:

  - Supports all common media formats that VLC [supports](https://wiki.videolan.org/VLC_Features_Formats/)
  - Load artist informations and images via [last.fm](http://www.lastfm.de/api) and [MusicBrainz](http://musicbrainz.org/)
  - Include your music foldersf
  - Create multiple playlists
  - Fulltext search

##Screenhot
I'm proud to present you a screenshot of jSona! =)
![jSona screenshot](https://dl.dropboxusercontent.com/u/3669658/github/jSona/jsona_ui_2.png "You Got Rick Rolled!")

##New features
You want **new features**? On the following page you can vote for and submit new feature requests.
[http://jsona.idea.informer.com](http://jsona.idea.informer.com)

[![Feature requests](http://rssimg.com/signature.png?url=http%3A%2F%2Fjsona.idea.informer.com%2Fproj%2Frss&style=transparent-post)](http://jsona.idea.informer.com)

##Version
Current version is 1.0.0 

##Configuration file / config.json

Here is an example of the default configuration file. You have to setup your **VLC path** correctly. If you use Java 32-bit/64-bit you also have to use VLC-32-bit/64-bit.
```json
{
  "PATH_TO_VLCJ": "D:/Program Files/VideoLAN/VLC",
  "MAX_SEARCH_RESULT_AMOUNT": 512,
  "VOLUME": 100,
  "FOLDERS": [
    "D:/media/musik",
    "C:/share",
    "C:/downloads/musik"
  ],
  "PLAYBACK_MODE": "NORMAL",
  "RECENTLY_ADDED_UNITL_TIME_IN_DAYS": -7,
  "THEME": "grey",
  "KEY_SKIP_TIME": 10,
  "WINDOW_UNDECORATED": true,
  "TITLE": "jSona - open source project by Frank Roth",
  "MIN_HEIGHT": 600,
  "MIN_WIDTH": 720,
  "COLORIZE_ITEMS": true
}
```

And here an explanation of all possible attributes:

###PATH_TO_VLCJ
Path to your VLC root directory where the libvlc or libvlccore library is. (In windows the files are called libvlc.dll and libvlccore.dll).

###MAX_SEARCH_RESULT_AMOUNT
Maximum search results of the lucene engine (smaller is faster)

###VOLUME
Default startup volume. Will be overwritten by jSona (always save the recently changed volume).

###FOLDERS
Your music folders. Care of JSON-Syntax and correct backslashes (/). See example above...

###PLAYBACK_MODE
Playbackmode of jSona. Choose one of them: {NORMAL, SHUFFLE}. Will be overwritten by jSona (always save the recently changed playback mode).

###RECENTLY_ADDED_UNITL_TIME_IN_DAYS
How long do you want to show new songs in the "New" tab. If you choose -7 then new songs will be displayed for one week in the "New" tab. This number should always be negative.

###THEME
Currently there is only one theme available: {"grey"}.

###KEY_SKIP_TIME
If you change the duration slider with the help of the arrow keys(left or right) the slider rewind or skips 10 seconds.

###WINDOW_UNDECORATED
If you set the value on **false** the normal OS look and feel will be used. If you set the value on **true** [in-sideFX Undecorator](https://github.com/in-sideFX/Undecorator) will be used to make the jSona window beautiful.

![jSona screenshot - undecorated: false](https://dl.dropboxusercontent.com/u/3669658/github/jSona/jsona_undecorated_2.png "undecorated: false")

###TITLE
Window title of jSona. Will only be displayed if the **WINDOW_UNDECORATED** property is set to **false**.

### MIN_HEIGHT
Minimum window height.

### MIN_WIDTH
Minimum window width.

###COLORIZE_ITEMS
If the value is set to **true** the same music items will be displayed with a different smooth background color. If the value is set to **false** then the default JavaFX list background will be used (see screenshot above).
s
![jSona screenshot](https://dl.dropboxusercontent.com/u/3669658/github/jSona/jsona_colorized_items.png "You Got Rick Rolled!")

##Download
* [Version 1.0.0](https://dl.dropboxusercontent.com/u/3669658/github/jSona/binary/jSona-1.0.0.zip)
* [Current version](https://dl.dropboxusercontent.com/u/3669658/github/jSona/binary/jSona-1.0.0.zip)

##Installation and Start
Download the current zip file and extract it. Then put in your correct VLC path into the config.json file and start jSona with the following command.
```
java -jar jSona-1.0.0.jar
```
jSona uses JavaFX so a current Java virtual machine with JavaFX support should be installed.

##Help / FAQ
Here is a screenshot of jSona that explains the easy to use user interface.
![jSona explaining the UI](https://dl.dropboxusercontent.com/u/3669658/github/jSona/jsona_explaining_the_ui.png)

##Thank you very much!
This project is based on a set of amazing projects. Thank you to all programmers!
* [VLCJ](https://github.com/caprica/vlcj)
* [Undecorator](https://github.com/in-sideFX/Undecorator)
* [java-string-similarity](https://github.com/rrice/java-string-similarity)
* [last.fm-java](https://code.google.com/p/lastfm-java/)
* [Apache Lucene](http://lucene.apache.org/core/)
* [JavaFX](http://www.oracle.com/technetwork/java/javafx/overview/index.html)

##License
MIT - **Free Software, Hell Yeah!**

    
