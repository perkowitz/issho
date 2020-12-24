/**
 * MidiModule
 *
 * Interface for a module that can send MIDI.
 */
package net.perkowitz.issho.controller.apps.hachi.modules;

public interface MidiModule {

    int getMidiChannel();
    void setMidiChannel(int channel);
}
