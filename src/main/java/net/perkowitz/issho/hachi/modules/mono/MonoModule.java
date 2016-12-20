package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Chordable;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Saveable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.MidiModule;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.Muteable;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.*;
import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.Gate.PLAY;
import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.ValueState.STEP_OCTAVE;
import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.View.SEQUENCE;

/**
 * Created by optic on 10/24/16.
 */
public class MonoModule extends MidiModule implements Module, Clockable, GridListener, Sessionizeable, Chordable, Saveable, Muteable {

    private static int MAX_VELOCITY = 127;
    private static int MAX_OCTAVE = 7;

    ObjectMapper objectMapper = new ObjectMapper();

    private MonoMemory memory = new MonoMemory();
    private int nextStepIndex = 0;
    private MonoDisplay monoDisplay;

    private Set<Integer> onNotes = Sets.newHashSet();

    private static Timer timer = null;
    private static int flashIntervalMillis = 125;
    private static int flashCount = 0;

    private MonoStep lastStep = null;

    private MonoUtil.View currentView = SEQUENCE;

    private Set<Integer> patternsPressed = Sets.newHashSet();
    private int patternsReleasedCount = 0;
    private List<Integer> patternEditIndexBuffer = Lists.newArrayList();
    private boolean patternEditing = false;

    private String filePrefix = "monomodule";
    private int currentFileIndex = 0;


    /***** Constructor ****************************************/

    public MonoModule(Transmitter inputTransmitter, Receiver outputReceiver, List<Color> palette, String filePrefix) {
        super(inputTransmitter, outputReceiver);
        monoDisplay = new MonoDisplay(this.display);
        monoDisplay.setPalette(palette);
        this.filePrefix = filePrefix;
//        startTimer();
        load(0);
    }


    /***** private implementation ****************************************/

    private void advance(boolean andReset) {

        if (andReset) {
            nextStepIndex = 0;
        }

        if (nextStepIndex == 0) {

            int currentSessionIndex = memory.getCurrentSessionIndex();
            Integer nextSessionIndex = memory.getNextSessionIndex();
            if (nextSessionIndex != null && nextSessionIndex != memory.getCurrentSessionIndex()) {
                memory.setCurrentSessionIndex(nextSessionIndex);
                monoDisplay.drawSessions(memory);
            }

            int currentPatternIndex = memory.getCurrentPatternIndex();
            memory.selectNextPattern();
            if (currentPatternIndex != memory.getCurrentPatternIndex()) {
                monoDisplay.drawPatterns(memory);
                monoDisplay.drawSteps(memory.currentPattern().getSteps());
            }
        }

        if (lastStep != null) {
            monoDisplay.drawStep(lastStep);
            lastStep = null;
        }

        MonoStep step = memory.currentPattern().getStep(nextStepIndex);
//        monoDisplay.drawKeyboard(memory, keyboardList);

        if (!step.isEnabled() || step.getGate() == MonoUtil.Gate.REST) {
            notesOff();
        } else if (step.isEnabled() && step.getGate() == PLAY) {
            notesOff();
            sendMidiNote(memory.getMidiChannel(), step.getNote(), step.getVelocity());
            onNotes.add(step.getNote());
        } else if (step.isEnabled() && step.getGate() == MonoUtil.Gate.TIE) {
            // do nothing
        }
        monoDisplay.drawStep(step, true);
        lastStep = step;

        // get new note
        nextStepIndex = (nextStepIndex + 1) % MonoPattern.STEP_COUNT;
    }

    private void displayValue(int value, int maxValue) {
        int valueAsEight = (value * 8) / maxValue;
        for (int index = 0; index < 8; index++) {
            GridControl control = MonoUtil.valueControls.get(index);
            if (index <= valueAsEight) {
                control.draw(display, monoDisplay.getPalette().get(MonoUtil.COLOR_VALUE_ON));
            } else {
                control.draw(display, monoDisplay.getPalette().get(MonoUtil.COLOR_VALUE_OFF));
            }
        }
    }

    private void notesOff() {
        for (Integer note : onNotes) {
            sendMidiNote(memory.getMidiChannel(), note, 0);

        }
        onNotes.clear();
    }

