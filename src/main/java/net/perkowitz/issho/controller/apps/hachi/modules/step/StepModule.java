/**
 * StepModule
 *
 * Notes on stage count: originally, Step was designed for the Launchpad, and
 * so supported 8 stages per pattern, to match the 8 columns of pads. With
 * a larger controller, Step should be able to support more stages.
 * - the module is set to 8 or 16 stage count
 * - this setting is system-wide (static), not per pattern; it is not stored
 * - Hachi can override the setting at launch based on hardware available
 * - changing the setting affects the behavior but does not change the underlying data
 * - if the setting is 8, stages >8 will be ignored
 * - if a pattern has 8 stages and the setting is 16, the extra stages will be treated as "skip"
 * - in that case, those stages can be edited and saved
 *
 *
 */
package net.perkowitz.issho.controller.apps.hachi.modules.step;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.controller.apps.hachi.Palette;
import net.perkowitz.issho.controller.apps.hachi.modules.Module;
import net.perkowitz.issho.controller.apps.hachi.modules.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.midi.MidiOut;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.Set;

import static net.perkowitz.issho.controller.apps.hachi.modules.step.Stage.Marker.*;


/**
 * Created by optic on 10/24/16.
 */
public class StepModule implements Module, SaveableModule, MidiModule, ModuleListener {

    private static final int LOG_LEVEL = Log.OFF;

    ObjectMapper objectMapper = new ObjectMapper();

    private ModuleController controller;
    private Settings settingsModule;
    private MidiOut midiOut;

    @Getter private boolean muted = false;
    @Getter private Palette palette = Palette.DEFAULT;

    private StepDisplay stepDisplay;
    @Getter @Setter private static int stageCount = 8;
    private StepMemory memory = new StepMemory();

    private Set<Integer> onNotes = Sets.newHashSet();

    // file save/load
    private String filePrefix = "step";
    @Getter @Setter private int fileIndex = 0;

    private boolean settingsView = false;
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

    public StepModule(ModuleController controller, MidiOut midiOut, Palette palette, String filePrefix) {
        this.controller = controller;
        this.midiOut = midiOut;
        this.settingsModule = new Settings(controller, this);
        this.palette = palette;
        this.filePrefix = filePrefix;
        load(fileIndex);
        currentSteps = currentStage().getSteps();
        currentMarker = Note;
        stepDisplay = new StepDisplay(controller, settingsModule);
        stepDisplay.setCurrentMarker(currentMarker);   // translate this
    }


    public String name() {
        return "Step";
    }

    public String shortName() {
        return "Step";
    }

    public String[] buttonLabels() {
        if (displayAltControls) {
            return StepUtil.BUTTON_LABELS_ALT;
        }
        return StepUtil.BUTTON_LABELS;
    }

    public void flipMuted() {
        setMuted(!muted);
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        if (muted) {
            notesOff();
        }
        stepDisplay.setMuted(muted);
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
        stepDisplay.setPalette(palette);
    }

    public void toggleStageCount() {
        if (stageCount == 8) {
            stageCount = 16;
        } else {
            stageCount = 8;
        }
    }

    /***** private implementation ****************************************/

    private void advance(boolean andReset) {

        // keep track of what needs to be redrawn AFTER midi notes are sent
        boolean mustRedrawStages = false;

        if (andReset) {
            currentStageIndex = 0;
            currentStageStepIndex = 0;
            currentSteps = currentStage().getSteps();
            // TODO: need to make sure stage0 has at least one step
            if (memory.getCurrentSessionIndex() != memory.getNextSessionIndex()) {
                memory.setCurrentSessionIndex(memory.getNextSessionIndex());
//                settingsModule.setCurrentSessionIndex(memory.getCurrentSessionIndex());
                mustRedrawStages = true;
            }
        }

        // we assume that the current stage/step exists (make sure of that during increment)
        // and we don't recompute current steps until we go to next stage
        if (currentSteps.size() == 0) {
            nextStep();
        }
        playStep(currentSteps.get(currentStageStepIndex));

        if (mustRedrawStages) {
            stepDisplay.drawStages(memory);
        } else {
            // on the next step, we need to redraw this step to make sure the
            // currently-playing note is highlighted and then turned off
            // TODO: this doesn't work for multiple steps played on one stage, only displays first note
            for (Integer redrawIndex : stagesToRedraw) {
                stepDisplay.drawStage(memory, redrawIndex);
            }
        }
        stagesToRedraw.clear();
        stagesToRedraw.add(currentStageIndex);

        nextStep();
    }

