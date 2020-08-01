/*
 *  Copyright (C) 2020 Cheerio <cheerio.the.bear@gmail.com>
 *
 *  This program is free software; you can redistribute it and/or modify it
 *  under the terms of the MIT license.
 *  See the license information described in LICENSE file.
 */

package com.github.cheeriotb.toolkit.refresh;

import javacard.framework.APDU;
import javacard.framework.Applet;
import javacard.framework.ISOException;
import javacard.framework.Util;

import sim.toolkit.ProactiveHandler;
import sim.toolkit.ProactiveResponseHandler;
import sim.toolkit.ToolkitConstants;
import sim.toolkit.ToolkitException;
import sim.toolkit.ToolkitInterface;
import sim.toolkit.ToolkitRegistry;

public class RefreshApplet extends Applet implements ToolkitInterface, ToolkitConstants {

    private static final byte[] MENU1_TITLE = new byte[] {
        'R', 'e', 'f', 'r', 'e', 's', 'h'
    };

    private static final byte[] MENU1_ITEM_SETTINGS = new byte[] {
        'S', 'e', 't', 't', 'i', 'n', 'g', 's'
    };
    private static final byte[] MENU1_ITEM_REQUEST_SYNC = new byte[] {
        'R', 'e', 'q', 'u', 'e', 's', 't', ' ', '(', 's', 'y', 'n', 'c', ')'
    };
    private static final byte[] MENU1_ITEM_REQUEST_ASYNC = new byte[] {
        'R', 'e', 'q', 'u', 'e', 's', 't', ' ', '(', 'a', 's', 'y', 'n', 'c', ')'
    };

    private Object[] MENU1_ITEMS = {
        MENU1_ITEM_SETTINGS,
        MENU1_ITEM_REQUEST_SYNC,
        MENU1_ITEM_REQUEST_ASYNC
    };

    private static final byte[] MENU2_TITLE = new byte[] {
        'R', 'e', 'f', 'r', 'e', 's', 'h', ' ', 'm', 'o', 'd', 'e'
    };

    private static final byte[] MENU2_ITEM_REFRESH0 = new byte[] {
        'N', 'A', 'A', ' ', 'I', 'n', 'i', 't', ' ', '&', ' ', 'F', 'F', 'C', 'N'
    };
    private static final byte[] MENU2_ITEM_REFRESH1 = new byte[] {
        'F', 'C', 'N'
    };
    private static final byte[] MENU2_ITEM_REFRESH2 = new byte[] {
        'N', 'A', 'A', ' ', 'I', 'n', 'i', 't', ' ', '&', ' ', 'F', 'C', 'N'
    };
    private static final byte[] MENU2_ITEM_REFRESH3 = new byte[] {
        'N', 'A', 'A', ' ', 'I', 'n', 'i', 't'
    };
    private static final byte[] MENU2_ITEM_REFRESH4 = new byte[] {
        'U', 'I', 'C', 'C', ' ', 'R', 'e', 's', 'e', 't'
    };
    private static final byte[] MENU2_ITEM_REFRESH5 = new byte[] {
        'N', 'A', 'A', ' ', 'A', 'p', 'p', ' ', 'R', 'e', 's', 'e', 't'
    };
    private static final byte[] MENU2_ITEM_REFRESH6 = new byte[] {
        'N', 'A', 'A', ' ', 'S', 'e', 's', 's', ' ', 'R', 'e', 's', 'e', 't'
    };

    private Object[] MENU2_ITEMS = {
        MENU2_ITEM_REFRESH0,
        MENU2_ITEM_REFRESH1,
        MENU2_ITEM_REFRESH2,
        MENU2_ITEM_REFRESH3,
        MENU2_ITEM_REFRESH4,
        MENU2_ITEM_REFRESH5,
        MENU2_ITEM_REFRESH6
    };
    private byte mQualifier = (byte) 0x00;

