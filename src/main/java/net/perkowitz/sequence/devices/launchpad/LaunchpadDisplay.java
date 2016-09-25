package net.perkowitz.sequence.devices.launchpad;

import net.perkowitz.sequence.SequencerDisplay;
import net.perkowitz.sequence.SequencerInterface;
import net.perkowitz.sequence.models.*;
import net.thecodersbreakfast.lp4j.api.*;

import java.util.Arrays;
import java.util.Map;

import static net.perkowitz.sequence.SequencerInterface.Mode.EXIT;
import static net.perkowitz.sequence.SequencerInterface.SETTINGS_MODULE_MODES;
import static net.perkowitz.sequence.devices.launchpad.LaunchpadUtil.*;

/**
 * Created by optic on 7/10/16.
 */
public class LaunchpadDisplay implements SequencerDisplay {

    private LaunchpadClient launchpadClient;
    private SequencerInterface.Module currentModule = SequencerInterface.Module.SEQUENCE;


    public LaunchpadDisplay(LaunchpadClient launchpadClient) {
        this.launchpadClient = launchpadClient;
    }

    public void initialize() {
        launchpadClient.reset();
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
        Color patternColor = LaunchpadUtil.COLOR_PLAYING_DIM;
        for (int y = LaunchpadUtil.PATTERNS_MIN_ROW; y <= LaunchpadUtil.PATTERNS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), patternColor, BackBufferOperation.NONE);
            }
        }
        for (int y = LaunchpadUtil.FILLS_MIN_ROW; y <= LaunchpadUtil.FILLS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), patternColor, BackBufferOperation.NONE);
            }
        }
        launchpadClient.setPadLight(modePadMap.get(SequencerInterface.Mode.TRACK_MUTE), patternColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(modePadMap.get(SequencerInterface.Mode.TRACK_EDIT), patternColor, BackBufferOperation.NONE);

        // track buttons are orange
        Color trackColor = LaunchpadUtil.COLOR_SELECTED_DIM;
        for (int y = LaunchpadUtil.TRACKS_MIN_ROW; y <= LaunchpadUtil.TRACKS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), trackColor, BackBufferOperation.NONE);
            }
        }
        launchpadClient.setPadLight(modePadMap.get(SequencerInterface.Mode.TRACK_MUTE), trackColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(modePadMap.get(SequencerInterface.Mode.TRACK_EDIT), trackColor, BackBufferOperation.NONE);

        // step buttons are red
        Color stepColor = LaunchpadUtil.COLOR_DISABLED;
        for (int y = STEPS_MIN_ROW; y <= LaunchpadUtil.STEPS_MAX_ROW; y++) {
            for (int x = 0; x < 8; x++) {
                launchpadClient.setPadLight(Pad.at(x, y), stepColor, BackBufferOperation.NONE);
            }
        }
        // step mode buttons
        launchpadClient.setPadLight(modePadMap.get(SequencerInterface.Mode.STEP_MUTE), stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(modePadMap.get(SequencerInterface.Mode.STEP_VELOCITY), stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(modePadMap.get(SequencerInterface.Mode.STEP_JUMP), stepColor, BackBufferOperation.NONE);
        launchpadClient.setPadLight(modePadMap.get(SequencerInterface.Mode.STEP_PLAY), stepColor, BackBufferOperation.NONE);

    }

    public void displayModule(SequencerInterface.Module module, Memory memory, Map<SequencerInterface.Mode,Boolean> modeIsActiveMap, int currentFileIndex) {

        switch (module) {
            case SEQUENCE:
                launchpadClient.reset();
                displayAll(memory, modeIsActiveMap);
                break;
            case SETTINGS:
                launchpadClient.reset();
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
        int y = LaunchpadUtil.PATTERNS_MIN_ROW + getY(session.getIndex());

        Color color = LaunchpadUtil.COLOR_SELECTED_DIM;
        if (session.isSelected()) {
            color = LaunchpadUtil.COLOR_SELECTED;
        } else if (session.isNext()) {
            color = LaunchpadUtil.COLOR_DISABLED;
        }
        launchpadClient.setPadLight(Pad.at(x, y), color, BackBufferOperation.NONE);

    }

    public void displayFiles(int currentFileIndex) {

        // load buttons are green; save buttons are red
        for (int x = 0; x < 8; x++) {
            if (x == currentFileIndex) {
                launchpadClient.setPadLight(Pad.at(x, LOAD_ROW), LaunchpadUtil.COLOR_PLAYING, BackBufferOperation.NONE);
                launchpadClient.setPadLight(Pad.at(x, SAVE_ROW), LaunchpadUtil.COLOR_ENABLED, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, LOAD_ROW), LaunchpadUtil.COLOR_PLAYING_DIM, BackBufferOperation.NONE);
                launchpadClient.setPadLight(Pad.at(x, SAVE_ROW), LaunchpadUtil.COLOR_DISABLED, BackBufferOperation.NONE);
            }
        }

    }

    public void displayPattern(Pattern pattern) {

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        int x = getX(pattern.getIndex());
        int y = LaunchpadUtil.PATTERNS_MIN_ROW + getY(pattern.getIndex());

        if (pattern instanceof FillPattern) {
            y = LaunchpadUtil.FILLS_MIN_ROW + getY(pattern.getIndex());
        }


        Color color = COLOR_PLAYING_DIM;
        if (pattern.isSelected() && pattern.isPlaying()) {
            color = COLOR_SELECTED;
        } else if (pattern.isSelected()) {
            color = COLOR_SELECTED_DIM;
        } else if (pattern.isPlaying())  {
            color = COLOR_PLAYING;
        } else if (pattern.isChained())  {
            color = COLOR_DISABLED;
        }
//        System.out.printf("displayPattern: %s, x=%d, y=%d, sel=%s, play=%s, chained=%s, color=%d,%d\n",
//                pattern, x, y, pattern.isSelected(), pattern.isPlaying(), pattern.isChained(),
//                color.getRed(), color.getGreen());

        launchpadClient.setPadLight(Pad.at(x, y), color, BackBufferOperation.NONE);

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
        int y = LaunchpadUtil.TRACKS_MIN_ROW + getY(track.getIndex());
//        if (displaySteps) {
//            System.out.printf("displayTrack: %s, x=%d, y=%d, play=%s, enab=%s, sel=%s, dispStep=%s\n",
//                    track, x, y, track.isPlaying(), track.isEnabled(), track.isSelected(), displaySteps);
//        }
        if (track.isPlaying()) {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_PLAYING, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_SELECTED_DIM, BackBufferOperation.NONE);
            }
        } else if (track.isSelected()) {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_SELECTED, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_SELECTED_DIM, BackBufferOperation.NONE);
            }
            if (displaySteps) {
                for (int i = 0; i < Track.getStepCount(); i++) {
                    displayStep(track.getStep(i));
                }
            }
        } else {
            if (track.isEnabled()) {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_DISABLED, BackBufferOperation.NONE);
            } else {
                launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_EMPTY, BackBufferOperation.NONE);
            }
        }
    }

    public void displayStep(Step step) {

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        int x = getX(step.getIndex());
        int y = STEPS_MIN_ROW + getY(step.getIndex());
//        System.out.printf("displayStep: %s, x=%d, y=%d\n", step, x, y);
//        if (step.isSelected()) {
//            launchpadClient.setPadLight(Pad.at(x, y), COLOR_SELECTED, BackBufferOperation.NONE);
//        } else if (step.isOn()) {
        if (step.isOn()) {
            launchpadClient.setPadLight(Pad.at(x, y), COLOR_ENABLED, BackBufferOperation.NONE);
        } else {
            launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_EMPTY, BackBufferOperation.NONE);
        }
    }

    public void clearSteps() {

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        for (int index = 0; index < Track.getStepCount(); index++) {
//            Step step = new Step(index);
//            displayStep(step);
            int x = getX(index);
            int y = STEPS_MIN_ROW + getY(index);
            launchpadClient.setPadLight(Pad.at(x, y), LaunchpadUtil.COLOR_EMPTY, BackBufferOperation.NONE);
        }
    }

    public void displayPlayingStep(int stepNumber) {

        if (currentModule != SequencerInterface.Module.SEQUENCE) { return; }

        int x = getX(stepNumber);
        int y = STEPS_MIN_ROW + getY(stepNumber);
        launchpadClient.setPadLight(Pad.at(x, y), COLOR_PLAYING, BackBufferOperation.NONE);
    }

    public void displayMode(SequencerInterface.Mode mode, boolean isActive) {

        if (currentModule == SequencerInterface.Module.SETTINGS && !Arrays.asList(SETTINGS_MODULE_MODES).contains(mode)) {
            return;
        }

        if (mode == EXIT && !LaunchpadUtil.debugMode) {
            return;
        }

        Color color = COLOR_DISABLED;// Color.of(2,1);
        if (isActive) {
            color = COLOR_PLAYING;
        }

        if (modeButtonMap.get(mode) != null) {
            launchpadClient.setButtonLight(modeButtonMap.get(mode), color, BackBufferOperation.NONE);
        } else if (modePadMap.get(mode) != null) {
            launchpadClient.setPadLight(modePadMap.get(mode), color, BackBufferOperation.NONE);
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
            launchpadClient.setButtonLight(Button.atRight(7-b), COLOR_EMPTY, BackBufferOperation.NONE);
        }
    }

    public void displayValue(int value, int minValue, int maxValue, SequencerInterface.ValueMode valueMode) {

        int buttons = 8 * (value - minValue) / (maxValue - minValue);

        Color color = LaunchpadUtil.COLOR_DISABLED;
        if (valueMode == SequencerInterface.ValueMode.TEMPO) {
            color = LaunchpadUtil.COLOR_PLAYING;
        } else if (valueMode == SequencerInterface.ValueMode.FILL_PERCENT) {
            color = LaunchpadUtil.COLOR_SELECTED;
        }

        for (int b = 0; b < 8; b++) {
            Color buttonColor = LaunchpadUtil.COLOR_EMPTY;
            if (b <= buttons) {
                buttonColor = color;
            }
            launchpadClient.setButtonLight(Button.atRight(7-b), buttonColor, BackBufferOperation.NONE);
        }

    }

    public void selectModule(SequencerInterface.Module module) {
        currentModule = module;
    }

    public void displaySwitches(Map<SequencerInterface.Switch, Boolean> switches) {

        if (currentModule != SequencerInterface.Module.SETTINGS) { return; }

        Color off = LaunchpadUtil.COLOR_DISABLED;
        Color on = LaunchpadUtil.COLOR_PLAYING;

        for (SequencerInterface.Switch switchx : switches.keySet()) {
            if (switchPadMap.get(switchx) != null) {
                if (switches.get(switchx)) {
                    launchpadClient.setPadLight(switchPadMap.get(switchx), on, BackBufferOperation.NONE);
                } else {
                    launchpadClient.setPadLight(switchPadMap.get(switchx), off, BackBufferOperation.NONE);
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
