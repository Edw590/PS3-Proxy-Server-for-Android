# PS3-Proxy-Server-for-Android
"PS3 proxy to allow PSN login on older firmwares" (and to block system updates) - for Android

## How to use

After installing the app, just choose a port (or use the default one - 8080), the IP is automatic (though, if needed, I can try to enable the IP choice and publish a version with that enabled), and then just go on the PS3, network settings, and configure the proxy there.

-----
Copied from the website where I first released this app (PSX-Place): https://www.psx-place.com/resources/ps3-proxy-server-for-android.795/.

### Introduction

Hi everyone.

So I'm releasing my first Android app and it is a proxy server to allow the PS3 to go online on older firmwares (system versions) and that blocks the system updates too (if you try to update, it won't be able to download the update file). It's very easy to use and has auto start and auto save features. You can change the port of the server too. But you can't change the IP of the server. If, for any reason, someone needs to change the IP, just tell me here and I'll try to implement that. It's not hard to enable the option, but to check if the server is really working, that's the worst part (at least for a begginer like me - before making the app a week ago, I had never really touched in Android Studio...).

The minimum known working version is 4.2 (Jelly Bean) - tested by @blckbear_ - and the maximum is 9.0 (Pie) - the last one, with Android Studio. Without Android studio, the newest known working version is 8.1 (Oreo) - tested by @blckbear_ (the only Android version I have is KitKat).

Btw, if the IP says "localhost", that's because you need to start the app with the Internet already connected, because you're not connected to an external network, so it uses the phone's internal network (or something like that, I don't really understand this network parts).

And in case you noticed, the icon it's PS3 Proxy Server GUI's icon with the android logo on it, just to be different and to mean it's the Android version.

It has 6 languages: Portuguese, English, Spanish (first one is the one I know best, since I'm PT-PT), French, German and Russian (these 3 I don't know, so the translations are as good as Google Translate). Spanish was improved by @blckbear_.

Hope you like it!

### WARNING!

This app only works on Android 4.0.3 or newer (I tried to lower as much as I could)!

So, if you need the server to work with Android 4.0 and older (being the minimum, Android 2.2, so lower than 2.2 there is no known way), please follow this tutorial, which was made by the same person who helped in this app: https://www.psx-place.com/threads/tutorial-how-to-set-up-ps3-proxy-server-on-android.22846/.

After seeing a comment (thanks to @diego18sn PSX), is now in the program the following line: '"br": { "BR", "8F", "br" },'.

### Credits

- blckbear_ (PSX-Place) - for helping and giving ideas (so huge credits for him);

- @mondul (GitHub/PSX-Place) - for making the program which is behind the app (so amazing credits for him or the app wouldn't be possible, at least so easily).

- "@elazarl for his goproxy library and examples. Without it this script [mondul's PS3 Proxy script] would not be possible."

Original program used in the app can be found on GitHub: https://github.com/mondul/PS3-Proxy

### Bugs and ideas:

It already has a bug where at least in KitKat and with my phone, if you enable any of the two options on the settings page, you go back to the initial page and then you restart the app. If you go again to the settings page, they will be disabled. So just don't go there, because if you don't the options will still be enabled. If you go to the settings page again, you'll cause them to disable themselves. I was much time with this and I still couldn't fix this (just a begginer hahaha).

If anyone would like another feature here in the app or if a bug happened (possible, because I'm a begginer), tell me here and I'll try to put/fix it!

Cheers and good gaming! (please buy the games if you can, the developers deserve it)

PS: I don't need this app, I just decided to try to do it because the other way takes TOO MUCH space to run a 3KB program and some people might not have enough space... So I'll not be using it and seeing if there are any bugs on it, so if there are, tell me please. Anyways, if someone needs the server on 4.0 or older, you'll have to use the other way anyways (unless, as I said, it's older than 2.2, for some weird reason, then there's no known way to have the server working).

PS: You won't find this app on PlayStore, since I need to pay 25$ to put it there... So out of question, at least for now. When/if I put it there, I'll say in PSX-Place and put a note here.