    private static final byte[] DEFAULT_FILE_LIST_WITH_NUMBER = new byte[] {
        (byte) 0x01,
        (byte) 0x3F, (byte) 0x00, (byte) 0x7F, (byte) 0xFF, (byte) 0x6F, (byte) 0x46
    };
    private static final byte[] DEFAULT_FILE_LIST_READABLE = new byte[] {
        '3', 'F', '0', '0', '7', 'F', 'F', 'F', '6', 'F', '4', '6'
    };
    private static final short MIN_FILE_LIST_SIZE = (short) 0x04;
    private static final short MAX_FILE_LIST_SIZE = (short) 0x18;
    private byte[] mFileList;
    private short mFileListSize;

    private static final byte[] QUERY_FILE_LIST = new byte[] {
        'F', 'i', 'l', 'e', ' ', 'l', 'i', 's', 't', '?', ' ', '(', 'e', 'x', '.', ' ', '3', 'F',
        'F', 'F', '7', 'F', 'F', 'F', '6', 'F', '4', '6', ')'
    };
    private static final byte[] QUERY_ALPHA_ID = new byte[] {
        'N', 'e', 'e', 'd', ' ', 'a', 'l', 'p', 'h', 'a', ' ', 'i', 'd', 'e', 'n', 't', 'i', 'f',
        'i', 'e', 'r', '?'
    };

    private static final byte[] TEXT_WAITING_TIMER = new byte[] {
            'W', 'a', 'i', 't', 'i', 'n', 'g', ' ', 'f', 'o', 'r', ' ', 't', 'h', 'e', ' ', 't',
            'i', 'm', 'e', 'o', 'u', 't'
    };
    private static final byte[] TEXT_INVALID_INPUT = new byte[] {
            'I', 'n', 'v', 'a', 'l', 'i', 'd', ' ', 'd', 'a', 't', 'a', ' ', 'i', 'n', 'p', 'u', 't'
    };
    private static final byte[] TEXT_RESOURCE_UNAVAILABLE = new byte[] {
            'R', 'e', 's', 'o', 'u', 'r', 'c', 'e', ' ', 'u', 'n', 'a', 'v', 'a', 'i', 'l', 'a',
            'b', 'l', 'e'
    };

    private static final byte CMD_QUALIFIER_YES_NO = (byte) 0x04;
    private static final byte CMD_QUALIFIER_ALPHABET = (byte) 0x01;

    private static final byte GET_INKEY_YES = (byte) 0x01;
    private static final byte GET_INKEY_NO = (byte) 0x00;

    private static final byte UNEXPECTED_RESPONSE = (byte) 0xFF;

