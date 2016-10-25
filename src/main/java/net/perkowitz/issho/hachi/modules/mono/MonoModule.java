package net.perkowitz.issho.hachi.modules.mono;

import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.modules.BasicModule;
import net.perkowitz.issho.hachi.modules.MidiModule;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmController;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmDisplay;
import net.perkowitz.issho.hachi.modules.rhythm.SequencerReceiver;
import net.perkowitz.issho.hachi.modules.rhythm.models.Memory;
import net.perkowitz.issho.hachi.modules.rhythm.models.Track;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

/**
 * Created by optic on 10/24/16.
 */
public class MonoModule extends MidiModule implements Module, Clockable, GridListener {

    private int midiChannel = 10;


    public MonoModule(Transmitter inputTransmitter, Receiver outputReceiver) {
        // connect the provided midi input to the sequencer's clock receiver
        this.inputTransmitter = inputTransmitter;
        this.inputTransmitter.setReceiver(this);

        // where to send the sequencer's midi output
        this.outputReceiver = outputReceiver;

    }



    /***** Module implementation *********************************************************************/

//    public void open() {}
//
//    public void close() {}
//
//    public GridListener getGridListener() {
//        return this;
//    }
//
//    public Memory getMemory() { return null; }
//
//    public void Save() {}
//
//    public void Load() {}


    /***** GridListener interface ****************************************/

    public void onPadPressed(GridPad pad, int velocity) {
//        System.out.printf("MonoModule: onPadPressed %s, %d\n", pad, velocity);
        int note = (7-pad.getY()) * 8 + pad.getX() + 36;
        this.sendMidiNote(midiChannel, note, velocity);
        display.setPad(pad, Color.fromIndex(velocity));
    }

    public void onPadReleased(GridPad pad) {
//        System.out.printf("MonoModule: onPadReleased %s\n", pad);
        int note = (7-pad.getY()) * 8 + pad.getX() + 36;
        this.sendMidiNote(midiChannel, note, 0);
        display.setPad(pad, Color.OFF);
    }

    public void onButtonPressed(GridButton button, int velocity) {

    }

    public void onButtonReleased(GridButton button) {

    }


    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        if (restart) {
        }
    }

    public void stop() {
    }

    public void tick() {
    }






}
