package net.perkowitz.issho.hachi.modules.mono;

import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Chordable;
import net.perkowitz.issho.hachi.modules.MidiModule;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.Sessionizeable;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class MonoModule extends MidiModule implements Module, Clockable, GridListener, Sessionizeable, Chordable {

    private int midiChannel = 10;
    private MonoMemory memory = new MonoMemory();
    private int currentStepIndex = 0;
    private MonoDisplay monoDisplay;

    private int selectedStepIndex;


    /***** Constructor ****************************************/

    public MonoModule(Transmitter inputTransmitter, Receiver outputReceiver) {
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

        if (pad.getY() >= MonoUtil.STEP_MIN_ROW && pad.getY() <= MonoUtil.STEP_MAX_ROW) {

            MonoStep currentStep = memory.currentPattern().getStep(selectedStepIndex);
            currentStep.setSelected(false);

            int index = (pad.getY() - MonoUtil.STEP_MIN_ROW) * 8 + pad.getX();
            selectedStepIndex = index;
            MonoStep newStep = memory.currentPattern().getStep(selectedStepIndex);
            newStep.setSelected(true);

            monoDisplay.drawKeyboard();
            monoDisplay.drawStep(currentStep);
            monoDisplay.drawStep(newStep);

        } else if (pad.getY() >= MonoUtil.KEYBOARD_MIN_ROW && pad.getY() <= MonoUtil.KEYBOARD_MAX_ROW) {
            int index = (pad.getY() - MonoUtil.KEYBOARD_MIN_ROW) * 8 + pad.getX();
            Integer note = MonoUtil.KEYBOARD_INDEX_TO_NOTE[index];
            System.out.printf("Keyboard index=%d, note=%d\n", index, note);

            MonoStep step = memory.currentPattern().getStep(selectedStepIndex);
            if (note != null) {
                step.setNote(note + 60);
            }

            monoDisplay.drawKeyboard();
            monoDisplay.drawStep(step);

        }
    }

    public void onPadReleased(GridPad pad) {
//        System.out.printf("MonoModule: onPadReleased %s\n", pad);
    }

    public void onButtonPressed(GridButton button, int velocity) {

        for (MonoUtil.StepEditMode mode : MonoUtil.modeButtonMap.keySet()) {
            GridButton modeButton = MonoUtil.modeButtonMap.get(mode);
            if (button.equals(modeButton)) {
                memory.setStepEditMode(mode);
                monoDisplay.drawModes(mode);
            }
        }

    }

    public void onButtonReleased(GridButton button) {

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
