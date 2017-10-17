package net.perkowitz.issho.hachi.modules.beatbox;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Multitrack;
import net.perkowitz.issho.hachi.Saveable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.MidiModule;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.Muteable;
import net.perkowitz.issho.hachi.modules.SettingsSubmodule;
import net.perkowitz.issho.hachi.modules.SettingsUtil;
import net.perkowitz.issho.util.MidiUtil;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.perkowitz.issho.hachi.modules.beatbox.BeatStep.GateMode.PLAY;
import static net.perkowitz.issho.hachi.modules.beatbox.BeatStep.GateMode.REST;
import static net.perkowitz.issho.hachi.modules.beatbox.BeatStep.GateMode.TIE;
import static net.perkowitz.issho.hachi.modules.beatbox.BeatUtil.*;


/**
 * Created by optic on 10/24/16.
 */
public class BeatModule extends MidiModule implements Module, Clockable, GridListener, Sessionizeable, Saveable, Muteable, Multitrack {

    ObjectMapper objectMapper = new ObjectMapper();

    private BeatMemory memory = new BeatMemory();
    private BeatDisplay beatDisplay;
    private SettingsSubmodule settingsModule;
    private boolean settingsView = false;

    private String filePrefix = "beat";
    @Setter private int midiNoteOffset = 0;
    @Setter private boolean tiesEnabled = false;

    private int nextStepIndex = 0;
    private Integer nextSessionIndex = null;
    private Integer nextChainStart = null;
    private Integer nextChainEnd = null;
    private boolean playing = false;

    private int currentMeasure = 0;
    private int currentBeat = 0;
    private int currentPulse = 0;

    private BeatPatternFill patternFill = null;

    private int selectedStep = 0;
    private int selectedControlStep = 0;
    private int patternsReleasedCount = 0;
    private Set<Integer> patternsPressed = Sets.newHashSet();
    private List<Integer> patternEditIndexBuffer = Lists.newArrayList();
    private boolean patternEditing = false;
    private boolean patternSelecting = false;
    private EditMode editMode = EditMode.GATE;
    private List<Integer> onNotes = Lists.newArrayList();


    /***** Constructor ****************************************/

    public BeatModule(Transmitter inputTransmitter, Receiver outputReceiver, Map<Integer, Color> palette, String filePrefix) {
        super(inputTransmitter, outputReceiver);
        this.beatDisplay = new BeatDisplay(this.display);
        this.beatDisplay.setPalette(palette);
        this.filePrefix = filePrefix;
        load(0);
        this.settingsModule = new SettingsSubmodule(true, true, true, true);
    }


    /***** private implementation ****************************************/

