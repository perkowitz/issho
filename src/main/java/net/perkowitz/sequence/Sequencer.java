package net.perkowitz.sequence;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import net.perkowitz.sequence.devices.GridListener;
import net.perkowitz.sequence.models.*;
import net.perkowitz.sequence.models.Track;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.sequence.SequencerInterface.ValueMode.FILL_PERCENT;
import static net.perkowitz.sequence.SequencerInterface.ValueMode.TEMPO;
import static net.perkowitz.sequence.SequencerInterface.ValueMode.VELOCITY;

/**
 * Created by optic on 7/8/16.
 */
public class Sequencer implements SequencerInterface {

    public enum StepMode { MUTE, VELOCITY, JUMP, PLAY }
    private static final int DEFAULT_TIMER = 125;
    private static final int VELOCITY_MIN = 0;
    private static final int VELOCITY_MAX = 128;
    private static final int TEMPO_MIN = 100;
    private static final int TEMPO_MAX = 132;
    private static final int FILL_PERCENT_MIN = 9;
    private static final int FILL_PERCENT_MAX = 113;
    private static final String FILENAME_PREFIX = "sequencer-";
    private static final String FILENAME_SUFFIX = ".json";


    ObjectMapper objectMapper = new ObjectMapper();

    private SequencerController controller;
    private SequencerDisplay display;
    private Transmitter inputTransmitter;
    private Receiver outputReceiver;

    private Map<Mode, Boolean> modeIsActiveMap = Maps.newHashMap();
    private Map<SequencerDisplay.DisplayButton, SequencerDisplay.ButtonState> buttonStateMap = Maps.newHashMap();

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
    private ValueMode valueMode = VELOCITY;
    private int currentFileIndex = 0;
    private Session nextSession = null;

    // triggers and clocks
    private boolean midiClockRunning = false;
    private int clockTicksPerTrigger = 6;
    private int tickCount = 0;

    private static CountDownLatch stop = new CountDownLatch(1);
    private Timer timer = null;


    /***** constructor *********************************************************************/

    public Sequencer(SequencerController controller, SequencerDisplay display, Transmitter inputTransmitter, Receiver outputReceiver) throws Exception {

        // set up controller and display
        this.controller = controller;
        this.controller.setSequencer(this);
        this.display = display;

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

        display.initialize();
//        display.displayHelp();
//        Thread.sleep(1000);
        display.displayAll(memory, modeIsActiveMap);

        startTimer();
        stop.await();

    }


    /***** public interface *********************************************************************/

    public void selectModule(Module module) {

        if (module == Module.SEQUENCE) {
            modeIsActiveMap.put(Mode.SEQUENCE, true);
            modeIsActiveMap.put(Mode.SETTINGS, false);
        } else if (module == Module.SETTINGS) {
            modeIsActiveMap.put(Mode.SEQUENCE, false);
            modeIsActiveMap.put(Mode.SETTINGS, true);
        }

        display.selectModule(module);
        display.displayModule(module, memory, modeIsActiveMap, currentFileIndex);

    }

    public void selectSession(int index) {
        nextSession = memory.getSession(index);
        nextSession.setNext(true);
        display.displaySession(nextSession);
    }

    public void loadData(int index) {
        load(FILENAME_PREFIX + index + FILENAME_SUFFIX);
        currentFileIndex = index;
        display.displayFiles(currentFileIndex);
    }

