package net.perkowitz.issho.hachi.modules.mono;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class MonoDisplay {

    @Setter private GridDisplay display;
    @Getter @Setter private List<Color> palette = MonoUtil.PALETTE_FUCHSIA;
    @Getter @Setter private boolean settingsMode = false;
    @Getter @Setter private int currentFileIndex = 0;

    public MonoDisplay(GridDisplay display) {
        this.display = display;
    }

    public void redraw(MonoMemory memory) {
        if (settingsMode) {
            drawSessions(memory);
            drawFiles(memory);
            drawMidiChannel(memory);
        } else {
            drawPatterns(memory);
            drawPatternEditControls(false, false);
            drawKeyboard(memory);
            drawSteps(memory.currentPattern().getSteps());
            drawStepEdits(memory.getStepEditState());
        }
    }

    public void drawFiles(MonoMemory memory) {
        if (!settingsMode) return;
        for (GridControl control : loadControls.getControls()) {
            Color color = palette.get(COLOR_FILE_LOAD);
            if (control.getIndex() == currentFileIndex) {
                color = palette.get(COLOR_FILE_ACTIVE);
            }
            control.draw(display, color);
        }
        for (GridControl control : saveControls.getControls()) {
            Color color = palette.get(COLOR_FILE_SAVE);
            if (control.getIndex() == currentFileIndex) {
                color = palette.get(COLOR_FILE_ACTIVE);
            }
            control.draw(display, color);
        }
    }

    public void drawSessions(MonoMemory memory) {
        if (!settingsMode) return;
        for (GridControl control : sessionControls.getControls()) {
            Color color = palette.get(COLOR_SESSION);
            if (control.getIndex() == memory.getCurrentSessionIndex()) {
                color = palette.get(COLOR_SESSION_ACTIVE);
            } else if (control.getIndex() == memory.getNextSessionIndex()) {
                color = palette.get(COLOR_SESSION_NEXT);
            }
            control.draw(display, color);
        }
    }

    public void drawMidiChannel(MonoMemory memory) {
        if (!settingsMode) return;
        for (GridControl control : midiChannelControls.getControls()) {
            Color color = palette.get(COLOR_MIDI_CHANNEL);
            if (control.getIndex() == memory.getMidiChannel()) {
                color = palette.get(COLOR_MIDI_CHANNEL_ACTIVE);
            }
            control.draw(display, color);
        }
    }

    public void drawPatterns(MonoMemory memory) {
        if (settingsMode) return;
        MonoPattern[] patterns = memory.currentSession().getPatterns();
        for (int i = 0; i < patterns.length; i++) {
            drawPattern(memory, patterns[i]);
        }


    }

    public void drawPatternEditControls(boolean copyActive, boolean clearActive) {
        if (copyActive) {
            patternCopyControl.draw(display, palette.get(COLOR_PATTERN_EDIT_SELECTED));
        } else {
            patternCopyControl.draw(display, palette.get(COLOR_PATTERN_EDIT));
        }
        if (clearActive) {
            patternClearControl.draw(display, palette.get(COLOR_PATTERN_EDIT_SELECTED));
        } else {
            patternClearControl.draw(display, palette.get(COLOR_PATTERN_EDIT));
        }
    }

    public void drawPattern(MonoMemory memory, MonoPattern pattern) {

        if (settingsMode) return;
        int index = pattern.getIndex();
        GridControl control = patternControls.get(index);
        Color color = palette.get(COLOR_PATTERN);
        if (memory.getCurrentPatternIndex() == index) {
            color = palette.get(COLOR_PATTERN_PLAYING);
        } else if (index == memory.getPatternChainNextIndex()) {
            color = palette.get(COLOR_PATTERN_CHAINED);
        } else if (index >= memory.getPatternChainMin() && index <= memory.getPatternChainMax()) {
            color = palette.get(COLOR_PATTERN_CHAINED);
        }

        control.draw(display, color);
    }

    public void drawKeyboard(MonoMemory memory) {
        if (settingsMode) return;
        keyboardControls.draw(display, palette.get(MonoUtil.COLOR_KEYBOARD_KEY));
//        MonoStep step = memory.currentStep();
//        GridControl control = controls.get(step.getOctaveNote());
//        control.draw(display, palette.get(MonoUtil.COLOR_KEYBOARD_SELECTED));
    }


    public void drawSteps(MonoStep[] steps) {
        if (settingsMode) return;
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

        if (settingsMode) return;

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

        if (settingsMode) return;

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

    public void drawFunctions(boolean isMuted) {
        for (GridControl control : functionControls.getControls()) {
            Color color = palette.get(COLOR_MODE_INACTIVE);
            if (control.getIndex() == FUNCTION_SETTINGS_INDEX && settingsMode) {
                color = palette.get(COLOR_MODE_ACTIVE);
            } else if (control.getIndex() == FUNCTION_MUTE_INDEX && isMuted) {
                color = palette.get(COLOR_MODE_ACTIVE);
            }
            control.draw(display, color);
        }
    }

    public boolean toggleSettings() {
        settingsMode = !settingsMode;
        return settingsMode;
    }

    public void initialize() {
        display.initialize();
    }

}
