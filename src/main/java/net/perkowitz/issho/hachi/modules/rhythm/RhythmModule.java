package net.perkowitz.issho.hachi.modules.rhythm;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.models.*;
import net.perkowitz.issho.hachi.modules.Module;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface.ValueMode.FILL_PERCENT;
import static net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface.ValueMode.TEMPO;
import static net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface.ValueMode.VELOCITY;


/**
 * Created by optic on 7/8/16.
 */
public class RhythmModule implements Module, RhythmInterface, Clockable {

    public enum StepMode { MUTE, VELOCITY, JUMP, PLAY }
    private static final int VELOCITY_MIN = 0;
    private static final int VELOCITY_MAX = 128;
    private static final int TEMPO_MIN = 100;
    private static final int TEMPO_MAX = 132;
    private static final int FILL_PERCENT_MIN = 9;
    private static final int FILL_PERCENT_MAX = 113;
    private static final String FILENAME_PREFIX = "sequencer-";
    private static final String FILENAME_SUFFIX = ".json";


    ObjectMapper objectMapper = new ObjectMapper();

    private RhythmController controller;
    private RhythmDisplay rhythmDisplay;
    private Transmitter inputTransmitter;
    private Receiver outputReceiver;

    private Map<RhythmInterface.Mode, Boolean> modeIsActiveMap = Maps.newHashMap();
    private Map<RhythmDisplay.DisplayButton, RhythmDisplay.ButtonState> buttonStateMap = Maps.newHashMap();

    private Memory memory;
    private int totalStepCount = 0;
    private int nextStepIndex = 0;
    private int totalMeasureCount = 0;
    private int tempo = 120;
    private int tempoIntervalInMillis = 125 * 120 / tempo;

    // sequencer states
    private boolean playing = false;
    private boolean trackSelectMode = true;
    private StepMode stepMode = StepMode.MUTE;
    private boolean patternEditMode = false;
    private RhythmInterface.ValueMode valueMode = VELOCITY;
    private int currentFileIndex = 0;
    private Session nextSession = null;

    // triggers and clocks
    private boolean midiClockRunning = false;
    private int clockTicksPerTrigger = 6;
    private int tickCount = 0;

    private static CountDownLatch stop = new CountDownLatch(1);
    private Timer timer = null;


    /***** constructor *********************************************************************/

    public RhythmModule(RhythmController controller, RhythmDisplay rhythmDisplay, Transmitter inputTransmitter, Receiver outputReceiver) {

        // set up controller and rhythmDisplay
        this.controller = controller;
        this.controller.setSequencer(this);
        this.rhythmDisplay = rhythmDisplay;

        // connect the provided midi input to the sequencer's clock receiver
        this.inputTransmitter = inputTransmitter;
        SequencerReceiver clockReceiver = new SequencerReceiver(this);
        this.inputTransmitter.setReceiver(clockReceiver);

        // where to send the sequencer's midi output
        this.outputReceiver = outputReceiver;

        load(FILENAME_PREFIX + currentFileIndex + FILENAME_SUFFIX);

        for (Mode mode : Mode.values()) {
            modeIsActiveMap.put(mode, false);
        }
        Mode[] activeModes = new Mode[] { Mode.PATTERN_PLAY, Mode.TRACK_EDIT, Mode.STEP_MUTE, Mode.SEQUENCE };
        for (Mode mode : activeModes) {
            modeIsActiveMap.put(mode, true);
        }

    }


    /***** RhythmModule interface *********************************************************************/


    public void redraw() {
        rhythmDisplay.displayAll(memory, modeIsActiveMap);
    }


    public void selectModule(Module module) {

        if (module == Module.SEQUENCE) {
            modeIsActiveMap.put(Mode.SEQUENCE, true);
            modeIsActiveMap.put(Mode.SETTINGS, false);
        } else if (module == Module.SETTINGS) {
            modeIsActiveMap.put(Mode.SEQUENCE, false);
            modeIsActiveMap.put(Mode.SETTINGS, true);
        }

        rhythmDisplay.selectModule(module);
        rhythmDisplay.displayModule(module, memory, modeIsActiveMap, currentFileIndex);

    }