    /**
     * whatever happens every time there's a clock tick
     *
     * @param andReset
     */
    private void advance(boolean andReset) {

        if (andReset) {
            nextStepIndex = 0;
        }

        if (nextStepIndex == 0) {
            int currentPatternIndex = memory.getPlayingPatternIndex();

            // check for new session
            if (nextSessionIndex != null && nextSessionIndex != memory.getCurrentSessionIndex()) {
                memory.selectSession(nextSessionIndex);
                settingsModule.setCurrentSessionIndex(nextSessionIndex);
                settingsModule.setSwingOffset(memory.getCurrentSession().getSwingOffset());
                nextSessionIndex = null;
                sendMidiPitchBendZero(memory.getMidiChannel()); // reset for new session
            }

            if (nextChainStart != null && nextChainEnd != null) {
                // set chain, and advance pattern to start of chain
                memory.selectChain(nextChainStart, nextChainEnd);
                nextChainStart = null;
                nextChainEnd = null;
                beatDisplay.setNextChainStart(null);
                beatDisplay.setNextChainEnd(null);
                sendMidiPitchBendZero(memory.getMidiChannel()); // reset when playing new patterns
            } else {
                // otherwise advance pattern
                memory.advancePattern();
            }

            if (memory.getPlayingPatternIndex() != currentPatternIndex) {
                beatDisplay.drawPatterns(memory);
                beatDisplay.drawSteps(memory);
            }
        }

        BeatPattern playingPattern = memory.getPlayingPattern();

        if (patternFill != null) {
            playingPattern = patternFill;
        }

        // send controllers before notes
        BeatControlStep controlStep = memory.getPlayingPattern().getControlTrack().getStep(nextStepIndex);
        if (controlStep.isEnabled()) {
            sendMidiPitchBend(memory.getMidiChannel(), controlStep.getPitchBend());
        }

        // if a fill is playing that shuffles the steps, this will figure out which step is actually playing
        int actualStep = playingPattern.getStep(0, nextStepIndex).getIndex();
        boolean drawMeasure = currentPulse < 12;
        beatDisplay.drawStepsClock(actualStep, currentMeasure, drawMeasure);


        // send the midi notes
        for (int trackIndex = 0; trackIndex < BeatUtil.TRACK_COUNT; trackIndex++) {

            BeatTrack track = playingPattern.getTrack(trackIndex);

            // when the selected track isn't the one currently being played (when there's a chain)
            // get the selected track so we can highlight the playing tracks as the notes hit
            BeatTrack playingTrack = memory.getSelectedPattern().getTrack(track.getIndex());

            BeatStep step = playingPattern.getStep(trackIndex, nextStepIndex);
            if (step.getGateMode() == PLAY) {
                // if it's a PLAY step, stop any previous notes and then play (if track enabled)
                noteOff(track.getNoteNumber());
                playingTrack.setPlaying(true);
                if (memory.getCurrentSession().trackIsEnabled(track.getIndex())) {
                    sendMidiNote(memory.getMidiChannel(), track.getNoteNumber(), step.getVelocity());
                }
            } else if (step.getGateMode() == REST) {
                // if it's a REST step, stop any previous notes
                noteOff(track.getNoteNumber());
            } else if (step.getGateMode() == TIE) {
                // if it's a TIE, you just let it keep going
            }
        }

        // THEN update track displays
        for (BeatTrack track : memory.getPlayingPattern().getTracks()) {
            BeatTrack playingTrack = memory.getSelectedPattern().getTrack(track.getIndex());
            beatDisplay.drawTrack(memory, track.getIndex());
//            track.setPlaying(false);
            playingTrack.setPlaying(false);
        }

        nextStepIndex = (nextStepIndex + 1) % BeatUtil.STEP_COUNT;

    }


    /***** Module implementation **********************************
     *
     * all the basic methods a Module must implement
     */

    /**
     * redraw all the device controls according to current state
     */
    public void redraw() {
        beatDisplay.initialize();
        if (settingsView) {
            settingsModule.redraw();
            beatDisplay.drawLeftControls();
        } else {
            beatDisplay.redraw(memory);
        }
    }

    /**
     * when using a settings module and a dedicated display class, need to make sure
     * the display object is passed on to them, since Hachi may set the module's display
     *
     * @param display: a GridDisplay where this module should display its state
     */
    public void setDisplay(GridDisplay display) {
        this.display = display;
        this.beatDisplay.setDisplay(display);
        this.settingsModule.setDisplay(display);
    }

    /**
     * anything the module should do when Hachi is turned off (e.g. turn off any lingering midi notes)
     */
    public void shutdown() {
        notesOff();
    }


    /***** Muteable implementation **********************************
     *
     * a Muteable is anything that may be muted (e.g. by a ShihaiModule
     */

    /**
     * toggle the muted state
     *
     * @param muted
     */
    public void mute(boolean muted) {
        this.isMuted = muted;
        beatDisplay.setMuted(isMuted);
    }

    /**
     * report the current muted state
     *
     * @return boolean
     */
    public boolean isMuted() {
        return isMuted;
    }

    /***** Multitrack implementation ************************************/

