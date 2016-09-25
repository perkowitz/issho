package net.perkowitz.issho.hachi.modules.rhythm;


import static net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface.Mode.*;

/**
 * Created by mperkowi on 7/15/16.
 */
public interface RhythmInterface {

    public enum Module {
        SEQUENCE, SETTINGS
    }

    public enum Mode {
        PLAY, EXIT, TEMPO, NO_VALUE,
        SAVE, LOAD, HELP,
        SEQUENCE, SETTINGS,
        COPY, CLEAR,
        PATTERN_PLAY, PATTERN_EDIT,
        TRACK_MUTE, TRACK_EDIT,
        STEP_MUTE, STEP_VELOCITY, STEP_JUMP, STEP_PLAY
    }

    public enum Switch {
        TRIGGER_ENABLED, MIDI_CLOCK_ENABLED, INTERNAL_CLOCK_ENABLED
    }



    public static final Mode[] TRACK_MODES = new Mode[] { TRACK_MUTE, TRACK_EDIT };
    public static final Mode[] STEP_MODES = new Mode[] { STEP_MUTE, STEP_VELOCITY, STEP_JUMP, STEP_PLAY };

    // all modes available in SEQUENCE module (for now)
    public static final Mode[] SETTINGS_MODULE_MODES = new Mode[] { PLAY, EXIT, SEQUENCE, SETTINGS};

    public enum ValueMode {
        VELOCITY, TEMPO, FILL_PERCENT
    }

    public enum SyncMode {
        INTERNAL, MIDI, TRIGGER
    }

    public void selectModule(Module module);

    public void selectSession(int index);
    public void loadData(int index);
    public void saveData(int index);
    public void setSync(SyncMode syncMode);

    public void selectPatterns(int minIndex, int maxIndex);
    public void selectFill(int index);
    public void selectTrack(int index);
    public void selectStep(int index);
    public void selectValue(int index);
    public void selectMode(Mode mode);
    public void selectSwitch(Switch switchx);

    public void trigger(boolean isReset);
    public void clockTick();
    public void clockStart();
    public void clockStop();



}
