package net.perkowitz.issho.controller.apps.hachi.modules.step;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.controller.apps.hachi.Palette;
import net.perkowitz.issho.controller.apps.hachi.modules.Module;
import net.perkowitz.issho.controller.apps.hachi.modules.ModuleController;
import net.perkowitz.issho.controller.apps.hachi.modules.ModuleListener;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.midi.MidiOut;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;
import java.util.Set;

import static net.perkowitz.issho.controller.apps.hachi.modules.step.Stage.Marker.*;


/**
 * Created by optic on 10/24/16.
 */
public class StepModule implements Module, ModuleListener {

    private static final int LOG_LEVEL = Log.INFO;

    ObjectMapper objectMapper = new ObjectMapper();

    private ModuleController controller;
    private MidiOut midiOut;

    @Getter @Setter private boolean muted = false;
    @Getter @Setter private Palette palette = Palette.DEFAULT;


    private StepMemory memory = new StepMemory();
    private StepDisplay stepDisplay;
//    private SettingsSubmodule settingsModule;
    private boolean settingsView = false;

    private Set<Integer> onNotes = Sets.newHashSet();

    // file save/load
    private String filePrefix = "step";
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

    public StepModule(ModuleController controller, MidiOut midiOut, Palette palette, String filePrefix) {
        this.controller = controller;
        this.midiOut = midiOut;
        this.palette = palette;
        this.filePrefix = filePrefix;
        currentSteps = currentStage().getSteps();
        currentMarker = Note;
        stepDisplay = new StepDisplay(controller);
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
        muted = !muted;
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
//                settingsModule.setCurrentSessionIndex(memory.getCurrentSessionIndex());
                draw();
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
                    noteIndices = currentStage().findMarker(Note);
                }
                break;
            case Rest:
                break;
        }
        for (Integer index : noteIndices) {
            if (index.equals(step.getHighlightedIndex()) || step.getMode() == Step.Mode.Tie) {
                stepDisplay.drawActiveNote(7-index, currentStageIndex);
            }
        }

        switch (step.getMode()) {
            case Play:
                notesOff();
                int note = step.getNote();
                onNotes.add(note);
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

    private void nextStep() {
        currentStageStepIndex++;
        currentSteps = currentStage().getSteps();
        int c = 0;
        while (currentStageStepIndex >= currentSteps.size() && c <= StepPattern.STAGE_COUNT * 4) {
            // this will just loop until c gets too big if all stages are SKIP
//            stagesToRedraw.add(currentStageIndex);
            if (randomOrder) {
                currentStageIndex = (int)(Math.random() * StepPattern.STAGE_COUNT);
            } else {
                currentStageIndex++;
            }
            currentStageIndex = currentStageIndex % StepPattern.STAGE_COUNT;
            if (currentStage().getRandomCount() > 0) {
                currentStage().computeSteps();
            }
            currentSteps = currentStage().getSteps();
            currentStageStepIndex = 0;
        }
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
        stepDisplay.initialize();
        if (settingsView) {
//            settingsModule.redraw();
//            stepDisplay.drawLeftControls();
        } else {
            stepDisplay.draw(memory);
        }
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
        currentStageIndex = index % StepPattern.STAGE_COUNT;
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
        Stage stage = memory.currentPattern().getStage(column);
        int index = 7 - row;
        if (currentMarker == stage.getMarker(index)) {
            stage.putMarker(index, Stage.Marker.None);
            controller.setPad(row, column, StepUtil.MARKER_COLORS.get(Stage.Marker.None));
        } else {
            stage.putMarker(index, currentMarker);
            controller.setPad(row, column, StepUtil.MARKER_COLORS.get(currentMarker));
        }
    }

    public void onPadReleased(int row, int column) {
    }


    public void onButtonPressed(int group, int index, int value) {

        Button button = Button.at(group, index);
        if (button.equals(StepUtil.copyPatternElement)) {
            savingPattern = true;
            stepDisplay.drawButton(button, true);

        } else if (button.equals(StepUtil.altControlsElement)) {
            displayAltControls = !displayAltControls;
            stepDisplay.setDisplayAltControls(displayAltControls);
            stepDisplay.drawButton(button, displayAltControls);
            stepDisplay.drawMarkers();

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
        Button button = Button.at(group, index);
        if (button.equals(StepUtil.shiftLeftElement) && displayAltControls) {
            stepDisplay.drawButton(button, false);

        } else if (button.equals(StepUtil.shiftRightElement)  && displayAltControls) {
            stepDisplay.drawButton(button, false);

        } else if (button.equals(StepUtil.copyPatternElement)) {
            savingPattern = false;
            stepDisplay.drawButton(button, false);

        } else if (StepUtil.patternElements.contains(button)) {
            stepDisplay.drawPatterns(memory);

        }
    }

    public void onKnob(int index, int value) {
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


}
