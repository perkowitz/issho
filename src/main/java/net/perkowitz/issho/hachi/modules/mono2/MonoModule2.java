package net.perkowitz.issho.hachi.modules.mono2;

import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Chordable;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.MidiModule;
import net.perkowitz.issho.hachi.modules.Module;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.List;

import static net.perkowitz.issho.hachi.modules.mono2.MonoUtil.ValueState.STEP_OCTAVE;

/**
 * Created by optic on 10/24/16.
 */
public class MonoModule2 extends MidiModule implements Module, Clockable, GridListener, Sessionizeable, Chordable {

    private static int MAX_VELOCITY = 127;
    private static int MAX_OCTAVE = 7;

    private int midiChannel = 10;
    private MonoMemory memory = new MonoMemory();
    private int currentStepIndex = 0;
    private MonoDisplay monoDisplay;

    private GridControlSet patternControls = GridControlSet.padRows(MonoUtil.PATTERN_MIN_ROW, MonoUtil.PATTERN_MAX_ROW);
    private GridControlSet stepControls = GridControlSet.padRows(MonoUtil.STEP_MIN_ROW, MonoUtil.STEP_MAX_ROW);
    private GridControlSet keyboardControls = new GridControlSet(MonoUtil.keyboardControls);
    private GridControlSet stepEditControls = GridControlSet.buttonSide(GridButton.Side.Bottom, 0, 5);
    private GridControlSet valueControls = GridControlSet.buttonSideInverted(GridButton.Side.Right);

    private List<Color> palette = MonoUtil.PALETTE_FUCHSIA;



    /***** Constructor ****************************************/

    public MonoModule2(Transmitter inputTransmitter, Receiver outputReceiver) {
        super(inputTransmitter, outputReceiver);
        this.monoDisplay = new MonoDisplay(this.display);
    }


    /***** private implementation ****************************************/

    private void advance() {

        // turn off previous note
        MonoStep step = memory.currentPattern().getStep(currentStepIndex);
        sendMidiNote(midiChannel, step.getNote(), 0);
        monoDisplay.drawStep(step);

        // get new note and play it
        currentStepIndex = (currentStepIndex + 1) % MonoPattern.STEP_COUNT;
        step = memory.currentPattern().getStep(currentStepIndex);
        if (step.isEnabled() && step.getMode() != MonoUtil.Gate.REST) {
            sendMidiNote(midiChannel, step.getNote(), step.getVelocity());
        }
        monoDisplay.drawStep(step, true);

    }

    private void displayValue(int value, int maxValue) {
        int valueAsEight = (value * 8) / maxValue;
        for (int index = 0; index < 8; index++) {
            GridControl control = valueControls.get(index);
            if (index <= valueAsEight) {
                control.draw(display, palette.get(MonoUtil.COLOR_VALUE_ON));
            } else {
                control.draw(display, palette.get(MonoUtil.COLOR_VALUE_OFF));
            }
        }
    }

    /***** Module implementation ***********************************/

    public void redraw() {
        monoDisplay.redraw(memory);
    }

    public void setDisplay(GridDisplay display) {
        this.display = display;
        this.monoDisplay.setDisplay(display);
    }

    /***** Chordable implementation ***********************************/

    public void setChordNotes(List<Integer> notes) {

    }


    /***** Sessionizeable implementation *************************************/

    public void selectSession(int index) {

    }

    public void selectPatterns(int firstIndex, int lastIndex) {

    }


    /***** GridListener interface ****************************************/

    public void onPadPressed(GridPad pad, int velocity) {
//        System.out.printf("MonoModule: onPadPressed %s, %d\n", pad, velocity);
        onControlPressed(new GridControl(pad, null), velocity);
    }

    public void onPadReleased(GridPad pad) {
//        System.out.printf("MonoModule: onPadReleased %s\n", pad);
    }

    public void onButtonPressed(GridButton button, int velocity) {
        onControlPressed(new GridControl(button, null), velocity);
    }

    public void onButtonReleased(GridButton button) {

    }

