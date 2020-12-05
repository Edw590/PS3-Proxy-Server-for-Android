# PS3-Proxy-Server-for-Android
PS3 Proxy server with various options, like firmware "spoof" and system update blocker - for Android

## How to use

After installing the app, just choose a port (or use the default one - 8080), the IP is automatic (though, if needed, I can try to enable the IP choice and publish a version with that enabled), and then just go on the PS3, network settings, and configure the proxy there.

-----
Copied from the website where I first released this app (PSX-Place): https://www.psx-place.com/resources/ps3-proxy-server-for-android.795/.

Downloads: 36 000+ (03-12-2020 -- DD/MM/YYYY) --> https://somsubhra.com/github-release-stats/?username=DADi590&repository=PS3-Proxy-Server-for-Android.

**Note:** To see what the next version might have, see this link: https://github.com/DADi590/PS3-Proxy-Server-for-Android/releases. I always write there what's coming and I keep updating it, so I don't forget what needs to be done and others can possibly see and say something in case they don't agree or have a better idea.

**This app can be used on ALL Firmwares! That means, OFW (HFW too..., which is OFW) and CFW. It was made for HAN, but works without anything on the PS3 - pure OFW. Works too with PS3HEN and CFW. Anything. It doesn't need anything special on the PS3 aside from configuring the network settings.**

### Latest app version: 2.1

### ATTENTION!!!!!!!

1. #### THIS APP RUNS ON ANY FIRMWARE VERSION!!!
2. #### ESTA APP FUNCIONA EM QUALQUER VERSÃO DE FIRMWARE!!!
3. #### ESTA APLICACIÓN FUNCIONA EN CUALQUIER VERSIÓN DE FIRMWARE !!!
4. #### CETTE APP FONCTIONNE SUR TOUTE VERSION FIRMWARE !!!
5. #### Diese App läuft auf einer beliebigen FIRMWARE-Version !!!
6. #### Это приложение работает на любой версии программного обеспечения!

**You could even use it on OFW 3.55 and it would still work (in theory)! No need to not update to 4.84 or 4.85 or whatever version! (unless Sºny finally fixes this issue, but then not even on lower versions the app will work) You can update with no problems!!! The app will still work! It doesn't work only in 4.82 or 4.83 or 4.8X or 4.XX. It's universal! The program inside the app puts the version required by PSN being 0.00, so any version above that one will work! You can update with no problems, don't worry!**

**Update (languages):** I'm sorry, the languages were reduced to English, Portuguese and Spanish. I don't have enough time to be translating to the others, because I more or less try to see if there is something that might be wrong in them (even though I don't understand them), so I've removed them, because I added a lot more written things. Though, if anyone wants to translate the app, just tell me.

### Introduction

Hi everyone.

So I'm releasing my first Android app and it is a proxy server to allow the PS3 to go online on older firmwares (system versions) and that blocks the system updates too (if you try to update, it won't be able to download the update file). It's very easy to use and has auto start and auto save features. You can change the port of the server too. But you can't change the IP of the server. If, for any reason, someone needs to change the IP, just tell me here and I'll try to implement that. It's not hard to enable the option, but to check if the server is really working, that's the worst part (at least for a begginer like me - before making the app a week ago, I had never really touched in Android Studio, few on Java and nothing about Object Oriented Programming...).

The minimum known working version is 4.2 (Jelly Bean) - tested by @blckbear_ - and the maximum is 9.0 (Pie) - the last one, with Android Studio. Without Android studio, the newest known working version is 8.1 (Oreo) - tested by @blckbear_ (the only Android version I have is 4.4.2 (KitKat)).

Btw, if the IP says "localhost", that's because you need to start the app with the Internet already connected, because you're not connected to an external network, so it uses the phone's internal network (or something like that, I don't really understand this network parts).

And in case you noticed, the icon it's PS3 Proxy Server GUI's icon with the android logo on it, just to be different and to mean it's the Android version.

It has 6 languages: Portuguese, English, Spanish (first one is the one I know best, since I'm PT-PT), French, German and Russian (these 3 I don't know, so the translations are as good as Google Translate). Spanish was improved by @blckbear_.

Hope you like it!

### WARNING!

This app only works on Android 4.0.3 or newer (I tried to lower as much as I could)!

So, if you need the server to work with Android 4.0 and older (being the minimum, Android 2.2, so lower than 2.2 there is no known way), please follow this tutorial, which was made by the same person who helped in this app: https://www.psx-place.com/threads/tutorial-how-to-set-up-ps3-proxy-server-on-android.22846/.

After seeing a comment (thanks to @diego18sn PSX), is now in the program the following line: '"br": { "BR", "8F", "br" },'.

### Credits

- blckbear_ (PSX-Place) - for helping, testing and giving ideas (so huge credits for him);

- [@mondul](https://github.com/mondul) (GitHub/PSX-Place) - for making the program which is behind the app (so amazing credits for him or the app wouldn't be possible, at least so easily).

- "[@elazarl](https://github.com/elazarl) for his goproxy library and examples. Without it this script [mondul's PS3 Proxy script] would not be possible."

Original program used in the app (not heavily modified) can be found on GitHub: https://github.com/mondul/PS3-Proxy

### Bugs and ideas:

If anyone would like another feature here in the app or if a bug happened (possible, because I'm a begginer), tell me here and I'll try to put/fix it!

Cheers and good gaming! (please buy the games if you can, the developers deserve it)

PS: I don't need this app, I just decided to try to do it because the other way takes TOO MUCH space to run a 3KB program and some people might not have enough space... So I'll not be using it and seeing if there are any bugs on it, so if there are, tell me please. Anyways, if someone needs the server on 4.0 or older, you'll have to use the other way anyways (unless, as I said, it's older than 2.2, for some weird reason, then there's no known way to have the server working).

PS_1: You won't find this app on PlayStore, since I need to pay 25$ to put it there... So out of question, at least for now. When/if I put it there, I'll say in PSX-Place and put a note here.
