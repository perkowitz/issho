package net.perkowitz.issho.controller.apps.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.*;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import java.awt.*;


public class YaeltexHachiTranslator implements HachiController, ControllerListener {

    private static final int LOG_LEVEL = Log.OFF;

    private static final int MODULE_BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_TOP;
    private static final int MAIN_BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_LEFT;
    private static final int SHIHAI_BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_LEFT;
    private static final int KNOB_MODE_BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_RIGHT;
    private static final int KNOBS_GROUP = YaeltexHachiXL.KNOBS_GROUP;
    private static final int KNOB_BUTTONS_GROUP = YaeltexHachiXL.KNOB_BUTTONS;
    private static final int PADS_GROUP = YaeltexHachiXL.PADS_GROUP;
    private static final int CLOCK_ROW = 0;
    private static final int MODULE_BUTTONS_GROUP_0 = YaeltexHachiXL.BUTTONS_LEFT;
    private static final int MODULE_BUTTONS_GROUP_1 = YaeltexHachiXL.BUTTONS_RIGHT;
    private static final int MODULE_BUTTONS_GROUP_2 = YaeltexHachiXL.BUTTONS_BOTTOM;

    // top module select and mute buttons
    private static ElementSet moduleSelectButtons = ElementSet.buttons(MODULE_BUTTONS_GROUP, 0, 7);
    private static ElementSet moduleMuteButtons = ElementSet.buttons(MODULE_BUTTONS_GROUP, 8, 15);

    // various main buttons and shihai buttons
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

    // knobs and knob modes
    private static Element knobModeMainButton = Button.at(KNOB_MODE_BUTTONS_GROUP, 0);
    private static Element knobModeShihaiButton = Button.at(KNOB_MODE_BUTTONS_GROUP, 8);
    private static Element knobModeModule1Button = Button.at(KNOB_MODE_BUTTONS_GROUP, 1);
    private static Element knobModeModule2Button = Button.at(KNOB_MODE_BUTTONS_GROUP, 9);
    private static ElementSet knobModeButtons = new ElementSet(Lists.newArrayList(
            knobModeMainButton, knobModeShihaiButton, knobModeModule1Button, knobModeModule2Button));
    private static ElementSet knobs = ElementSet.knobs(KNOBS_GROUP, 0, 7);
    private static ElementSet knobButtons = ElementSet.buttons(KNOB_BUTTONS_GROUP, 0, 7);

    // clock mode
    private static ElementSet clockPads = ElementSet.pads(PADS_GROUP,
            CLOCK_ROW, CLOCK_ROW, 0, YaeltexHachiXL.PADS_MAX_COLUMNS);

    // module elements: pads and all the other buttons
    private static ElementSet pads = ElementSet.pads(PADS_GROUP, 0, YaeltexHachiXL.PADS_MAX_ROWS, 0, YaeltexHachiXL.PADS_MAX_COLUMNS);
    private static ElementSet moduleButtonsLeft1 = ElementSet.buttons(YaeltexHachiXL.BUTTONS_LEFT, 3, 7);
    private static ElementSet moduleButtonsLeft2 = ElementSet.buttons(YaeltexHachiXL.BUTTONS_LEFT, 11, 15);
    private static ElementSet moduleButtonsRight1 = ElementSet.buttons(YaeltexHachiXL.BUTTONS_RIGHT, 2, 7);
    private static ElementSet moduleButtonsRight2 = ElementSet.buttons(YaeltexHachiXL.BUTTONS_RIGHT, 10, 15);
    private static ElementSet moduleButtonsBottom = ElementSet.buttons(YaeltexHachiXL.BUTTONS_BOTTOM, 0, 11);

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

    public void clear() { hachi.initialize(); }

