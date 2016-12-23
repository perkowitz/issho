package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.Chordable;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Saveable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.*;
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
public class StepModule extends MidiModule implements Module, Clockable, GridListener, Sessionizeable, Chordable, Saveable, Muteable {

    ObjectMapper objectMapper = new ObjectMapper();

    private StepMemory memory = new StepMemory();
    private StepDisplay stepDisplay;
    private SettingsSubmodule settingsModule;
    private boolean settingsView = false;

    private Set<Integer> onNotes = Sets.newHashSet();

    private String filePrefix = "monomodule";
    private int currentFileIndex = 0;
    private int midiChannel = 0;

    private int currentStageIndex = 0;
    private int currentStageStepIndex = 0;
    private List<Step> currentSteps = null;
    private Stage.Marker currentMarker = Note;
    private List<Integer> stagesToRedraw = Lists.newArrayList();
    private boolean randomOrder = false;
    private boolean displayAltControls = false;
    private boolean savingPattern = false;


    /***** Constructor ****************************************/

    public StepModule(Transmitter inputTransmitter, Receiver outputReceiver, String filePrefix) {
        super(inputTransmitter, outputReceiver);
        this.stepDisplay = new StepDisplay(this.display);
        this.filePrefix = filePrefix;
        load(0);
        currentSteps = currentStage().getSteps();
        this.settingsModule = new SettingsSubmodule();
        currentMarker = Note;
        stepDisplay.setCurrentMarker(currentMarker);
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
        playStep(currentSteps.get(currentStageStepIndex));

        nextStep();
    }

    private void playStep(Step step) {

        List<Integer> noteIndices = Lists.newArrayList();
        switch (step.getMode()) {
            case Play:
            case Slide:
                noteIndices = currentStage().findMarker(Stage.Marker.Note);
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
            stepDisplay.drawActiveNote(GridPad.at(currentStageIndex, 7 - index));
        }

        switch (step.getMode()) {
            case Play:
                notesOff();
                onNotes.add(step.getNote());
                sendMidiNote(memory.getMidiChannel(), step.getNote(), step.getVelocity());
                break;
            case Tie:
                break;
            case Rest:
                notesOff();
                break;
            case Slide:
                sendMidiNote(memory.getMidiChannel(), step.getNote(), step.getVelocity());
//                if (!onNotes.contains(step.getNote())) {
//                    notesOff();
//                }
                // NOTE: the Sub 37 requires you to send a note off for every note on, even if you send 2 note ons for the same note
                // not sure if other synths do this; if others do not, should remove this notesOff() and uncomment above if()
                notesOff();
                onNotes.add(step.getNote());
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


    /***** Chordable implementation ***********************************/

    public void setChordNotes(List<Integer> notes) {

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

        } else if (settingsView) {
               onControlPressedSettings(control, velocity);

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
        }
    }

    private void onControlReleased(GridControl control) {
        if (control.equals(StepUtil.saveControl)) {
            stepDisplay.drawControl(StepUtil.saveControl, false);
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

    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        notesOff();
        sendAllNotesOff(memory.getMidiChannel());
        currentStageIndex = 0;
        currentStageStepIndex = 0;
    }

    public void stop() {
        notesOff();
        sendAllNotesOff(memory.getMidiChannel());
        currentStageIndex = 0;
        currentStageStepIndex = 0;
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
                memory = objectMapper.readValue(file, StepMemory.class);
            } else {
                memory = new StepMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String filename(int index) {
        return filePrefix + "-" + index + ".json";
    }



}
