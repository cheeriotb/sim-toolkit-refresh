# Java Card applet for SIM Toolkit (REFRESH)

This Java Card applet adds one menu option "Refresh" to the main main of SIM Toolkit application. It can be used for making trigger to send REFRESH command with the specified refresh mode from SIM card to terminal. There are two execution modes (syncronous and asynchronous) available.

![refresh](https://user-images.githubusercontent.com/44401044/89115849-f60f4e00-d4c7-11ea-8a9e-bdbe3b79953f.gif)

# Usage

The following menu structure is added by this applet. Please choose "Request (sync)" or "Request (async)" after selecting the expected refresh mode by using "Settings" menu. If you choose "Request (async)", the terminal can receive REFRESH command after some delay made by TIMER MANAGEMENT.

* Refresh
    * Settings
        * Refresh mode
    * Request (sync)
    * Request (async)

You are also supposed to specify "File List" for some refresh modes and it is also possible to decide whether alpha identifier is appended.

# Dependency

Java Card 2.2.1 was used while this applet was developed due to the limitation of the test card used.

SIM Tools provided by the OSMOCOM (Open Source Mobile Communications) community was used for developing the applet. You can get it from their own git repository (http://git.osmocom.org/sim/sim-tools/). Or, it is also okay to use a forked version (https://github.com/cheeriotb/osmocom-sim-tools) which was modified just for the adaptation to Python 3 (3.7.0).

# Setup

The make file in this git repository is just a sample one and has a deep dependency on the forked version of the OSMOCOM SIM Tools (https://github.com/cheeriotb/osmocom-sim-tools). You should prepare your own appropriate make file for your development environment.

The following commands were used when this applet was developed. You need to specify appropriate parameters for your one.

## Load
```
$ python ../osmocom-sim-tools/shadysim/shadysim.py \
    --pcsc \
    -l ./build/javacard/com/github/cheeriotb/toolkit/refresh/javacard/refresh.cap \
    --kic 34F9628AE3B3D69FD8C7B28BF03B4E3A \
    --kid B30115CB9912FD6EEC7770B54F91A314
```
## Install
```
$ python ../osmocom-sim-tools/shadysim/shadysim.py \
    --pcsc \
    -i ./build/javacard/com/github/cheeriotb/toolkit/refresh/javacard/refresh.cap \
    --enable-sim-toolkit \
    --module-aid D07002CA44900102 \
    --instance-aid D07002CA44900102 \
    --nonvolatile-memory-required 0100 \
    --volatile-memory-for-install 0100 \
    --max-timers 1 \
    --max-menu-entry-text 15 \
    --max-menu-entries 01 \
    --kic 34F9628AE3B3D69FD8C7B28BF03B4E3A \
    --kid B30115CB9912FD6EEC7770B54F91A314
```
## Delete
```
$ python ../osmocom-sim-tools/shadysim/shadysim.py \
    --pcsc \
    -d D07002CA449001 \
    --kic 34F9628AE3B3D69FD8C7B28BF03B4E3A \
    --kid B30115CB9912FD6EEC7770B54F91A314 
```
## List applets
```
$ python ../osmocom-sim-tools/shadysim/shadysim.py \
    --pcsc \
    --list-applets \
    --kic 34F9628AE3B3D69FD8C7B28BF03B4E3A \
    --kid B30115CB9912FD6EEC7770B54F91A314
```

# Licence

This software is released under the MIT License, see LICENSE.

# Author

Cheerio (cheerio.the.bear@gmail.com)

# References

* Test SIM card
    * sysmousim-SJS1-4FF - http://shop.sysmocom.de/products/sysmousim-sjs1-4ff
* Standard
    * ETSI TS 102.220 V15.0.0
    * ETSI TS 102.221 V16.0.0
    * ETSI TS 102.223 V15.0.0
