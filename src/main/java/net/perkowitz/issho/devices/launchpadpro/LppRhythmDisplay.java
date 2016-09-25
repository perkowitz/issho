package net.perkowitz.issho.devices.launchpadpro;


import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.models.*;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmDisplay;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface;

import java.util.Arrays;
import java.util.Map;

import static net.perkowitz.issho.devices.GridButton.Side.Right;
import static net.perkowitz.issho.devices.launchpadpro.LppRhythmUtil.*;
import static net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface.Mode.EXIT;
import static net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface.SETTINGS_MODULE_MODES;

/**
 * Created by optic on 7/10/16.
 */
public class LppRhythmDisplay implements RhythmDisplay {

    @Setter private GridDisplay display;
    private RhythmInterface.Module currentModule = RhythmInterface.Module.SEQUENCE;


    public LppRhythmDisplay(GridDisplay display) {
        this.display = display;
    }

    public void initialize() {
        display.initialize();
    }

    public void displayAll(Memory memory, Map<RhythmInterface.Mode,Boolean> modeIsActiveMap) {

        Session session = memory.selectedSession();
        for (Pattern pattern : session.getPatterns()) {
            displayPattern(pattern);
        }

        for (FillPattern fill : session.getFills()) {
            displayFill(fill);
        }

        Pattern pattern = memory.selectedPattern();
        for (Track track : pattern.getTracks()) {
            displayTrack(track);
        }

        if (modeIsActiveMap != null) {
            displayModes(modeIsActiveMap);
        }

    }

