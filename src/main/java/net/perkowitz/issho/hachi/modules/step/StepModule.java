package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Saveable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.*;
import net.perkowitz.issho.hachi.modules.Module;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.util.List;
import java.util.Set;

import static net.perkowitz.issho.hachi.modules.step.Stage.Marker.*;
import static net.perkowitz.issho.hachi.modules.step.StepUtil.*;

/**
 * Created by optic on 10/24/16.
 */
public class StepModule extends ChordModule implements Module, Clockable, GridListener, Sessionizeable, Saveable, Muteable {

    ObjectMapper objectMapper = new ObjectMapper();

    private StepMemory memory = new StepMemory();
    private StepDisplay stepDisplay;
    private SettingsSubmodule settingsModule;
    private boolean settingsView = false;

    private Set<Integer> onNotes = Sets.newHashSet();

    private String filePrefix = "monomodule";
    private int currentFileIndex = 0;

    private int currentStageIndex = 0;
    private int currentStageStepIndex = 0;
    private List<Step> currentSteps = null;
    private Stage.Marker currentMarker = Note;
    private List<Integer> stagesToRedraw = Lists.newArrayList();
    private boolean randomOrder = false;
    private boolean previousRandomOrder = false;
    private boolean displayAltControls = false;
    private boolean savingPattern = false;
    int swingOffset = 0;


    /***** Constructor ****************************************/

    public StepModule(Transmitter inputTransmitter, Receiver outputReceiver, String filePrefix) {
        super(inputTransmitter, outputReceiver);
        this.stepDisplay = new StepDisplay(this.display);
        this.filePrefix = filePrefix;
        currentSteps = currentStage().getSteps();
        this.settingsModule = new SettingsSubmodule(true, true, true, true);
        load(0);
        currentMarker = Note;
        stepDisplay.setCurrentMarker(currentMarker);
    }


    public String name() {
        return "Step";
    }

    public String shortName() {
        return "Step";
    }


    /***** private implementation ****************************************/

    private void advance(boolean andReset) {

        for (Integer redrawIndex : stagesToRedraw) {
            stepDisplay.drawStage(memory, redrawIndex);
        }
        stagesToRedraw.clear();

        if (andReset) {
            currentStageIndex = 0;
            currentStageStepIndex = 0;
            currentSteps = currentStage().getSteps();
            // need to make sure stage0 has at least one step
            if (memory.getCurrentSessionIndex() != memory.getNextSessionIndex()) {
                memory.setCurrentSessionIndex(memory.getNextSessionIndex());
                settingsModule.setCurrentSessionIndex(memory.getCurrentSessionIndex());
                redraw();
            }
        }

        stagesToRedraw.add(currentStageIndex);

        // we assume that the current stage/step exists (make sure of that during increment)
        // and we don't recompute current steps until we go to next stage
        if (currentSteps.size() == 0) {
            nextStep();
        }
        playStep(currentSteps.get(currentStageStepIndex));

        nextStep();
    }

    private void playStep(Step step) {

        // there may be multiple markers to highlight in the step
        List<Integer> noteIndices = Lists.newArrayList();
        switch (step.getMode()) {
            case Play:
            case Slide:
                if (step.getHighlightedIndex() != null) {
                    noteIndices.add(step.getHighlightedIndex());
                }
                break;
            case Tie:
                noteIndices = currentStage().findMarker(Stage.Marker.Tie);
                if (noteIndices.size() == 0) {
                    noteIndices = currentStage().findMarker(Stage.Marker.Note);
                }
                break;
            case Rest:
                break;
        }
        for (Integer index : noteIndices) {
            if (index.equals(step.getHighlightedIndex()) || step.getMode() == Step.Mode.Tie) {
                stepDisplay.drawActiveNote(GridPad.at(currentStageIndex, 7 - index));
            }
        }

        switch (step.getMode()) {
            case Play:
                notesOff();
                int note = step.getNote();
                onNotes.add(note);
                sendMidiNote(memory.getMidiChannel(), note, step.getVelocity());
                break;
            case Tie:
                break;
            case Rest:
                notesOff();
                break;
            case Slide:
                note = step.getNote();
                sendMidiNote(memory.getMidiChannel(), note, step.getVelocity());
//                if (!onNotes.contains(step.getNote())) {
//                    notesOff();
//                }
                // NOTE: the Sub 37 requires you to send a note off for every note on, even if you send 2 note ons for the same note
                // not sure if other synths do this; if others do not, should remove this notesOff() and uncomment above if()
                notesOff();
                onNotes.add(note);
                break;
        }
    }

