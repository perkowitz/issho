package net.perkowitz.issho.hachi.modules.mono2;

import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.mono2.MonoUtil.*;


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
        drawModes(memory.getStepEditState());
    }

    public void drawKeyboard() {

        for (int x = 0; x < 7; x++) {
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

    public void drawStepOff(MonoStep step) {
        // get step location
        int x = step.getIndex() % 8;
        int y = step.getIndex() / 8 + MonoUtil.STEP_MIN_ROW;
        display.setPad(GridPad.at(x, y), Color.OFF);
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

        // display the selected step's note on the keyboard
        if (step.isSelected() && step.isEnabled()) {
            display.setPad(GridPad.at(keyX, keyY), palette.get(MonoUtil.COLOR_KEYBOARD_SELECTED));
        }
        // display the selected step's note on the keyboard
        if (step.isSelected() && step.isEnabled()) {
            display.setPad(GridPad.at(keyX, keyY), palette.get(MonoUtil.COLOR_KEYBOARD_SELECTED));
        }


        Color stepColor = palette.get(MonoUtil.COLOR_STEP_OFF);
        if (highlight) {
            stepColor = palette.get(MonoUtil.COLOR_STEP_HIGHLIGHT);
            if (highlight && step.isEnabled()) {
                display.setPad(GridPad.at(keyX, keyY), palette.get(MonoUtil.COLOR_KEYBOARD_SELECTED));
            }
        } else if (step.isEnabled()) {
            switch (step.getGate()) {
                case PLAY:
                    stepColor = palette.get(MonoUtil.COLOR_STEP_PLAY);
                    break;
                case TIE:
                    stepColor = palette.get(MonoUtil.COLOR_STEP_TIE);
                    break;
                case REST:
                    stepColor = palette.get(MonoUtil.COLOR_STEP_REST);
                    break;
            }
        }
        display.setPad(GridPad.at(x, y), stepColor);

    }

    public void drawModes(MonoUtil.StepEditState currentMode) {

        for (MonoUtil.StepEditState mode : MonoUtil.StepEditState.values()) {
            GridButton button = MonoUtil.modeButtonMap.get(mode);
            if (button != null) {
                Color color = palette.get(COLOR_MODE_INACTIVE);
                if (mode == currentMode) {
                    color = palette.get(MonoUtil.COLOR_MODE_ACTIVE);
                }
                display.setButton(button, color);
            }
        }

    }

    public void drawValue(int count, Color color) {
        for (int index = 0; index < count; index++) {
            display.setButton(GridButton.at(GridButton.Side.Right, index), color);
        }
    }

    public void drawFunctions(GridControlSet functionControls) {
        functionControls.draw(display, palette.get(COLOR_MODE_INACTIVE));
    }



}
