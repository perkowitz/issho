package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Multitrack;
import net.perkowitz.issho.hachi.Saveable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.hachi.modules.*;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.util.MidiUtil;
import org.codehaus.jackson.map.ObjectMapper;

import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.perkowitz.issho.hachi.modules.seq.SeqStep.GateMode.*;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.*;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.EditMode.*;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.SeqMode.BEAT;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.SeqMode.MONO;


/**
 * Created by optic on 10/24/16.
 */
public class SeqModule extends MidiModule implements Module, Clockable, GridListener, Sessionizeable, Saveable, Muteable, Multitrack {

    ObjectMapper objectMapper = new ObjectMapper();

    @Setter private List<Integer> controllerNumbers = Lists.newArrayList(SeqControlTrack.controllersDefault);
    private Map<Integer, List<Integer>> controllersByTrack = Maps.newHashMap();
    private SeqMemory memory = new SeqMemory();
    private SeqDisplay seqDisplay;
    private SettingsSubmodule settingsModule;
    private boolean settingsView = false;

    private SeqMode mode;
    private String filePrefix = "beat";
    @Setter private int midiNoteOffset = 0;
    @Setter private boolean tiesEnabled = false;

    private int nextStepIndex = 0;
    private Integer nextSessionIndex = null;
    private Integer nextChainStart = null;
    private Integer nextChainEnd = null;
    private boolean playing = false;

    private int currentMeasure = 0;
    private int currentSeq = 0;
    private int currentPulse = 0;
    private int currentOctave = 4;

    private SeqPatternFill patternFill = null;

    private int selectedStep = 0;
    private int selectedPitchStep = 0;
    private int selectedControlStep = 0;
    private int patternsReleasedCount = 0;
    private Set<Integer> patternsPressed = Sets.newHashSet();
    private List<Integer> patternEditIndexBuffer = Lists.newArrayList();
    private boolean patternEditing = false;
    private boolean patternSelecting = false;
    private EditMode editMode = GATE;
    private List<Integer> onNotes = Lists.newArrayList(); // TODO: list or Set?
    @Setter private List<Integer> sessionPrograms = Lists.newArrayList();
    private Integer valuePressed = null;


    /***** Constructor ****************************************/

    public SeqModule(Transmitter inputTransmitter, Receiver outputReceiver, Map<Integer, Color> palette, String filePrefix, SeqMode mode) {
        super(inputTransmitter, outputReceiver);
        this.mode = mode;
        this.seqDisplay = new SeqDisplay(this.display);
        this.seqDisplay.setPalette(palette);
        this.seqDisplay.setMode(mode);
        this.filePrefix = filePrefix;
        this.settingsModule = new SettingsSubmodule(true, true, true, true);
        this.tiesEnabled = (mode != BEAT);
        load(0);
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
                sendMidiPitchBendZero(memory.getMidiChannel()); // reset for new session
                // TODO: reset all controllers for new session
                if (sessionPrograms != null && nextSessionIndex < sessionPrograms.size() &&
                        sessionPrograms.get(nextSessionIndex) != null && sessionPrograms.get(nextSessionIndex) >= 0) {
                    sendMidiProgramChange(memory.getMidiChannel(), sessionPrograms.get(nextSessionIndex));
                }
                nextSessionIndex = null;
            }

            if (nextChainStart != null && nextChainEnd != null) {
                // set chain, and advance pattern to start of chain
                memory.selectChain(nextChainStart, nextChainEnd);
                nextChainStart = null;
                nextChainEnd = null;
                seqDisplay.setNextChainStart(null);
                seqDisplay.setNextChainEnd(null);
                sendMidiPitchBendZero(memory.getMidiChannel()); // reset when playing new patterns
            } else {
                // otherwise advance pattern
                memory.advancePattern();
            }