    public void selectSession(int index) {
        nextSession = memory.getSession(index);
        nextSession.setNext(true);
        rhythmDisplay.displaySession(nextSession);
    }

    public void loadData(int index) {
        load(FILENAME_PREFIX + index + FILENAME_SUFFIX);
        currentFileIndex = index;
        rhythmDisplay.displayFiles(currentFileIndex);
    }

    public void saveData(int index) {
        save(FILENAME_PREFIX + index + FILENAME_SUFFIX);
        currentFileIndex = index;
        rhythmDisplay.displayFiles(currentFileIndex);
    }

    public void setSync(SyncMode syncMode) {

    }

    public void selectPatterns(int minIndex, int maxIndex) {

//        System.out.printf("selectPatterns: %d - %d\n", minIndex, maxIndex);

        if (patternEditMode) {
            Pattern selected = memory.selectedPattern();
            Pattern pattern = memory.selectedSession().getPattern(minIndex);
            memory.setSpecialSelected(true);
            memory.select(pattern);
            rhythmDisplay.displayPattern(selected);
            rhythmDisplay.displayPattern(pattern);

            // clear value in case a fill was being displayed
            valueMode = VELOCITY;
            rhythmDisplay.clearValue();

        } else {
            // retrieve current selected pattern and chain and save them to re-rhythmDisplay
            Set<Pattern> patternsToDisplay = Sets.newHashSet();
            patternsToDisplay.add(memory.selectedPattern());
            patternsToDisplay.addAll(memory.getPatternChain());


            // select the new pattern and set it as the chain (chained)
            List<Pattern> newChain = memory.setPatternChain(minIndex, maxIndex, minIndex);
            if (!playing) {
                // if not currently playing, you can advance directly to the new pattern
                //memory.advancePattern();
            }

            // when a new chain is set, we default to normal selection (the first of the chain)
            memory.setSpecialSelected(false);
            memory.select(newChain.get(0));
            patternsToDisplay.addAll(newChain);

            // update rhythmDisplay of all affected patterns
            for (Pattern pattern : patternsToDisplay) {
                rhythmDisplay.displayPattern(pattern);
            }
        }

    }

    public void selectFill(int index) {

        if (patternEditMode) {
            Pattern selected = memory.selectedPattern();
            FillPattern fill = memory.selectedSession().getFill(index);
            memory.setSpecialSelected(true);
            memory.select(fill);
            rhythmDisplay.displayPattern(selected);
            rhythmDisplay.displayFill(fill);

            int fillPercent = fill.getFillPercent();
            valueMode = FILL_PERCENT;
            rhythmDisplay.displayValue(fillPercent, FILL_PERCENT_MIN, FILL_PERCENT_MAX, FILL_PERCENT);

        } else {
            FillPattern fill = memory.selectedSession().getFill(index);
            fill.setChained(!fill.isChained());
            rhythmDisplay.displayFill(fill);
        }
    }


    public void selectTrack(int index) {

        Track track = memory.selectedPattern().getTrack(index);
//        System.out.printf("selectTrack: %s, patt=%s\n", track, memory.selectedPattern());
        if (trackSelectMode) {
            // unselect the currently selected track
//            System.out.printf("- Selecting track: %d, %s\n", index, track);
            Track currentTrack = memory.selectedTrack();
//            System.out.printf("- Unselecting track: %d, %s\n", currentTrack.getIndex(), currentTrack);
            memory.select(track);
            rhythmDisplay.displayTrack(currentTrack, true);
            rhythmDisplay.displayTrack(track, true);

        } else {
            // toggle track enabled
            track.setEnabled(!track.isEnabled());
            rhythmDisplay.displayTrack(track);
//            System.out.printf("- Toggling track: %d, %s, enab=%s\n", index, track, track.isEnabled());

        }

    }

