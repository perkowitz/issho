package net.perkowitz.issho.controller;

import java.awt.*;

/**
 * Created by mikep on 7/28/20.
 */
public class Test {

    public static void main(String args[]) throws Exception {

        TestController t = new TestController(new TestListener());

        for (int group = 0; group < 8; group++) {
            for (int index = 0; index < 4; index++) {
                t.setButton(net.perkowitz.issho.controller.elements.Button.at(group, index), new Color(index, index, index));
//                t.setPad(Pad.at(group, index), new Color(index, index, index));
//                t.setLight(Light.at(group, index), new Color(index, index, index));
//                t.setKnob(Knob.at(group, index), new Color(index, index, index));
            }
        }

    }

}