    private void save(String filename) {

        try {

            File file = new File(filename);
            if (file.exists()) {
                // make a backup, but will overwrite any previous backups
                Files.copy(file, new File(filename + ".backup"));
            }

            objectMapper.writeValue(file, memory);
//            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(memory);
//            System.out.println(json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void load(String filename) {

        try {
            File file = new File(filename);

            if (file.exists()) {
                memory = objectMapper.readValue(file, MonoMemory.class);
            } else {
                memory = new MonoMemory();
//                memory.select(memory.selectedPattern().getTrack(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void startTimer() {

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
//                MonoStep step = memory.currentStep();
//                if (step != null) {
//                    if (flashCount == 0) {
//                        monoDisplay.drawStepOff(step);
//                    } else {
//                        monoDisplay.drawStep(step);
//                    }
//                }
//                flashCount = (flashCount + 1) % 2;
            }
        }, flashIntervalMillis, flashIntervalMillis);
    }


    /***** Module implementation ***********************************/

    public void redraw() {
        monoDisplay.redraw(memory);
        monoDisplay.drawFunctions(isMuted);
    }

    public void setDisplay(GridDisplay display) {
        this.display = display;
        this.monoDisplay.setDisplay(display);
    }

    public void shutdown() {
        notesOff();
    }


    /***** Muteable implementation ***********************************/

    public void mute(boolean muted) {
        this.isMuted = muted;
        notesOff();
    }

    public boolean isMuted() {
        return isMuted;
    }

    /***** Chordable implementation ***********************************/

    public void setChordNotes(List<Integer> notes) {

    }


    /***** Sessionizeable implementation *************************************/

    public void selectSession(int index) {
        memory.setNextSessionIndex(index);
        monoDisplay.drawSessions(memory);
    }

    public void selectPatterns(int firstIndex, int lastIndex) {
        memory.selectPatternChain(firstIndex, lastIndex);
        patternsPressed.clear();
        patternsReleasedCount = 0;
        monoDisplay.drawPatterns(memory);
    }


    /***** GridListener interface ****************************************/

    public void onPadPressed(GridPad pad, int velocity) {
        onControlPressed(new GridControl(pad, null), velocity);
    }

    public void onPadReleased(GridPad pad) {
        onControlReleased(new GridControl(pad, null));
    }

    public void onButtonPressed(GridButton button, int velocity) {
        onControlPressed(new GridControl(button, null), velocity);
    }

    public void onButtonReleased(GridButton button) {
        onControlReleased(new GridControl(button, null));
    }

