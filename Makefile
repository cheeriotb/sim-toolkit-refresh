SIMTOOLS_DIR        = ../osmocom-sim-tools

PACKAGE_AID         = 0xD0:0x70:0x02:0xCA:0x44:0x90:0x01
PACKAGE_NAME        = com.github.cheeriotb.toolkit.refresh
PACKAGE_VERSION     = 1.00

APPLET_AID          = 0xD0:0x70:0x02:0xCA:0x44:0x90:0x01:0x02
APPLET_NAME         = com.github.cheeriotb.toolkit.refresh.RefreshApplet

SOURCES             = ./src/com/github/cheeriotb/toolkit/refresh/*.java

BUILD_DIR           = ./build
BUILD_CLASSES_DIR   = $(BUILD_DIR)/classes
BUILD_JAVACARD_DIR  = $(BUILD_DIR)/javacard
JAVACARD_SDK_DIR    ?= $(SIMTOOLS_DIR)/javacard
JAVACARD_EXPORT_DIR ?= $(JAVACARD_SDK_DIR)/api21_export_files

ifdef COMSPEC
CLASSPATH           = $(JAVACARD_SDK_DIR)/lib/api21.jar;$(JAVACARD_SDK_DIR)/lib/sim.jar
else
CLASSPATH           = $(JAVACARD_SDK_DIR)/lib/api21.jar:$(JAVACARD_SDK_DIR)/lib/sim.jar
endif

JFLAGS              = -target 1.1 -source 1.3 -J-Duser.language=en -g -d $(BUILD_CLASSES_DIR) -classpath "$(CLASSPATH)"
JAVA                ?= java
JC                  ?= javac

.SUFFIXES: .java .class
.java.class:
	mkdir -p $(BUILD_CLASSES_DIR)
	mkdir -p $(BUILD_JAVACARD_DIR)

	$(JC) $(JFLAGS) $*.java

	$(JAVA) -jar $(JAVACARD_SDK_DIR)/bin/converter.jar    \
		-d $(BUILD_JAVACARD_DIR)                          \
		-classdir $(BUILD_CLASSES_DIR)                    \
		-exportpath $(JAVACARD_EXPORT_DIR)                \
		-applet $(APPLET_AID) $(APPLET_NAME)              \
		$(PACKAGE_NAME) $(PACKAGE_AID) $(PACKAGE_VERSION)

default: classes

classes: $(SOURCES:.java=.class)

clean:
	$(RM) -rf $(BUILD_DIR)
