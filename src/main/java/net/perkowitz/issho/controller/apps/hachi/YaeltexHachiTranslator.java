package net.perkowitz.issho.controller.apps.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.elements.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import java.awt.*;

import static net.perkowitz.issho.controller.Colors.BLACK;


public class YaeltexHachiTranslator implements HachiController, ControllerListener {

    private static final int MODULE_BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_TOP;
    private static final int MAIN_BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_LEFT;
    private static final int SHIHAI_BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_LEFT;
    private static final int KNOB_MODE_BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_RIGHT;
    private static final int KNOBS_GROUP = YaeltexHachiXL.KNOBS_GROUP;
    private static final int KNOB_BUTTONS_GROUP = YaeltexHachiXL.KNOB_BUTTONS;
    private static final int PADS_GROUP = YaeltexHachiXL.PADS_GROUP;
    private static final int CLOCK_ROW = 0;

    private static ElementSet moduleSelectButtons = ElementSet.buttons(MODULE_BUTTONS_GROUP, 0, 7);
    private static ElementSet moduleMuteButtons = ElementSet.buttons(MODULE_BUTTONS_GROUP, 8, 15);
    private static Element mainPlayButton = Button.at(MAIN_BUTTONS_GROUP, 0);
    private static Element mainClockResetButton = Button.at(MAIN_BUTTONS_GROUP, 1);
    private static Element mainExitButton = Button.at(MAIN_BUTTONS_GROUP, 8);
    private static Element mainPanicButton = Button.at(MAIN_BUTTONS_GROUP, 9);
    private static ElementSet mainButtons = new ElementSet(Lists.newArrayList(
            mainPlayButton, mainClockResetButton, mainExitButton, mainPanicButton));
    private static Element shihaiSelectButton = Button.at(SHIHAI_BUTTONS_GROUP, 2);
    private static Element shihaiFillButton = Button.at(SHIHAI_BUTTONS_GROUP, 10);
    private static ElementSet shihaiButtons = new ElementSet(Lists.newArrayList(
            shihaiSelectButton, shihaiFillButton));
    private static Element knobModeMainButton = Button.at(KNOB_MODE_BUTTONS_GROUP, 0);
    private static Element knobModeShihaiButton = Button.at(KNOB_MODE_BUTTONS_GROUP, 8);
    private static Element knobModeModule1Button = Button.at(KNOB_MODE_BUTTONS_GROUP, 1);
    private static Element knobModeModule2Button = Button.at(KNOB_MODE_BUTTONS_GROUP, 9);
    private static ElementSet knobModeButtons = new ElementSet(Lists.newArrayList(
            knobModeMainButton, knobModeShihaiButton, knobModeModule1Button, knobModeModule2Button));
    private static ElementSet knobs = ElementSet.knobs(KNOBS_GROUP, 0, 7);
    private static ElementSet knobButtons = ElementSet.buttons(KNOB_BUTTONS_GROUP, 0, 7);
    private static ElementSet clockPads = ElementSet.pads(PADS_GROUP,
            CLOCK_ROW, CLOCK_ROW, 0, YaeltexHachiXL.PADS_MAX_COLUMNS);


    private YaeltexHachiXL hachi;
    private HachiListener listener;

    public YaeltexHachiTranslator(YaeltexHachiXL hachi, HachiListener listener) {
        this.hachi = hachi;
        this.listener = listener;
    }



    /***** HachiController implementation *****/

    public void initialize() {
        hachi.initialize();
    }

    public void close() {
        hachi.close();
    }

    public void setModuleSelect(int index, Color color) {
        hachi.setButton((Button)moduleSelectButtons.get(index), color);
    }

    public void setModuleMute(int index, Color color) {
        hachi.setButton((Button)moduleMuteButtons.get(index), color);
    }

    public void setMainButton(int index, Color color) {
        hachi.setButton((Button)mainButtons.get(index), color);
    }

    public void setShihaiButton(int index, Color color) {
        hachi.setButton((Button)shihaiButtons.get(index), color);
    }

    public void setKnobValue(int index, int value) {
        hachi.setKnobValue((Knob)knobs.get(index), value);
    }

    public void setKnobColor(int index, Color color) {
        hachi.setButton((Button)knobButtons.get(index), color);
    }

    public void setKnobModeButton(int index, Color color) {
        hachi.setButton((Button)knobModeButtons.get(index), color);
    }

    public void setPad(Pad pad, Color color) {
        if (pad.getRow() >= 0 && pad.getRow() < YaeltexHachiXL.PADS_MAX_ROWS && pad.getRow() != CLOCK_ROW
                && pad.getColumn() >= 0 && pad.getColumn() < YaeltexHachiXL.PADS_MAX_COLUMNS) {
            hachi.setPad(pad, color);
        }
    }

    public void showClock(int measure, int step, Color measureColor, Color stepColor, Color offColor) {
        int s = step % YaeltexHachiXL.PADS_MAX_COLUMNS;
        int lastStep = (s + YaeltexHachiXL.PADS_MAX_COLUMNS - 1) % YaeltexHachiXL.PADS_MAX_COLUMNS;
        int m = measure % YaeltexHachiXL.PADS_MAX_COLUMNS;
        int lastMeasure = (m + YaeltexHachiXL.PADS_MAX_COLUMNS - 1) % YaeltexHachiXL.PADS_MAX_COLUMNS;
        hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, lastStep), offColor);
        hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, lastMeasure), offColor);
        hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, s), stepColor);
        hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, m), measureColor);

//        for (int c = 0; c < YaeltexHachiXL.PADS_MAX_COLUMNS; c++) {
//            Color color = BLACK;
//            if (s == c && m == c) {
//                color = bothColor;
//            } else if (m == c) {
//                color = measureColor;
//            } else if (s == c) {
//                color = stepColor;
//            }
//            hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, c), color);
//        }
    }

    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        if (moduleSelectButtons.contains(element)) {
            listener.onModuleSelectPressed(moduleSelectButtons.getIndex(element));
        } else if (moduleMuteButtons.contains(element)) {
            listener.onModuleMutePressed(moduleMuteButtons.getIndex(element));
        } else if (mainButtons.contains(element)) {
            listener.onMainButtonPressed(mainButtons.getIndex(element));
        } else if (knobModeButtons.contains(element)) {
            listener.onKnobModePressed(knobModeButtons.getIndex(element));
        }
    }


    public void onElementChanged(Element element, int delta) {}

    public void onElementReleased(Element element) {}

}
