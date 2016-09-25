package net.perkowitz.issho.devices.launchpadpro;

import com.google.common.collect.Sets;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmController;
import net.perkowitz.issho.hachi.modules.rhythm.RhythmInterface;
import net.perkowitz.sequence.devices.launchpadpro.LaunchpadProUtil;

import java.util.Set;

import static net.perkowitz.sequence.devices.launchpadpro.LaunchpadProUtil.*;

/**
 * Created by mperkowi on 7/15/16.
 */
public class LppRhythmController implements GridListener, RhythmController {

    private RhythmInterface sequencer = null;

    private Set<Integer> patternsPressed = Sets.newHashSet();
    private int patternsReleasedCount = 0;

    private RhythmInterface.Module currentModule = RhythmInterface.Module.SEQUENCE;

    public LppRhythmController() {
    }

    public void setSequencer(RhythmInterface sequencer) {
        this.sequencer = sequencer;
    }

    public void onPadPressed(GridPad pad, int velocity) {

//        System.out.printf("onPadPressed: %s, %s\n", pad, timestamp);

        try {

            if (currentModule == RhythmInterface.Module.SEQUENCE) {

                if (pad.getY() >= PATTERNS_MIN_ROW && pad.getY() <= PATTERNS_MAX_ROW) {
                    // pressing a pattern pad
                    int index = pad.getX() + (pad.getY() - PATTERNS_MIN_ROW) * 8;
                    patternsPressed.add(index);

                } else if (pad.getY() >= FILLS_MIN_ROW && pad.getY() <= FILLS_MAX_ROW) {
                    // pressing a track pad
                    int index = pad.getX() + (pad.getY() - FILLS_MIN_ROW) * 8;
                    sequencer.selectFill(index);

                } else if (pad.getY() >= TRACKS_MIN_ROW && pad.getY() <= TRACKS_MAX_ROW) {
                    // pressing a track pad
                    int index = pad.getX() + (pad.getY() - TRACKS_MIN_ROW) * 8;
                    sequencer.selectTrack(index);

                } else if (pad.getY() >= STEPS_MIN_ROW && pad.getY() <= STEPS_MAX_ROW) {
                    // pressing a step pad
                    int index = pad.getX() + (pad.getY() - STEPS_MIN_ROW) * 8;
                    sequencer.selectStep(index);

                } else if (pad.equals(modePadMap.get(RhythmInterface.Mode.TRACK_MUTE))) {
                    sequencer.selectMode(RhythmInterface.Mode.TRACK_MUTE);

                } else if (pad.equals(modePadMap.get(RhythmInterface.Mode.TRACK_EDIT))) {
                    sequencer.selectMode(RhythmInterface.Mode.TRACK_EDIT);

                } else if (pad.equals(modePadMap.get(RhythmInterface.Mode.STEP_MUTE))) {
                    sequencer.selectMode(RhythmInterface.Mode.STEP_MUTE);

                } else if (pad.equals(modePadMap.get(RhythmInterface.Mode.STEP_VELOCITY))) {
                    sequencer.selectMode(RhythmInterface.Mode.STEP_VELOCITY);

                } else if (pad.equals(modePadMap.get(RhythmInterface.Mode.STEP_JUMP))) {
                    sequencer.selectMode(RhythmInterface.Mode.STEP_JUMP);

                } else if (pad.equals(modePadMap.get(RhythmInterface.Mode.STEP_PLAY))) {
                    sequencer.selectMode(RhythmInterface.Mode.STEP_PLAY);

                }

            } else if (currentModule == RhythmInterface.Module.SETTINGS) {

                if (pad.getY() >= SESSIONS_MIN_ROW && pad.getY() <= SESSIONS_MAX_ROW) {
                    // pressing a session pad
                    int index = pad.getX() + (pad.getY() - SESSIONS_MIN_ROW) * 8;
                    sequencer.selectSession(index);

                } else if (pad.getY() == LOAD_ROW) {
                    // pressing a load pad
                    int index = pad.getX() + (pad.getY() - LOAD_ROW) * 8;
                    sequencer.loadData(index);

                } else if (pad.getY() == SAVE_ROW) {
                    // pressing a save pad
                    int index = pad.getX() + (pad.getY() - SAVE_ROW) * 8;
                    sequencer.saveData(index);

                } else if (pad.getY() == SWITCHES_ROW) {
                    // toggling a switch
                    if (pad.equals(switchPadMap.get(RhythmInterface.Switch.INTERNAL_CLOCK_ENABLED))) {
                        sequencer.selectSwitch(RhythmInterface.Switch.INTERNAL_CLOCK_ENABLED);
                    } else if (pad.equals(switchPadMap.get(RhythmInterface.Switch.MIDI_CLOCK_ENABLED))) {
                        sequencer.selectSwitch(RhythmInterface.Switch.MIDI_CLOCK_ENABLED);
                    } else if (pad.equals(switchPadMap.get(RhythmInterface.Switch.TRIGGER_ENABLED))) {
                        sequencer.selectSwitch(RhythmInterface.Switch.TRIGGER_ENABLED);
                    }

                }

            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }

    }


    
    public void onPadReleased(GridPad pad) {

//        System.out.printf("onPadReleased: %s, %s\n", pad, timestamp);

        try {

            if (currentModule == RhythmInterface.Module.SEQUENCE) {
                if (pad.getY() >= PATTERNS_MIN_ROW && pad.getY() <= PATTERNS_MAX_ROW) {
                    // releasing a pattern pad
                    // don't activate until the last pattern pad is released (so additional releases don't look like a new press/release)
                    patternsReleasedCount++;
                    if (patternsReleasedCount >= patternsPressed.size()) {
                        int index = pad.getX() + (pad.getY() - PATTERNS_MIN_ROW) * 8;
                        patternsPressed.add(index); // just to make sure
                        if (patternsPressed.size() == 1) {
                            sequencer.selectPatterns(index, index);
                        } else {
                            int min = 100;
                            int max = -1;
                            for (Integer pattern : patternsPressed) {
                                if (pattern < min) {
                                    min = pattern;
                                }
                                if (pattern > max) {
                                    max = pattern;
                                }
                            }
                            sequencer.selectPatterns(min, max);
                        }
                        patternsPressed.clear();
                        patternsReleasedCount = 0;
                    }
                }

            } else if (currentModule == RhythmInterface.Module.SETTINGS) {

            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    
    public void onButtonPressed(GridButton button, int velocity) {

        // buttons that are available in all modules
        if (button.equals(modeButtonMap.get(RhythmInterface.Mode.PLAY))) {
            sequencer.selectMode(RhythmInterface.Mode.PLAY);

        } else if (button.equals(modeButtonMap.get(RhythmInterface.Mode.EXIT))) {
            if (LaunchpadProUtil.debugMode) {
                sequencer.selectMode(RhythmInterface.Mode.EXIT);
            }

        } else if (button.equals(modeButtonMap.get(RhythmInterface.Mode.SEQUENCE))) {
            currentModule = RhythmInterface.Module.SEQUENCE;
            sequencer.selectModule(RhythmInterface.Module.SEQUENCE);

        } else if (button.equals(modeButtonMap.get(RhythmInterface.Mode.SETTINGS))) {
            currentModule = RhythmInterface.Module.SETTINGS;
            sequencer.selectModule(RhythmInterface.Module.SETTINGS);

        } else if (button.getSide() == GridButton.Side.Right) {
            // pressing one of the value buttons
            int index = 7 - button.getIndex();
            sequencer.selectValue(index);

        } else if (currentModule == RhythmInterface.Module.SEQUENCE) {
            // buttons only in sequence module

            if (button.equals(modeButtonMap.get(RhythmInterface.Mode.SAVE))) {
                sequencer.selectMode(RhythmInterface.Mode.SAVE);

            } else if (button.equals(modeButtonMap.get(RhythmInterface.Mode.TEMPO))) {
                sequencer.selectMode(RhythmInterface.Mode.TEMPO);

            } else if (button.equals(modeButtonMap.get(RhythmInterface.Mode.PATTERN_EDIT))) {
                sequencer.selectMode(RhythmInterface.Mode.PATTERN_EDIT);

            }

        }

    }

    
    public void onButtonReleased(GridButton button) {

        if (button.equals(modeButtonMap.get(RhythmInterface.Mode.PATTERN_EDIT))) {
            sequencer.selectMode(RhythmInterface.Mode.PATTERN_PLAY);

        } else if (button.equals(modeButtonMap.get(RhythmInterface.Mode.TEMPO))) {
            sequencer.selectMode(RhythmInterface.Mode.NO_VALUE);

        }

    }


}
