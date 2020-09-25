package net.perkowitz.issho.devices.yaeltex;

import lombok.Setter;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.apps.draw.Draw;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.Knob;
import net.perkowitz.issho.controller.elements.Pad;
import net.perkowitz.issho.controller.novation.LaunchpadPro;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;
import net.perkowitz.issho.devices.*;

import javax.sound.midi.MidiMessage;

import java.awt.Color;
import java.util.Set;


public class HachiTranslator implements GridDevice, ControllerListener {

    private YaeltexHachiXL hachi;
    @Setter private GridListener listener;


    public HachiTranslator(YaeltexHachiXL hachi) {
        this.hachi = hachi;
    }


    /***** GridDisplay implementation *****/

    public void initialize() {
        hachi.initialize();
    }

    public void initialize(boolean pads, Set<GridButton.Side> buttonSides) {
        // TODO: need to translate the pad/sides options into appropriate calls to hachi
        hachi.initialize();
    }

    public void setPad(GridPad pad, GridColor color) {
        Color c = LaunchpadPro.indexToColor(color.getIndex());
        hachi.setPad(Pad.at(YaeltexHachiXL.PADS_GROUP, pad.getY(), pad.getX()), c);
    }

    public void setButton(GridButton button, GridColor color) {
        Color c = LaunchpadPro.indexToColor(color.getIndex());
        int group = sideToGroup(button.getSide());
        hachi.setButton(Button.at(group, button.getIndex()), c);
    }

    public void setKnob(GridKnob knob, int value) {
        hachi.setKnobValue(Knob.at(YaeltexHachiXL.KNOBS_GROUP, knob.getIndex()), value);
    }

    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        if (element.getType() == Element.Type.BUTTON) {
            GridButton.Side side = groupToSide(element.getGroup());
            listener.onButtonPressed(GridButton.at(side, element.getIndex()), value);
        } else if (element.getType() == Element.Type.PAD) {
            Pad pad = (Pad) element;
            listener.onPadPressed(GridPad.at(pad.getColumn(), pad.getRow()), value);
        }
    }

    public void onElementChanged(Element element, int delta) {}

    public void onElementReleased(Element element) {
        if (element.getType() == Element.Type.BUTTON) {
            GridButton.Side side = groupToSide(element.getGroup());
            listener.onButtonReleased(GridButton.at(side, element.getIndex()));
        } else if (element.getType() == Element.Type.PAD) {
            Pad pad = (Pad) element;
            listener.onPadReleased(GridPad.at(pad.getColumn(), pad.getRow()));
        }
    }


    /***** Receiver implementation *****/

    public void send(MidiMessage message, long timeStamp) {
        hachi.send(message, timeStamp);
    }

    public void close() {
        hachi.close();
    }


    /***** private implementation *****/

    private GridButton.Side groupToSide(int group) {
        switch (group) {
            case YaeltexHachiXL.BUTTONS_TOP:
                return GridButton.Side.Top;
            case YaeltexHachiXL.BUTTONS_BOTTOM:
                return GridButton.Side.Bottom;
            case YaeltexHachiXL.BUTTONS_LEFT:
                return GridButton.Side.Left;
            case YaeltexHachiXL.BUTTONS_RIGHT:
                return GridButton.Side.Right;
        }
        return GridButton.Side.Top;
    }

    private int sideToGroup(GridButton.Side side) {
        switch (side) {
            case Top:
                return YaeltexHachiXL.BUTTONS_TOP;
            case Bottom:
                return YaeltexHachiXL.BUTTONS_BOTTOM;
            case Left:
                return YaeltexHachiXL.BUTTONS_LEFT;
            case Right:
                return YaeltexHachiXL.BUTTONS_RIGHT;
        }
        return YaeltexHachiXL.BUTTONS_TOP;
    }


}
