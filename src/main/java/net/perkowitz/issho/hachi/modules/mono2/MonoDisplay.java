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
        drawKeyboard(memory);
        drawSteps(memory.currentPattern().getSteps());
        drawStepEdits(memory.getStepEditState());
    }

    public void drawKeyboard(MonoMemory memory) {
        keyboardControls.draw(display, palette.get(MonoUtil.COLOR_KEYBOARD_KEY));
//        MonoStep step = memory.currentStep();
//        GridControl control = controls.get(step.getOctaveNote());
//        control.draw(display, palette.get(MonoUtil.COLOR_KEYBOARD_SELECTED));
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


        Color stepColor = palette.get(MonoUtil.COLOR_STEP_OFF);
        if (highlight) {
            stepColor = palette.get(MonoUtil.COLOR_STEP_HIGHLIGHT);
            if (highlight && step.isEnabled()) {
//                display.setPad(GridPad.at(keyX, keyY), palette.get(MonoUtil.COLOR_KEYBOARD_HIGHLIGHT));
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

    public void drawStepEdits(MonoUtil.StepEditState currentState) {

        // some of the step edit controls are step edit mode buttons
        MonoUtil.StepEditState[] states = MonoUtil.StepEditState.values();
        for (int i = 0; i < states.length; i++) {
            GridControl control = stepEditControls.get(i);
            StepEditState state = states[i];
            Color color = palette.get(COLOR_MODE_INACTIVE);
            if (state == currentState) {
                color = palette.get(MonoUtil.COLOR_MODE_ACTIVE);
            }
            control.draw(display, color);
        }

        // and there's some other stuff
        stepEditControls.get(MonoUtil.STEP_CONTROL_SHIFT_LEFT_INDEX).draw(display, palette.get(COLOR_MODE_INACTIVE));
        stepEditControls.get(MonoUtil.STEP_CONTROL_SHIFT_RIGHT_INDEX).draw(display, palette.get(COLOR_MODE_INACTIVE));

    }

    public void drawValue(int count, Color color) {
        for (int index = 0; index < count; index++) {
            display.setButton(GridButton.at(GridButton.Side.Right, index), color);
        }
    }

    public void drawFunctions() {
        functionControls.draw(display, palette.get(COLOR_MODE_INACTIVE));
    }



}