    public void selectStep(int index) {

//        System.out.printf("selectStep: %d, %s\n", index, stepMode);
        Step step = memory.selectedTrack().getStep(index);
        if (stepMode == StepMode.MUTE) {
            // in mute mode, both mute/unmute and select that step
            step.setOn(!step.isOn());
            memory.select(step);
            rhythmDisplay.displayStep(step);
            valueMode = VELOCITY;
            rhythmDisplay.displayValue(step.getVelocity(), VELOCITY_MIN, VELOCITY_MAX, VELOCITY);
        } else if (stepMode == StepMode.JUMP) {
            setNextStepIndex(index);
            nextStepIndex = (index + Track.getStepCount()) % Track.getStepCount();
        } else if (stepMode == StepMode.VELOCITY) {
            memory.select(step);
            valueMode = VELOCITY;
            rhythmDisplay.displayValue(step.getVelocity(), VELOCITY_MIN, VELOCITY_MAX, VELOCITY);
        } else if (stepMode == StepMode.PLAY) {
            Track track = memory.selectedPattern().getTrack(index);
            sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), 100);
        }
    }

    public void selectValue(int index) {

//        System.out.printf("selectValue: %d\n", index);
        if (valueMode == VELOCITY) {
            Step step = memory.selectedStep();
            if (step != null) {
                int velocity = ((index + 1) * 16) - 1;
//                System.out.printf("- for step %s, v=%d, set v=%d\n", step, step.getVelocity(), velocity);
                step.setVelocity(velocity);
                rhythmDisplay.displayValue(velocity, VELOCITY_MIN, VELOCITY_MAX, VELOCITY);
            }

        } else if (valueMode == TEMPO) {
            tempo = index * (TEMPO_MAX - TEMPO_MIN) / 8 + TEMPO_MIN;
            tempoIntervalInMillis = 125 * 120 / tempo;
//            System.out.printf("Tempo: %d, %d\n", tempo, tempoIntervalInMillis);
            rhythmDisplay.displayValue(tempo, TEMPO_MIN, TEMPO_MAX, TEMPO);
            startTimer();

        } else if (valueMode == FILL_PERCENT) {
            Pattern pattern = memory.selectedPattern();
            if (pattern instanceof FillPattern) {
                int fillPercent = index * (FILL_PERCENT_MAX - FILL_PERCENT_MIN) / 8 + FILL_PERCENT_MIN;
                FillPattern fillPattern = (FillPattern) pattern;
                fillPattern.setFillPercent(fillPercent);
                rhythmDisplay.displayValue(fillPercent, FILL_PERCENT_MIN, FILL_PERCENT_MAX, FILL_PERCENT);
            }
        }
    }

    public void selectMode(Mode mode) {

//        System.out.printf("selectMode: %s\n", mode);
        switch (mode) {

            case PATTERN_PLAY:
                patternEditMode = false;
                rhythmDisplay.displayMode(Mode.PATTERN_EDIT, false);
                break;

            case PATTERN_EDIT:
                patternEditMode = true;
                rhythmDisplay.displayMode(Mode.PATTERN_EDIT, true);
                break;

            case TRACK_MUTE:
                trackSelectMode = false;
                rhythmDisplay.displayModeChoice(Mode.TRACK_MUTE, TRACK_MODES);
                break;

            case TRACK_EDIT:
                if (trackSelectMode) {
                    // if you press select mode a second time, it unselects the selected track (so no track is selected)
                    memory.selectedTrack().setSelected(false);
                    rhythmDisplay.displayTrack(memory.selectedTrack());
//                    rhythmDisplay.clearSteps();
                }
                trackSelectMode = true;
                rhythmDisplay.displayModeChoice(Mode.TRACK_EDIT, TRACK_MODES);
                break;

            case STEP_MUTE:
                stepMode = StepMode.MUTE;
                rhythmDisplay.displayModeChoice(Mode.STEP_MUTE, STEP_MODES);
                rhythmDisplay.displayTrack(memory.selectedTrack());
                break;

            case STEP_VELOCITY:
                stepMode = stepMode.VELOCITY;
                rhythmDisplay.displayModeChoice(Mode.STEP_VELOCITY, STEP_MODES);
                rhythmDisplay.displayTrack(memory.selectedTrack());
                break;

            case STEP_JUMP:
                stepMode = stepMode.JUMP;
                rhythmDisplay.displayModeChoice(Mode.STEP_JUMP, STEP_MODES);
//                memory.setSelectedStep(null);
                rhythmDisplay.clearSteps();
                break;

            case STEP_PLAY:
                stepMode = stepMode.PLAY;
                rhythmDisplay.displayModeChoice(Mode.STEP_PLAY, STEP_MODES);
//                memory.setSelectedStep(null);
                rhythmDisplay.clearSteps();
                break;

            case PLAY:
                toggleStartStop();
                break;

            case TEMPO:
                valueMode = TEMPO;
                rhythmDisplay.displayValue(tempo, TEMPO_MIN, TEMPO_MAX, TEMPO);
                break;

            case NO_VALUE:
                valueMode = VELOCITY;
                rhythmDisplay.clearValue();
                break;

            case EXIT:
                shutdown();
                break;

            case SAVE:
                save(FILENAME_PREFIX + currentFileIndex + FILENAME_SUFFIX);
                break;

            case HELP:
                rhythmDisplay.displayHelp();
                break;

            //         LOAD, COPY, CLEAR, PATTERN_PLAY, PATTERN_EDIT,

        }
    }

    public void selectSwitch(Switch switchx) {
        memory.flipSwitch(switchx);
        rhythmDisplay.displaySwitches(memory.getSettingsSwitches());
    }

    public void trigger(boolean isReset) {
        if (memory.isSet(Switch.TRIGGER_ENABLED)) {
            advance(isReset);
        }
    }

    public void clockTick() {
        if (memory != null && memory.isSet(Switch.MIDI_CLOCK_ENABLED) && midiClockRunning) {
            if (tickCount % clockTicksPerTrigger == 0) {
                advance(false);
            }
            tickCount++;
        }
    }

    public void clockStart() {
        if (memory.isSet(Switch.MIDI_CLOCK_ENABLED)) {
            tickCount = 0;
            midiClockRunning = true;
            startStop(true);
        }
    }

    public void clockStop() {
        if (memory.isSet(Switch.MIDI_CLOCK_ENABLED)) {
            tickCount = 0;
            midiClockRunning = false;
            startStop(false);
        }

    }


    /***** Module implementation *********************************************************************/

    public void open() {}

    public void close() {}

    public GridListener getGridListener() {
        return null;
    }

    public void setDisplay(GridDisplay display) {
        rhythmDisplay.setDisplay(display);
    }

    public Memory getMemory() { return null; }
    public void Save() {}
    public void Load() {}


    /***** private implementation *********************************************************************/

    public void shutdown() {
        rhythmDisplay.initialize();
        System.exit(0);
    }

    public void toggleStartStop() {
        startStop(!playing);
    }

    public void startStop(boolean setToPlaying) {
        playing = setToPlaying;
        if (playing) {
            rhythmDisplay.displayMode(Mode.PLAY, true);
            startTimer();
        } else {
            rhythmDisplay.displayMode(Mode.PLAY, false);
            totalStepCount = 0;
            totalMeasureCount = 0;
            if (timer != null) {
                timer.cancel();
            }
        }
        nextStepIndex = 0;
        memory.resetPatternChainIndex();
        List<Pattern> patternChain = memory.getPatternChain();
        if (patternChain.size() > 0) {
//            nextPattern(patternChain.get(0));
        }

    }

    private void advanceSession() {
        Session currentSession = memory.selectedSession();
        if (nextSession != null && nextSession != currentSession) {
            memory.select(nextSession);
            rhythmDisplay.displaySession(currentSession);
            rhythmDisplay.displaySession(nextSession);
            nextSession = null;
        }
    }


    public void startTimer() {

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (playing && memory.isSet(Switch.INTERNAL_CLOCK_ENABLED)) {
                    boolean andReset = false;
                    if (totalStepCount % Track.getStepCount() == 0) {
                        andReset = true;
                    }
                    advance(andReset);
                }
            }
        }, tempoIntervalInMillis, tempoIntervalInMillis);
    }

    private void setNextStepIndex(int stepNumber) {
        nextStepIndex = (stepNumber + Track.getStepCount()) % Track.getStepCount();
    }

    private void advance(boolean andReset) {

        if (andReset) {
            nextStepIndex = 0;
        }

        // new pattern on reset/0
        // minimize the work before sending midi notes, so do rhythmDisplay later
        boolean isNewPattern = false;
        Pattern currentPattern = null;
        if (nextStepIndex == 0) {

            // check for new session
            if (nextSession != null) {
                advanceSession();
            }

            totalMeasureCount++;
            currentPattern = memory.playingPattern();
            Pattern next = memory.advancePattern(totalMeasureCount);
            if (next != currentPattern) {
                isNewPattern = true;
            }
        }

        // send the midi notes
        for (Track track : memory.playingPattern().getTracks()) {
            Step step = track.getStep(nextStepIndex);
            if (track.isEnabled()) {
                if (step.isOn()) {
                    sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), step.getVelocity());
                }
            }
        }
        // THEN update track displays
        for (Track track : memory.playingPattern().getTracks()) {
            Step step = track.getStep(nextStepIndex);
            if (step.isOn()) {
                track.setPlaying(true);
            }
            rhythmDisplay.displayTrack(track, false);
            track.setPlaying(false);

        }

        // and rhythmDisplay patterns
        if (isNewPattern) {
            Pattern next = memory.playingPattern();
            rhythmDisplay.displayPattern(currentPattern);
            rhythmDisplay.displayPattern(next);
            if (!memory.isSpecialSelected()) {
                Pattern selected = memory.selectedPattern();
                rhythmDisplay.displayPattern(selected);
            }
        }



        totalStepCount++;
        int oldStepNumber = nextStepIndex;
        setNextStepIndex(nextStepIndex + 1);
        // NB: assumes that the play steps are always displayed using the step buttons
        rhythmDisplay.displayStep(memory.selectedTrack().getStep(oldStepNumber));
        rhythmDisplay.displayPlayingStep(nextStepIndex);
//        System.out.printf("Advance: done\n");

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
                memory = objectMapper.readValue(file, Memory.class);
            } else {
                memory = new Memory();
                memory.select(memory.selectedPattern().getTrack(8));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /************************************************************************
     * midi output implementation
     *
     */
    private void sendMidiNote(int channel, int noteNumber, int velocity) {
//        System.out.printf("Note: %d, %d, %d\n", channel, noteNumber, velocity);
        // TODO how do we send note off?

        try {
            ShortMessage noteMessage = new ShortMessage();
            noteMessage.setMessage(ShortMessage.NOTE_ON, channel, noteNumber, velocity);
            outputReceiver.send(noteMessage, -1);

        } catch (InvalidMidiDataException e) {
            System.err.println(e);
        }

    }


    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        if (restart) {
        }
    }

    public void stop() {
    }

    public void tick() {
//        if (playing && memory.isSet(Switch.INTERNAL_CLOCK_ENABLED)) {
//            boolean andReset = false;
//            if (totalStepCount % Track.getStepCount() == 0) {
//                andReset = true;
//            }
//            advance(andReset);
//        }
        boolean andReset = false;
        if (totalStepCount % Track.getStepCount() == 0) {
            andReset = true;
        }
        advance(andReset);
    }



}