    public void close() {
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

    public void setButton(Button button, Color color) {
        hachi.setButton(button, color);
    }

    public void setPad(Pad pad, Color color) {
        if (pad.getRow() >= 0 && pad.getRow() < YaeltexHachiXL.PADS_MAX_ROWS && pad.getRow() != CLOCK_ROW
                && pad.getColumn() >= 0 && pad.getColumn() < YaeltexHachiXL.PADS_MAX_COLUMNS) {
            hachi.setPad(pad, color);
        }
    }

    public void setKnobValue(int group, int index, int value) {
        hachi.setKnobValue(Knob.at(YaeltexHachiXL.KNOBS_GROUP, index), value);
    }
    public void setKnobColor(int group, int index, Color color) {
        hachi.setKnob(Knob.at(YaeltexHachiXL.KNOBS_GROUP, index), color);
    }

    public void setModulePad(int row, int column, Color color) {
        hachi.setPad(Pad.at(PADS_GROUP, row, column), color);
    }

    public void setModuleButton(int group, int index, Color color) {
        int g = 0;
        int i = index;
        boolean found = false;
        switch (group) {
            case 0:
                // left buttons, but the top 6 buttons are reserved for other things
                g = MODULE_BUTTONS_GROUP_0;
                if (i >= 0 && i < 5) {
                    i += 3;
                    found = true;
                } else if (i < 10) {
                    i += 6;
                    found = true;
                }
                break;
            case 1:
                // right buttons, but the top 4 buttons are reserved for other things
                g = MODULE_BUTTONS_GROUP_1;
                if (i >= 0 && i < 6) {
                    i += 2;
                    found = true;
                } else if (i < 12) {
                    i += 4;
                    found = true;
                }
                break;
            case 2:
                // bottom buttons
                g = MODULE_BUTTONS_GROUP_2;
                if (i >= 0 && i < 12) {
                    found = true;
                }
                break;
        }
        if (found) {
            hachi.setButton(Button.at(g, i), color);
        }
    }


    public void flush() {
        hachi.flush();
    }


    public void showClock(int measure, int beat, int pulse, Color measureColor, Color stepColor, Color offColor) {
//        int s = step % YaeltexHachiXL.PADS_MAX_COLUMNS;
//        int lastStep = (s + YaeltexHachiXL.PADS_MAX_COLUMNS - 1) % YaeltexHachiXL.PADS_MAX_COLUMNS;
//        int m = measure % YaeltexHachiXL.PADS_MAX_COLUMNS;
//        int lastMeasure = (m + YaeltexHachiXL.PADS_MAX_COLUMNS - 1) % YaeltexHachiXL.PADS_MAX_COLUMNS;
//        hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, lastStep), offColor);
//        hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, lastMeasure), offColor);
//        hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, s), stepColor);
//        hachi.setPad(Pad.at(PADS_GROUP, CLOCK_ROW, m), measureColor);
//
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

    public int MODULE_COUNT() { return 8; }
    public int MAIN_COUNT() { return 6; };
    public int SHIHAI_COUNT() { return 2; }
    public int KNOB_COUNT() { return 8; }
    public int KNOB_MODE_COUNT() { return 4; }
    public int PAD_ROWS_COUNT() { return 8; }
    public int PAD_COLUMNS_COUNT() { return 16; }
    public int BUTTON_GROUPS_COUNT() { return 3; }
    public int BUTTONS_COUNT(int group) {
        switch (group) {
            case 0:
                return 10;
            case 1:
                return 12;
            case 2:
                return 12;
            default:
                return 0;
        }
    }


    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        Log.log(this, LOG_LEVEL, "%s %d", element, value);
        if (moduleSelectButtons.contains(element)) {
            listener.onModuleSelectPressed(moduleSelectButtons.getIndex(element));
        } else if (moduleMuteButtons.contains(element)) {
            listener.onModuleMutePressed(moduleMuteButtons.getIndex(element));
        } else if (mainButtons.contains(element)) {
            listener.onMainButtonPressed(mainButtons.getIndex(element));
        } else if (knobModeButtons.contains(element)) {
            listener.onKnobModePressed(knobModeButtons.getIndex(element));
        } else if (pads.contains(element)) {
            Pad pad = (Pad)element;
            listener.onModulePadPressed(pad.getRow(), pad.getColumn(), value);
        } else if (moduleButtonsLeft1.contains(element)) {
            Log.log(this, LOG_LEVEL, "left1 %d:%d", MODULE_BUTTONS_GROUP_0, moduleButtonsLeft1.getIndex(element));
            listener.onModuleButtonPressed(0, moduleButtonsLeft1.getIndex(element), value);
        } else if (moduleButtonsLeft2.contains(element)) {
            Log.log(this, LOG_LEVEL, "left2 %d:%d", MODULE_BUTTONS_GROUP_0, moduleButtonsLeft2.getIndex(element) + 5);
            listener.onModuleButtonPressed(0, moduleButtonsLeft2.getIndex(element) + 5, value);
        } else if (moduleButtonsRight1.contains(element)) {
            Log.log(this, LOG_LEVEL, "right1 %d:%d", MODULE_BUTTONS_GROUP_1, moduleButtonsRight1.getIndex(element));
            listener.onModuleButtonPressed(1, moduleButtonsRight1.getIndex(element), value);
        } else if (moduleButtonsRight2.contains(element)) {
            Log.log(this, LOG_LEVEL, "right2 %d:%d", MODULE_BUTTONS_GROUP_1, moduleButtonsRight2.getIndex(element) + 6);
            listener.onModuleButtonPressed(1, moduleButtonsRight2.getIndex(element) + 6, value);
        } else if (moduleButtonsBottom.contains(element)) {
            Log.log(this, LOG_LEVEL, "bottom %d:%d", MODULE_BUTTONS_GROUP_2, moduleButtonsBottom.getIndex(element));
            listener.onModuleButtonPressed(2, moduleButtonsBottom.getIndex(element), value);
        }
    }


    public void onElementChanged(Element element, int delta) {}

    public void onElementReleased(Element element) {}

}
