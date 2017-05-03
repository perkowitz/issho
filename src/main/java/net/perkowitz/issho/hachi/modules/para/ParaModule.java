package net.perkowitz.issho.hachi.modules.para;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Saveable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.ChordModule;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.Muteable;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.util.*;

import static net.perkowitz.issho.hachi.modules.para.ParaUtil.*;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.Gate.PLAY;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.Gate.TIE;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.View.SEQUENCE;

/**
 * Created by optic on 10/24/16.
 */
public class ParaModule extends ChordModule implements Module, Clockable, GridListener, Sessionizeable, Saveable, Muteable {

    private static int MAX_VELOCITY = 127;
    private static int MAX_OCTAVE = 7;

    ObjectMapper objectMapper = new ObjectMapper();

    private ParaMemory memory = new ParaMemory();
    private int nextStepIndex = 0;
    private ParaDisplay paraDisplay;

    private Set<Integer> onNotes = Sets.newHashSet();

    private static Timer timer = null;
    private static int flashIntervalMillis = 125;
    private static int flashCount = 0;

    private ParaStep lastStep = null;

    private View currentView = SEQUENCE;

    private Set<Integer> patternsPressed = Sets.newHashSet();
    private int patternsReleasedCount = 0;
    private List<Integer> patternEditIndexBuffer = Lists.newArrayList();
    private boolean patternEditing = false;
    private boolean stepEditing = false;
    private int currentKeyboardOctave = 5;

    private String filePrefix = "polymodule";
    private int currentFileIndex = 0;

    private int transpose = 0;


    /***** Constructor ****************************************/

    public ParaModule(Transmitter inputTransmitter, Receiver outputReceiver, Map<Integer, Color> palette, String filePrefix) {
        super(inputTransmitter, outputReceiver);
        paraDisplay = new ParaDisplay(this.display);
        paraDisplay.setPalette(palette);
        this.filePrefix = filePrefix;
//        startTimer();
        load(0);
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
            paraDisplay.drawKeyboardNotes(onNotes, true, false);
        }

