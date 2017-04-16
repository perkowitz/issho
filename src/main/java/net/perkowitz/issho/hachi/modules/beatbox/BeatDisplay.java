package net.perkowitz.issho.hachi.modules.beatbox;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;

import static net.perkowitz.issho.hachi.modules.beatbox.BeatUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class BeatDisplay {

    @Setter private GridDisplay display;
    @Getter @Setter private Map<Integer, Color> palette = BeatUtil.PALETTE_PINK;
    @Getter @Setter private int currentFileIndex = 0;
    @Setter private boolean settingsView = false;
    @Setter private boolean isMuted = false;
    @Setter private Integer nextChainStart = null;
    @Setter private Integer nextChainEnd = null;
    @Setter private EditMode editMode = EditMode.ENABLE;


    public BeatDisplay(GridDisplay display) {
        this.display = display;
    }


    /**
     * redraw should know how to draw everything
     *
     * @param memory
     */
    public void redraw(BeatMemory memory) {
        if (!settingsView) {
            drawPatterns(memory);
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

    public void drawPatterns(BeatMemory memory) {

        if (settingsView) return;

        int playingIndex = memory.getPlayingPatternIndex();
        int selectedIndex = memory.getSelectedPatternIndex();

        for (int index = 0; index < BeatUtil.PATTERN_COUNT; index++) {
            GridControl playingControl = BeatUtil.patternPlayControls.get(index);
            Color color = palette.get(BeatUtil.COLOR_PATTERN);
            if (playingIndex == index) {
                color = palette.get(BeatUtil.COLOR_PATTERN_PLAYING);
            } else if (selectedIndex == index) {
                color = palette.get(BeatUtil.COLOR_PATTERN_SELECTED);
            } else if (nextChainStart != null && nextChainEnd != null && index >= nextChainStart && index <= nextChainEnd) {
                color = palette.get(BeatUtil.COLOR_PATTERN_NEXT);
            } else if (memory.patternIsChained(index)) {
                color = palette.get(BeatUtil.COLOR_PATTERN_CHAINED);
            }
            playingControl.draw(display, color);

        }
    }

    public void drawTracks(BeatMemory memory) {
        for (int index = 0; index < BeatUtil.TRACK_COUNT; index++) {
              drawTrack(memory, index);
        }
    }

    public void drawTrack(BeatMemory memory, int index) {

        if (settingsView) return;

        BeatTrack track = memory.getSelectedPattern().getTrack(index);
        boolean enabled = memory.getCurrentSession().trackIsEnabled(index);
        Color color = palette.get(BeatUtil.COLOR_TRACK);
        if (track.isPlaying() && enabled) {
            color = palette.get(BeatUtil.COLOR_TRACK_PLAYING);
        } else if (track.isPlaying() && !enabled) {
            color = palette.get(BeatUtil.COLOR_TRACK_PLAYING_MUTED);
        } else if (!track.isPlaying() && enabled) {
            color = palette.get(BeatUtil.COLOR_TRACK);
        } else if (!track.isPlaying() && !enabled) {
            color = palette.get(BeatUtil.COLOR_TRACK_MUTED);
        }
        GridControl muteControl = BeatUtil.trackMuteControls.get(index);
        muteControl.draw(display, color);

        GridControl selectControl = BeatUtil.trackSelectControls.get(index);
        color = palette.get(BeatUtil.COLOR_TRACK_SELECTION);
        if (track.isPlaying()) {
            color = palette.get(BeatUtil.COLOR_TRACK_PLAYING);
        } else if (index == memory.getSelectedTrackIndex()) {
            color = palette.get(BeatUtil.COLOR_TRACK_SELECTED);
        }
        selectControl.draw(display, color);
    }

    public void drawSteps(BeatMemory memory) {

        if (settingsView) return;

        BeatTrack track = memory.getSelectedTrack();

        for (int index = 0; index < BeatUtil.STEP_COUNT; index++) {
            GridControl control = BeatUtil.stepControls.get(index);
            Color color = palette.get(BeatUtil.COLOR_STEP_OFF);
            if (track.getStep(index).isEnabled()) {
                color = palette.get(BeatUtil.COLOR_STEP_ON);
            }
            control.draw(display, color);
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
        if (isOn) {
            control.draw(display, palette.get(COLOR_ON));
        } else {
            control.draw(display, palette.get(COLOR_OFF));
        }
    }

    public void drawEditMode() {
        for (int i = 0; i < EditMode.values().length; i++) {
            Color color = palette.get(COLOR_OFF);
            if (i == editMode.ordinal()) {
                color = palette.get(COLOR_ON);
            }
            editModeControls.get(i).draw(display, color);
        }
    }

    public void drawValue(int value, int maxValue) {
        int valueAsEight = (value * 8) / maxValue;
        for (int index = 0; index < 8; index++) {
            GridControl control = BeatUtil.valueControls.get(index);
            if ((7 - index) <= valueAsEight) {
                control.draw(display, palette.get(BeatUtil.COLOR_VALUE_ON));
            } else {
                control.draw(display, palette.get(BeatUtil.COLOR_VALUE_OFF));
            }
        }
    }


}