    public void saveData(int index) {
        save(FILENAME_PREFIX + index + FILENAME_SUFFIX);
        currentFileIndex = index;
        display.displayFiles(currentFileIndex);
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
            display.displayPattern(selected);
            display.displayPattern(pattern);

            // clear value in case a fill was being displayed
            valueMode = VELOCITY;
            display.clearValue();

        } else {
            // retrieve current selected pattern and chain and save them to re-display
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

            // update display of all affected patterns
            for (Pattern pattern : patternsToDisplay) {
                display.displayPattern(pattern);
            }
        }

    }

    public void selectFill(int index) {

        if (patternEditMode) {
            Pattern selected = memory.selectedPattern();
            FillPattern fill = memory.selectedSession().getFill(index);
            memory.setSpecialSelected(true);
            memory.select(fill);
            display.displayPattern(selected);
            display.displayFill(fill);

            int fillPercent = fill.getFillPercent();
            valueMode = FILL_PERCENT;
            display.displayValue(fillPercent, FILL_PERCENT_MIN, FILL_PERCENT_MAX, FILL_PERCENT);

        } else {
            FillPattern fill = memory.selectedSession().getFill(index);
            fill.setChained(!fill.isChained());
            display.displayFill(fill);
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
            display.displayTrack(currentTrack, true);
            display.displayTrack(track, true);

        } else {
            // toggle track enabled
            track.setEnabled(!track.isEnabled());
            display.displayTrack(track);
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
            display.displayStep(step);
            valueMode = VELOCITY;
            display.displayValue(step.getVelocity(), VELOCITY_MIN, VELOCITY_MAX, ValueMode.VELOCITY);
        } else if (stepMode == StepMode.JUMP) {
            setNextStepIndex(index);
            nextStepIndex = (index + net.perkowitz.sequence.models.Track.getStepCount()) % net.perkowitz.sequence.models.Track.getStepCount();
        } else if (stepMode == StepMode.VELOCITY) {
            memory.select(step);
            valueMode = VELOCITY;
            display.displayValue(step.getVelocity(), VELOCITY_MIN, VELOCITY_MAX, ValueMode.VELOCITY);
        } else if (stepMode == StepMode.PLAY) {
            net.perkowitz.sequence.models.Track track = memory.selectedPattern().getTrack(index);
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
                display.displayValue(velocity, VELOCITY_MIN, VELOCITY_MAX, ValueMode.VELOCITY);
            }

        } else if (valueMode == TEMPO) {
            tempo = index * (TEMPO_MAX - TEMPO_MIN) / 8 + TEMPO_MIN;
            tempoIntervalInMillis = 125 * 120 / tempo;
//            System.out.printf("Tempo: %d, %d\n", tempo, tempoIntervalInMillis);
            display.displayValue(tempo, TEMPO_MIN, TEMPO_MAX, ValueMode.TEMPO);
            startTimer();

        } else if (valueMode == FILL_PERCENT) {
            Pattern pattern = memory.selectedPattern();
            if (pattern instanceof FillPattern) {
                int fillPercent = index * (FILL_PERCENT_MAX - FILL_PERCENT_MIN) / 8 + FILL_PERCENT_MIN;
                FillPattern fillPattern = (FillPattern) pattern;
                fillPattern.setFillPercent(fillPercent);
                display.displayValue(fillPercent, FILL_PERCENT_MIN, FILL_PERCENT_MAX, ValueMode.FILL_PERCENT);
            }
        }
    }

    public void selectMode(Mode mode) {

//        System.out.printf("selectMode: %s\n", mode);
        switch (mode) {

            case PATTERN_PLAY:
                patternEditMode = false;
                display.displayMode(Mode.PATTERN_EDIT, false);
                break;

            case PATTERN_EDIT:
                patternEditMode = true;
                display.displayMode(Mode.PATTERN_EDIT, true);
                break;

            case TRACK_MUTE:
                trackSelectMode = false;
                display.displayModeChoice(Mode.TRACK_MUTE, TRACK_MODES);
                break;

            case TRACK_EDIT:
                if (trackSelectMode) {
                    // if you press select mode a second time, it unselects the selected track (so no track is selected)
                    memory.selectedTrack().setSelected(false);
                    display.displayTrack(memory.selectedTrack());
//                    display.clearSteps();
                }
                trackSelectMode = true;
                display.displayModeChoice(Mode.TRACK_EDIT, TRACK_MODES);
                break;

            case STEP_MUTE:
                stepMode = StepMode.MUTE;
                display.displayModeChoice(Mode.STEP_MUTE, STEP_MODES);
                display.displayTrack(memory.selectedTrack());
                break;

            case STEP_VELOCITY:
                stepMode = stepMode.VELOCITY;
                display.displayModeChoice(Mode.STEP_VELOCITY, STEP_MODES);
                display.displayTrack(memory.selectedTrack());
                break;

            case STEP_JUMP:
                stepMode = stepMode.JUMP;
                display.displayModeChoice(Mode.STEP_JUMP, STEP_MODES);
//                memory.setSelectedStep(null);
                display.clearSteps();
                break;

            case STEP_PLAY:
                stepMode = stepMode.PLAY;
                display.displayModeChoice(Mode.STEP_PLAY, STEP_MODES);
//                memory.setSelectedStep(null);
                display.clearSteps();
                break;

            case PLAY:
                toggleStartStop();
                break;

            case TEMPO:
                valueMode = TEMPO;
                display.displayValue(tempo, TEMPO_MIN, TEMPO_MAX, TEMPO);
                break;

            case NO_VALUE:
                valueMode = VELOCITY;
                display.clearValue();
                break;

            case EXIT:
                shutdown();
                break;

            case SAVE:
                save(FILENAME_PREFIX + currentFileIndex + FILENAME_SUFFIX);
                break;

            case HELP:
                display.displayHelp();
                break;

            //         LOAD, COPY, CLEAR, PATTERN_PLAY, PATTERN_EDIT,

        }
    }

    public void selectSwitch(Switch switchx) {
        memory.flipSwitch(switchx);
        display.displaySwitches(memory.getSettingsSwitches());
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



    /***** private implementation *********************************************************************/


    public void shutdown() {
        display.initialize();
        System.exit(0);
    }

    public void toggleStartStop() {
        startStop(!playing);
    }

    public void startStop(boolean setToPlaying) {
        playing = setToPlaying;
        if (playing) {
            display.displayMode(Mode.PLAY, true);
            startTimer();
        } else {
            display.displayMode(Mode.PLAY, false);
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
            display.displaySession(currentSession);
            display.displaySession(nextSession);
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
                    if (totalStepCount % net.perkowitz.sequence.models.Track.getStepCount() == 0) {
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
        // minimize the work before sending midi notes, so do display later
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
        for (net.perkowitz.sequence.models.Track track : memory.playingPattern().getTracks()) {
            Step step = track.getStep(nextStepIndex);
            if (track.isEnabled()) {
                if (step.isOn()) {
                    sendMidiNote(track.getMidiChannel(), track.getNoteNumber(), step.getVelocity());
                }
            }
        }
        // THEN update track displays
        for (net.perkowitz.sequence.models.Track track : memory.playingPattern().getTracks()) {
            Step step = track.getStep(nextStepIndex);
            if (step.isOn()) {
                track.setPlaying(true);
            }
            display.displayTrack(track, false);
            track.setPlaying(false);

        }

        // and display patterns
        if (isNewPattern) {
            Pattern next = memory.playingPattern();
            display.displayPattern(currentPattern);
            display.displayPattern(next);
            if (!memory.isSpecialSelected()) {
                Pattern selected = memory.selectedPattern();
                display.displayPattern(selected);
            }
        }



        totalStepCount++;
        int oldStepNumber = nextStepIndex;
        setNextStepIndex(nextStepIndex + 1);
        // NB: assumes that the play steps are always displayed using the step buttons
        display.displayStep(memory.selectedTrack().getStep(oldStepNumber));
        display.displayPlayingStep(nextStepIndex);
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


}
