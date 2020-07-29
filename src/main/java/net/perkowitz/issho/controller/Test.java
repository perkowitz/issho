package net.perkowitz.issho.controller;

import javax.sound.midi.MidiMessage;
import java.awt.*;

/**
 * Created by mikep on 7/28/20.
 */
public class Test {

    public static void main(String args[]) throws Exception {

        TestController t = new TestController(new TestListener());

        for (int group = 0; group < 8; group++) {
            for (int index = 0; index < 4; index++) {
                t.setButton(ButtonElement.at(group, index), new Color(index, index, index));
//                t.setPad(PadElement.at(group, index), new Color(index, index, index));
//                t.setLight(LightElement.at(group, index), new Color(index, index, index));
//                t.setKnob(KnobElement.at(group, index), new Color(index, index, index));
            }
        }

    }

}
