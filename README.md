# FreeMediaPlayer

A free media player for Android. This app can play both audio and video files.

This is a work in progress. Any contribution or feedback would be greatly appreciated.

## Screens

![First Screens](https://user-images.githubusercontent.com/4550185/169659972-384351dd-deef-4157-b41e-0bae2bf815f3.jpg)
![2nd Screen](https://user-images.githubusercontent.com/4550185/169660013-10806aa9-a9d4-4422-9e02-bebe76726525.jpg)
![screen 4](https://user-images.githubusercontent.com/4550185/169660231-e93abf5a-d014-4f53-a864-daab78a857d3.jpg)

## Features

### Permission Popup

<img src="https://user-images.githubusercontent.com/4550185/169656765-d1cb973d-c3a1-4583-af3a-c922fd5f4669.png" width="270" height="570">

### Bottom Navigation

https://user-images.githubusercontent.com/4550185/169657912-5300726a-cd76-472b-9485-9ea872ca7316.mp4

### Sort files based on media type

https://user-images.githubusercontent.com/4550185/169658008-4d5d5ce5-5875-423d-9274-afc2bd4b4f3c.mp4

### Files Browser

https://user-images.githubusercontent.com/4550185/169658068-fac18c9f-b966-4a0e-bd08-2d26192ac9d0.mp4

### Music Player

https://user-images.githubusercontent.com/4550185/169658157-7949881f-71a3-4740-b134-2712c97a52e9.mp4

### Portrait Video Player

https://user-images.githubusercontent.com/4550185/169658257-f546efce-0b81-4a78-a575-0b21ee681a53.mp4

### Landscaped Video Player

https://user-images.githubusercontent.com/4550185/169658361-00cbfde1-17b3-4d05-a32b-8dde1cd76630.mp4

### Active Playlist drag-and-drop to re-order

https://user-images.githubusercontent.com/4550185/169658656-e341f22c-71bd-411e-a079-4d8cbe635715.mp4

### Active Playlist swipe-to-remove

https://user-images.githubusercontent.com/4550185/169658776-57072bfd-74c8-4b71-a69d-cde74b0b606a.mp4

### Media Controller in Notification

https://user-images.githubusercontent.com/4550185/169658875-21db9a8f-0216-42a0-895e-cbbc3369c8fc.mp4

### Media Controller with home screen widget

https://user-images.githubusercontent.com/4550185/169659016-7870eff1-fd84-412c-ad67-8f751c5025b6.mp4

### Dark and Light mode switch

https://user-images.githubusercontent.com/4550185/169659163-0a4ec774-59a5-4630-9a12-331f5456e788.mp4

### Dynamic Colors theme

https://user-images.githubusercontent.com/4550185/169659346-6aa0fbad-d197-4b84-b2b3-19df48809dce.mp4

# Technical Stuff

## Project Structure

**data**: All classes belonging to the data layer should go here. The types of class in this package are **Repository**, **Repository Manager**, **DataSource**, **Room** classes (**Dao**, `@Entity`, **Database**, etc.), **DataStore**, **Worker**, etc.

**domain**: All **UseCase** classes should go here.

**hilt**: All Hilt configuration classes such as **Module** should go here.

**service**: All services should go here.

**ui**: All **Activity**, **Fragment**, **ViewModel**, or **Widget** should go here.

## Architecture
1. The app heavily follows Google's guide to app achitecture https://developer.android.com/topic/architecture?hl=en. All of the layers are separated.
2. For the Audio and Video players, the app also use the Google guide to Media app architecture https://developer.android.com/guide/topics/media-apps/media-apps-overview.
3. Media files and metadata relies on Android's MediaStore.

### UI architecture
1. For simple screens, databinding and simple callbacks are used.
2. For complicated screens with lots of actions, such as the Video player, MVI is used to model user actions **AND** actions coming from the data layer.

## Theming
The app heavily relies on Material 3 for theming purposes.

## Testing
I try my best to provide adequate Unit and instrumented tests if I have time.
