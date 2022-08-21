# Callgate - Android App
Callgate is a self-hosted app designed for opening electric gates.
Unlike other apps, callgate automates the opening of gates using tracking and Bluetooth connectivity without user intervention.

# about
Callgate is written in Kotlin and uses the Android SDK. The code demonstrates the use of MVVM articture. This app depends on Background \ Foreground services with the notification system, along with broadcast recivers.

To set the gate by location, the user can select the location of the gate via Google Maps and choose the radius in which to open it. To save time, the app triggers itself through a Bluetooth connection or manually by the user. After that, the app calculates the time to reach the nearest gate and asks for location updates at that time.




#API
- Firebase dynamic links
- Google Maps API
- Firebase Auth
- Room
- Live-Data
- Corrutines



## Features

With the Android app you can:
- Open electric gates based on your location.
- Connect your car via bluetooth.
- Completely ad-free.
- Share gates with other users.
- Support for English and Hebrew.
- Switch between dark and light themes.

## Permissions
On Android versions prior to Android 6.0, wallabag requires the following permissions:
- Full Network Access.
- GPS
- Phone calls
- Read \ Write Contacts.


## Screenshots
[<img src="/images/main.jpeg" align="left"
width="200"
    hspace="10" vspace="10">](/images/main.jpeg)

<img src="/images/maps activity.jpeg" align="left"
width="200"
    hspace="10" vspace="10">

<img src="/images/settings.jpeg" align="left"
width="200"
    hspace="10" vspace="10">




