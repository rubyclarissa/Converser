CONVERSER (ANDROID TRANSLATOR APP)

Version 1.0 May 2022
Author: Ruby Clarissa Osborne (rco1)

----------------------------------------
CONTENTS
----------------------------------------
I   Introduction
II  Structure and Content
III Build Instructions
VI Current Bugs

----------------------------------------
I. INTRODUCTION
----------------------------------------
This is a native Android app which translates spoken and textual input between 2 languages
from a selection of 59 different languages. It is intended to facilitate conversation
between 2 people who speak different languages. It requires WIFI internet connection the first
time a language is used, but can be used to translate offline once a language is downloaded.

----------------------------------------
II. STRUCTURE AND CONTENT
----------------------------------------
View – package contains classes for controlling the UI
    MainActivity – main entry point of the app

    Home - package for home (translator) screen
    TranslatorFragment – main worker within the app
    ConversationAdapter – adapter class for recyclerView in TranslateFragment

    DownloadedLanguages - package for downloading languages
        DownloadLanguagesFragment – downloads languages for offline use
        DownloadLanguagesAdapter – adapter class for recyclerView in DownloadLanguagesFragment

    Dialogs - package for dialogs used in the app
        DownloadLanguageModelDialogFragment – dialog fragment for downloading a language
        ConfirmConversationRefreshDialogFragment –  dialog fragment for refreshing a conversation

ViewModel – package contains the Translator View Model class

Model – package contains all classes associated with the data within the app
    ConverserRoomDatabase – database for managing translation items.
    ConverserRepository – abstraction layer for the database

    Home - package for model class associated with home (translator) screen
        TranslationItemDAO – interface for querying and inserting into the database
        TranslationItem – translation item in a conversation, stored in the database
        PositionInConversation – Enum is used to specify an items position in a conversation

Res/menu – contains an XML file that specifies a menu with items for the translator
and download languages fragments.

Res/navigation – contains an XML file that specifies a navigation graph for Main Activity

Res/layout – contains the XML layout files for the UI

----------------------------------------
III. BUILD INSTRUCTIONS
----------------------------------------
Android Studio with emulator:
- Create a virtual device using the virtual device manager
- Run the virtual device
- Select app in Run/Debug configuration
- Select virtual device as the running device
- Run the app

Android Studio with real device:
- Connect device to machine (pc) with USB
- Enable USB debugging (see https://developer.android.com/studio/debug/dev-options)
- Select app in Run/Debug configuration
- Select device as the running device
- Run the app
The app will load on the device

APK file on real device:
** APK has been generated for testing purposes - not deployment - it is unsigned **
- enable "Allow unknown sources" option in device settings
- Connect device to machine (pc) with USB
- Select "Media Device" when prompted
- Open device's folder on the machine (pc) and copy the APK file onto the device
- Tap APK file on device to install

Alternatively install onto device from a browser on the device

** you must uninstall when finished as the app will take up significant space currently **

----------------------------------------
VI. CURRENT BUGS
----------------------------------------
A number of bugs are present
PLEASE SEE TECHNICAL REPORT
