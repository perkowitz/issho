package net.perkowitz.issho.hachi.modules.para;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Saveable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.*;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.util.*;

import static net.perkowitz.issho.hachi.modules.para.ParaUtil.*;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.Gate.PLAY;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.Gate.TIE;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.StepSelectMode.CONTROL;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.StepSelectMode.SELECT;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.StepSelectMode.TOGGLE;

/**
 * Created by optic on 10/24/16.
 */
public class ParaModule extends ChordModule implements Module, Clockable, GridListener, Sessionizeable, Saveable, Muteable {

    ObjectMapper objectMapper = new ObjectMapper();

    private ParaMemory memory = new ParaMemory();
    private ParaDisplay paraDisplay;
    private SettingsSubmodule settingsModule;

    private Set<Integer> onNotes = Sets.newHashSet();

    private ParaStep lastStep = null;

    private int nextStepIndex = 0;
    private Set<Integer> patternsPressed = Sets.newHashSet();
    private int patternsReleasedCount = 0;
    private List<Integer> patternEditIndexBuffer = Lists.newArrayList();
    private boolean patternEditing = false;
    private boolean stepEditing = false;
    private int currentKeyboardOctave = 5;
    @Setter private boolean monophonic = false;
    @Setter int controlA = 15;
    private int swingOffset = 0;
    @Setter private Integer[] controllerNumbers = { 1, 16, 17, 18 };

    private String filePrefix = "polymodule";
    private int currentFileIndex = 0;

    private int transpose = 0;


    /***** Constructor ****************************************/

    public ParaModule(Transmitter inputTransmitter, Receiver outputReceiver, Map<Integer, Color> palette, String filePrefix) {
        super(inputTransmitter, outputReceiver);
        paraDisplay = new ParaDisplay(this.display);
        paraDisplay.setPalette(palette);
        this.filePrefix = filePrefix;
        load(0);
        this.settingsModule = new SettingsSubmodule(true, true, true, true);
    }


    /***** private implementation ****************************************/

    private void advance(boolean andReset) {

        if (andReset) {
            nextStepIndex = 0;
        }

        // keep track of which things need to be redrawn and draw them AFTER sending midi notes
        boolean drawSessions = false;
        boolean drawPatterns = false;
        boolean drawSteps = false;
        boolean drawKeyboardNotes = false;

        // figure out if we're starting a new pattern or session
        if (nextStepIndex == 0) {
            Integer nextSessionIndex = memory.getNextSessionIndex();
            if (nextSessionIndex != null && nextSessionIndex != memory.getCurrentSessionIndex()) {
                memory.setCurrentSessionIndex(nextSessionIndex);
                settingsModule.setCurrentSessionIndex(nextSessionIndex);
                drawSessions = true;
            }
            int currentPatternIndex = memory.getCurrentPatternIndex();
            memory.selectNextPattern();
            if (currentPatternIndex != memory.getCurrentPatternIndex()) {
                drawPatterns = true;
                drawSteps = true;
                drawKeyboardNotes = true;
            }
        }

        // unhighlight the previous step
        if (lastStep != null) {
            paraDisplay.drawStep(memory, lastStep, false);
//            lastStep = null;
        }

//        paraDisplay.drawKeyboard(memory, memory.currentStep(), false);

        if (!stepEditing) {
            paraDisplay.drawKeyboardNotes(onNotes, true, false, memory.getStepSelectMode());
        }

        // advance to the next step and play notes
        ParaStep step = memory.currentPattern().getStep(nextStepIndex);

        // send controllers before sending notes
        // if controller is enabled for step, send controller value (even if step is a TIE or no notes programmed)
        for (int index = 0; index < ParaMemory.CONTROLLER_COUNT; index++) {
            if (controllerNumbers[index] != null && step.getControllerEnabled(index)) {
                sendMidiCC(memory.getMidiChannel(), controllerNumbers[index], step.getControllerValue(index));
            }
        }

        // send notes
        if (!step.isEnabled()) {
            // if the step is disabled, it's a rest so we stop any previous notes
            notesOff();
            drawKeyboardNotes = true;
        } else if (step.isEnabled() && step.getGate() == PLAY) {
            // if it's PLAY stop any previous notes so we can play new ones
            notesOff();
            drawKeyboardNotes = true;
            for (int note : step.getNotes()) {
                sendMidiNote(memory.getMidiChannel(), transpose + note, step.getVelocity());
                onNotes.add(note);
            }
        } else if (step.isEnabled() && step.getGate() == TIE) {
            // for a TIE we just keep doing what we've been doing
        }

        // now redraw what needs to be redrawn
        if (paraDisplay.isSettingsMode() && drawSessions) { settingsModule.drawSessions(); }
        if (drawPatterns) { paraDisplay.drawPatterns(memory); }
        if (drawSteps) { paraDisplay.drawSteps(memory, memory.currentPattern().getSteps()); }
//        if (drawKeyboardNotes) {
////            paraDisplay.drawKeyboard();
//            if (lastStep != null) {
//                paraDisplay.drawKeyboardNotes(onNotes, true);
//            }
//            paraDisplay.drawKeyboardNotes(step.getNotes(), false);
//        }

//        paraDisplay.drawKeyboard();
        if (!stepEditing) {
            boolean editFlag = false;
            if (step.equals(memory.selectedStep())) {
                editFlag = true;
            }
            paraDisplay.drawKeyboardNotes(onNotes, false, editFlag, memory.getStepSelectMode());
        }

        // always draw the step itself
        paraDisplay.drawStep(memory, step, true);

        lastStep = step;
        nextStepIndex = (nextStepIndex + 1) % ParaPattern.STEP_COUNT;
    }

