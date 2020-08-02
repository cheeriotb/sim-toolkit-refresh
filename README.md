# Java Card applet for SIM Toolkit (REFRESH)

This Java Card applet adds one menu option "Refresh" to the main main of SIM Toolkit application. It can be used for making trigger to send REFRESH command with the specified refresh mode from SIM card to terminal. There are two execution modes (syncronous and asynchronous) available.

# Dependency

Java Card 2.2.1 was used while this applet was developed due to the limitation of the test card used.

SIM Tools provided by the OSMOCOM (Open Source Mobile Communications) community was used for developing the applet. You can get it from their own git repository (http://git.osmocom.org/sim/sim-tools/). Or, it is also okay to use a forked version (https://github.com/cheeriotb/osmocom-sim-tools) which was modified just for the adaptation to Python 3 (3.7.0).

# Setup

The make file in this git repository is just a sample one and has a deep dependency on the forked version of the OSMOCOM SIM Tools (https://github.com/cheeriotb/osmocom-sim-tools). You should prepare your own appropriate make file for your development environment.

# Usage

The following menu structure is added by this applet. Please choose "Request (sync)" or "Request (async)" after selecting the expected refresh mode by using "Settings" menu. If you choose "Request (async)", the terminal can receive REFRESH command after some delay made by TIMER MANAGEMENT.

* Refresh
    * Settings
        * Refresh mode
    * Request (sync)
    * Request (async)

You are also supposed to specify "File List" for some refresh modes and it is also possible to decide whether alpha identifier is appended.

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
