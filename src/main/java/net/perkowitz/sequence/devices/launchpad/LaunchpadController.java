package net.perkowitz.sequence.devices.launchpad;

import com.google.common.collect.Sets;
import net.perkowitz.sequence.SequencerController;
import net.perkowitz.sequence.SequencerInterface;
import net.thecodersbreakfast.lp4j.api.Button;
import net.thecodersbreakfast.lp4j.api.LaunchpadListenerAdapter;
import net.thecodersbreakfast.lp4j.api.Pad;

import java.util.Set;

import static net.perkowitz.sequence.devices.launchpad.LaunchpadUtil.*;

/**
 * Created by mperkowi on 7/15/16.
 */
public class LaunchpadController extends LaunchpadListenerAdapter implements SequencerController {

    private SequencerInterface sequencer = null;

    private Set<Integer> patternsPressed = Sets.newHashSet();
    private int patternsReleasedCount = 0;

    private SequencerInterface.Module currentModule = SequencerInterface.Module.SEQUENCE;

    public LaunchpadController() {
    }

    public void setSequencer(SequencerInterface sequencer) {
        this.sequencer = sequencer;
    }

    @Override
    public void onPadPressed(Pad pad, long timestamp) {

//        System.out.printf("onPadPressed: %s, %s\n", pad, timestamp);

        try {

            if (currentModule == SequencerInterface.Module.SEQUENCE) {

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

                } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.TRACK_MUTE))) {
                    sequencer.selectMode(SequencerInterface.Mode.TRACK_MUTE);

                } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.TRACK_EDIT))) {
                    sequencer.selectMode(SequencerInterface.Mode.TRACK_EDIT);

                } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.STEP_MUTE))) {
                    sequencer.selectMode(SequencerInterface.Mode.STEP_MUTE);

                } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.STEP_VELOCITY))) {
                    sequencer.selectMode(SequencerInterface.Mode.STEP_VELOCITY);

                } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.STEP_JUMP))) {
                    sequencer.selectMode(SequencerInterface.Mode.STEP_JUMP);

                } else if (pad.equals(modePadMap.get(SequencerInterface.Mode.STEP_PLAY))) {
                    sequencer.selectMode(SequencerInterface.Mode.STEP_PLAY);

                }

            } else if (currentModule == SequencerInterface.Module.SETTINGS) {

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
                    if (pad.equals(switchPadMap.get(SequencerInterface.Switch.INTERNAL_CLOCK_ENABLED))) {
                        sequencer.selectSwitch(SequencerInterface.Switch.INTERNAL_CLOCK_ENABLED);
                    } else if (pad.equals(switchPadMap.get(SequencerInterface.Switch.MIDI_CLOCK_ENABLED))) {
                        sequencer.selectSwitch(SequencerInterface.Switch.MIDI_CLOCK_ENABLED);
                    } else if (pad.equals(switchPadMap.get(SequencerInterface.Switch.TRIGGER_ENABLED))) {
                        sequencer.selectSwitch(SequencerInterface.Switch.TRIGGER_ENABLED);
                    }

                }

            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }

    }


    @Override
    public void onPadReleased(Pad pad, long timestamp) {

//        System.out.printf("onPadReleased: %s, %s\n", pad, timestamp);

        try {

            if (currentModule == SequencerInterface.Module.SEQUENCE) {
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

            } else if (currentModule == SequencerInterface.Module.SETTINGS) {

            }

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    @Override
    public void onButtonPressed(Button button, long timestamp) {

        // buttons that are available in all modules
        if (button.equals(modeButtonMap.get(SequencerInterface.Mode.PLAY))) {
            sequencer.selectMode(SequencerInterface.Mode.PLAY);

        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.EXIT))) {
            if (LaunchpadUtil.debugMode) {
                sequencer.selectMode(SequencerInterface.Mode.EXIT);
            }

        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.SEQUENCE))) {
            currentModule = SequencerInterface.Module.SEQUENCE;
            sequencer.selectModule(SequencerInterface.Module.SEQUENCE);

        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.SETTINGS))) {
            currentModule = SequencerInterface.Module.SETTINGS;
            sequencer.selectModule(SequencerInterface.Module.SETTINGS);

        } else if (button.isRightButton()) {
            // pressing one of the value buttons
            int index = 7 - button.getCoordinate();
            sequencer.selectValue(index);

        } else if (currentModule == SequencerInterface.Module.SEQUENCE) {
            // buttons only in sequence module

            if (button.equals(modeButtonMap.get(SequencerInterface.Mode.SAVE))) {
                sequencer.selectMode(SequencerInterface.Mode.SAVE);

            } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.TEMPO))) {
                sequencer.selectMode(SequencerInterface.Mode.TEMPO);

            } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.PATTERN_EDIT))) {
                sequencer.selectMode(SequencerInterface.Mode.PATTERN_EDIT);

            }

        }

    }

    @Override
    public void onButtonReleased(Button button, long timestamp) {

        if (button.equals(modeButtonMap.get(SequencerInterface.Mode.PATTERN_EDIT))) {
            sequencer.selectMode(SequencerInterface.Mode.PATTERN_PLAY);

        } else if (button.equals(modeButtonMap.get(SequencerInterface.Mode.TEMPO))) {
            sequencer.selectMode(SequencerInterface.Mode.NO_VALUE);

        }

    }


}
