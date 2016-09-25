package net.perkowitz.issho.hachi.modules.rhythm;


import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.hachi.models.*;

import java.util.Map;

/**
 * Created by optic on 7/10/16.
 */
public interface RhythmDisplay {

    public enum DisplayButton {
        PLAY, EXIT, SAVE, HELP,
        TRACK_MUTE_MODE, TRACK_SELECT_MODE,
        STEP_MUTE_MODE, STEP_VELOCITY_MODE, STEP_JUMP_MODE, STEP_PLAY_MODE
    }
    public enum ButtonState { EMPTY, ENABLED, DISABLED, SELECTED, PLAYING, PLAYING_SELECTED }

    public void initialize();
    public void displayAll(Memory memory, Map<RhythmInterface.Mode, Boolean> modeIsActiveMap);
    public void displayHelp();

    public void setDisplay(GridDisplay display);

    public void displayModule(RhythmInterface.Module module, Memory memory, Map<RhythmInterface.Mode, Boolean> modeIsActiveMap, int currentFileIndex);

    public void displaySession(Session session);
    public void displayFiles(int currentFileIndex);

    public void displayPattern(Pattern pattern);
    public void displayFill(FillPattern fill);

    public void displayTrack(Track track, boolean displaySteps);
    public void displayTrack(Track track);

    public void displayStep(Step step);
    public void clearSteps();
    public void displayPlayingStep(int stepNumber);

    public void displayMode(RhythmInterface.Mode mode, boolean isActive);
    public void displayModes(Map<RhythmInterface.Mode, Boolean> modeIsActiveMap);
    public void displayModeChoice(RhythmInterface.Mode mode, RhythmInterface.Mode[] modeChoices);

    public void clearValue();
    public void displayValue(int value, int minValue, int maxValue, RhythmInterface.ValueMode valueMode);

    public void selectModule(RhythmInterface.Module module);

    public void displaySwitches(Map<RhythmInterface.Switch, Boolean> switches);

}
