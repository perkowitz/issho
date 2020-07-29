# Overview

Hachi will run up to 8 "modules" on a Launchpad Pro, with its 8x8 pad grid and rows of 8 buttons. Coincidentally,
"hachi" means "8" in Japanese. All kinds of functionality can be implemented in a module; this repo includes
a rhythm sequencer, a couple of monophonic melody sequencers, and a drawing/animation program. Hachi reserves
the top row of 8 buttons to select modules (and one more button for start/stop); each module takes 
over the rest of the grid and buttons while it's in focus.

This manual describes Hachi's basic functions. Separate manuals provide
details on specific modules.

# Getting Started and Configuration

To set up your devices and run Hachi, see the [Getting Started](../getting-started.md) manual. 

Hachi uses a JSON configuration file to specify what devices and modules to use. Examples are included
in this repo, but custom config files can be created. Provide the config file path as the first
argument when running Hachi.

See [this config file](../../src/main/resources/hachi-mac.json).

The repo includes two config files, `hachi-mac.json` and `hachi-pi.json` which should work to get Hachi running 
on a Mac or Pi. Once you have Hachi running, you'll probably want to create your own config. Create a file in 
your main hachi directory similar to the above, and just provide the filename of that file as the argument 
to Hachi when you run.

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
Launchpad's MIDI out port. For example, I have my NanoKontrol programmed to element a few parameters on my 
Mutable Shruthi. While using Hachi to sequence the Shruthi, I can plug the NanoKontrol into the Pi and use
it to tweak the Shruthi's sounds.

### Knobby Configuration

A "knobby" device can be added for sending MIDI controllers to downstream MIDI modules. Adding the device to the config will tell
Hachi to route the controller's output to the Launchpad Pro's MIDI out, so that element messages can be sent to any devices
on the MIDI chain. The controller must be set up to send the desired messages; Hachi will not remap the MIDI messages in any way.
The knobby device can also define a value controller, by specifying a ```valueControlChannel``` and
```valueControlController```. These are the channel and controller number for a controller that, rather than being passed to the
MIDI out, will be used to edit values within any modules that can accept value input. This allows a knob on an external 
controller to set values like note velocity, pitch bend amount, and sequenced controller data.

```
  "devices": {
    "knobby": {
      "names":[
        "nanokontrol"
      ],
      "valueControlChannel": 14,
      "valueControlController": 16
    }
  }
```

### Keyboard Configuration

A MIDI keyboard device can be added to Hachi. Hachi will route MIDI from the keyboard into a ChordReceiver object, which monitors the currently played
notes to maintain the current chord. This chord is passed on to modules like MonoModule and StepModule, which can remap their MIDI notes to use 
only notes in the specified chord. In other words, playing a chord on the connected keyboard will cause Hachi's sequencers to play their sequences
in that key. 

The ChordReceiver may have hold enabled or disabled. When disabled, the chord will be maintained only as long as the keys are held down.
When a key is released, it is removed from the chord, and when all keys are released the chord is cleared. With hold enabled, as long as 
a note is held down, additional notes played will be added to the current chord. When all notes are released, the current chord will be 
held. The chord will be cleared when the ChordReceiver receives a MIDI controller message (any value) for the 
holdClearControllerNumber (defaults to 64, hold). Hold can be enabled or disabled in the configuration with "chordHoldEnabled" set to
true or false. The hold clear controller number can also be specified. For example, if your MIDI keyboard has a mod wheel but no hold controller,
set the controller number to 1 to use the mod wheel to clear hold.


```
  "devices": {
    "keyboard": {
      "names":[
        "LPK25"
      ],
      "chordHoldEnabled": false,
      "holdClearControllerNumber": 64
    }
  }
```

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

## Other Configuration

```
  "midiContinueAsStart": true,
  "midiSendRealtime": false,
  "debugMode": true,
  "textDisplay": false,
  "devices": {..},
  "modules": {..}
```

Setting the `midiContinueAsStart` option to `true` will cause Hachi to restart on MIDI continue messages; when it is `false`, Hachi will continue
running from the current step. When `midiSendRealtime` is `true`, Hachi will echo all MIDI realtime messages (clock, start/stop/continue, sysex)
to its output; otherwise these messages will not be sent, and other MIDI devices cannot sync to Hachi. Note that MIDI realtime messages
are not supported on Apple OSX, so this option should be `false` when running on a Mac. Enabling `debugMode` will allow you to exit Hachi
by tapping the exit button and may display additional console output; when `debugMode` is `false`, the exit button must be held down for
at least two seconds to exit. Setting `textDisplay` to `true` will display labels for Hachi's buttons and pads in the console. The labels
will update when the active module changes. `textDisplay` uses ANSI escape sequences, so may not work in all clients. Set it to `false` to disable. 


# Using Hachi

## Selecting Modules

<img width="600px" src="hachi.png"/>

Each button in Hachi's top row corresponds to one of the loaded modules. Tap the button to select
the corresponding module. A selected module will take over the rest of the Launchpad's controls -- 
the 8x8 grid, and the left, right, and bottom buttons. Each module will "redraw" the controls when
selected. All modules will continue to run even when not selected, but only the selected module can be
directly controlled (though, of course, it's possible to create a module that can element other
modules; see [Shihai](modules/shihai.md)).

## Play and Exit

The top two buttons on the left group are also reserved for main Hachi element. The top button
starts and stops the sequencer, when Hachi is the master clock. The second button exits Hachi;
hold it down for two seconds to quit.  

## Clock

Hachi includes a simple built-in clock for running clockable modules. The clock is started and stopped
by tapping the topmost button on the left group (which is also reserved for Hachi). The clock runs at a rate
of 120 beats per minute, delivering a tick to the modules for every 16th note (i.e. every 125 milliseconds).
The tempo cannot be changed from Hachi directly, but a [Shihai](modules/shihai.md) can vary the clock speed. 
