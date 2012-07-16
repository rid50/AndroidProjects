Android Native App
GPS communicator, Android SQLite Database, Web Services support

The idea of the app is getting coordinates of person’s current location, bearing, accuracy and date from a GPS system. Then depending on an availability of an Internet connection the information will either be uploaded to an external MySQL database (hosted on a cloud) or saved in an internal SQLite database. In the latter case, as soon as the app spots the Internet connection it will automatically upload all collected information from the internal to the external database.
There is a web service set to query MySQL database. The following URL is pointing to the web based application that can be used to track the paths provided by Android GPS system.

http://yaruss.org/kuwaitindex

My ID: b27669 - driving direction from my home to Kuwait Ministry of Oil.
My daughter’s ID: ef1209 - walking from Champs-Élysées to Notre Dame (Paris)

The application can be installed from a custom repository http://yaruss.org/ShowMap.apk

Tags: Google Android SDK, Java, Eclipse, MySQL
