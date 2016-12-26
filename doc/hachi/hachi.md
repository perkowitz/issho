# Overview

Hachi will run up to 8 "modules" on a Launchpad Pro, with its 8x8 pad grid and rows of 8 buttons. Coincidentally,
"hachi" means "8" in Japanese. All kinds of functionality can be implemented in a module; this repo includes
a rhythm sequencer, a couple of monophonic melody sequencers, and a drawing/animation program. Hachi reserves
the top row of 8 buttons to select modules (and one more button for start/stop); each module takes 
over the rest of the grid and buttons while it's in focus.

This manual describes Hachi's basic functions. Separate manuals will provide
details on specific modules.

# Getting Started and Configuration

To set up your devices and run Hachi, see the [Getting Started](../getting-started.md) manual. 

Hachi uses a JSON configuration file to specify what devices and modules to use. Examples are included
in this repo, but custom config files can be created. Provide the config file path as the first
argument when running Hachi.

See [this sample config file](../../src/main/resources/hachi-mac.json).

## Device Configuration

The device configuration section specifies MIDI devices to look for and what to look for in their name and description
fields. Note that the same MIDI device may appear with different descriptions on difference hosts, so device
configuration may need to be customized for each host. A device like the Novation Launchpad will have multiple
logical ports visible to the host, with various names.

Here's an example device configuration, specifying how to find the Launchpad device itself and how to address
its MIDI ports. This configuration works well on a Mac.

```
  "devices": {
    "controller": {
      "names": [
        "Launchpad",
        "Standalone"
        ]
    },
    "midi": {
      "names":[
        "Launchpad Pro",
        "Midi Port"
        ]
    },
    "knobby": {
      "names":[
        "nanokontrol"
      ]
    }
  }
```

Here's a similar configuration for a Raspberry Pi. For whatever reason, the Launchpad's 
MIDI ports appear with different names on the Pi. 

```
  "devices": {
    "controller": {
      "names": [
        "Launchpad",
        "1,0,1"
        ]
    },
    "midi": {
      "names":[
        "Launchpad Pro",
        "1,0,2"
        ]
    }
  }
```

If Hachi can't find MIDI devices matching those defined in the configuration, it will print a list of
devices found, to help you correct the configuration.

Knobby, shown in the first configuration above, simply allows a MIDI controller to be routed to the 
Launchpad's MIDI out port. For example, I have my NanoKontrol programmed to control a few parameters on my 
Mutable Shruthi. While using Hachi to sequence the Shruthi, I can plug the NanoKontrol into the Pi and use
it to tweak the Shruthi's sounds.


## Module Configuration

Here's an example module configuration. The configuration includes a list of up to 8 modules, which will
correspond to the 8 top buttons, from left to right. Every module's class must be specified, and some will
have additional options. For example, modules that save data will typically take a file prefix for naming
the data files. 

```
{
  "modules": [
    {
      "class": "ShihaiModule"
    },
    {
      "class": "DrawingModule",
      "filePrefix": "drawing"
    },
    {
      "class": "RhythmModule",
      "filePrefix": "rhythm1"
    },
    {
      "class": "RhythmModule",
      "filePrefix": "rhythm2",
      "palette": "red",
      "midiNoteOffset": 24
    },
    {
      "class": "MonoModule",
      "filePrefix": "mono1"
    },
    {
      "class": "MonoModule",
      "filePrefix": "mono2",
      "palette": "orange"
    },
    {
      "class": "PaletteModule"
    }
  ]
}
```

# Selecting Modules

<img width="600px" src="hachi.png"/>

Each button in Hachi's top row corresponds to one of the loaded modules. Tap the button to select
the corresponding module. A selected module will take over the rest of the Launchpad's controls -- 
the 8x8 grid, and the left, right, and bottom buttons. Each module will "redraw" the controls when
selected. All modules will continue to run even when not selected, but only the selected module can be
directly controlled (though, of course, it's possible to create a module that can control other
modules; see [Shihai](modules/shihai.md)).

# Clock

Hachi includes a simple built-in clock for running clockable modules. The clock is started and stopped
by tapping the topmost button on the left side (which is also reserved for Hachi). The clock runs at a rate
of 120 beats per minute, delivering a tick to the modules for every 16th note (i.e. every 125 milliseconds).
The tempo cannot be changed from Hachi directly, but a [Shihai](modules/shihai.md) can vary the clock speed. 