            if (memory.getPlayingPatternIndex() != currentPatternIndex) {
                seqDisplay.drawPatterns(memory);
                seqDisplay.drawSteps(memory);
            }
        }

        SeqPattern playingPattern = memory.getPlayingPattern();

        if (patternFill != null) {
            playingPattern = patternFill;
        }

        // if a fill is playing that shuffles the steps, this will figure out which step is actually playing
        int actualStepIndex = playingPattern.getStep(0, nextStepIndex).getIndex();
        boolean drawMeasure = currentPulse < 12;
        seqDisplay.drawStepsClock(actualStepIndex, currentMeasure, drawMeasure);

        // send pitchbend and controllers before notes
        SeqPitchStep pitchStep = memory.getSelectedPattern().getPitchStep(selectedPitchStep);
        if (pitchStep != null &&  pitchStep.isEnabled()) {
            sendMidiPitchBend(memory.getMidiChannel(), pitchStep.getPitchBend());
        }
        for (SeqControlTrack controlTrack : memory.getPlayingPattern().getControlTracks()) {
            if (memory.getCurrentSession().controlTrackIsEnabled(controlTrack.getIndex())) {
                SeqControlStep controlStep = controlTrack.getStep(nextStepIndex);
                Integer controlNumber = controllerNumbers.get(controlTrack.getIndex());
                if (controlNumber != null && controlStep.isEnabled()) {
                    sendMidiCC(memory.getMidiChannel(), controlNumber, controlStep.getValue());
                    controlTrack.setPlaying(true);
                }
            }
        }

        // send the midi notes
        for (SeqTrack track : playingPattern.getTracks()) {
            // when the selected track isn't the one currently being played (when there's a chain)
            // get the selected track so we can highlight the playing tracks as the notes hit
            SeqTrack playingTrack = memory.getSelectedPattern().getTrack(track.getIndex());

            SeqStep step = playingPattern.getStep(track.getIndex(), actualStepIndex);
            if (step.getGateMode() == PLAY) {
                // if it's a PLAY step, stop any previous notes and then play (if track enabled)
                if (track.getNoteNumber() != null) {
                    noteOff(track.getNoteNumber());
                } else {
                    notesOff();
                }
                playingTrack.setPlaying(true);
                seqDisplay.setPlayingStep(step);
                if (memory.getCurrentSession().trackIsEnabled(track.getIndex())) {
                    // TODO embed this in a track function that will use the track note or step note as needed
                    Integer note = track.getNoteNumber();
                    if (note == null) {
                        note = step.getNote();
                    }
                    sendMidiNote(memory.getMidiChannel(), note, step.getVelocity());
                }
            } else if (step.getGateMode() == REST) {
                // if it's a REST step, stop any previous notes
                if (track.getNoteNumber() != null) {
                    noteOff(track.getNoteNumber());
                } else {
                    notesOff();
                }
            } else if (step.getGateMode() == TIE) {
                // if it's a TIE, you just let it keep going
            }
        }

        // THEN update track displays
        for (SeqTrack track : memory.getPlayingPattern().getTracks()) {
            // if the playing pattern is different from the currently selected pattern, get the equivalent track from the playing pattern
            if (mode == BEAT) {
                seqDisplay.drawTrack(memory, track.getIndex());
                seqDisplay.drawTrackMute(memory, track.getIndex());
            } else if (mode == MONO) {
                seqDisplay.drawKeyboard(memory);
                seqDisplay.drawModifiers(memory);
            }
            SeqTrack playingTrack = memory.getSelectedPattern().getTrack(track.getIndex());
            playingTrack.setPlaying(false);
            seqDisplay.setPlayingStep(null);
        }
        for (SeqControlTrack controlTrack : memory.getPlayingPattern().getControlTracks()) {
            controlTrack.setPlaying(false);
        }

        nextStepIndex = (nextStepIndex + 1) % SeqUtil.STEP_COUNT;

    }


    /***** Module implementation **********************************
     *
     * all the basic methods a Module must implement
     */

    /**
     * redraw all the device controls according to current state
     */
    public void redraw() {
        seqDisplay.initialize();
        if (settingsView) {
            settingsModule.redraw();
            seqDisplay.drawLeftControls();
        } else {
            seqDisplay.redraw(memory);
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
        this.seqDisplay.setDisplay(display);
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
        seqDisplay.setMuted(isMuted);
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
        return SeqUtil.BEAT_TRACK_COUNT;
    }

    public boolean getTrackEnabled(int index) {
        return memory.getCurrentSession().getTracksEnabled().get(index);
    }

    public void setTrackEnabled(int index, boolean enabled) {
        memory.getCurrentSession().setTrackEnabled(index, enabled);
        seqDisplay.drawTracks(memory);
    }

    public void toggleTrackEnabled(int index) {
        memory.getCurrentSession().toggleTrackEnabled(index);
        seqDisplay.drawTracks(memory);
    }

    public void setControlTrackEnabled(int index, boolean enabled) {
        memory.getCurrentSession().setControlTrackEnabled(index, enabled);
        seqDisplay.drawTracks(memory);
    }

    public void toggleControlTrackEnabled(int index) {
        memory.getCurrentSession().toggleControlTrackEnabled(index);
        seqDisplay.drawTracks(memory);
    }

    public GridColor getEnabledColor() {
        return seqDisplay.getPalette().get(SeqUtil.COLOR_TRACK_SELECTION);
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
        nextSessionIndex = index;
        // TODO: won't show any change if it's not playing
    }

    /**
     * select a new pattern or chain of patterns.
     * module may do as it likes, but most modules load new patterns on the next measure start
     *
     * @param firstIndex
     * @param lastIndex
     */
    public void selectPatterns(int firstIndex, int lastIndex) {
        nextChainStart = firstIndex;
        nextChainEnd = lastIndex;
        seqDisplay.setNextChainStart(nextChainStart);
        seqDisplay.setNextChainEnd(nextChainEnd);
    }

    public void fillOn(Integer fillIndex) {
        // TODO: choose random when fillIndex is null or out of range, otherwise choose specific fill
        patternFill = SeqPatternFill.chooseRandom(memory.getPlayingPattern());
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

        control.press();
//        System.out.printf("Control pressed: %s, %d\n", control, control.getIndex());

        // these controls apply in main view or settings view
        if (control.equals(settingsControl)) {
            settingsView = !settingsView;
            seqDisplay.setSettingsView(settingsView);
            this.redraw();

        } else if (control.equals(muteControl)) {
            isMuted = !isMuted();
            seqDisplay.setMuted(isMuted);
            seqDisplay.drawLeftControls();

        } else if (control.equals(saveControl)) {
            this.save(settingsModule.getCurrentFileIndex());
            seqDisplay.drawControl(control, true);

        } else if (settingsView) {
            // now check if we're in settings view and then process the input accordingly
           onControlPressedSettings(control, velocity);

        // these controls are main view only
        } else if (control.equals(copyControl)) {
            patternEditIndexBuffer.clear();
            patternEditing = true;
            seqDisplay.drawControl(control, true);

        } else if (control.equals(patternSelectControl)) {
            patternSelecting = true;
            seqDisplay.drawControl(control, true);

        } else if (patternPlayControls.contains(control)) {
            int index = patternPlayControls.getIndex(control);
            if (patternEditing) {
                patternEditIndexBuffer.add(index);
            } else if (patternSelecting) {
                memory.selectPattern(index);
                seqDisplay.drawPatterns(memory);
                seqDisplay.drawSteps(memory);
            } else {
                patternsPressed.add(index);
            }

        } else if (editMode == CONTROL && trackMuteControls.contains(control)) {
            int index = trackMuteControls.getIndex(control);
            memory.getCurrentSession().toggleControlTrackEnabled(index);
            seqDisplay.drawTracks(memory);

        } else if (mode == BEAT && trackMuteControls.contains(control)) {
            int index = trackMuteControls.getIndex(control);
            memory.getCurrentSession().toggleTrackEnabled(index);
            seqDisplay.drawTracks(memory);

        } else if (mode == MONO && editMode == GATE && keyboardControls.contains(control)) {
            // have to check this before trackSelectControls
            int index = keyboardControls.getIndex(control);
            SeqStep step = memory.getSelectedTrack().getStep(selectedStep);
            step.setSemitone(index);
            seqDisplay.drawKeyboard(memory);

        } else if (mode == MONO && editMode == STEP && keyboardControls.contains(control)) {
            // have to check this before trackSelectControls
            int index = keyboardControls.getIndex(control);
            SeqStep step = memory.getSelectedTrack().getStep(selectedStep);
            step.set(PLAY, index, currentOctave, velocity);
            selectedStep = (selectedStep + 1) % STEP_COUNT;
            memory.selectStep(selectedStep);
            seqDisplay.drawKeyboard(memory);
            seqDisplay.drawSteps(memory);
            seqDisplay.drawModifiers(memory);

        } else if (mode == MONO && editMode == STEP && stepRestControl.equals(control)) {
            SeqStep step = memory.getSelectedTrack().getStep(selectedStep);
            step.setGateMode(REST);
            selectedStep = (selectedStep + 1) % STEP_COUNT;
            memory.selectStep(selectedStep);
            seqDisplay.drawKeyboard(memory);
            seqDisplay.drawSteps(memory);
            seqDisplay.drawModifiers(memory);

        } else if (mode == MONO && editMode == STEP && stepTieControl.equals(control)) {
            SeqStep step = memory.getSelectedTrack().getStep(selectedStep);
            step.setGateMode(TIE);
            selectedStep = (selectedStep + 1) % STEP_COUNT;
            memory.selectStep(selectedStep);
            seqDisplay.drawKeyboard(memory);
            seqDisplay.drawSteps(memory);
            seqDisplay.drawModifiers(memory);

        } else if (mode == MONO && (editMode == GATE || editMode == STEP) && trackSelectControls.contains(control)) {
            // if it's in the trackSelectControls but not in the keyboard, do nothing

        } else if (trackSelectControls.contains(control)) {
            int index = trackSelectControls.getIndex(control);
            switch (editMode) {
                case GATE:
                    if (mode == BEAT) {
                        memory.selectTrack(index);
                        seqDisplay.drawTracks(memory);
                        seqDisplay.drawSteps(memory);
                    }
                    // took care of MONO mode above
                    break;
                case CONTROL:
                    memory.selectControlTrack(index);
                    seqDisplay.drawTracks(memory);
                    seqDisplay.drawControlSteps(memory);
                    break;
                case PITCH:
                    // doesn't do anything in pitch mode
                    break;
                case JUMP:
                    // track buttons play a sound in jump mode
                    SeqTrack track = memory.getSelectedPattern().getTrack(index);
                    sendMidiNote(memory.getMidiChannel(), track.getNoteNumber(), velocity);
                    seqDisplay.drawControlHighlight(control, true);
                    break;
            }

        } else if (mode == MONO && editMode == GATE && octaveControls.contains(control)) {
            int index = octaveControls.getIndex(control);
            SeqStep step = memory.getSelectedTrack().getStep(selectedStep);
            step.setOctave(index);
            seqDisplay.drawModifiers(memory);

        } else if (mode == MONO && editMode == STEP && octaveControls.contains(control)) {
            int index = octaveControls.getIndex(control);
            currentOctave = index;
            seqDisplay.setCurrentOctave(currentOctave);
            seqDisplay.drawModifiers(memory);

        } else if (stepControls.contains(control)) {
            int index = stepControls.getIndex(control);
            SeqStep step = memory.getSelectedTrack().getStep(index);
            switch (editMode) {
                case GATE:
                case STEP:
                    // selects step for editing
                    selectedStep = index;  // TODO replace this with references to memory.getSelectedStep()
                    memory.selectStep(index);
                    // we don't actually toggle the step until release
                    break;
                case CONTROL:
                    // select that step for controller editing
                    selectedControlStep = index;
                    SeqControlTrack track = memory.getSelectedControlTrack();
                    SeqControlStep controlStep = track.getStep(index);
                    seqDisplay.drawValue(controlStep.getValue(), 127);
                    // we don't actually toggle the step until release
                    break;
                case PITCH:
                    // select that step for pitch editing
                    selectedPitchStep = index;
                    SeqPitchStep pitchStep = memory.getSelectedPattern().getPitchStep(selectedPitchStep);
                    pitchStep.toggleEnabled();
                    seqDisplay.drawSteps(memory);
                    seqDisplay.drawValue(pitchStep.getPitchBend(), MidiUtil.MIDI_PITCH_BEND_MAX);
                    break;
                case JUMP:
                    // jumps to that step
                    nextStepIndex = index;
                    break;
            }

        } else if (editModeControls.contains(control)) {
            int index = editModeControls.getIndex(control);
            editMode = EditMode.values()[index];
            seqDisplay.setEditMode(editMode);
            seqDisplay.redraw(memory);

        } else if (mode == MONO && stepControl.equals(control)) {
            editMode = STEP;
            selectedStep = 0;
            memory.selectStep(0);
            seqDisplay.setEditMode(editMode);
            seqDisplay.redraw(memory);

        } else if (jumpControl.equals(control)) {
            editMode = EditMode.JUMP;
            seqDisplay.setEditMode(editMode);
            seqDisplay.drawEditMode();
            seqDisplay.drawSteps(memory);
            seqDisplay.drawTracks(memory);
            seqDisplay.drawValue(7, 7, SeqDisplay.ValueMode.HIGHLIGHT);

        } else if (fillControl.equals(control)) {
            fillOn(null);
            seqDisplay.drawFillControl(true);

        } else if (valueControls.contains(control)) {
            SeqStep step = memory.getSelectedTrack().getStep(selectedStep);
            Integer index = valueControls.getIndex(control);
            switch (editMode) {
                case GATE:
                    if (valuePressed == null) {
                        valuePressed = index;
                        step.setVelocity(SeqUtil.baseToValue(7 - index));
                    } else if (index == valuePressed-1) {
                        step.incrementVelocity();
                    } else if (index < valuePressed) {
                        step.incrementVelocityMore();
                    } else if (index == valuePressed+1) {
                        step.decrementVelocity();
                    } else if (index > valuePressed) {
                        step.decrementVelocityMore();
                    }
                    seqDisplay.drawValue(step.getVelocity(), 127);
                    break;
                case CONTROL:
                    SeqControlTrack track = memory.getSelectedControlTrack();
                    SeqControlStep controlStep = track.getStep(selectedControlStep);
                    // buttons are reversed top-to-bottom
                    if (valuePressed == null) {
                        valuePressed = index;
                        controlStep.setValue(SeqUtil.baseToValue(7 - index));
                    } else if (index == valuePressed-1) {
                        controlStep.incrementValue();
                    } else if (index < valuePressed) {
                        controlStep.incrementValueMore();
                    } else if (index == valuePressed+1) {
                        controlStep.decrementValue();
                    } else if (index > valuePressed) {
                        controlStep.decrementValueMore();
                    }
                    seqDisplay.drawValue(controlStep.getValue(), 127);
                    break;
                case PITCH:
                    SeqPitchStep pitchStep = memory.getSelectedPattern().getPitchStep(selectedPitchStep);
                    pitchStep.setPitchBendByIndex(7 - index);
                    seqDisplay.drawValue(pitchStep.getPitchBend(), MidiUtil.MIDI_PITCH_BEND_MAX);
                    break;
                case JUMP:
                    int pitchBend = SeqPitchStep.pitchBendByIndex(7 - index);
                    sendMidiPitchBend(memory.getMidiChannel(), pitchBend);
                    seqDisplay.drawValue(7 - index, 7, SeqDisplay.ValueMode.HIGHLIGHT);
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

        Long elapsed = control.release();

        if (control.equals(saveControl)) {
            seqDisplay.drawControl(control, false);

        } else if (settingsView) {
            // now check if we're in settings view and then process the input accordingly
            onControlReleasedSettings(control);

        } else if (control.equals(copyControl)) {
            if (patternEditIndexBuffer.size() >= 2) {
                Integer fromIndex = patternEditIndexBuffer.get(0);
                Integer toIndex = patternEditIndexBuffer.get(1);
                if (fromIndex != null && toIndex != null) {
                    SeqSession currentSession = memory.getCurrentSession();
                    SeqPattern fromPattern = currentSession.getPattern(fromIndex);
                    SeqPattern clone = SeqPattern.copy(fromPattern, toIndex);
                    currentSession.getPatterns().set(toIndex, clone);
//                    memory.getCurrentSession().getPatterns().set(toIndex, SeqPattern.copy(memory.getCurrentSession().getPattern(fromIndex), toIndex));
                }
            }
            patternEditIndexBuffer.clear();
            patternEditing = false;
            seqDisplay.drawControl(control, false);

        } else if (control.equals(patternSelectControl)) {
            patternSelecting = false;
            seqDisplay.drawControl(control, false);

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
                    seqDisplay.setNextChainStart(nextChainStart);
                    seqDisplay.setNextChainEnd(nextChainEnd);

                    memory.selectPattern(min);
                    patternsPressed.clear();
                    patternsReleasedCount = 0;
                    seqDisplay.drawPatterns(memory);
                }
            }

        } else if (trackSelectControls.contains(control)) {
            int index = trackSelectControls.getIndex(control);
            switch (editMode) {
                case JUMP:
                    SeqTrack track = memory.getSelectedPattern().getTrack(index);
                    sendMidiNote(memory.getMidiChannel(), track.getNoteNumber(), 0);
                    seqDisplay.drawControlHighlight(control, false);
                    break;
                case PITCH:
                    break;
            }

        } else if (stepControls.contains(control)) {
            int index = stepControls.getIndex(control);
            SeqStep step = memory.getSelectedTrack().getStep(index);
            switch (editMode) {
                case GATE:
                    // step was selected and displayed on press; on release decide whether to toggle step and redraw
                    if (elapsed < LONG_PRESS_IN_MILLIS) {
                        step.toggleEnabled();
                        step.advanceGateMode(tiesEnabled);
                    }
                    seqDisplay.drawSteps(memory);
                    break;
                case CONTROL:
                    // step was selected and displayed on press; on release decide whether to toggle step and redraw
                    selectedControlStep = index;
                    SeqControlTrack track = memory.getSelectedControlTrack();
                    SeqControlStep controlStep = track.getStep(index);
                    if (elapsed < LONG_PRESS_IN_MILLIS) {
                        controlStep.toggleEnabled();
                    }
                    seqDisplay.drawControlSteps(memory);
                    break;
            }

        } else if (fillControl.equals(control)) {
            fillOff();
            seqDisplay.drawFillControl(false);

        } else if (valueControls.contains(control)) {
            int index = valueControls.getIndex(control);
            switch (editMode) {
                case GATE:
                case CONTROL:
                    if (index == valuePressed) {
                        valuePressed = null;
                    }
                    break;
                case JUMP:
                    sendMidiPitchBendZero(memory.getMidiChannel());
                    seqDisplay.drawValue(7, 7, SeqDisplay.ValueMode.HIGHLIGHT);
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
                    SeqSession fromSession = memory.getSessions().get(settingsModule.getCopyFromSessionIndex());
                    int toSessionIndex = settingsModule.getCopyToSessionIndex();
                    memory.getSessions().set(toSessionIndex, SeqSession.copy(fromSession, toSessionIndex));
                    System.out.printf("Completed copy: %d -> %d\n", settingsModule.getCopyFromSessionIndex(), settingsModule.getCopyToSessionIndex());
                }
                break;

            case COPY_SESSION_TO_FILE:
                if (settingsModule.getCopyFromSessionIndex() != null && settingsModule.getCopyToSessionIndex() != null &&
                        settingsModule.getCopyToFileIndex() != null) {
                    SeqSession fromSession = memory.getSessions().get(settingsModule.getCopyFromSessionIndex());
                    int toSessionIndex = settingsModule.getCopyToSessionIndex();
                    int toFileIndex = settingsModule.getCopyToFileIndex();
                    SeqMemory toMemory = loadMemory(toFileIndex);
                    toMemory.setMidiChannel(memory.getMidiChannel());  // midi channel is per memory, which is kind of weird, but ok
                    toMemory.getSessions().set(toSessionIndex, SeqSession.copy(fromSession, toSessionIndex));
                    saveMemory(toFileIndex, toMemory);
                    System.out.printf("Completed copy to file: %d -> %d, f=%d\n",
                            settingsModule.getCopyFromSessionIndex(), settingsModule.getCopyToSessionIndex(), settingsModule.getCopyToFileIndex());
                }
                break;

            case CLEAR_SESSION:
                Integer sessionIndex = settingsModule.getClearSessionIndex();
                if (sessionIndex != null) {
                    memory.getSessions().set(sessionIndex, new SeqSession(sessionIndex, mode));
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
        currentSeq = beat;
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

    public void saveMemory(int index, SeqMemory saveMemory) {
        try {
            String filename = filename(index);
            File file = new File(filename);
            if (file.exists()) {
                // make a backup, but will overwrite any previous backups
                Files.copy(file, new File(filename + ".backup"));
            }
            objectMapper.writeValue(file, saveMemory);

            String sessionFilename = filename + "-session.json";
            file = new File(sessionFilename);
            objectMapper.writeValue(file, saveMemory.getCurrentSession());

            String patternFilename = filename + "-pattern.json";
            file = new File(patternFilename);
            objectMapper.writeValue(file, saveMemory.getCurrentSession().getPattern(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load(int index) {
        memory = loadMemory(index);
        settingsModule.setMidiChannel(memory.getMidiChannel());
    }

    public SeqMemory loadMemory(int index) {
        try {
            String filename = filename(index);
            File file = new File(filename);

            String patternFilename = filename + "-pattern.json";
            file = new File(patternFilename);
            if (file.exists()) {
                System.out.println("Trying to load pattern..");
                SeqPattern pattern = objectMapper.readValue(file, SeqPattern.class);
            }

            String sessionFilename = filename + "-session.json";
            file = new File(sessionFilename);
            if (file.exists()) {
                System.out.println("Trying to load session..");
                SeqSession session = objectMapper.readValue(file, SeqSession.class);
            }

            System.out.println("Trying to load memory..");
            file = new File(filename);
            if (file.exists()) {
                return objectMapper.readValue(file, SeqMemory.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new SeqMemory(mode);
    }

    private String filename(int index) {
        return filePrefix + "-" + index + ".json";
    }



}
