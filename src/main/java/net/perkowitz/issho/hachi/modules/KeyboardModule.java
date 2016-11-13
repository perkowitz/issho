package net.perkowitz.issho.hachi.modules;

import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Graphics;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * Created by optic on 9/12/16.
 */
public class KeyboardModule extends MidiModule {

    private static int HIGHLIGHT_NOTE_INDEX = 0;
    private static Color COLOR_HIGHLIGHT = Color.DARK_GRAY;

    private int midiChannel = 0;
    private int lowNote = 36;

    public KeyboardModule(Transmitter inputTransmitter, Receiver outputReceiver, int midiChannel, int lowNote) {
        super(inputTransmitter, outputReceiver);
        this.midiChannel = midiChannel;
        this.lowNote = lowNote;
    }


    /***** Module interface ****************************************/

    public void redraw() {
        for (int i = 0; i < 64; i++) {
            if ((i + lowNote) % 12 == HIGHLIGHT_NOTE_INDEX) {
                int x = i % 8;
                int y = 7 - i / 8;
                display.setPad(GridPad.at(x, y), COLOR_HIGHLIGHT);
            }
        }
    }


    /***** GridListener interface ****************************************/

    public void onPadPressed(GridPad pad, int velocity) {
//        System.out.printf("KeyboardModule: onPadPressed %s, %d\n", pad, velocity);
        int note = (7-pad.getY()) * 8 + pad.getX() + lowNote;
        this.sendMidiNote(midiChannel, note, velocity);
        display.setPad(pad, Color.fromIndex(velocity));
    }

    public void onPadReleased(GridPad pad) {
//        System.out.printf("KeyboardModule: onPadReleased %s\n", pad);
        int note = (7-pad.getY()) * 8 + pad.getX() + lowNote;
        this.sendMidiNote(midiChannel, note, 0);

        if (note % 12 == HIGHLIGHT_NOTE_INDEX) {
            display.setPad(pad, COLOR_HIGHLIGHT);
        } else {
            display.setPad(pad, Color.OFF);
        }
    }

    public void onButtonPressed(GridButton button, int velocity) {

    }

    public void onButtonReleased(GridButton button) {

    }



}
