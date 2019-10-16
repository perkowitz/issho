package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;

import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.*;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.EditMode.*;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.SeqMode.BEAT;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.SeqMode.MONO;


/**
 * Created by optic on 10/25/16.
 */
public class SeqDisplay {

    public enum ValueMode {
        DEFAULT, HIGHLIGHT
    }

    @Setter private GridDisplay display;
    @Getter @Setter private Map<Integer, Color> palette = SeqUtil.getPalette("pink");
    @Getter @Setter private int currentFileIndex = 0;
    @Setter private boolean settingsView = false;
    @Setter private boolean isMuted = false;
    @Setter private Integer nextChainStart = null;
    @Setter private Integer nextChainEnd = null;
    @Setter private SeqMode mode = BEAT;
    @Setter private EditMode editMode = GATE;
    @Setter private SeqStep playingStep = null;
    @Setter private int currentOctave = 0;


    public SeqDisplay(GridDisplay display) {
        this.display = display;
    }


    /**
     * redraw should know how to draw everything
     *
     * @param memory
     */
    public void redraw(SeqMemory memory) {
        if (!settingsView) {
            drawPatterns(memory);
            drawKeyboard(memory);
            drawModifiers(memory);
            drawTracks(memory);
            drawSteps(memory);
            drawLeftControls();
            drawValue(0, 7);
            drawEditMode();
        }
    }


    /**
     * initialize should usually not try to initialize the things that Hachi draws
     * (top row of buttons, top button on left side)
     */
    public void initialize() {
        display.initialize(true, Sets.newHashSet(GridButton.Side.Bottom, GridButton.Side.Right));
    }


    /***** draw main view ****************************************/

    public void drawPatterns(SeqMemory memory) {

        if (settingsView) return;

        int playingIndex = memory.getPlayingPatternIndex();
        int selectedIndex = memory.getSelectedPatternIndex();

        for (int index = 0; index < SeqUtil.PATTERN_COUNT; index++) {
            GridControl playingControl = SeqUtil.patternPlayControls.get(index);
            Color color = palette.get(SeqUtil.COLOR_PATTERN);
            if (playingIndex == index) {
                color = palette.get(SeqUtil.COLOR_PATTERN_PLAYING);
            } else if (selectedIndex == index) {
                color = palette.get(SeqUtil.COLOR_PATTERN_SELECTED);
            } else if (nextChainStart != null && nextChainEnd != null && index >= nextChainStart && index <= nextChainEnd) {
                color = palette.get(SeqUtil.COLOR_PATTERN_NEXT);
            } else if (memory.patternIsChained(index)) {
                color = palette.get(SeqUtil.COLOR_PATTERN_CHAINED);
            }
            playingControl.draw(display, color);

        }
    }

    public void drawTracks(SeqMemory memory) {

        if (settingsView) return;

        for (int index = 0; index < SeqUtil.BEAT_TRACK_COUNT; index++) {
            drawTrackMute(memory, index);
        }

        switch (editMode) {
            case GATE:
                if (mode == BEAT) {
                    for (int index = 0; index < SeqUtil.BEAT_TRACK_COUNT; index++) {
                        drawTrack(memory, index);
                    }
                } else if (mode == MONO) {
                    drawKeyboard(memory);
                    drawModifiers(memory);
                }
                break;
            case CONTROL:
                Color color = palette.get(COLOR_TRACK_SELECTION);
                trackSelectControls.draw(display, palette.get(COLOR_HIGHLIGHT_DIM));
                // TODO iterate over tracks to do isPlaying
                int i = memory.getSelectedControlTrackIndex();
                trackSelectControls.get(i).draw(display, palette.get(COLOR_TRACK_SELECTED));
                break;
            case PITCH:
                trackSelectControls.draw(display, Color.OFF);
                trackSelectControls.get(0).draw(display, palette.get(COLOR_HIGHLIGHT));
                break;
            case JUMP:
                trackSelectControls.draw(display, Color.OFF);
                break;
        }
    }

    public void drawTrack(SeqMemory memory, int index) {

        if (settingsView) return;

        SeqTrack track = memory.getSelectedPattern().getTrack(index);
        if (track == null) {
            return;
        }
        if (mode == BEAT) {
            if (editMode == GATE) {
                GridControl selectControl = SeqUtil.trackSelectControls.get(index);
                Color color = palette.get(SeqUtil.COLOR_TRACK_SELECTION);
                if (track.isPlaying()) {
                    color = palette.get(SeqUtil.COLOR_TRACK_PLAYING);
                } else if (index == memory.getSelectedTrackIndex()) {
                    color = palette.get(SeqUtil.COLOR_TRACK_SELECTED);
                }
                selectControl.draw(display, color);
            }
        }
    }

