package net.perkowitz.sequence.devices.launchpadpro;

import net.perkowitz.sequence.SequencerDisplay;
import net.perkowitz.sequence.SequencerInterface;
import net.perkowitz.sequence.models.*;

import java.util.Arrays;
import java.util.Map;

import static net.perkowitz.sequence.SequencerInterface.Mode.EXIT;
import static net.perkowitz.sequence.SequencerInterface.SETTINGS_MODULE_MODES;
import static net.perkowitz.sequence.devices.GridButton.Side.Right;
import static net.perkowitz.sequence.devices.launchpadpro.LaunchpadProUtil.*;

/**
 * Created by optic on 7/10/16.
 */
public class LaunchpadProDisplay implements SequencerDisplay {

    private LaunchpadPro launchpadPro;
    private SequencerInterface.Module currentModule = SequencerInterface.Module.SEQUENCE;


    public LaunchpadProDisplay(LaunchpadPro launchpadPro) {
        this.launchpadPro = launchpadPro;
    }

    public void initialize() {
        launchpadPro.initialize();
    }

    public void displayAll(Memory memory, Map<SequencerInterface.Mode,Boolean> modeIsActiveMap) {

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
        Color patternColor = LaunchpadProUtil.COLOR_PATTERN;
        for (int y = LaunchpadProUtil.PATTERNS_MIN_ROW; y <= LaunchpadProUtil.PATTERNS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadPro.setPad(Pad.at(x, y), patternColor);
            }
        }
        for (int y = LaunchpadProUtil.FILLS_MIN_ROW; y <= LaunchpadProUtil.FILLS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadPro.setPad(Pad.at(x, y), patternColor);
            }
        }
        launchpadPro.setPad(modePadMap.get(SequencerInterface.Mode.TRACK_MUTE), patternColor);
        launchpadPro.setPad(modePadMap.get(SequencerInterface.Mode.TRACK_EDIT), patternColor);

        // track buttons are orange
        Color trackColor = LaunchpadProUtil.COLOR_PATTERN_SELECTED;
        for (int y = LaunchpadProUtil.TRACKS_MIN_ROW; y <= LaunchpadProUtil.TRACKS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadPro.setPad(Pad.at(x, y), trackColor);
            }
        }
        launchpadPro.setPad(modePadMap.get(SequencerInterface.Mode.TRACK_MUTE), trackColor);
        launchpadPro.setPad(modePadMap.get(SequencerInterface.Mode.TRACK_EDIT), trackColor);

        // step buttons are red
        Color stepColor = LaunchpadProUtil.COLOR_TRACK;
        for (int y = STEPS_MIN_ROW; y <= LaunchpadProUtil.STEPS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadPro.setPad(Pad.at(x, y), stepColor);
            }
        }
        // step mode buttons
        launchpadPro.setPad(modePadMap.get(SequencerInterface.Mode.STEP_MUTE), stepColor);
        launchpadPro.setPad(modePadMap.get(SequencerInterface.Mode.STEP_VELOCITY), stepColor);
        launchpadPro.setPad(modePadMap.get(SequencerInterface.Mode.STEP_JUMP), stepColor);
        launchpadPro.setPad(modePadMap.get(SequencerInterface.Mode.STEP_PLAY), stepColor);

    }

    public void displayModule(SequencerInterface.Module module, Memory memory, Map<SequencerInterface.Mode,Boolean> modeIsActiveMap, int currentFileIndex) {

        switch (module) {
            case SEQUENCE:
                launchpadPro.initialize();
                displayAll(memory, modeIsActiveMap);
                break;
            case SETTINGS:
                launchpadPro.initialize();
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

        if (currentModule != SequencerInterface.Module.SETTINGS) { return; }

        int x = getX(session.getIndex());
        int y = LaunchpadProUtil.PATTERNS_MIN_ROW + getY(session.getIndex());

        Color color = LaunchpadProUtil.COLOR_PATTERN;
        if (session.isSelected()) {
            color = LaunchpadProUtil.COLOR_PATTERN_SELECTED;
        } else if (session.isNext()) {
            color = LaunchpadProUtil.COLOR_PATTERN_CHAINED;
        }
        launchpadPro.setPad(Pad.at(x, y), color);

    }

    public void displayFiles(int currentFileIndex) {

        for (int x = 0; x < 8; x++) {
            if (x == currentFileIndex) {
                launchpadPro.setPad(Pad.at(x, LOAD_ROW), LaunchpadProUtil.COLOR_PATTERN_PLAYING);
                launchpadPro.setPad(Pad.at(x, SAVE_ROW), LaunchpadProUtil.COLOR_TRACK_SELECTED);
            } else {
                launchpadPro.setPad(Pad.at(x, LOAD_ROW), LaunchpadProUtil.COLOR_PATTERN);
                launchpadPro.setPad(Pad.at(x, SAVE_ROW), LaunchpadProUtil.COLOR_TRACK);
            }
        }

    }

    public void displayPattern(Pattern pattern) {

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        int x = getX(pattern.getIndex());
        int y = LaunchpadProUtil.PATTERNS_MIN_ROW + getY(pattern.getIndex());

        if (pattern instanceof FillPattern) {
            y = LaunchpadProUtil.FILLS_MIN_ROW + getY(pattern.getIndex());
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

        launchpadPro.setPad(Pad.at(x, y), color);

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

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        int x = getX(track.getIndex());
        int y = LaunchpadProUtil.TRACKS_MIN_ROW + getY(track.getIndex());
//        if (displaySteps) {
//            System.out.printf("displayTrack: %s, x=%d, y=%d, play=%s, enab=%s, sel=%s, dispStep=%s\n",
//                    track, x, y, track.isPlaying(), track.isEnabled(), track.isSelected(), displaySteps);
//        }
        if (track.isPlaying()) {
            if (track.isEnabled()) {
                launchpadPro.setPad(Pad.at(x, y), LaunchpadProUtil.COLOR_TRACK_PLAYING);
            } else {
                launchpadPro.setPad(Pad.at(x, y), LaunchpadProUtil.COLOR_TRACK_MUTED_PLAYING);
            }
        } else if (track.isSelected()) {
            if (track.isEnabled()) {
                launchpadPro.setPad(Pad.at(x, y), LaunchpadProUtil.COLOR_TRACK_SELECTED);
            } else {
                launchpadPro.setPad(Pad.at(x, y), LaunchpadProUtil.COLOR_TRACK_MUTED_SELECTED);
            }
            if (displaySteps) {
                for (int i = 0; i < Track.getStepCount(); i++) {
                    displayStep(track.getStep(i));
                }
            }
        } else {
            if (track.isEnabled()) {
                launchpadPro.setPad(Pad.at(x, y), LaunchpadProUtil.COLOR_TRACK);
            } else {
                launchpadPro.setPad(Pad.at(x, y), LaunchpadProUtil.COLOR_TRACK_MUTED);
            }
        }
    }

    public void displayStep(Step step) {

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        int x = getX(step.getIndex());
        int y = STEPS_MIN_ROW + getY(step.getIndex());
//        System.out.printf("displayStep: %s, x=%d, y=%d\n", step, x, y);
//        if (step.isSelected()) {
//            launchpadPro.setPad(Pad.at(x, y), COLOR_SELECTED);
//        } else if (step.isOn()) {
        if (step.isOn()) {
            launchpadPro.setPad(Pad.at(x, y), COLOR_STEP_ON);
        } else {
            launchpadPro.setPad(Pad.at(x, y), LaunchpadProUtil.COLOR_STEP);
        }
    }

    public void clearSteps() {

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        for (int index = 0; index < Track.getStepCount(); index++) {
//            Step step = new Step(index);
//            displayStep(step);
            int x = getX(index);
            int y = STEPS_MIN_ROW + getY(index);
            launchpadPro.setPad(Pad.at(x, y), LaunchpadProUtil.COLOR_STEP);
        }
    }

    public void displayPlayingStep(int stepNumber) {

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        int x = getX(stepNumber);
        int y = STEPS_MIN_ROW + getY(stepNumber);
        launchpadPro.setPad(Pad.at(x, y), COLOR_STEP_PLAYING);
    }

    public void displayMode(SequencerInterface.Mode mode, boolean isActive) {

        if (currentModule == SequencerInterface.Module.SETTINGS && !Arrays.asList(SETTINGS_MODULE_MODES).contains(mode)) {
            return;
        }

        if (mode == EXIT && !LaunchpadProUtil.debugMode) {
            return;
        }

        Color color = COLOR_MODE_INACTIVE;// Color.of(2,1);
        if (isActive) {
            color = COLOR_MODE_ACTIVE;
        }

        if (modeButtonMap.get(mode) != null) {
            launchpadPro.setButton(modeButtonMap.get(mode), color);
        } else if (modePadMap.get(mode) != null) {
            launchpadPro.setPad(modePadMap.get(mode), color);
        }

    }

    public void displayModes(Map<SequencerInterface.Mode,Boolean> modeIsActiveMap) {
        for (Map.Entry<SequencerInterface.Mode, Boolean> entry : modeIsActiveMap.entrySet()) {
            displayMode(entry.getKey(), entry.getValue());
        }
    }

    public void displayModeChoice(SequencerInterface.Mode mode, SequencerInterface.Mode[] modeChoices) {
        for (SequencerInterface.Mode modeChoice : modeChoices) {
            if (modeChoice == mode) {
                displayMode(modeChoice, true);
            } else {
                displayMode(modeChoice, false);
            }
        }
    }

    public void clearValue() {
        for (int b = 0; b < 8; b++) {
            launchpadPro.setButton(Button.at(Right, 7-b), COLOR_VALUE);
        }
    }

    public void displayValue(int value, int minValue, int maxValue, SequencerInterface.ValueMode valueMode) {

        int buttons = 8 * (value - minValue) / (maxValue - minValue);

        Color color = LaunchpadProUtil.COLOR_STEP_ON;
        if (valueMode == SequencerInterface.ValueMode.TEMPO) {
            color = LaunchpadProUtil.COLOR_STEP_PLAYING;
        } else if (valueMode == SequencerInterface.ValueMode.FILL_PERCENT) {
            color = LaunchpadProUtil.COLOR_PATTERN;
        }

        for (int b = 0; b < 8; b++) {
            Color buttonColor = LaunchpadProUtil.COLOR_VALUE;
            if (b <= buttons) {
                buttonColor = color;
            }
            launchpadPro.setButton(Button.at(Right, 7-b), buttonColor);
        }

    }

    public void selectModule(SequencerInterface.Module module) {
        currentModule = module;
    }

    public void displaySwitches(Map<SequencerInterface.Switch, Boolean> switches) {

        if (currentModule != SequencerInterface.Module.SETTINGS) { return; }

        Color off = LaunchpadProUtil.COLOR_MODE_INACTIVE;
        Color on = LaunchpadProUtil.COLOR_MODE_ACTIVE;

        for (SequencerInterface.Switch switchx : switches.keySet()) {
            if (switchPadMap.get(switchx) != null) {
                if (switches.get(switchx)) {
                    launchpadPro.setPad(switchPadMap.get(switchx), on);
                } else {
                    launchpadPro.setPad(switchPadMap.get(switchx), off);
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