    public int trackCount() {
        return BeatUtil.TRACK_COUNT;
    }

    public boolean getTrackEnabled(int index) {
        return memory.getCurrentSession().getTracksEnabled().get(index);
    }

    public void setTrackEnabled(int index, boolean enabled) {
        memory.getCurrentSession().setTrackEnabled(index, enabled);
        beatDisplay.drawTracks(memory);
    }

    public void toggleTrackEnabled(int index) {
        memory.getCurrentSession().toggleTrackEnabled(index);
        beatDisplay.drawTracks(memory);
    }

    public GridColor getEnabledColor() {
        return beatDisplay.getPalette().get(BeatUtil.COLOR_TRACK_SELECTION);
    }




    /***** Sessionizeable implementation ************************************
    *
    * a Sessionizeable implements sessions and/or patterns and can be told
    * which sessions or patterns to load (e.g. by a ShihaiModule)
    * sessions/patterns may be defined differently for every module; they are just referred to by index
    */

    /**
     * select a new Session.
     * module may do as it likes, but most modules load new sessions on the next measure start
     *
     * @param index
     */
    public void selectSession(int index) {
        if (playing) {
            nextSessionIndex = index;
        } else {
            memory.selectSession(index);
            settingsModule.setCurrentSessionIndex(index);
            redraw();
        }
    }

    /**
     * select a new pattern or chain of patterns.
     * module may do as it likes, but most modules load new patterns on the next measure start
     *
     * @param firstIndex
     * @param lastIndex
     */
    public void selectPatterns(int firstIndex, int lastIndex) {
        memory.selectChain(firstIndex, lastIndex);
    }

    public void fillOn(Integer fillIndex) {
        // TODO: choose random when fillIndex is null or out of range, otherwise choose specific fill
        patternFill = BeatPatternFill.chooseRandom(memory.getPlayingPattern());
    }

    public void fillOff() {
        patternFill = null;
    }


    /***** GridListener interface ***************************************
     *
     * a GridListener receives events from user input: press and release of pads and buttons
     */

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

