# Overview

These are instructions for setting up Issho on a portable, headless Raspberry Pi. The same 
instructions will also work for a Mac or other Unix machine; just skip the steps about 
booting the Pi directly into Hachi. Hachi should also run on Windows, but it's untested.

For these instructions, I'll assume basic familiarity with Unix and the ability to find and 
install standard tools.

# Hardware

- Raspberry Pi, with 1 free USB port (2 if you want to add USB wifi for remote login).
- Novation Launchpad Pro

# Build the Code

Clone the git repo into your src directory (or wherever you like), and then build the project. 
You can build the code directly on the Pi, or build it elsewhere and copy it over.

`> mkdir ~/src`

`> cd ~/src`

`> git clone git@github.com:perkowitz/issho.git`

`> cd issho`

`> mvn package`

Issho will be built into a jar file with all dependencies, in `target/issho-NNN-shaded.jar` 
(NNN is the version number). You can run it using the `hachi` command in the root directory of the repo. 
If you wish to install it somewhere else, copy the script, the jar, and the `sample.properties` 
file, but be sure to edit the script to point to the copied jar. If you build the code elsewhere, 
copy the script and jar to your Pi.

For example, copy the script, jar, settings, and properties to your `~/bin` directory, and edit the script like this:

`#!/bin/sh`

`java -cp ~/bin/issho-1.0-shaded.jar net.perkowitz.issho.hachi.Hachi sample.properties`

# Set up the Pi

To make a fully portable sequencer, you'll want to run the Pi with no monitor or keyboard, set up so 
that when you turn it on it will log in and run the sequencer automatically. To set up automatic login, 
see [these instructions](http://elinux.org/RPi_Debian_Auto_Login). To run the sequencer on login, 
add the script to the end of your .bashrc (or whatever you use). If you copied the script and JAR 
to your `~/bin` directory, for example, add `bin/seq` to your .bashrc.

# Set up your hardware

- Plug the Launchpad Pro into a USB port on the Pi
- Plug the MIDI output of the Launchpad into your drum module, sampler, etc
- Plug the output of your main clock source into the MIDI input of the Launchpad
- Plug in the Pi and wait

# Define your properties

Sequence looks for a properties file to define the names and ports of the MIDI devices it will use. 
If you wish to override the default settings, you can provide a path to a properties file as an 
argument. See the `seq` script in the root of the repo. 

To run Hachi, you may need to edit the `sample.properties` file to provide the name of the Launchpad
device. If Issho can't find a matching device, it will print a list of the devices found. In the 
properties file, you can provide one or more strings (case insensitive) to match against the device 
name or description; all the provided strings must be found. Note that different machines may report 
device names and descriptions differently (and the names may be changed with midi settings), 
so this may take some trial and error.

If the specified device isn't found, you'll see something like this:

`Getting app settings from sample.properties..`

`Setting memory sizes..`

`Finding controller device..`

`Loading device info..`

`Found midi device: MIDISPORT 8x8/s 1`

`Found midi device: MIDISPORT 8x8/s 2`

`Unable to find controller input device matching name: Launchpad`

If the devices are found, the buttons and pads on the Launchpad will light up and you'll be ready to go!

