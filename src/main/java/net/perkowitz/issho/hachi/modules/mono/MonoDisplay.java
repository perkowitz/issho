package net.perkowitz.issho.hachi.modules.mono;

import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;
import java.util.Map;


/**
 * Created by optic on 10/25/16.
 */
public class MonoDisplay {

    @Setter private GridDisplay display;
    private List<Color> palette = MonoUtil.PALETTE_FUCHSIA;


    public MonoDisplay(GridDisplay display) {
        this.display = display;
    }

    public void redraw(MonoMemory memory) {
        drawKeyboard();
        drawSteps(memory.currentPattern().getSteps());
        drawModes(memory.getStepEditMode());
    }

    public void drawKeyboard() {

        for (int x = 0; x < 8; x++) {
            if (x != 0 && x != 3 && x != 7) {
                display.setPad(GridPad.at(x, MonoUtil.KEYBOARD_MIN_ROW), palette.get(MonoUtil.COLOR_KEYBOARD_BLACK));
            }
            display.setPad(GridPad.at(x, MonoUtil.KEYBOARD_MAX_ROW), palette.get(MonoUtil.COLOR_KEYBOARD_WHITE));
        }
    }


    public void drawSteps(MonoStep[] steps) {
        for (int index = 0; index < steps.length; index++) {
            drawStep(steps[index]);
        }
    }

    public void drawStep(MonoStep step) {
        drawStep(step, false);
    }

    public void drawStep(MonoStep step, boolean highlight) {

        // get step location
        int x = step.getIndex() % 8;
        int y = step.getIndex() / 8 + MonoUtil.STEP_MIN_ROW;

        // get keyboard location
        int octaveNote = step.getNote() % 12;
        int index = MonoUtil.KEYBOARD_NOTE_TO_INDEX[octaveNote];
        int keyX = index % 8;
        int keyY = MonoUtil.KEYBOARD_MIN_ROW + index / 8;

        Color stepColor = palette.get(MonoUtil.COLOR_STEP_OFF);
        if (highlight) {
            stepColor = palette.get(MonoUtil.COLOR_STEP_HIGHLIGHT);
        } else if (step.isSelected()) {
            display.setPad(GridPad.at(keyX, keyY), palette.get(MonoUtil.COLOR_KEYBOARD_SELECTED));
            stepColor = palette.get(MonoUtil.COLOR_STEP_SELECTED);
        } else if (step.isEnabled()) {
            stepColor = palette.get(MonoUtil.COLOR_STEP_ON);
        }
        display.setPad(GridPad.at(x, y), stepColor);

    }

    public void drawModes(MonoUtil.StepEditMode currentMode) {

        for (MonoUtil.StepEditMode mode : MonoUtil.StepEditMode.values()) {
            GridButton button = MonoUtil.modeButtonMap.get(mode);
            Color color = palette.get(MonoUtil.COLOR_MODE_INACTIVE);
            if (mode == currentMode) {
                color = palette.get(MonoUtil.COLOR_MODE_ACTIVE);
            }
            display.setButton(button, color);
        }

    }

    public void drawValue(int count, Color color) {
        for (int index = 0; index < count; index++) {
            display.setButton(GridButton.at(GridButton.Side.Right, index), color);
        }
    }

}