    /**
     * it is generally easier to use the GridControl abstraction, which generalizes both pads and buttons.
     * many modules pass the listener functions directly here, and then define their controls in their Util class
     *
     * @param control
     * @param velocity
     */
    private void onControlPressed(GridControl control, int velocity) {

        // these controls apply in main view or settings view
        if (control.equals(settingsControl)) {
            settingsView = !settingsView;
            beatDisplay.setSettingsView(settingsView);
            this.redraw();

        } else if (control.equals(muteControl)) {
            isMuted = !isMuted();
            beatDisplay.setMuted(isMuted);
            beatDisplay.drawLeftControls();

        } else if (control.equals(saveControl)) {
            this.save(settingsModule.getCurrentFileIndex());
            beatDisplay.drawControl(control, true);

        } else if (settingsView) {
            // now check if we're in settings view and then process the input accordingly
           onControlPressedSettings(control, velocity);

        // these controls are main view only
        } else if (control.equals(copyControl)) {
            patternEditIndexBuffer.clear();
            patternEditing = true;
            beatDisplay.drawControl(control, true);

        } else if (control.equals(patternSelectControl)) {
            patternSelecting = true;
            beatDisplay.drawControl(control, true);

        } else if (patternPlayControls.contains(control)) {
            int index = patternPlayControls.getIndex(control);
            if (patternEditing) {
                patternEditIndexBuffer.add(index);
            } else if (patternSelecting) {
                memory.selectPattern(index);
                beatDisplay.drawPatterns(memory);
                beatDisplay.drawSteps(memory);
            } else {
                patternsPressed.add(index);
            }

        } else if (trackMuteControls.contains(control)) {
            int index = trackMuteControls.getIndex(control);
            memory.getCurrentSession().toggleTrackEnabled(index);
            beatDisplay.drawTracks(memory);

        } else if (trackSelectControls.contains(control)) {
            int index = trackSelectControls.getIndex(control);
            switch (editMode) {
                case GATE:
                case VELOCITY:
                    memory.selectTrack(index);
                    beatDisplay.drawTracks(memory);
                    beatDisplay.drawSteps(memory);
                    break;
                case JUMP:
                    BeatTrack track = memory.getSelectedPattern().getTrack(index);
                    sendMidiNote(memory.getMidiChannel(), track.getNoteNumber(), velocity);
                    beatDisplay.drawControlHighlight(control, true);
                    break;
                case PITCH:
                    break;
            }

        } else if (stepControls.contains(control)) {
            int index = stepControls.getIndex(control);
            BeatStep step = memory.getSelectedTrack().getStep(index);
            switch (editMode) {
                case GATE:
                    step.toggleEnabled();
                    step.advanceGateMode(tiesEnabled);
                    selectedStep = index;
                    beatDisplay.drawSteps(memory);
                    beatDisplay.drawValue(step.getVelocity(), 127);
                    break;
                case VELOCITY:
                    selectedStep = index;
                    beatDisplay.drawSteps(memory);
                    beatDisplay.drawValue(step.getVelocity(), 127);
                    break;
                case JUMP:
                    nextStepIndex = index;
                    break;
                case PITCH:
                    selectedControlStep = index;
                    BeatControlStep controlStep = memory.getSelectedPattern().getControlTrack().getStep(selectedControlStep);
                    controlStep.toggleEnabled();
                    beatDisplay.drawSteps(memory);
                    beatDisplay.drawValue(controlStep.getPitchBend(), MidiUtil.MIDI_PITCH_BEND_MAX);
                    break;
            }

        } else if (editModeControls.contains(control)) {
            int index = editModeControls.getIndex(control);
            editMode = EditMode.values()[index];
            beatDisplay.setEditMode(editMode);
            beatDisplay.drawEditMode();
            beatDisplay.drawSteps(memory);
            switch (editMode) {
                case PITCH:
                    beatDisplay.drawTracks(memory, true);
                    break;
            }

        } else if (jumpControl.equals(control)) {
            editMode = EditMode.JUMP;
            beatDisplay.setEditMode(editMode);
            beatDisplay.drawEditMode();
            beatDisplay.drawSteps(memory);
            beatDisplay.drawTracks(memory, true);
            beatDisplay.drawValue(7, 7, BeatDisplay.ValueMode.HIGHLIGHT);

        } else if (fillControl.equals(control)) {
            fillOn(null);
            beatDisplay.drawFillControl(true);

        } else if (valueControls.contains(control)) {
            BeatStep step = memory.getSelectedTrack().getStep(selectedStep);
            Integer index = valueControls.getIndex(control);
            switch (editMode) {
                case GATE:
                case VELOCITY:
                    step.setVelocity((8 - index) * 16 - 1);
                    beatDisplay.drawValue(step.getVelocity(), 127);
                    break;
                case PITCH:
                    BeatControlStep controlStep = memory.getSelectedPattern().getControlTrack().getStep(selectedControlStep);
                    controlStep.setPitchBendByIndex(7 - index);
                    beatDisplay.drawValue(controlStep.getPitchBend(), MidiUtil.MIDI_PITCH_BEND_MAX);
                    break;
                case JUMP:
                    int pitchBend = BeatControlStep.pitchBendByIndex(7 - index);
                    sendMidiPitchBend(memory.getMidiChannel(), pitchBend);
                    beatDisplay.drawValue(7 - index, 7, BeatDisplay.ValueMode.HIGHLIGHT);
                    break;
            }

        }


    }

