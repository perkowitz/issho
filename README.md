# Issho
A collection of standalone music tools.

# Hachi
A multi-function midi sequencer that runs on a Novation Launchpad Pro and a headless computer 
like a Raspberry Pi. Hachi is written in Java, and so can run on a Pi (tested), 
a Mac (tested), and many other platforms (untested), with or without keyboard and monitor.

Hachi (= "eight" in Japanese) can run up to 8 "modules" at a time. Available modules 
include several midi sequencers, a paint/animation toy, and a sequencer-controller that 
lets you coordinate your sequencers. Hachi is a generalized version 
of [this simpler sequencer](https://github.com/perkowitz/sequence) 
that I wrote for the original Launchpad. 
That basic sequencer runs as one module in Hachi.

# Documentation

- [Getting Started](doc/getting-started.md): how to set up your computer and MIDI devices to run Issho applications.
- [Hachi](doc/hachi/hachi.md): how to use Hachi, and how to configure and use various modules.
- [Hachi Development](doc/hachi/development.md): details about Hachi's code and how to write your own modules. 

# Issues

Tracking bugs, needed improvements, and all-new features using [issues](https://github.com/perkowitz/issho/issues) 
here in Github, but also via [HuBoard](https://huboard.com/perkowitz/issho#/milestones). 

# Release notes

## 2017-11-26 v1.0.5
- In Beatbox, combine jump and play modes into a single mode. Add momentary pitch control and measure clock.
- In Shihai, a fill button that tells all Sessionizeable modules to play a fill.
- In Paraphonic, add controller sequencing.
- Add ValueSettable, which allows an external midi controller to be used for setting values (in addition
    to the array of 8 buttons).
- Fix bug in displaying midi channel in Settings.
- Send midi program change on session load; values set in config.
- In Paraphonic, replace pattern clear with edit select, to allow editing of patterns that aren't currently playing.

## 2017-09-13 v1.0.4
- In Beatbox, add auto-generated fill patterns that are played when holding down the fill button
- In Beatbox, add sequencing of pitch bend per pattern step
- In Beatbox, fix bug where chain wouldn't reset on stop/start

## 2017-09-06 v1.0.3
- In Step, if multiple note modifiers are added to a stage, play the notes one at a time, from low to high
- Added framework for different multi-note modes, but implemented only low-to-high

## 2017-09-02 v1.0.2
- Session copy/clear from settings screen for Beatbox & Para
- Follow 24ppqn MIDI clock, enable swing by MIDI pulse
- Allow multiple devices to control Hachi simultaneously
- Beatbox replaces Rhythm
- Minibeat module
- Memory management app

## 2017-01-29: v1.0.1

- Add configurable keyboard device
- Added Chords, ChordReceiver, ChordModule to track chords from keyboard device and apply chords to outgoing MIDI notes
- Updated MonoModule and StepModule to extend ChordModule so they follow chords
- Added chord options to keyboard configuration
- Updated doc to cover chords and keyboard configuration
- Added tests for Chords

# Hachi

Here is our friend, the other Hachi: 

![The Other Hachi](doc/hachi/hachi-face.jpg)