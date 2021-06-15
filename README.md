# shareyourvoice
A super simple Android app that uses Bravobit's FFmpeg-Android to create videos from audios
## How to use
Simply select an image/audio file from your device and let the app do the rest for you. This app uses FFmpeg's loop option to "convert" the image file to a video file and append the audio file to it.
## Screenshots
![Main screen](https://github.com/tlegx/shareyourvoice/blob/main/demo/Screenshot%202021-06-09%20171452.png) ![Warning screen](https://github.com/tlegx/shareyourvoice/blob/main/demo/Screenshot%202021-06-09%20171532.png) 
![Loading screen](https://github.com/tlegx/shareyourvoice/blob/main/demo/Screenshot%202021-06-09%20171613.png) ![Player screen](https://github.com/tlegx/shareyourvoice/blob/main/demo/Screenshot%202021-06-09%20171748.png)
![Credits screen](https://github.com/tlegx/shareyourvoice/blob/main/demo/Screenshot%202021-06-09%20171813.png)
## Warning
- Due to Android restrictions (not providing a URI translation tool), **you _must_ open the "Hamburger" menu, then select "Internal Storage/Device name" or "SD card/Secondary Storage"**. Doing any other way than this will result in **app crash**.
- Due to a bug in Android 10, this app has to lower the targetSdkVersion and compileSdkVersion from 30 to 28, so it has to run in compability mode for devices running Android 10.
- **This app is still a little buggy and I'm constantly adding new features so any contribution to the app is welcomed.**
## Acknowledgements
Thanks to these amazing people with their amazing repositories to make this app possible:
- [Bravobit](https://github.com/bravobit) with [FFmpeg-Android fork](https://github.com/bravobit/FFmpeg-Android)
- [hiteshsondhi88](https://github.com/hiteshsondhi88) with [FFmpeg-Android-Java](https://github.com/WritingMinds/ffmpeg-android-java)
## License
MIT License

Copyright (c) 2021 tlegx<br/>
For more information, please see [LICENSE](https://github.com/tlegx/shareyourvoice/blob/main/LICENSE)