    /**
     * any momentary controls may need to be lit on press and unlit on release
     *
     * @param control
     */
    private void onControlReleased(GridControl control) {

        if (control.equals(saveControl)) {
            beatDisplay.drawControl(control, false);

        } else if (settingsView) {
            // now check if we're in settings view and then process the input accordingly
            onControlReleasedSettings(control);

        } else if (control.equals(copyControl)) {
            if (patternEditIndexBuffer.size() >= 2) {
                Integer fromIndex = patternEditIndexBuffer.get(0);
                Integer toIndex = patternEditIndexBuffer.get(1);
                if (fromIndex != null && toIndex != null) {
                    BeatSession currentSession = memory.getCurrentSession();
                    BeatPattern fromPattern = currentSession.getPattern(fromIndex);
                    BeatPattern clone = BeatPattern.copy(fromPattern, toIndex);
                    currentSession.getPatterns().set(toIndex, clone);
//                    memory.getCurrentSession().getPatterns().set(toIndex, BeatPattern.copy(memory.getCurrentSession().getPattern(fromIndex), toIndex));
                }
            }
            patternEditIndexBuffer.clear();
            patternEditing = false;
            beatDisplay.drawControl(control, false);

        } else if (control.equals(patternSelectControl)) {
            patternSelecting = false;
            beatDisplay.drawControl(control, false);

        } else if (patternPlayControls.contains(control)) {
            // releasing a pattern pad
            // don't activate until the last pattern pad is released (so additional releases don't look like a new press/release)
            if (!patternEditing && !patternSelecting) {
                patternsReleasedCount++;
                if (patternsReleasedCount >= patternsPressed.size()) {
                    GridControl selectedControl = patternPlayControls.get(control);
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
                    }
                    nextChainStart = min;
                    nextChainEnd = max;
                    beatDisplay.setNextChainStart(nextChainStart);
                    beatDisplay.setNextChainEnd(nextChainEnd);

                    memory.selectPattern(min);
                    patternsPressed.clear();
                    patternsReleasedCount = 0;
                    beatDisplay.drawPatterns(memory);
                }
            }

        } else if (trackSelectControls.contains(control)) {
            int index = trackSelectControls.getIndex(control);
            switch (editMode) {
                case JUMP:
                    BeatTrack track = memory.getSelectedPattern().getTrack(index);
                    sendMidiNote(memory.getMidiChannel(), track.getNoteNumber(), 0);
                    beatDisplay.drawControlHighlight(control, false);
                    break;
                case PITCH:
                    break;
            }

        } else if (fillControl.equals(control)) {
            fillOff();
            beatDisplay.drawFillControl(false);

        } else if (valueControls.contains(control)) {
            switch (editMode) {
                case JUMP:
                    sendMidiPitchBendZero(memory.getMidiChannel());
                    beatDisplay.drawValue(7, 7, BeatDisplay.ValueMode.HIGHLIGHT);
                    break;
            }

        }

    }

    /**
     * when settings view is active, the user input should be passed to the SettingsSubmodule
     * but then the module must check with the SettingsSubmodule to see what was changed and
     * follow up accordingly. settings being similar for many modules, this still saves
     * some implementation effort
     *
     * @param control
     * @param velocity
     */
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
                memory.getCurrentSession().setSwingOffset(settingsModule.getSwingOffset());
                break;
        }
    }

    /**
     * when settings view is active, the user input should be passed to the SettingsSubmodule
     * but then the module must check with the SettingsSubmodule to see what was changed and
     * follow up accordingly. settings being similar for many modules, this still saves
     * some implementation effort
     *
     * @param control
     */
    private void onControlReleasedSettings(GridControl control) {

        SettingsUtil.SettingsChanged settingsChanged = settingsModule.controlReleased(control);
        switch (settingsChanged) {
            case COPY_SESSION:
                if (settingsModule.getCopyFromSessionIndex() != null && settingsModule.getCopyToSessionIndex() != null) {
                    BeatSession fromSession = memory.getSessions().get(settingsModule.getCopyFromSessionIndex());
                    int toSessionIndex = settingsModule.getCopyToSessionIndex();
                    memory.getSessions().set(toSessionIndex, BeatSession.copy(fromSession, toSessionIndex));
                    System.out.printf("Completed copy: %d -> %d\n", settingsModule.getCopyFromSessionIndex(), settingsModule.getCopyToSessionIndex());
                }
                break;

            case COPY_SESSION_TO_FILE:
                if (settingsModule.getCopyFromSessionIndex() != null && settingsModule.getCopyToSessionIndex() != null &&
                        settingsModule.getCopyToFileIndex() != null) {
                    BeatSession fromSession = memory.getSessions().get(settingsModule.getCopyFromSessionIndex());
                    int toSessionIndex = settingsModule.getCopyToSessionIndex();
                    int toFileIndex = settingsModule.getCopyToFileIndex();
                    BeatMemory toMemory = loadMemory(toFileIndex);
                    toMemory.setMidiChannel(memory.getMidiChannel());  // midi channel is per memory, which is kind of weird, but ok
                    toMemory.getSessions().set(toSessionIndex, BeatSession.copy(fromSession, toSessionIndex));
                    saveMemory(toFileIndex, toMemory);
                    System.out.printf("Completed copy to file: %d -> %d, f=%d\n",
                            settingsModule.getCopyFromSessionIndex(), settingsModule.getCopyToSessionIndex(), settingsModule.getCopyToFileIndex());
                }
                break;

            case CLEAR_SESSION:
                Integer sessionIndex = settingsModule.getClearSessionIndex();
                if (sessionIndex != null) {
                    memory.getSessions().set(sessionIndex, new BeatSession(sessionIndex));
                    System.out.printf("Completed clear session %d\n", sessionIndex);
                }
                break;
        }
    }


    public void onKnobChanged(GridKnob knob, int delta) {}
    public void onKnobSet(GridKnob knob, int value) {}



    /***** Clockable implementation ***************************************
     *
     * a Clockable can receive clock ticks and start/stop messages from Hachi's master clock.
     */

    public void start(boolean restart) {
        memory.resetChain();
        playing = true;
    }

    public void stop() {
        playing = false;
        notesOff();
    }

    public void tick(boolean andReset) {
        if (playing) {
            advance(andReset);
        }
    }

    public void clock(int measure, int beat, int pulse) {
        currentMeasure = measure;
        currentBeat = beat;
        currentPulse = pulse;
        if ((pulse == 0 || pulse == 6 + memory.getCurrentSession().getSwingOffset() || pulse == 12 || pulse == 18 + memory.getCurrentSession().getSwingOffset()) && playing) {
            advance(beat == 0 && pulse == 0);
        }
    }


    /************************************************************************
     * midi output implementation
     *
     */
    protected void sendMidiNote(int channel, int noteNumber, int velocity) {
        if (velocity != 0) {
            onNotes.add(noteNumber);
        }
        int offsetNoteNumber = midiNoteOffset + noteNumber;
        super.sendMidiNote(channel, offsetNoteNumber, velocity);
    }

    private void notesOff() {
        for (Integer note : onNotes) {
            sendMidiNote(memory.getMidiChannel(), note, 0);

        }
        onNotes.clear();
    }

    private void noteOff(Integer note) {
        if (onNotes.contains(note)) {
            sendMidiNote(memory.getMidiChannel(), note, 0);
            onNotes.remove(note);
        }
    }



    /***** Saveable implementation ***************************************
     *
     * a Saveable can write its memory to a file and load memory from a file.
     * Saveables can also be asked (e.g. by a ShihaiModule) to save/load by index.
     * many modules store a filePrefix and then just select files by appending an index number to the prefix
     */

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void save(int index) {
        saveMemory(index, memory);
    }

    public void saveMemory(int index, BeatMemory saveMemory) {
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

    public BeatMemory loadMemory(int index) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                return objectMapper.readValue(file, BeatMemory.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new BeatMemory();
    }

    private String filename(int index) {
        return filePrefix + "-" + index + ".json";
    }



}
