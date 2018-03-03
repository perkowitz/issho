# Overview

These are instructions for setting up Issho on a portable, headless Raspberry Pi. The same 
instructions will also work for a Mac or other Unix machine; just skip the steps about 
booting the Pi directly into Hachi. Hachi should also run on Windows, but it's mostly untested.

For these instructions, I'll assume basic familiarity with Unix and the ability to find and 
install standard tools.

# Hardware

- Raspberry Pi, with 1 free USB port (2 if you want to add USB wifi for remote login).
- Novation Launchpad Pro

# Getting and running the code

## Download binaries

The most recent release of Hachi can be found in the 
[releases section](https://github.com/perkowitz/issho/releases) of the Issho repository. Download
the shaded-jar file and put it into its own directory (e.g. `~/hachi`).

## Build the repo yourself

If you prefer, you can build the application yourself from the source code.
Clone the git repo into your src directory (or wherever you like), and then build the project. 
You can build the code directly on the Pi, or build it elsewhere and copy it over.

`> mkdir ~/src`

`> cd ~/src`

`> git clone git@github.com:perkowitz/issho.git`

`> cd issho`

`> mvn package`

Issho will be built into a jar file with all dependencies, in `target/issho-NNN-shaded.jar` 
(NNN is the version number). Make a directory for the application (e.g. `~/hachi`) and
copy the jar there.

## Running the application

At a unix command line, go into your hachi directory to run the application. Hachi will save
sequence data in the directory you run it from, so always run it from here.

On a Pi, run it with this command:

`> java -cp issho-1.0.5-shaded.jar net.perkowitz.issho.hachi.Hachi hachi-pi.json`

On a Mac:

`> java -cp issho-1.0-5-shaded.jar net.perkowitz.issho.hachi.Hachi hachi-mac.json`

On Windows:
* running the app

`> java -cp issho-1.0.5-shaded.jar net.perkowitz.issho.hachi.Hachi <your-json-config>` 
* listing available midi devices:

`> java -cp issho-1.0.5-shaded.jar net.perkowitz.issho.util.FindMidiDevices` 

# Set up the Pi

To make a fully portable sequencer, you'll want to run the Pi with no monitor or keyboard, set up so 
that when you turn it on it will log in and run the sequencer automatically. To set up automatic login, 
see [these instructions](http://elinux.org/RPi_Debian_Auto_Login). To run Hachi on login,
add the above run command to the end of your .bashrc (or whatever you use). If you put the jar in
a directory like `~/hachi`, add the following to the end of your .bashrc.

`cd ~/hachi; java -cp issho-1.0-shaded.jar net.perkowitz.issho.hachi.Hachi hachi-pi.json` 


# Define your configuration

Hachi looks for a config file to tell it what MIDI devices to look for and what modules to load.
For help setting up a config file, see [the Hachi manual](hachi/hachi.md).


# Set up your hardware

- Plug the Launchpad Pro into a USB port on the Pi
- Plug the MIDI output of the Launchpad into your drum module, sampler, etc
- Plug the output of your main clock source into the MIDI input of the Launchpad
- Plug in the Pi and wait