    private void onControlPressed(GridControl control, int velocity) {

        if (monoDisplay.isSettingsMode()) {
            onControlPressedSettings(control, velocity);
            return;
        }

        if (MonoUtil.patternControls.contains(control)) {

            // find the control's index and get the corresponding pattern
            GridControl selectedControl = MonoUtil.patternControls.get(control);
            Integer index = selectedControl.getIndex();
            if (index != null) {
                if (patternEditing) {
                    patternEditIndexBuffer.add(index);
                } else {
                    patternsPressed.add(index);
                }
            }

        } else if (patternCopyControl.equals(control)) {
            patternEditIndexBuffer.clear();
            patternEditing = true;
            monoDisplay.drawPatternEditControls(true, false);

        } else if (patternClearControl.equals(control)) {
            patternEditIndexBuffer.clear();
            patternEditing = true;
            monoDisplay.drawPatternEditControls(false, true);

        } else if (MonoUtil.stepControls.contains(control)) {

            // unselect the current selected step and redraw it
            MonoStep step = memory.currentStep();
            step.setSelected(false);
            monoDisplay.drawStep(step);

            // find the control's index and get the corresponding step
            GridControl selectedControl = MonoUtil.stepControls.get(control);
            Integer index = selectedControl.getIndex();   // todo null check
            memory.selectStep(index);
            step = memory.currentStep();

            monoDisplay.drawSteps(memory.currentPattern().getSteps());

            // highlight the step's note in the keyboard
            MonoUtil.keyboardControls.draw(display, monoDisplay.getPalette().get(MonoUtil.COLOR_KEYBOARD_KEY)); // or just redraw the current key?
            int note = step.getOctaveNote();
            GridControl keyControl = MonoUtil.keyboardControls.get(note);
//            keyControl.draw(display, palette.get(MonoUtil.COLOR_KEYBOARD_HIGHLIGHT));

            // what to update based on current step edit state
            switch (memory.getStepEditState()) {
                case NOTE:
                    memory.setValueState(STEP_OCTAVE);
                    displayValue(step.getOctave() - MonoUtil.LOWEST_OCTAVE, MAX_OCTAVE);
                    break;
                case GATE:
                    memory.setValueState(MonoUtil.ValueState.NONE);
                    displayValue(0, -1);
                    if (step.getGate() == PLAY) {
                        step.setGate(MonoUtil.Gate.TIE);
                    } else if (step.getGate() == MonoUtil.Gate.TIE) {
                        step.setGate(MonoUtil.Gate.REST);
                    } else if (step.getGate() == MonoUtil.Gate.REST) {
                        step.setGate(PLAY);
                    }
                    break;
                case VELOCITY:
                    memory.setValueState(MonoUtil.ValueState.VELOCITY);
                    displayValue(step.getVelocity(), MAX_VELOCITY);
                    break;
//                case PLAY:
//                    memory.setValueState(MonoUtil.ValueState.KEYBOARD_OCTAVE);
//                    displayValue(memory.getKeyboardOctave() - MonoUtil.LOWEST_OCTAVE, MAX_OCTAVE);
//                    break;

            }
            monoDisplay.drawSteps(memory.currentPattern().getSteps());

        } else if (MonoUtil.keyboardControls.contains(control)) {

            // get current note index
            int currentOctaveNote = 0; // todo ???

            monoDisplay.drawKeyboard(memory);

            // find the control's index, get the current step
            GridControl selectedControl = MonoUtil.keyboardControls.get(control);
            Integer index = selectedControl.getIndex();
            MonoStep step = memory.currentStep();

            switch (memory.getStepEditState()) {
                case NOTE:
                case VELOCITY:
                    step.setOctaveNote(index);
                    selectedControl.draw(display, monoDisplay.getPalette().get(MonoUtil.COLOR_KEYBOARD_SELECTED));
                    // todo redraw the old key
                    break;
//                case PLAY:
//                    int note = memory.getKeyboardOctave() * 12 + index;
//                    sendMidiNote(memory.getMidiChannel(), note, velocity);
//                    break;
            }

        } else if (MonoUtil.stepEditControls.contains(control)) {

            // find the control's index, get the current step
            Integer index = MonoUtil.stepEditControls.getIndex(control);
            if (index != null) {
                MonoUtil.StepEditState[] states = MonoUtil.StepEditState.values();
                if (index >= 0 && index < states.length) {
                    memory.setStepEditState(states[index]);
                } else if (index == MonoUtil.STEP_CONTROL_SHIFT_LEFT_INDEX) {
                    memory.currentPattern().shift(-1);
                    monoDisplay.drawSteps(memory.currentPattern().getSteps());
                } else if (index == MonoUtil.STEP_CONTROL_SHIFT_RIGHT_INDEX) {
                    memory.currentPattern().shift(1);
                    monoDisplay.drawSteps(memory.currentPattern().getSteps());
                }
            }
            monoDisplay.drawStepEdits(memory.getStepEditState());

        } else if (MonoUtil.valueControls.contains(control)) {

            MonoStep step = memory.currentStep();
            if (step != null) {
                Integer index = MonoUtil.valueControls.getIndex(control);
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
                    case NONE:
                        break;
                }
            }

        } else if (MonoUtil.functionControls.contains(control)) {

            // find the control's index, get the current step
            Integer index = MonoUtil.functionControls.getIndex(control);
            if (index != null) {
                if (index == FUNCTION_SAVE_INDEX) {
                    save(currentFileIndex);
//                } else if (index == FUNCTION_LOAD_INDEX) {
//                    load(currentFileIndex);
//                    monoDisplay.redraw(memory);
                } else if (index == FUNCTION_MUTE_INDEX) {
                    this.isMuted = !isMuted;
                    monoDisplay.redraw(memory);
                } else if (index == FUNCTION_SETTINGS_INDEX) {
                    monoDisplay.toggleSettings();
                    monoDisplay.initialize();
                    monoDisplay.redraw(memory);
                }
                monoDisplay.drawFunctions(isMuted);
            }

        }

    }

    private void onControlPressedSettings(GridControl control, int velocity) {

        if (MonoUtil.sessionControls.contains(control)) {
            Integer index = MonoUtil.sessionControls.getIndex(control);
            selectSession(index);

        } else if (MonoUtil.loadControls.contains(control)) {
            Integer index = MonoUtil.loadControls.getIndex(control);
            currentFileIndex = index;
            monoDisplay.setCurrentFileIndex(currentFileIndex);
            load(currentFileIndex);
            monoDisplay.redraw(memory);

        } else if (MonoUtil.saveControls.contains(control)) {
            Integer index = MonoUtil.saveControls.getIndex(control);
            currentFileIndex = index;
            monoDisplay.setCurrentFileIndex(currentFileIndex);
            save(currentFileIndex);
            monoDisplay.redraw(memory);

        } else if (MonoUtil.midiChannelControls.contains(control)) {
            Integer index = MonoUtil.midiChannelControls.getIndex(control);
            notesOff();
            memory.setMidiChannel(index);
            monoDisplay.drawMidiChannel(memory);

        } else if (MonoUtil.functionControls.contains(control)) {
            Integer index = MonoUtil.functionControls.getIndex(control);
            if (index != null) {
                if (index == FUNCTION_SETTINGS_INDEX) {
                    monoDisplay.toggleSettings();
                    monoDisplay.initialize();
                    monoDisplay.redraw(memory);
                }
                monoDisplay.drawFunctions(isMuted);
            }

        }
    }

    private void onControlReleased(GridControl control) {

        if (MonoUtil.patternControls.contains(control)) {

            if (!patternEditing) {
                // releasing a pattern pad
                // don't activate until the last pattern pad is released (so additional releases don't look like a new press/release)
                patternsReleasedCount++;
                if (patternsReleasedCount >= patternsPressed.size()) {
                    GridControl selectedControl = MonoUtil.patternControls.get(control);
                    Integer index = selectedControl.getIndex();
                    patternsPressed.add(index); // just to make sure
                    int min = index;
                    int max = index;
                    if (patternsPressed.size() > 1) {
                        for (Integer pattern : patternsPressed) {
                            if (pattern < min) {
                                min = pattern;
                            }
                            if (pattern > max) {
                                max = pattern;
                            }
                        }
                        memory.selectPatternChain(min, max);
                    }
                    selectPatterns(min, max);
                }
            }

        } else if (patternCopyControl.equals(control)) {
            if (patternEditIndexBuffer.size() >= 2) {
                Integer fromIndex = patternEditIndexBuffer.get(0);
                Integer toIndex = patternEditIndexBuffer.get(1);
                if (fromIndex != null && toIndex != null) {
                    memory.currentSession().getPatterns()[toIndex] = MonoPattern.copy(memory.currentSession().getPattern(fromIndex), toIndex);;
                }
            }
            patternEditIndexBuffer.clear();
            patternEditing = false;
            monoDisplay.drawPatternEditControls(false, false);

        } else if (patternClearControl.equals(control)) {
            if (patternEditIndexBuffer.size() >= 0) {
                for (Integer index : patternEditIndexBuffer) {
                    memory.currentSession().getPatterns()[index] = new MonoPattern(index);
                }
            }
            patternEditIndexBuffer.clear();
            patternEditing = false;
            monoDisplay.drawPatternEditControls(false, false);

        }
    }


    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        if (restart) {
            nextStepIndex = 0;
        }
    }

    public void stop() {
        notesOff();
    }

    public void tick(boolean andReset) {
        advance(andReset);
    }


    /***** Saveable implementation ****************************************/

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void save(int index) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                // make a backup, but will overwrite any previous backups
                Files.copy(file, new File(filename + ".backup"));
            }
            objectMapper.writeValue(file, memory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(int index) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                memory = objectMapper.readValue(file, MonoMemory.class);
            } else {
                memory = new MonoMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String filename(int index) {
        return filePrefix + "-" + index + ".json";
    }



}