    public void drawTrackMute(SeqMemory memory, int index) {

        if (settingsView) return;

        if (mode == BEAT && editMode != CONTROL ) {
            SeqTrack track = memory.getSelectedPattern().getTrack(index);
            if (track == null) {
                return;
            }
            boolean enabled = memory.getCurrentSession().trackIsEnabled(index);
            Color color = palette.get(SeqUtil.COLOR_TRACK);
            if (track.isPlaying() && enabled) {
                color = palette.get(SeqUtil.COLOR_TRACK_PLAYING);
            } else if (track.isPlaying() && !enabled) {
                color = palette.get(SeqUtil.COLOR_TRACK_PLAYING_MUTED);
            } else if (!track.isPlaying() && enabled) {
                color = palette.get(SeqUtil.COLOR_TRACK);
            } else if (!track.isPlaying() && !enabled) {
                color = palette.get(SeqUtil.COLOR_TRACK_MUTED);
            }
            GridControl muteControl = SeqUtil.trackMuteControls.get(index);
            muteControl.draw(display, color);
        } else if (editMode == CONTROL) {
            SeqControlTrack track = memory.getSelectedPattern().getControlTrack(index);
            if (track == null) {
                return;
            }
            boolean enabled = memory.getCurrentSession().controlTrackIsEnabled(index);
            Color color = palette.get(COLOR_TRACK_MUTED);
            if (enabled) {
                color = palette.get(COLOR_TRACK);
            }
            GridControl muteControl = SeqUtil.trackMuteControls.get(index);
            muteControl.draw(display, color);
        } else if (mode == BEAT) {
            trackMuteControls.draw(display, Color.OFF);
        }
    }
    
    public void drawSteps(SeqMemory memory) {

        if (settingsView) return;

        if (editMode == CONTROL) {
            drawControlSteps(memory);
            return;
        }

        SeqTrack track = memory.getSelectedTrack();
        if (track == null) {
            return;
        }

        for (int index = 0; index < SeqUtil.STEP_COUNT; index++) {
            GridControl control = SeqUtil.stepControls.get(index);
            Color color = palette.get(COLOR_STEP_REST);
            boolean selected = (memory.getSelectedStepIndex() == index);
            if (editMode == GATE || editMode == STEP) {
                switch (track.getStep(index).getGateMode()) {
                    case PLAY:
                        color = palette.get(COLOR_STEP_PLAY);
                        if (selected) { color = palette.get(COLOR_HIGHLIGHT); }
                        break;
                    case TIE:
                        color = palette.get(COLOR_STEP_TIE);
                        if (selected) { color = palette.get(COLOR_HIGHLIGHT_MID); }
                        break;
                    case REST:
                        color = palette.get(COLOR_STEP_REST);
                        if (selected) { color = palette.get(COLOR_HIGHLIGHT_DIM); }
                        break;
                }
            } else if (editMode == EditMode.PITCH) {
                if (memory.getSelectedPattern().getPitchStep(index).isEnabled()) {
                    color = palette.get(SeqUtil.COLOR_STEP_PLAY);
                }
            }
            control.draw(display, color);
        }
    }

    public void drawKeyboard(SeqMemory memory) {

        if (settingsView) return;

        if (mode != MONO) return;

        if (editMode == GATE || editMode == STEP) {
            notKeyboardControls.draw(display, Color.OFF);
            SeqStep step = memory.getSelectedTrack().getStep(memory.getSelectedStepIndex());
            for (GridControl control : keyboardControls.getControls()) {
                Color color = Color.OFF;
                GridPad pad = control.getPad();
                if (playingStep != null && control.getIndex() == playingStep.getSemitone()) {
                    // if the key is being played
                    color = palette.get(COLOR_PATTERN);
                } else if (control.getIndex() == step.getSemitone()) {
                    // if the key is set for the current selected step
                    color = palette.get(COLOR_HIGHLIGHT);
                } else if (pad != null && pad.getY() == KEYBOARD_BLACK_ROW) {
                    // it's a black key
                    color = palette.get(COLOR_KEY_BLACK);
                } else if (pad != null && pad.getY() == KEYBOARD_WHITE_ROW) {
                    // it's a white key
                    color = palette.get(COLOR_KEY_WHITE);
                }
                control.draw(display, color);
            }
            if (editMode == STEP) {
                stepTieControl.draw(display, STEP_TIE_COLOR);
                stepRestControl.draw(display, STEP_REST_COLOR);
            } else {
                stepTieControl.draw(display, Color.OFF);
                stepRestControl.draw(display, Color.OFF);
            }
        }
    }

    public void drawModifiers(SeqMemory memory) {

        if (settingsView) return;

        if (mode == MONO && (editMode == GATE || editMode == STEP)) {
            SeqStep step = memory.getSelectedTrack().getStep(memory.getSelectedStepIndex());
            octaveControls.draw(display, palette.get(COLOR_PATTERN));
            if (editMode == STEP) {
                octaveControls.get(currentOctave).draw(display, palette.get(COLOR_PATTERN_SELECTED));
            } else {
                octaveControls.get(step.getOctave()).draw(display, palette.get(COLOR_PATTERN_SELECTED));
            }
            if (playingStep != null) {
                octaveControls.get(playingStep.getOctave()).draw(display, palette.get(COLOR_PATTERN_PLAYING));
            }
        }
    }