    private void nextStep() {
        currentStageStepIndex++;
        currentSteps = currentStage().getSteps();
        while (currentStageStepIndex >= currentSteps.size()) {
            // todo this could loop forever if all stages are SKIP
//            stagesToRedraw.add(currentStageIndex);
            if (randomOrder) {
                currentStageIndex = (int)(Math.random() * StepPattern.STAGE_COUNT);
            } else {
                currentStageIndex++;
            }
            currentStageIndex = currentStageIndex % StepPattern.STAGE_COUNT;
            currentSteps = currentStage().getSteps();
            currentStageStepIndex = 0;
        }
    }

    private Stage currentStage() {
        return memory.currentPattern().getStage(currentStageIndex);
    }

    private void notesOff() {
        for (Integer note : onNotes) {
            sendMidiNote(memory.getMidiChannel(), note, 0);

        }
        onNotes.clear();
    }


    /***** Module implementation ***********************************/

    public void redraw() {
        stepDisplay.initialize();
        if (settingsView) {
            settingsModule.redraw();
            stepDisplay.drawLeftControls();
        } else {
            stepDisplay.redraw(memory);
        }
    }

    public void setDisplay(GridDisplay display) {
        this.display = display;
        this.stepDisplay.setDisplay(display);
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
    }

    public void selectPatterns(int firstIndex, int lastIndex) {
        memory.setCurrentPatternIndex(firstIndex);
        stepDisplay.drawPatterns(memory);
        stepDisplay.drawStages(memory);
    }

    public void fillOn(Integer fillIndex) {
        previousRandomOrder = randomOrder;
        randomOrder = true;
        stepDisplay.setRandomOrder(randomOrder);
        stepDisplay.drawMarkers();
    }

    public void fillOff() {
        randomOrder = previousRandomOrder;
        stepDisplay.setRandomOrder(randomOrder);
        stepDisplay.drawMarkers();
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

        if (control.equals(StepUtil.settingsControl)) {
            settingsView = !settingsView;
            stepDisplay.setSettingsView(settingsView);
            this.redraw();

        } else if (control.equals(StepUtil.muteControl)) {
            isMuted = !isMuted;
            stepDisplay.setMuted(isMuted);
            stepDisplay.drawLeftControls();

        } else if (control.equals(StepUtil.saveControl)) {
            save(currentFileIndex);
            stepDisplay.drawControl(StepUtil.saveControl, true);

        } else if (settingsView) {
            onControlPressedSettings(control, velocity);

        } else if (control.equals(StepUtil.savePatternControl)) {
            savingPattern = true;
            stepDisplay.drawControl(StepUtil.savePatternControl, true);

//        } else if (control.equals(StepUtil.panicControl)) {
//            sendMidiCC(memory.getMidiChannel(), MidiModule.MIDI_ALL_NOTES_OFF_CC, 0);
//            stepDisplay.drawControl(StepUtil.panicControl, true);

        } else if (control.equals(StepUtil.altControlsControl)) {
            displayAltControls = !displayAltControls;
            stepDisplay.setDisplayAltControls(displayAltControls);
            stepDisplay.drawControl(StepUtil.altControlsControl, displayAltControls);
            stepDisplay.drawMarkers();

        } else if (markerControls.contains(control) && !displayAltControls) {
            Stage.Marker newMarker = markerPaletteMap.get(control);
            if (currentMarker == newMarker) {
                if (currentMarker == OctaveUp) {
                    newMarker = OctaveDown;
                } else if (currentMarker == OctaveDown) {
                    newMarker = OctaveUp;
                } else if (currentMarker == Sharp) {
                    newMarker = Flat;
                } else if (currentMarker == Flat) {
                    newMarker = Sharp;
                } else if (currentMarker == VolumeUp) {
                    newMarker = VolumeDown;
                } else if (currentMarker == VolumeDown) {
                    newMarker = VolumeUp;
                } else if (currentMarker == Repeat) {
                    newMarker = Skip;
                } else if (currentMarker == Skip) {
                    newMarker = Repeat;
                } else if (currentMarker == Longer) {
                    newMarker = Tie;
                } else if (currentMarker == Tie) {
                    newMarker = Longer;
                }
            }
            currentMarker = newMarker;
            stepDisplay.setCurrentMarker(currentMarker);
            stepDisplay.drawLeftControls();

        } else if (patternControls.contains(control)) {
            int patternIndex = patternControls.getIndex(control);
            if (!savingPattern) {
                memory.setCurrentPatternIndex(patternIndex);
                stepDisplay.drawPatterns(memory);
                stepDisplay.drawStages(memory);
            } else {
                StepPattern pattern = StepPattern.copy(memory.currentPattern(), patternIndex);
                memory.currentSession().setPattern(patternIndex, pattern);
                stepDisplay.drawControl(patternControls.get(patternIndex), true);
            }

        } else if (control.getPad() != null) {
            GridPad pad = control.getPad();
            Stage stage = memory.currentPattern().getStage(pad.getX());
            int index = 7 - pad.getY();
            if (currentMarker == stage.getMarker(index)) {
                stage.putMarker(index, Stage.Marker.None);
                control.draw(display, StepUtil.MARKER_COLORS.get(Stage.Marker.None));
            } else {
                stage.putMarker(index, currentMarker);
                control.draw(display, StepUtil.MARKER_COLORS.get(currentMarker));
            }

        // we only get to these if a marker is pressed and we're on alt controls
        } else if (control.equals(StepUtil.randomOrderControl)) {
            randomOrder = !randomOrder;
            stepDisplay.setRandomOrder(randomOrder);
            stepDisplay.drawMarkers();

        } else if (control.equals(StepUtil.shiftLeftControl)) {
            memory.currentPattern().shift(-1);
            redraw();
            stepDisplay.drawControl(StepUtil.shiftLeftControl, true);

        } else if (control.equals(StepUtil.shiftRightControl)) {
            memory.currentPattern().shift(1);
            redraw();
            stepDisplay.drawControl(StepUtil.shiftRightControl, true);

        }

    }