    private void playStep(Step step) {

        if (midiOut == null) return;

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
                    noteIndices = currentStage().findMarker(Note);
                }
                break;
            case Rest:
                break;
        }

        if (!muted) {
            switch (step.getMode()) {
                case Play:
                    notesOff();
                    int note = step.getNote();
                    onNotes.add(note);
//                    Log.log(this, Log.INFO, "Note - %s", Log.stopWatchTimes());
                    midiOut.note(memory.getMidiChannel(), note, step.getVelocity());
                    break;
                case Tie:
                    break;
                case Rest:
                    notesOff();
                    break;
                case Slide:
                    note = step.getNote();
                    midiOut.note(memory.getMidiChannel(), note, step.getVelocity());
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

        for (Integer index : noteIndices) {
            if (index.equals(step.getHighlightedIndex()) || step.getMode() == Step.Mode.Tie) {
                stepDisplay.drawActiveNote(7-index, currentStageIndex);
            }
        }
    }

    private void nextStep() {
        try {
            currentStageStepIndex++;
            if (currentStage() == null) {
                nextStage();
                return;
            }
            currentSteps = currentStage().getSteps();
            int c = 0;
            while (currentStageStepIndex >= currentSteps.size() && c <= stageCount * 4) {
                // this will just loop until c gets too big if all stages are SKIP
                //            stagesToRedraw.add(currentStageIndex);
                nextStage();
                c++;
            }
            // TODO: figure out why NPEs are being swallowed here. is it because it's event handling?
        } catch (Exception e) {
            System.err.println(e);
        }

    }

    private void nextStage() {
        if (randomOrder) {
            currentStageIndex = (int) (Math.random() * stageCount);
        } else {
            currentStageIndex++;
        }

        while (currentStage() == null && currentStageIndex < stageCount) {
            // TODO handle random
            currentStageIndex++;
        }
        currentStageIndex = currentStageIndex % stageCount;
        if (currentStage() == null) {
            Stage stage = new Stage(currentStageIndex);
            memory.currentPattern().setStage(currentStageIndex, stage);
            stepDisplay.drawStage(memory, currentStageIndex);
        }
        if (currentStage().getRandomCount() > 0) {
            currentStage().computeSteps();
        }
        currentSteps = currentStage().getSteps();
        currentStageStepIndex = 0;
    }

    private Stage currentStage() {
        return memory.currentPattern().getStage(currentStageIndex);
    }

    private void notesOff() {
        for (Integer note : onNotes) {
            midiOut.note(memory.getMidiChannel(), note, 0);
        }
        onNotes.clear();
    }


    /***** Module implementation ***********************************/

    public void draw() {
        stepDisplay.draw(memory);
    }


    public void setController(ModuleController controller) {
        this.controller = controller;
        this.stepDisplay.setController(controller);
//        this.settingsModule.setDisplay(display);
    }

    public void shutdown() {
        notesOff();
    }


    /***** Jumpable implementation **************************/

    public void jumpTo(int index) {
        if (index < 0) return;
        currentStageIndex = index % stageCount;
        currentStageStepIndex = 0;
        currentSteps = currentStage().getSteps();
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


    /***** ModuleListener implementation *****/

    public void onPadPressed(int row, int column, int value) {
        if (settingsView) {
            settingsModule.onPadPressed(row, column, value);
            return;
        }

        Stage stage = memory.currentPattern().getStage(column);
        int index = 7 - row;
        if (stage == null && column >= 0 && column <= getStageCount()) {
            stage = new Stage(column);
            memory.currentPattern().setStage(column, stage);
            stepDisplay.drawStage(memory, column);
        } else if (stage == null) {
            return;
        }
        if (currentMarker == stage.getMarker(index)) {
            stage.putMarker(index, Stage.Marker.None);
            controller.setPad(row, column, StepUtil.MARKER_COLORS.get(Stage.Marker.None));
        } else {
            stage.putMarker(index, currentMarker);
            controller.setPad(row, column, StepUtil.MARKER_COLORS.get(currentMarker));
        }
    }

    public void onPadReleased(int row, int column) {
        if (settingsView) {
            settingsModule.onPadReleased(row, column);
            return;
        }
    }


    public void onButtonPressed(int group, int index, int value) {

        Button button = Button.at(group, index);
        Log.log(this, LOG_LEVEL, "button=%s", button);

        if (button.equals(StepUtil.settingsElement)) {
            settingsView = !settingsView;
            stepDisplay.setSettingsView(settingsView);
            if (settingsView) {
                settingsModule.draw();
            } else {
                stepDisplay.drawStages(memory);
            }
            stepDisplay.drawButton(button, settingsView);

        } else if (settingsView) {
            settingsModule.onButtonPressed(group, index, value);
            return;

        } else if (button.equals(StepUtil.copyPatternElement)) {
            savingPattern = true;
            stepDisplay.drawButton(button, true);

        } else if (button.equals(StepUtil.altControlsElement)) {
            displayAltControls = !displayAltControls;
            stepDisplay.setDisplayAltControls(displayAltControls);
            stepDisplay.drawButton(button, displayAltControls);
            stepDisplay.drawMarkers();

        } else if (button.equals(StepUtil.saveElement)) {
            stepDisplay.drawButton(button, true);
            this.save(fileIndex);

        } else if (StepUtil.markerElements.contains(button) && !displayAltControls) {
            Stage.Marker newMarker = StepUtil.markerPaletteMap.get(button);
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
                } else if (currentMarker == Slide) {
                    newMarker = Random;
                } else if (currentMarker == Random) {
                    newMarker = Slide;
                }
            }
            currentMarker = newMarker;
            stepDisplay.setCurrentMarker(currentMarker);
            stepDisplay.drawLeftControls();

        } else if (StepUtil.patternElements.contains(button)) {
            int patternIndex = StepUtil.patternElements.getIndex(button);
            if (!savingPattern) {
                memory.setCurrentPatternIndex(patternIndex);
                stepDisplay.drawPatterns(memory);
                stepDisplay.drawStages(memory);
            } else {
                StepPattern pattern = StepPattern.copy(memory.currentPattern(), patternIndex);
                memory.currentSession().setPattern(patternIndex, pattern);
                stepDisplay.drawButton(button, true);
            }

        // we only get to these if a marker is pressed and we're on alt controls
        } else if (button.equals(StepUtil.randomOrderElement)) {
            randomOrder = !randomOrder;
            stepDisplay.setRandomOrder(randomOrder);
            stepDisplay.drawMarkers();

        } else if (button.equals(StepUtil.shiftLeftElement)) {
            memory.currentPattern().shift(-1);
            draw();
            stepDisplay.drawButton(button, true);

        } else if (button.equals(StepUtil.shiftRightElement)) {
            memory.currentPattern().shift(1);
            draw();
            stepDisplay.drawButton(button, true);

        }
    }

    public void onButtonReleased(int group, int index) {
        if (settingsView) {
            settingsModule.onButtonReleased(group, index);
            return;
        }

        Button button = Button.at(group, index);
        if (button.equals(StepUtil.shiftLeftElement) && displayAltControls) {
            stepDisplay.drawButton(button, false);

        } else if (button.equals(StepUtil.shiftRightElement)  && displayAltControls) {
            stepDisplay.drawButton(button, false);

        } else if (button.equals(StepUtil.copyPatternElement)) {
            savingPattern = false;
            stepDisplay.drawButton(button, false);

        } else if (button.equals(StepUtil.saveElement)) {
            stepDisplay.drawButton(button, false);

        } else if (StepUtil.patternElements.contains(button)) {
            stepDisplay.drawPatterns(memory);

        }
    }

    public void onKnob(int index, int value) {
        if (settingsView) {
            settingsModule.onKnob(index, value);
            return;
        }
    }



    /***** Clockable implementation ****************************************/
    public void onStart(boolean restart) {
        notesOff();
        midiOut.allNotesOff(memory.getMidiChannel());
        if (restart) {
            currentStageIndex = 0;
            currentStageStepIndex = 0;
        }
    }

    public void onStop() {
        notesOff();
        midiOut.allNotesOff(memory.getMidiChannel());
    }

    public void onTick() {
        advance(false);
    }
    public void onTick(boolean andReset) {
        advance(andReset);
    }

    public void onClock(int measure, int beat, int pulse) {
        if (pulse == 0 || pulse == 6 + swingOffset || pulse == 12 || pulse == 18 + swingOffset) {
            advance(beat == 0 && pulse == 0);
        }
    }


    /***** MidiModule  implementation ****************************************/

    public int getMidiChannel() {
        return memory.getMidiChannel();
    }
    public void setMidiChannel(int channel) {
        notesOff();
        memory.setMidiChannel(channel);
    }



    /***** SaveableModule implementation ****************************************/

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void save() {
        save(fileIndex);
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

    public void load() {
        load(fileIndex);
    }

    public void load(int index) {
        memory = loadMemory(index);
//        settingsModule.setMidiChannel(memory.getMidiChannel());
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
