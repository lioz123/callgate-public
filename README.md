# Callgate - Android App
Callgate is self hosted app made for openning electrical gates.
Unlike other apps, callgate automates oppening of gates using location and bluetooth connection without the user interaction.

# about
Callgate is written in kotlin using the android SDK.
The code demonstrate the use of MVVM articture.
This app is depended on Background \ Foreground services using the notification system, along side with Broadcast Recivers.

Inorder to set gate by location the user can select the location of the gate through google maps and choose the radius that he wants to open it.
Inorder to save buttery, the app will trigger itself by bluetooth connection or manuly by the user, afterhand the app will calculate the appromixtly arrive time to the closest gate and will start asking for location upadates by that time.





#API
- Firebase dynamic links
- Google Maps API
- Firebase Auth
- Room
- Live-Data
- Corrutines



## Features

The android app lets you:
- Open electrical gates based on your location.
- Connect your car through bluetooth.
- Completely ad-free.
- Share gates between users.
- Support for English\Hebrew.
- Switch between dark\light theme.

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

[<img src="/images/maps activity.jpeg" align="left"
width="200"
    hspace="10" vspace="10">]

[<img src="/images/settings.jpeg" align="left"
width="200"
    hspace="10" vspace="10">](/images/settings.jpeg)