    public void drawControlSteps(SeqMemory memory) {

        if (settingsView) return;

        int trackIndex = memory.getSelectedControlTrackIndex();
        SeqControlTrack track = memory.getPlayingPattern().getControlTrack(trackIndex);
        for (int index = 0; index < CONTROL_TRACK_COUNT; index++) {
            GridControl control = SeqUtil.stepControls.get(index);
            Color color = palette.get(COLOR_STEP_REST);
            SeqControlStep step = track.getStep(index);
            if (step.isEnabled()) {
                color = palette.get(COLOR_STEP_PLAY);
            }
            control.draw(display, color);
        }
    }

    public void drawStepsClock(Integer playingStepIndex, int measure, boolean drawMeasure) {

        if (settingsView) return;

        if (editMode == JUMP) {
            stepControls.draw(display, Color.OFF);
            if (playingStepIndex != null && playingStepIndex >= 0 && playingStepIndex < SeqUtil.STEP_COUNT) {
                GridControl control = stepControls.get(playingStepIndex);
                control.draw(display, palette.get(COLOR_HIGHLIGHT));
            }
            if (drawMeasure) {
//                trackSelectControls.draw(display, Color.OFF);
                GridControl control = trackSelectControls.get(measure % 8 + 8);
                control.draw(display, palette.get(COLOR_HIGHLIGHT));
            } else {
                trackSelectControls.draw(display, Color.OFF);
            }
        }
    }

    public void drawLeftControls() {
        drawControl(settingsControl, settingsView);
        drawControl(muteControl, isMuted);
        drawControl(saveControl, false);
        drawControl(copyControl, false);
        drawControl(patternSelectControl, false);
    }

    public void drawControl(GridControl control, boolean isOn) {
        Color color = palette.get(isOn ? COLOR_ON : COLOR_OFF);;
        if (settingsControl.equals(control) || muteControl.equals(control)) {
            color = palette.get(isOn ? COLOR_LEFT_DEFAULT_ON : COLOR_LEFT_DEFAULT_OFF);
        } else if (copyControl.equals(control) || patternSelectControl.equals(control)) {
            color = palette.get(isOn ? COLOR_LEFT_PATTERNS_ON : COLOR_LEFT_PATTERNS_OFF);
        } else if (saveControl.equals(control)) {
            color = palette.get(isOn ? COLOR_LEFT_SAVE_ON : COLOR_LEFT_SAVE_OFF);
        }
        control.draw(display, color);
    }

    public void drawControlHighlight(GridControl control, boolean isOn) {
        if (isOn) {
            control.draw(display, palette.get(COLOR_HIGHLIGHT));
        } else {
            control.draw(display, Color.OFF);
        }
    }

    public void drawEditMode() {

        // draw the regular edit controls
        for (GridControl control : editModeControls.getControls()) {
            Color color = palette.get(COLOR_OFF);
            if (control.getIndex() == editMode.ordinal()) {
                color = palette.get(COLOR_ON);
            }
            control.draw(display, color);
        }

        // step view is in mono mode only
        Color color = Color.OFF;
        if (mode == MONO) {
            color = palette.get(COLOR_OFF);
            if (editMode == STEP) {
                color = palette.get(COLOR_ON);
            }
        }
        stepControl.draw(display, color);

        // jump mode is elsewhere and uses highlight colors
        color = palette.get(COLOR_OFF);
        if (editMode == JUMP) {
            color = palette.get(COLOR_HIGHLIGHT);
        }
        jumpControl.draw(display, color);

        drawFillControl(false);
    }

    public void drawFillControl(boolean isOn) {
        Color color = palette.get(COLOR_OFF);
        if (isOn) {
            color = palette.get(COLOR_HIGHLIGHT);
        }
        fillControl.draw(display, color);
    }

    public void drawValue(int value, int maxValue) {
        drawValue(value, maxValue, ValueMode.DEFAULT);
    }

    public void drawValue(int value, int maxValue, ValueMode valueMode) {
        if (maxValue == 127) {
            drawValue127(value);
            return;
        }
        int valueAsEight = (value * 8) / maxValue;
        for (int index = 0; index < 8; index++) {
            GridControl control = SeqUtil.valueControls.get(index);
            if ((7 - index) <= valueAsEight) {
                Color color = palette.get(SeqUtil.COLOR_VALUE_ON);
                if (valueMode == ValueMode.HIGHLIGHT) {
                    color = palette.get(SeqUtil.COLOR_HIGHLIGHT);
                }
                control.draw(display, color);
            } else {
                control.draw(display, palette.get(SeqUtil.COLOR_VALUE_OFF));
            }
        }
    }

    private void drawValue127(int value) {

        int base = valueToBase(value);
        int accent = base;
        int v = baseToValue(base);
        if (value > v) {
            accent = base + 1;
        } else if (value < v) {
            accent = base - 1;
        }

        valueControls.draw(display, Color.OFF);
        GridControl baseControl = valueControls.get(7 - base);
        baseControl.draw(display, palette.get(COLOR_VALUE_ON));
        if (accent != base && accent >= 0 && accent < 8) {
            GridControl accentControl = valueControls.get(7 - accent);
            accentControl.draw(display, palette.get(COLOR_VALUE_ACCENT));
        }

    }


}