        // advance to the next step and decide what to do
        ParaStep step = memory.currentPattern().getStep(nextStepIndex);
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
        if (drawSessions) { paraDisplay.drawSessions(memory); }
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
            paraDisplay.drawKeyboardNotes(onNotes, false, false);
        }

        // always draw the step itself
        paraDisplay.drawStep(memory, step, true);

        lastStep = step;
        nextStepIndex = (nextStepIndex + 1) % ParaPattern.STEP_COUNT;
    }

    private void displayValue(int value, int maxValue) {
        int valueAsEight = (value * 8) / maxValue;
        for (int index = 0; index < 8; index++) {
            GridControl control = ParaUtil.valueControls.get(index);
            if (index <= valueAsEight) {
                control.draw(display, paraDisplay.getPalette().get(ParaUtil.COLOR_VALUE_ON));
            } else {
                control.draw(display, paraDisplay.getPalette().get(ParaUtil.COLOR_VALUE_OFF));
            }
        }
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

    public void startTimer() {

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
//                ParaStep step = memory.currentStep();
//                if (step != null) {
//                    if (flashCount == 0) {
//                        paraDisplay.drawStepOff(step);
//                    } else {
//                        paraDisplay.drawStep(step);
//                    }
//                }
//                flashCount = (flashCount + 1) % 2;
            }
        }, flashIntervalMillis, flashIntervalMillis);
    }


    /***** Module implementation ***********************************/

    public void redraw() {
        paraDisplay.redraw(memory);
        paraDisplay.drawFunctions(isMuted);
    }

    public void setDisplay(GridDisplay display) {
        this.display = display;
        this.paraDisplay.setDisplay(display);
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
        paraDisplay.drawSessions(memory);
    }

    public void selectPatterns(int firstIndex, int lastIndex) {
        memory.selectPatternChain(firstIndex, lastIndex);
        patternsPressed.clear();
        patternsReleasedCount = 0;
        paraDisplay.drawPatterns(memory);
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

        if (paraDisplay.isSettingsMode()) {
            onControlPressedSettings(control, velocity);
            return;
        }

        if (ParaUtil.patternControls.contains(control)) {
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
//            paraDisplay.drawStep(step);

            // find the control's index and get the corresponding step
            Integer index = ParaUtil.stepControls.getIndex(control);
            memory.selectStep(index);
            step = memory.selectedStep();
            switch (memory.getStepSelectMode()) {
                case TOGGLE:
                    step.toggleEnabled();
                    break;
                case SELECT:
                    break;
            }
            stepEditing = true;
            paraDisplay.setStepEditing(stepEditing);
            paraDisplay.drawStep(memory, step, false);
            paraDisplay.drawKeyboard();
            paraDisplay.drawKeyboardNotes(step.getNotes(), false, true);

        } else if (ParaUtil.keyboardControls.contains(control)) {
            // find the control's index, get the current step
            Integer index = ParaUtil.keyboardControls.getIndex(control);
            ParaStep step = memory.selectedStep();
            int note = currentKeyboardOctave * 12 + index;
            step.toggleNote(note);
            paraDisplay.drawStep(memory, step, false);
            paraDisplay.drawKeyboard();
            paraDisplay.drawKeyboardNotes(step.getNotes(), false, true);

        } else if (ParaUtil.stepSelectModeControls.contains(control)) {
            // find the control's index, get the current step
            Integer index = ParaUtil.stepSelectModeControls.getIndex(control);
            if (index != null) {
                StepSelectMode[] states = StepSelectMode.values();
                if (index >= 0 && index < states.length) {
                    memory.setStepSelectMode(states[index]);
                } else if (index == ParaUtil.STEP_CONTROL_SHIFT_LEFT_INDEX) {
                    memory.currentPattern().shift(-1);
                    paraDisplay.drawSteps(memory, memory.currentPattern().getSteps());
                } else if (index == ParaUtil.STEP_CONTROL_SHIFT_RIGHT_INDEX) {
                    memory.currentPattern().shift(1);
                    paraDisplay.drawSteps(memory, memory.currentPattern().getSteps());
                }
            }
            paraDisplay.drawStepEditControls(memory.getStepSelectMode());

        } else if (ParaUtil.stepGateControls.contains(control)) {
            // find the control's index, get the current step
            Integer index = ParaUtil.stepGateControls.getIndex(control);
            if (index != null) {
                switch (index) {
                    case 2:
                        memory.currentStep().setGate(PLAY);
                        break;
                    case 3:
                        memory.currentStep().setGate(TIE);
                        break;
                }
            }
            paraDisplay.drawStep(memory, memory.currentStep(), false);

        } else if (ParaUtil.valueControls.contains(control)) {

            ParaStep step = memory.currentStep();
            if (step != null) {
                Integer index = ParaUtil.valueControls.getIndex(control);
                displayValue(index, 7);
                switch (memory.getValueState()) {
                    case STEP_OCTAVE:
                        break;
                    case VELOCITY:
                        step.setVelocity((index + 1) * 16 - 1);
                        break;
                    case KEYBOARD_OCTAVE:
                        break;
                    case NONE:
                        break;
                }
            }

        } else if (ParaUtil.functionControls.contains(control)) {

            // find the control's index, get the current step
            Integer index = ParaUtil.functionControls.getIndex(control);
            if (index != null) {
                if (index == FUNCTION_SAVE_INDEX) {
                    save(currentFileIndex);
//                } else if (index == FUNCTION_LOAD_INDEX) {
//                    load(currentFileIndex);
//                    paraDisplay.redraw(memory);
                } else if (index == FUNCTION_MUTE_INDEX) {
                    this.isMuted = !isMuted;
                    paraDisplay.redraw(memory);
                } else if (index == FUNCTION_SETTINGS_INDEX) {
                    paraDisplay.toggleSettings();
                    paraDisplay.initialize();
                    paraDisplay.redraw(memory);
                }
                paraDisplay.drawFunctions(isMuted);
            }

        }

    }

    private void onControlPressedSettings(GridControl control, int velocity) {

        if (ParaUtil.sessionControls.contains(control)) {
            Integer index = ParaUtil.sessionControls.getIndex(control);
            selectSession(index);

        } else if (ParaUtil.loadControls.contains(control)) {
            Integer index = ParaUtil.loadControls.getIndex(control);
            currentFileIndex = index;
            paraDisplay.setCurrentFileIndex(currentFileIndex);
            load(currentFileIndex);
            paraDisplay.redraw(memory);

        } else if (ParaUtil.saveControls.contains(control)) {
            Integer index = ParaUtil.saveControls.getIndex(control);
            currentFileIndex = index;
            paraDisplay.setCurrentFileIndex(currentFileIndex);
            save(currentFileIndex);
            paraDisplay.redraw(memory);

        } else if (ParaUtil.midiChannelControls.contains(control)) {
            Integer index = ParaUtil.midiChannelControls.getIndex(control);
            notesOff();
            memory.setMidiChannel(index);
            paraDisplay.drawMidiChannel(memory);

        } else if (ParaUtil.functionControls.contains(control)) {
            Integer index = ParaUtil.functionControls.getIndex(control);
            if (index != null) {
                if (index == FUNCTION_SETTINGS_INDEX) {
                    paraDisplay.toggleSettings();
                    paraDisplay.initialize();
                    paraDisplay.redraw(memory);
                }
                paraDisplay.drawFunctions(isMuted);
            }

        }
    }

    private void onControlReleased(GridControl control) {

        if (ParaUtil.patternControls.contains(control)) {

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
            stepEditing = false;
            paraDisplay.setStepEditing(stepEditing);
            paraDisplay.drawStep(memory, step, false);
//            paraDisplay.drawKeyboard(memory, step, false);

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
                memory = objectMapper.readValue(file, ParaMemory.class);
            } else {
                memory = new ParaMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String filename(int index) {
        return filePrefix + "-" + index + ".json";
    }



}