    private void onControlReleased(GridControl control) {
        if (control.equals(StepUtil.saveControl)) {
            stepDisplay.drawControl(StepUtil.saveControl, false);
        } else if (settingsView) {
            onControlReleasedSettings(control);
        } else if (control.equals(StepUtil.shiftLeftControl) && displayAltControls) {
            stepDisplay.drawControl(StepUtil.shiftLeftControl, false);
        } else if (control.equals(StepUtil.shiftRightControl)  && displayAltControls) {
            stepDisplay.drawControl(StepUtil.shiftRightControl, false);
        } else if (control.equals(StepUtil.savePatternControl)) {
            savingPattern = false;
            stepDisplay.drawControl(StepUtil.savePatternControl, false);
//        } else if (control.equals(StepUtil.panicControl)) {
//            stepDisplay.drawControl(StepUtil.panicControl, false);
        } else if (patternControls.contains(control)) {
            stepDisplay.drawPatterns(memory);
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
                notesOff();
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
                    StepSession fromSession = memory.getSessions()[settingsModule.getCopyFromSessionIndex()];
                    int toSessionIndex = settingsModule.getCopyToSessionIndex();
                    memory.getSessions()[toSessionIndex] =  StepSession.copy(fromSession, toSessionIndex);
                    System.out.printf("Completed copy: %d -> %d\n", settingsModule.getCopyFromSessionIndex(), settingsModule.getCopyToSessionIndex());
                }
                break;

            case COPY_SESSION_TO_FILE:
                if (settingsModule.getCopyFromSessionIndex() != null && settingsModule.getCopyToSessionIndex() != null &&
                        settingsModule.getCopyToFileIndex() != null) {
                    StepSession fromSession = memory.getSessions()[settingsModule.getCopyFromSessionIndex()];
                    int toSessionIndex = settingsModule.getCopyToSessionIndex();
                    int toFileIndex = settingsModule.getCopyToFileIndex();
                    StepMemory toMemory = loadMemory(toFileIndex);
                    toMemory.setMidiChannel(memory.getMidiChannel());
                    toMemory.getSessions()[toSessionIndex] = StepSession.copy(fromSession, toSessionIndex);
                    saveMemory(toFileIndex, toMemory);
                    System.out.printf("Completed copy to file: %d -> %d, f=%d\n",
                            settingsModule.getCopyFromSessionIndex(), settingsModule.getCopyToSessionIndex(), settingsModule.getCopyToFileIndex());
                }
                break;

            case CLEAR_SESSION:
                Integer sessionIndex = settingsModule.getClearSessionIndex();
                if (sessionIndex != null) {
                    memory.getSessions()[sessionIndex] = new StepSession(sessionIndex);
                    System.out.printf("Completed clear session %d\n", sessionIndex);
                }
                break;
        }
    }


    public void onKnobChanged(GridKnob knob, int delta) {}
    public void onKnobSet(GridKnob knob, int value) {}


    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        notesOff();
        sendAllNotesOff(memory.getMidiChannel());
        if (restart) {
            currentStageIndex = 0;
            currentStageStepIndex = 0;
        }
    }

    public void stop() {
        notesOff();
        sendAllNotesOff(memory.getMidiChannel());
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

    public void saveMemory(int index, StepMemory saveMemory) {
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
        settingsModule.setMidiChannel(memory.getMidiChannel());
    }

    public StepMemory loadMemory(int index) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                return objectMapper.readValue(file, StepMemory.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new StepMemory();
    }

    private String filename(int index) {
        return filePrefix + "-" + index + ".json";
    }



}