    public void displayHelp() {

        // pattern buttons are green
        Color patternColor = LppRhythmUtil.COLOR_PATTERN;
        for (int y = LppRhythmUtil.PATTERNS_MIN_ROW; y <= LppRhythmUtil.PATTERNS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                display.setPad(GridPad.at(x, y), patternColor);
            }
        }
        for (int y = LppRhythmUtil.FILLS_MIN_ROW; y <= LppRhythmUtil.FILLS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                display.setPad(GridPad.at(x, y), patternColor);
            }
        }
        display.setPad(modePadMap.get(RhythmInterface.Mode.TRACK_MUTE), patternColor);
        display.setPad(modePadMap.get(RhythmInterface.Mode.TRACK_EDIT), patternColor);

        // track buttons are orange
        Color trackColor = LppRhythmUtil.COLOR_PATTERN_SELECTED;
        for (int y = LppRhythmUtil.TRACKS_MIN_ROW; y <= LppRhythmUtil.TRACKS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                display.setPad(GridPad.at(x, y), trackColor);
            }
        }
        display.setPad(modePadMap.get(RhythmInterface.Mode.TRACK_MUTE), trackColor);
        display.setPad(modePadMap.get(RhythmInterface.Mode.TRACK_EDIT), trackColor);

        // step buttons are red
        Color stepColor = LppRhythmUtil.COLOR_TRACK;
        for (int y = STEPS_MIN_ROW; y <= LppRhythmUtil.STEPS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                display.setPad(GridPad.at(x, y), stepColor);
            }
        }
        // step mode buttons
        display.setPad(modePadMap.get(RhythmInterface.Mode.STEP_MUTE), stepColor);
        display.setPad(modePadMap.get(RhythmInterface.Mode.STEP_VELOCITY), stepColor);
        display.setPad(modePadMap.get(RhythmInterface.Mode.STEP_JUMP), stepColor);
        display.setPad(modePadMap.get(RhythmInterface.Mode.STEP_PLAY), stepColor);

    }

    public void displayModule(RhythmInterface.Module module, Memory memory, Map<RhythmInterface.Mode,Boolean> modeIsActiveMap, int currentFileIndex) {

        switch (module) {
            case SEQUENCE:
                display.initialize();
                displayAll(memory, modeIsActiveMap);
                break;
            case SETTINGS:
                display.initialize();
                for (Session session : memory.getSessions()) {
                    displaySession(session);
                }
                displayFiles(currentFileIndex);
                if (modeIsActiveMap != null) {
                    displayModes(modeIsActiveMap);
                }

                displaySwitches(memory.getSettingsSwitches());

                break;
        }

    }

    public void displaySession(Session session) {

        if (currentModule != RhythmInterface.Module.SETTINGS) { return; }

        int x = getX(session.getIndex());
        int y = LppRhythmUtil.PATTERNS_MIN_ROW + getY(session.getIndex());

        Color color = LppRhythmUtil.COLOR_PATTERN;
        if (session.isSelected()) {
            color = LppRhythmUtil.COLOR_PATTERN_SELECTED;
        } else if (session.isNext()) {
            color = LppRhythmUtil.COLOR_PATTERN_CHAINED;
        }
        display.setPad(GridPad.at(x, y), color);

    }

    public void displayFiles(int currentFileIndex) {

        for (int x = 0; x < 8; x++) {
            if (x == currentFileIndex) {
                display.setPad(GridPad.at(x, LOAD_ROW), LppRhythmUtil.COLOR_PATTERN_PLAYING);
                display.setPad(GridPad.at(x, SAVE_ROW), LppRhythmUtil.COLOR_TRACK_SELECTED);
            } else {
                display.setPad(GridPad.at(x, LOAD_ROW), LppRhythmUtil.COLOR_PATTERN);
                display.setPad(GridPad.at(x, SAVE_ROW), LppRhythmUtil.COLOR_TRACK);
            }
        }

    }

    public void displayPattern(Pattern pattern) {

        if (currentModule != RhythmInterface.Module.SEQUENCE) { return; }

        int x = getX(pattern.getIndex());
        int y = LppRhythmUtil.PATTERNS_MIN_ROW + getY(pattern.getIndex());

        if (pattern instanceof FillPattern) {
            y = LppRhythmUtil.FILLS_MIN_ROW + getY(pattern.getIndex());
        }


        Color color = COLOR_PATTERN;
        if (pattern.isSelected() && pattern.isPlaying()) {
            color = COLOR_PATTERN_SELECTED_PLAYING;
        } else if (pattern.isSelected()) {
            color = COLOR_PATTERN_SELECTED;
        } else if (pattern.isPlaying())  {
            color = COLOR_PATTERN_PLAYING;
        } else if (pattern.isChained())  {
            color = COLOR_PATTERN_CHAINED;
        }
//        System.out.printf("displayPattern: %s, x=%d, y=%d, sel=%s, play=%s, chained=%s, color=%d,%d\n",
//                pattern, x, y, pattern.isSelected(), pattern.isPlaying(), pattern.isChained(),
//                color.getRed(), color.getGreen());

        display.setPad(GridPad.at(x, y), color);

        if (pattern.isSelected()) {
            for (Track track : pattern.getTracks()) {
                displayTrack(track);
            }
        }

    }

    public void displayFill(FillPattern fill) {
        displayPattern(fill);
    }


    public void displayTrack(Track track) {
        displayTrack(track, true);
    }

    public void displayTrack(Track track, boolean displaySteps) {

        if (currentModule != RhythmInterface.Module.SEQUENCE) { return; }

        int x = getX(track.getIndex());
        int y = LppRhythmUtil.TRACKS_MIN_ROW + getY(track.getIndex());
//        if (displaySteps) {
//            System.out.printf("displayTrack: %s, x=%d, y=%d, play=%s, enab=%s, sel=%s, dispStep=%s\n",
//                    track, x, y, track.isPlaying(), track.isEnabled(), track.isSelected(), displaySteps);
//        }
        if (track.isPlaying()) {
            if (track.isEnabled()) {
                display.setPad(GridPad.at(x, y), LppRhythmUtil.COLOR_TRACK_PLAYING);
            } else {
                display.setPad(GridPad.at(x, y), LppRhythmUtil.COLOR_TRACK_MUTED_PLAYING);
            }
        } else if (track.isSelected()) {
            if (track.isEnabled()) {
                display.setPad(GridPad.at(x, y), LppRhythmUtil.COLOR_TRACK_SELECTED);
            } else {
                display.setPad(GridPad.at(x, y), LppRhythmUtil.COLOR_TRACK_MUTED_SELECTED);
            }
            if (displaySteps) {
                for (int i = 0; i < Track.getStepCount(); i++) {
                    displayStep(track.getStep(i));
                }
            }
        } else {
            if (track.isEnabled()) {
                display.setPad(GridPad.at(x, y), LppRhythmUtil.COLOR_TRACK);
            } else {
                display.setPad(GridPad.at(x, y), LppRhythmUtil.COLOR_TRACK_MUTED);
            }
        }
    }

    public void displayStep(Step step) {

        if (currentModule != RhythmInterface.Module.SEQUENCE) { return; }

        int x = getX(step.getIndex());
        int y = STEPS_MIN_ROW + getY(step.getIndex());
//        System.out.printf("displayStep: %s, x=%d, y=%d\n", step, x, y);
//        if (step.isSelected()) {
//            display.setPad(GridPad.at(x, y), COLOR_SELECTED);
//        } else if (step.isOn()) {
        if (step.isOn()) {
            display.setPad(GridPad.at(x, y), COLOR_STEP_ON);
        } else {
            display.setPad(GridPad.at(x, y), LppRhythmUtil.COLOR_STEP);
        }
    }

    public void clearSteps() {

        if (currentModule != RhythmInterface.Module.SEQUENCE) { return; }

        for (int index = 0; index < Track.getStepCount(); index++) {
//            Step step = new Step(index);
//            displayStep(step);
            int x = getX(index);
            int y = STEPS_MIN_ROW + getY(index);
            display.setPad(GridPad.at(x, y), LppRhythmUtil.COLOR_STEP);
        }
    }

    public void displayPlayingStep(int stepNumber) {

        if (currentModule != RhythmInterface.Module.SEQUENCE) { return; }

        int x = getX(stepNumber);
        int y = STEPS_MIN_ROW + getY(stepNumber);
        display.setPad(GridPad.at(x, y), COLOR_STEP_PLAYING);
    }

    public void displayMode(RhythmInterface.Mode mode, boolean isActive) {

        if (currentModule == RhythmInterface.Module.SETTINGS && !Arrays.asList(SETTINGS_MODULE_MODES).contains(mode)) {
            return;
        }

        if (mode == EXIT && !LppRhythmUtil.debugMode) {
            return;
        }

        Color color = COLOR_MODE_INACTIVE;// Color.of(2,1);
        if (isActive) {
            color = COLOR_MODE_ACTIVE;
        }

        if (modeButtonMap.get(mode) != null) {
            display.setButton(modeButtonMap.get(mode), color);
        } else if (modePadMap.get(mode) != null) {
            display.setPad(modePadMap.get(mode), color);
        }

    }

    public void displayModes(Map<RhythmInterface.Mode,Boolean> modeIsActiveMap) {
        for (Map.Entry<RhythmInterface.Mode, Boolean> entry : modeIsActiveMap.entrySet()) {
            displayMode(entry.getKey(), entry.getValue());
        }
    }

    public void displayModeChoice(RhythmInterface.Mode mode, RhythmInterface.Mode[] modeChoices) {
        for (RhythmInterface.Mode modeChoice : modeChoices) {
            if (modeChoice == mode) {
                displayMode(modeChoice, true);
            } else {
                displayMode(modeChoice, false);
            }
        }
    }

    public void clearValue() {
        for (int b = 0; b < 8; b++) {
            display.setButton(GridButton.at(Right, 7-b), COLOR_VALUE);
        }
    }

    public void displayValue(int value, int minValue, int maxValue, RhythmInterface.ValueMode valueMode) {

        int buttons = 8 * (value - minValue) / (maxValue - minValue);

        Color color = LppRhythmUtil.COLOR_STEP_ON;
        if (valueMode == RhythmInterface.ValueMode.TEMPO) {
            color = LppRhythmUtil.COLOR_STEP_PLAYING;
        } else if (valueMode == RhythmInterface.ValueMode.FILL_PERCENT) {
            color = LppRhythmUtil.COLOR_PATTERN;
        }

        for (int b = 0; b < 8; b++) {
            Color buttonColor = LppRhythmUtil.COLOR_VALUE;
            if (b <= buttons) {
                buttonColor = color;
            }
            display.setButton(GridButton.at(Right, 7-b), buttonColor);
        }

    }

    public void selectModule(RhythmInterface.Module module) {
        currentModule = module;
    }

    public void displaySwitches(Map<RhythmInterface.Switch, Boolean> switches) {

        if (currentModule != RhythmInterface.Module.SETTINGS) { return; }

        Color off = LppRhythmUtil.COLOR_MODE_INACTIVE;
        Color on = LppRhythmUtil.COLOR_MODE_ACTIVE;

        for (RhythmInterface.Switch switchx : switches.keySet()) {
            if (switchPadMap.get(switchx) != null) {
                if (switches.get(switchx)) {
                    display.setPad(switchPadMap.get(switchx), on);
                } else {
                    display.setPad(switchPadMap.get(switchx), off);
                }
            }
        }

    }




    /***** private implementation ***************************************************************/

    private int getX(int index) {
        return index % 8;
    }

    private int getY(int index) {
        return index / 8;
    }


}
