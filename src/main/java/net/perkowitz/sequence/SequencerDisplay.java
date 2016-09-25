package net.perkowitz.sequence;

import net.perkowitz.sequence.models.*;

import java.util.Map;

/**
 * Created by optic on 7/10/16.
 */
public interface SequencerDisplay {

    public enum DisplayButton {
        PLAY, EXIT, SAVE, HELP,
        TRACK_MUTE_MODE, TRACK_SELECT_MODE,
        STEP_MUTE_MODE, STEP_VELOCITY_MODE, STEP_JUMP_MODE, STEP_PLAY_MODE
    }
    public enum ButtonState { EMPTY, ENABLED, DISABLED, SELECTED, PLAYING, PLAYING_SELECTED }

    public void initialize();
    public void displayAll(Memory memory, Map<SequencerInterface.Mode,Boolean> modeIsActiveMap);
    public void displayHelp();

    public void displayModule(SequencerInterface.Module module, Memory memory, Map<SequencerInterface.Mode,Boolean> modeIsActiveMap, int currentFileIndex);

    public void displaySession(Session session);
    public void displayFiles(int currentFileIndex);

    public void displayPattern(Pattern pattern);
    public void displayFill(FillPattern fill);

    public void displayTrack(Track track, boolean displaySteps);
    public void displayTrack(Track track);

    public void displayStep(Step step);
    public void clearSteps();
    public void displayPlayingStep(int stepNumber);

    public void displayMode(SequencerInterface.Mode mode, boolean isActive);
    public void displayModes(Map<SequencerInterface.Mode,Boolean> modeIsActiveMap);
    public void displayModeChoice(SequencerInterface.Mode mode, SequencerInterface.Mode[] modeChoices);

    public void clearValue();
    public void displayValue(int value, int minValue, int maxValue, SequencerInterface.ValueMode valueMode);

    public void selectModule(SequencerInterface.Module module);

    public void displaySwitches(Map<SequencerInterface.Switch, Boolean> switches);

}