    private void notesOff() {
        for (Integer note : onNotes) {
            sendMidiNote(memory.getMidiChannel(), transpose + note, 0);

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
                memory = objectMapper.readValue(file, ParaMemory.class);
            } else {
                memory = new ParaMemory();
//                memory.select(memory.selectedPattern().getTrack(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /***** Module implementation ***********************************/

    public void redraw() {
        if (paraDisplay.isSettingsMode()) {
            settingsModule.redraw();
        } else {
            paraDisplay.redraw(memory);
        }
        paraDisplay.drawFunctions(isMuted);
    }

    public void setDisplay(GridDisplay display) {
        this.display = display;
        this.paraDisplay.setDisplay(display);
        this.settingsModule.setDisplay(display);
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

    /***** Sessionizeable implementation *************************************/

    public void selectSession(int index) {
        memory.setNextSessionIndex(index);
        settingsModule.setCurrentSessionIndex(index);
        redraw();
    }

    public void selectPatterns(int firstIndex, int lastIndex) {
        memory.selectPatternChain(firstIndex, lastIndex);
        patternsPressed.clear();
        patternsReleasedCount = 0;
        paraDisplay.drawPatterns(memory);
    }

    public void fillOn(Integer fillIndex) {}

    public void fillOff() {}


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

        // these controls apply in main view or settings view
        if (ParaUtil.functionControls.contains(control)) {
            // find the control's index, get the current step
            Integer index = ParaUtil.functionControls.getIndex(control);
            if (index != null) {
                if (index == FUNCTION_SAVE_INDEX) {
                    this.save(settingsModule.getCurrentFileIndex());
                    save(currentFileIndex);
                    paraDisplay.drawFunctions(isMuted);
                } else if (index == FUNCTION_MUTE_INDEX) {
                    this.isMuted = !isMuted;
                    paraDisplay.drawFunctions(isMuted);
                } else if (index == FUNCTION_SETTINGS_INDEX) {
                    paraDisplay.toggleSettings();
                    this.redraw();
                }
            }

        } else if (paraDisplay.isSettingsMode()) {
            // now check if we're in settings view and then process the input accordingly
            onControlPressedSettings(control, velocity);

        } else if (ParaUtil.patternControls.contains(control)) {
            // find the control's index and get the corresponding pattern
            GridControl selectedControl = ParaUtil.patternControls.get(control);
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
            paraDisplay.drawPatternEditControls(true, false);

        } else if (patternClearControl.equals(control)) {
            patternEditIndexBuffer.clear();
            patternEditing = true;
            paraDisplay.drawPatternEditControls(false, true);

        } else if (ParaUtil.stepControls.contains(control)) {
            // unselect the current selected step and redraw it
            ParaStep step = memory.currentStep();
            step.setSelected(false);
            // find the control's index and get the corresponding step
            Integer index = ParaUtil.stepControls.getIndex(control);
            memory.selectStep(index);
            step = memory.selectedStep();
            switch (memory.getStepSelectMode()) {
                case TOGGLE:
                    step.toggleEnabled();
                case SELECT:
                    stepEditing = true;
                    paraDisplay.setStepEditing(stepEditing);
                    paraDisplay.drawStep(memory, step, false);
                    paraDisplay.drawKeyboard(memory);
                    paraDisplay.drawKeyboardNotes(step.getNotes(), false, true, memory.getStepSelectMode());
                    int count = step.getVelocity() / 16 + 1;
                    memory.setValueState(ValueState.VELOCITY);
                    paraDisplay.drawValue(count, ValueState.VELOCITY);
                    break;
                case CONTROL:
                    int controller = memory.getSelectedController();
                    step.toggleControllerEnabled(controller);
                    paraDisplay.drawStep(memory, step, false);
                    count = step.getControllerValue(controller) / 16 + 1;
                    memory.setValueState(ValueState.CONTROL);
                    paraDisplay.drawValue(count, ValueState.CONTROL);
                    break;
            }

        } else if (ParaUtil.keyboardControls.contains(control) &&
                (memory.getStepSelectMode() == TOGGLE || memory.getStepSelectMode() == SELECT)) {
            // find the control's index, get the current step
            Integer index = ParaUtil.keyboardControls.getIndex(control);
            ParaStep step = memory.selectedStep();
            int note = currentKeyboardOctave * 12 + index;
            if (monophonic) {
                step.setNote(note);
            } else {
                step.toggleNote(note);
            }
            paraDisplay.drawStep(memory, step, false);
            paraDisplay.drawKeyboard(memory);
            paraDisplay.drawKeyboardNotes(step.getNotes(), false, true, memory.getStepSelectMode());

        } else if (ParaUtil.controllerSelectControls.contains(control) &&
                memory.getStepSelectMode() == CONTROL) {
            // find the control's index, get the current step
            Integer index = ParaUtil.controllerSelectControls.getIndex(control);
            if (index != null) {
                memory.setSelectedController(index);
                paraDisplay.drawControllerSelect(memory);
                paraDisplay.drawSteps(memory, memory.currentPattern().getSteps());
            }

        } else if (octaveDownControl.equals(control)) {
            currentKeyboardOctave--;
            if (currentKeyboardOctave < MIN_OCTAVE) {
                currentKeyboardOctave = MIN_OCTAVE;
            }
            paraDisplay.setCurrentKeyboardOctave(currentKeyboardOctave);
            paraDisplay.drawKeyboard(memory);
            if (stepEditing) {
                paraDisplay.drawKeyboardNotes(memory.selectedStep().getNotes(), false, true, memory.getStepSelectMode());
            }

        } else if (octaveUpControl.equals(control)) {
            currentKeyboardOctave++;
            if (currentKeyboardOctave > MAX_OCTAVE) {
                currentKeyboardOctave = MAX_OCTAVE;
            }
            paraDisplay.setCurrentKeyboardOctave(currentKeyboardOctave);
            paraDisplay.drawKeyboard(memory);
            if (stepEditing) {
                paraDisplay.drawKeyboardNotes(memory.selectedStep().getNotes(), false, true, memory.getStepSelectMode());
            }

        } else if (transposeDownControl.equals(control) || transposeUpControl.equals(control)) {
            int steps = 12;
            if (transposeDownControl.equals(control)) {
                steps = -12;
            }
            if (memory.selectedStep() != null) {
                memory.selectedStep().transpose(steps);
            }
            paraDisplay.drawKeyboard(memory);
            if (stepEditing) {
                paraDisplay.drawKeyboardNotes(memory.selectedStep().getNotes(), false, true, memory.getStepSelectMode());
            }

        } else if (ParaUtil.stepSelectModeControls.contains(control)) {
            // find the control's index, get the current step
            Integer index = ParaUtil.stepSelectModeControls.getIndex(control);
            if (index != null) {
                StepSelectMode[] states = StepSelectMode.values();
                if (index >= 0 && index < states.length) {
                    memory.setStepSelectMode(states[index]);
                    paraDisplay.redraw(memory);
// for another time
//                } else if (index == ParaUtil.STEP_CONTROL_SHIFT_LEFT_INDEX) {
//                    memory.currentPattern().shift(-1);
//                    paraDisplay.drawSteps(memory, memory.currentPattern().getSteps());
//                } else if (index == ParaUtil.STEP_CONTROL_SHIFT_RIGHT_INDEX) {
//                    memory.currentPattern().shift(1);
//                    paraDisplay.drawSteps(memory, memory.currentPattern().getSteps());
                }
            }
            paraDisplay.drawStepEditControls(memory.getStepSelectMode());

        } else if (ParaUtil.stepGateControls.contains(control)) {
            // find the control's index, get the current step
            Integer index = ParaUtil.stepGateControls.getIndex(control);
            if (index != null &&
                    (memory.getStepSelectMode() == TOGGLE || memory.getStepSelectMode() == SELECT)) {
                switch (index) {
                    case 3:
                        memory.selectedStep().setGate(PLAY);
                        break;
                    case 4:
                        memory.selectedStep().setGate(TIE);
                        break;
                }
            }
            paraDisplay.drawStep(memory, memory.selectedStep(), false);

        } else if (ParaUtil.valueControls.contains(control)) {
            ParaStep step = memory.selectedStep();
            if (step != null) {
                Integer index = ParaUtil.valueControls.getIndex(control);
                int value = (index + 1) * 16 - 1;
                paraDisplay.drawValue(index, memory.getValueState());
                switch (memory.getValueState()) {
                    case STEP_OCTAVE:
                        break;
                    case VELOCITY:
                        step.setVelocity(value);
                        break;
                    case KEYBOARD_OCTAVE:
                        break;
                    case CONTROL:
                        step.setControllerValue(memory.getSelectedController(), value);
                        break;
                    case NONE:
                        break;
                }
            }

        }

    }

    private void onControlReleased(GridControl control) {

        if (paraDisplay.isSettingsMode()) {
            // now check if we're in settings view and then process the input accordingly
            onControlReleasedSettings(control);

        } else if (ParaUtil.patternControls.contains(control)) {
            if (!patternEditing) {
                // releasing a pattern pad
                // don't activate until the last pattern pad is released (so additional releases don't look like a new press/release)
                patternsReleasedCount++;
                if (patternsReleasedCount >= patternsPressed.size()) {
                    GridControl selectedControl = ParaUtil.patternControls.get(control);
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

        } else if (ParaUtil.stepControls.contains(control)) {
            Integer index = ParaUtil.stepControls.getIndex(control);
            ParaStep step = memory.currentPattern().getStep(index);
            switch (memory.getStepSelectMode()) {
                case TOGGLE:
                case SELECT:
                    stepEditing = false;
                    paraDisplay.setStepEditing(stepEditing);
                    paraDisplay.drawStep(memory, step, false);
//                    paraDisplay.drawKeyboard(memory, step, false);
                    break;

                case CONTROL:
                    break;

            }

        } else if (patternCopyControl.equals(control)) {
            if (patternEditIndexBuffer.size() >= 2) {
                Integer fromIndex = patternEditIndexBuffer.get(0);
                Integer toIndex = patternEditIndexBuffer.get(1);
                if (fromIndex != null && toIndex != null) {
                    memory.currentSession().getPatterns()[toIndex] = ParaPattern.copy(memory.currentSession().getPattern(fromIndex), toIndex);;
                }
            }
            patternEditIndexBuffer.clear();
            patternEditing = false;
            paraDisplay.drawPatternEditControls(false, false);

        } else if (patternClearControl.equals(control)) {
            if (patternEditIndexBuffer.size() >= 0) {
                for (Integer index : patternEditIndexBuffer) {
                    memory.currentSession().getPatterns()[index] = new ParaPattern(index);
                }
            }
            patternEditIndexBuffer.clear();
            patternEditing = false;
            paraDisplay.drawPatternEditControls(false, false);

        }
    }

    private void onControlPressedSettings(GridControl control, int velocity) {

        SettingsUtil.SettingsChanged settingsChanged = settingsModule.controlPressed(control, velocity);
        switch (settingsChanged) {
            case SELECT_SESSION:
                selectSession(settingsModule.getNextSessionIndex());
                break;
            case LOAD_FILE:
                load(settingsModule.getCurrentFileIndex());
                break;
            case SAVE_FILE:
                save(settingsModule.getCurrentFileIndex());
                break;
            case SET_MIDI_CHANNEL:
                memory.setMidiChannel(settingsModule.getMidiChannel());
                break;
            case SET_SWING:
                swingOffset = settingsModule.getSwingOffset();
                break;
        }
    }

    private void onControlReleasedSettings(GridControl control) {

        SettingsUtil.SettingsChanged settingsChanged = settingsModule.controlReleased(control);
        switch (settingsChanged) {
            case COPY_SESSION:
                if (settingsModule.getCopyFromSessionIndex() != null && settingsModule.getCopyToSessionIndex() != null) {
                    ParaSession fromSession = memory.getSessions()[settingsModule.getCopyFromSessionIndex()];
                    int toSessionIndex = settingsModule.getCopyToSessionIndex();
                    memory.getSessions()[toSessionIndex] = ParaSession.copy(fromSession, toSessionIndex);
                    System.out.printf("Completed copy: %d -> %d\n", settingsModule.getCopyFromSessionIndex(), settingsModule.getCopyToSessionIndex());
                }
                break;

            case COPY_SESSION_TO_FILE:
                if (settingsModule.getCopyFromSessionIndex() != null && settingsModule.getCopyToSessionIndex() != null &&
                        settingsModule.getCopyToFileIndex() != null) {
                    ParaSession fromSession = memory.getSessions()[settingsModule.getCopyFromSessionIndex()];
                    int toSessionIndex = settingsModule.getCopyToSessionIndex();
                    int toFileIndex = settingsModule.getCopyToFileIndex();
                    ParaMemory toMemory = loadMemory(toFileIndex);
                    toMemory.setMidiChannel(memory.getMidiChannel());  // midi channel is per memory, which is kind of weird, but ok
                    toMemory.setPatternChainMin(memory.getPatternChainMin()); // same with these
                    toMemory.setPatternChainMax(memory.getPatternChainMax());
                    toMemory.setCurrentPatternIndex(memory.getCurrentPatternIndex());
                    toMemory.getSessions()[toSessionIndex] =ParaSession.copy(fromSession, toSessionIndex);
                    saveMemory(toFileIndex, toMemory);
                    System.out.printf("Completed copy to file: %d -> %d, f=%d\n",
                            settingsModule.getCopyFromSessionIndex(), settingsModule.getCopyToSessionIndex(), settingsModule.getCopyToFileIndex());
                }
                break;

            case CLEAR_SESSION:
                Integer sessionIndex = settingsModule.getClearSessionIndex();
                if (sessionIndex != null) {
                    memory.getSessions()[sessionIndex] = new ParaSession(sessionIndex);
                    System.out.printf("Completed clear session %d\n", sessionIndex);
                }
                break;
        }
    }

    public void onKnobChanged(GridKnob knob, int delta) {}
    public void onKnobSet(GridKnob knob, int value) {}



    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        memory.resetChain();
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

    public void clock(int measure, int beat, int pulse) {
        if (pulse == 0 || pulse == 6 + swingOffset || pulse == 12 || pulse == 18 + swingOffset) {
            advance(beat == 0 && pulse == 0);
        }
    }


    /***** Saveable implementation ****************************************/

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void save(int index) {
        saveMemory(index, memory);
    }

    public void saveMemory(int index, ParaMemory saveMemory) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                // make a backup, but will overwrite any previous backups
                Files.copy(file, new File(filename + ".backup"));
            }
            objectMapper.writeValue(file, saveMemory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(int index) {
        memory = loadMemory(index);
    }

    public ParaMemory loadMemory(int index) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                return objectMapper.readValue(file, ParaMemory.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ParaMemory();
    }

    private String filename(int index) {
        return filePrefix + "-" + index + ".json";
    }



}