    private byte mTimerId = (byte) 0x00;
    private static byte[] TIMER_VALUE = new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x30 };
    private byte[] mTimerAllocated = new byte[] { 'T', 'i', 'm', 'e', 'r', ' ', '0', ' ', 'a', 'l',
            'l', 'o', 'c', 'a', 't', 'e', 'd' };
    private static short POSITION_TIMER_VALUE = (short) 0x06;

    private boolean mAlphaIdentifierRequired = true;
    private byte[] mAlphaIdentifier = new byte[] { 'R', 'e', 'f', 'r', 'e', 's', 'h', ' ', 'm', 'o',
            'd', 'e', ' ', '0', '0' };
    private static short POSITION_REFRESH_MODE = (short) 0x0E;

    private byte[] mInputBuffer;
    private short mInputDataSize;

    private Exception mException;

    private RefreshApplet() {
        ToolkitRegistry registry = ToolkitRegistry.getEntry();
        registry.initMenuEntry(
                MENU1_TITLE,                // menuEntry: byte[]
                (short) 0,                  // offset: short
                (short) MENU1_TITLE.length, // length: short
                PRO_CMD_SELECT_ITEM,        // nextAction: byte
                false,                      // helpSupported: boolean
                (byte) 0,                   // iconQualifier: byte
                (short) 0                   // iconIdentifier: short
                );

        /*
          The following filed is used for keeping not only "Files" but also "Number of files".

          Byte(s)                        | Description         | Length
          -------------------------------+---------------------+-------
          (Y - 1) + 3                    | Number of files (n) | 1
          (Y - 1) + 4 to (Y - 1) + X + 2 | Files               | X - 1
         */
        mFileList = new byte[MAX_FILE_LIST_SIZE + 1];

        mInputBuffer = new byte[MAX_FILE_LIST_SIZE * 2];

        initialize();

        mException = new Exception();
    }

    private void initialize() {
        // Set the default parameters for REFRESH command
        mQualifier = (byte) 0x00;
        mAlphaIdentifierRequired = true;
        mFileListSize = (short) DEFAULT_FILE_LIST_WITH_NUMBER.length;
        Util.arrayCopy(DEFAULT_FILE_LIST_WITH_NUMBER, (short) 0, mFileList, (short) 0,
                mFileListSize);

        mInputDataSize = (short) DEFAULT_FILE_LIST_READABLE.length;
        Util.arrayCopy(DEFAULT_FILE_LIST_READABLE, (short) 0, mInputBuffer, (short) 0,
                mInputDataSize);
    }
 
    public static void install(byte[] buffer, short offset, byte length) {
        RefreshApplet applet = new RefreshApplet();
        applet.register();
    }

    public void process(APDU arg0) throws ISOException {
        // So far there is nothing to do here.
    }

    public void processToolkit(byte event) throws ToolkitException {
        boolean stayAtSecondaryMenu = true;

        if (event == EVENT_MENU_SELECTION) {
            do {
                if (mTimerId == (byte) 0x00) {
                    switch (selectItem(MENU1_TITLE, MENU1_ITEMS)) {
                        case 1:  // MENU1_ITEM_SETTINGS
                            configure();
                            break;
                        case 2:  // MENU1_ITEM_REQUEST_SYNC
                            refresh();
                            break;
                        case 3:  // MENU1_ITEM_REQUEST_ASYNC
                            try {
                                startTimer();
                            } catch (Exception e) {
                                displayText(TEXT_RESOURCE_UNAVAILABLE);
                            }
                            stayAtSecondaryMenu = false;
                            break;
                        default:
                            stayAtSecondaryMenu = false;
                            break;
                    }
                } else {
                    displayText(TEXT_WAITING_TIMER);
                    stayAtSecondaryMenu = false;
                }
            } while (stayAtSecondaryMenu);
        } else if (event == EVENT_TIMER_EXPIRATION) {
            ToolkitRegistry.getEntry().releaseTimer(mTimerId);
            mTimerId = (byte) 0x00;
            refresh();
        }
    }

    private void configure() {
        byte qualifier = selectItem(MENU2_TITLE, MENU2_ITEMS);
        if (qualifier == UNEXPECTED_RESPONSE) return;
        mQualifier = --qualifier;

        /*
           For the refresh modes "File Change Notification", "NAA Initialization and File Change
           Notification" and "NAA Session Reset", the UICC shall supply a File List data object,
           indicating which EFs need to be refreshed.

           '01' = File Change Notification;
           '02' = NAA Initialization and File Change Notification;
           '06' = NAA Session Reset, only applicable for a 3G platform;
         */
        if ((qualifier == (byte) 0x01) || (qualifier == (byte) 0x02)
                || (qualifier == (byte) 0x06)) {
            short inputSize = getInput(CMD_QUALIFIER_ALPHABET, QUERY_FILE_LIST,
                    (short) QUERY_FILE_LIST.length, mInputBuffer, mInputDataSize,
                    MIN_FILE_LIST_SIZE, MAX_FILE_LIST_SIZE);
            byte number = 0;
            try {
                if ((inputSize < MIN_FILE_LIST_SIZE) || (MAX_FILE_LIST_SIZE < inputSize)
                        || (inputSize % 4 != 0)) {
                        throw mException;
                }
                for (short index = 0; index < inputSize; index += 2) {
                    byte combined = (byte) ((charToHex(mInputBuffer[index]) << 4)
                            + charToHex(mInputBuffer[(short) (index + 1)]));
                    mFileList[(short) ((index / 2) + 1)] = combined;
                    if ((index % 4 == 0) && (combined == 0x3F)) {
                        number++;
                    }
                }
            } catch (Exception e) {
                displayText(TEXT_INVALID_INPUT);
                initialize();
                return;
            }
            mInputDataSize = inputSize;
            mFileListSize = (short) ((inputSize / 2) + 1);
            mFileList[0] = number;
        }

        byte needAlphaId = getInkey(CMD_QUALIFIER_YES_NO, QUERY_ALPHA_ID);
        if (needAlphaId == UNEXPECTED_RESPONSE) return;
        mAlphaIdentifierRequired = (needAlphaId == GET_INKEY_YES);
    }

    private byte charToHex(byte character) throws Exception {
        if (('0' <= character) && (character <= '9')) {
            return (byte) (0x00 + character - '0');
        }
        if (('A' <= character) && (character <= 'F')) {
            return (byte) (0x0A + character - 'A');
        }
        if (('a' <= character) && (character <= 'f')) {
            return (byte) (0x0A + character - 'a');
        }
        throw mException;
    }

    private void refresh() {
        /*
           Command Qualifier for REFRESH

           '00' = NAA Initialization and Full File Change Notification; 
           '01' = File Change Notification;
           '02' = NAA Initialization and File Change Notification;
           '03' = NAA Initialization;
           '04' = UICC Reset; 
           '05' = NAA Application Reset, only applicable for a 3G platform;
           '06' = NAA Session Reset, only applicable for a 3G platform;
         */
        ProactiveHandler command = ProactiveHandler.getTheHandler();
        command.init((byte) PRO_CMD_REFRESH, (byte) mQualifier, DEV_ID_ME);

        /*
           For the refresh modes "File Change Notification", "NAA Initialization and File Change
           Notification" and "NAA Session Reset", the UICC shall supply a File List data object,
           indicating which EFs need to be refreshed.

           '01' = File Change Notification;
           '02' = NAA Initialization and File Change Notification;
           '06' = NAA Session Reset, only applicable for a 3G platform;
         */
        if ((mQualifier == (byte) 0x01) || (mQualifier == (byte) 0x02)
                || (mQualifier == (byte) 0x06)) {
            command.appendTLV((byte) (TAG_FILE_LIST | TAG_SET_CR), mFileList,
                    (short) 0, mFileListSize);
        }

        if (mAlphaIdentifierRequired) {
            mAlphaIdentifier[POSITION_REFRESH_MODE] = (byte) ('0' + mQualifier);
            command.appendTLV((byte) (TAG_ALPHA_IDENTIFIER | TAG_SET_CR), mAlphaIdentifier,
                    (short) 0, (short) mAlphaIdentifier.length);
        }

        command.send();
    }

    private byte selectItem(byte[] alphaIdentifier, Object[] items) {
        /*
           Command Qualifier for SELECT ITEM

           bit 1: 0 = presentation type is not specified;
                  1 = presentation type is specified in bit 2.
           bit 2: 0 = presentation as a choice of data values if bit 1 = '1';
                  1 = presentation as a choice of navigation options if bit 1 is '1'.
           bit 3: 0 = no selection preference;
                  1 = selection using soft key preferred.
           bit 8: 0 = no help information available;
                  1 = help information available.
         */

        ProactiveHandler command = ProactiveHandler.getTheHandler();
        command.init((byte) PRO_CMD_SELECT_ITEM, (byte) 0, DEV_ID_ME);
        command.appendTLV((byte) (TAG_ALPHA_IDENTIFIER | TAG_SET_CR), alphaIdentifier, (short) 0,
                (short) alphaIdentifier.length);
        for (short index = 0; index < items.length; index++) {
            command.appendTLV((byte) (TAG_ITEM | TAG_SET_CR), (byte) (index + 1),
                    (byte[]) items[index], (short) 0,
                    (short) ((byte[]) items[index]).length);
        }
        command.send();

        ProactiveResponseHandler response = ProactiveResponseHandler.getTheHandler();
        if (response.getGeneralResult() == RES_CMD_PERF /* Command performed successfully */) {
            return response.getItemIdentifier();
        }

        return UNEXPECTED_RESPONSE;
    }

    private byte getInkey(byte qualifier, byte[] text) {
        /*
           Command Qualifier for GET INKEY

           bit 1: 0 = digit (0 to 9, *, # and L) only;
                  1 = alphabet set.
           bit 2: 0 = SMS default alphabet;
                  1 = UCS2 alphabet.
           bit 3: 0 = character sets defined by bit 1 and bit 2 are enabled;
                  1 = character sets defined by bit 1 and bit 2 are disabled
                      and the "Yes/No" response is requested.
           bit 4: 0 = user response shall be displayed.
                      The terminal may allow alteration and/or confirmation;
                  1 = an immediate digit response (0 to 9, * and #) is requested.
           bit 8: 0 = no help information available;
                  1 = help information available.
         */

        ProactiveHandler command = ProactiveHandler.getTheHandler();
        command.initGetInkey(qualifier, DCS_8_BIT_DATA, text, (short) 0,
                (short) text.length);
        command.send();

        ProactiveResponseHandler response = ProactiveResponseHandler.getTheHandler();
        if (response.getGeneralResult() == RES_CMD_PERF /* Command performed successfully */) {
            if (response.findTLV(TAG_TEXT_STRING, (byte) 1) == TLV_FOUND_CR_SET) {
                if (response.getValueLength() > 1) {
                    // Retrieve the second byte because the first byte indicates the DCS.
                    return response.getValueByte((short) 1);
                }
            }
        }

        return UNEXPECTED_RESPONSE;
    }

    private short getInput(byte qualifier, byte[] text, short textLength, byte[] defaultText,
            short defaultTextLength, short min, short max) {
        /*
           Command Qualifier for GET INPUT

           bit 1: 0 = digit (0 to 9, *, # and L) only;
                  1 = alphabet set.
           bit 2: 0 = SMS default alphabet;
                  1 = UCS2 alphabet.
           bit 3: 0 = terminal may echo user input on the display;
                  1 = user input shall not be revealed in any way (see note).
           bit 4: 0 = user input to be in unpacked format;
                  1 = user input to be in SMS packed format.
           bit 8: 0 = no help information available;
                  1 = help information available.
         */
        ProactiveHandler command = ProactiveHandler.getTheHandler();
        command.initGetInput(qualifier, DCS_8_BIT_DATA, text, (short) 0, (short) textLength, min,
                max);
        if (defaultText != null && defaultTextLength > 0) {
            command.appendTLV((byte) (TAG_DEFAULT_TEXT | TAG_SET_CR), DCS_8_BIT_DATA, defaultText,
                    (short) 0, defaultTextLength);
        }
        command.send();

        ProactiveResponseHandler response = ProactiveResponseHandler.getTheHandler();
        if (response.getGeneralResult() == RES_CMD_PERF /* Command performed successfully */) {
            response.copyTextString(mInputBuffer, (short) 0);
            return response.getTextStringLength();
        }

        return 0;
    }

    private void startTimer() throws ToolkitException {
        byte timerId = ToolkitRegistry.getEntry().allocateTimer();
        ProactiveHandler command = ProactiveHandler.getTheHandler();

        try {
            /*
               Command Qualifier for TIMER MANAGEMENT

               bit 1 to 2: 00 = start;
                           01 = deactivate;
                           10 = get current value.
            */
            command.init((byte) PRO_CMD_TIMER_MANAGEMENT, (byte) 0, DEV_ID_ME);
            command.appendTLV((byte) (TAG_TIMER_IDENTIFIER | TAG_SET_CR), timerId);
            command.appendTLV((byte) (TAG_TIMER_VALUE | TAG_SET_CR), TIMER_VALUE, (short) 0,
                    (short) TIMER_VALUE.length);
            command.send();

            mTimerId = timerId;
            mTimerAllocated[POSITION_TIMER_VALUE] = (byte) ('0' + timerId);
            displayText(mTimerAllocated);
        } catch (Exception e) {
            ToolkitRegistry.getEntry().releaseTimer(timerId);
        }
    }

    private void displayText(byte[] buffer) {
        /*
           Command Qualifier for DISPLAY TEXT

           bit 1: 0 = normal priority;
                  1 = high priority
           bit 8: 0 = clear message after a delay;
                  1 = wait for user to clear message.
        */
        ProactiveHandler command = ProactiveHandler.getTheHandler();
        command.initDisplayText((byte) 0, DCS_8_BIT_DATA, buffer, (short) 0, (short) buffer.length);
        command.appendTLV((byte) (TAG_IMMEDIATE_RESPONSE | TAG_SET_CR), buffer, (short) 0,
                (short) 0);
        command.send();
    }
}