    private void onControlPressed(GridControl control, int velocity) {

        if (patternControls.contains(control)) {

        } else if (stepControls.contains(control)) {

            // unselect the current selected step and redraw it
            MonoStep step = memory.currentStep();
            step.setSelected(false);
            monoDisplay.drawStep(step);

            // find the control's index and get the corresponding step
            GridControl selectedControl = stepControls.get(control);
            Integer index = selectedControl.getIndex();   // todo null check
            memory.selectStep(index);
            step = memory.currentStep();

            monoDisplay.drawSteps(memory.currentPattern().getSteps());

            // highlight the step's note in the keyboard
            keyboardControls.draw(display, palette.get(MonoUtil.COLOR_KEYBOARD_WHITE)); // or just redraw the current key?
            int note = step.getOctaveNote();
            GridControl keyControl = keyboardControls.get(note);
            keyControl.draw(display, palette.get(MonoUtil.COLOR_KEYBOARD_SELECTED));

            // what to update based on current step edit state
            switch (memory.getStepEditState()) {
                case MUTE:
                    memory.setValueState(STEP_OCTAVE);
                    displayValue(step.getOctave() - MonoUtil.LOWEST_OCTAVE, MAX_OCTAVE); // todo need to scale by MonoUtil.LOWEST_OCTAVE
                    step.toggleEnabled();
                    monoDisplay.drawStep(step);
                    break;
                case NOTE:
                    memory.setValueState(STEP_OCTAVE);
                    displayValue(step.getOctave() - MonoUtil.LOWEST_OCTAVE, MAX_OCTAVE);
                    break;
                case VELOCITY:
                    memory.setValueState(MonoUtil.ValueState.VELOCITY);
                    displayValue(step.getVelocity(), MAX_VELOCITY);
                    break;
                case PLAY:
                    memory.setValueState(MonoUtil.ValueState.KEYBOARD_OCTAVE);
                    displayValue(memory.getKeyboardOctave() - MonoUtil.LOWEST_OCTAVE, MAX_OCTAVE);
                    break;

            }
            monoDisplay.drawSteps(memory.currentPattern().getSteps());

        } else if (keyboardControls.contains(control)) {

            // get current note index
            int currentOctaveNote = 0; // todo ???

            monoDisplay.drawKeyboard();

            // find the control's index, get the current step
            GridControl selectedControl = keyboardControls.get(control);
            Integer index = selectedControl.getIndex();
            MonoStep step = memory.currentStep();

            switch (memory.getStepEditState()) {
                case NOTE:
                case MUTE:
                case VELOCITY:
                    step.setNote(index);
                    selectedControl.draw(display, palette.get(MonoUtil.COLOR_KEYBOARD_SELECTED));
                    // todo redraw the old key
                    break;
                case PLAY:
                    int note = memory.getKeyboardOctave() * 12 + index;
                    sendMidiNote(midiChannel, note, velocity);
                    break;
            }

        } else if (stepEditControls.contains(control)) {

            // find the control's index, get the current step
            GridControl selectedControl = stepEditControls.get(control);
            Integer index = selectedControl.getIndex();
            MonoUtil.StepEditState[] states = MonoUtil.StepEditState.values();
            if (index != null && index >= 0 && index < states.length) {
                memory.setStepEditState(states[index]);
            }
            monoDisplay.drawModes(memory.getStepEditState());

        } else if (valueControls.contains(control)) {

            MonoStep step = memory.currentStep();
            if (step != null) {
                Integer index = valueControls.getIndex(control);
                displayValue(index, 7);
                switch (memory.getValueState()) {
                    case STEP_OCTAVE:
                        step.setOctave(index + MonoUtil.LOWEST_OCTAVE);
                        break;
                    case VELOCITY:
                        step.setVelocity((index + 1) * 16 - 1);
                        break;
                    case KEYBOARD_OCTAVE:
                        memory.setKeyboardOctave(index + MonoUtil.LOWEST_OCTAVE);
                        break;

                }
            }

        }

    }


    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        if (restart) {
            currentStepIndex = 0;
        }
    }

    public void stop() {
    }

    public void tick() {
        advance();
    }






}
