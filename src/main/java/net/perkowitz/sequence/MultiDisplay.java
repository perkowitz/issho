package net.perkowitz.sequence;

import net.perkowitz.sequence.models.*;

import java.util.Map;

/**
 * Created by optic on 7/10/16.
 */
public class MultiDisplay implements SequencerDisplay {

    private SequencerDisplay[] displays;
    
    public MultiDisplay(SequencerDisplay[] displays) {
        this.displays = displays;
    }

    
    public void initialize() {
        for (SequencerDisplay display : displays) {
            display.initialize();
        }
    }
    
    public void displayAll(Memory memory, Map<SequencerInterface.Mode, Boolean> modeIsActiveMap) {
        for (SequencerDisplay display : displays) {
            display.displayAll(memory, modeIsActiveMap);
        }
    }
    
    public void displayHelp() {
        for (SequencerDisplay display : displays) {
            display.displayHelp();
        }
    }

    public void displayModule(SequencerInterface.Module module, Memory memory, Map<SequencerInterface.Mode, Boolean> modeIsActiveMap, int currentFileIndex) {
        for (SequencerDisplay display : displays) {
            display.displayModule(module, memory, modeIsActiveMap, currentFileIndex);
        }
    }

    public void displaySession(Session session) {
        for (SequencerDisplay display : displays) {
            display.displaySession(session);
        }
    }
    
    public void displayFiles(int currentFileIndex) {
        for (SequencerDisplay display : displays) {
            display.displayFiles(currentFileIndex);
        }
    }

    public void displayPattern(Pattern pattern) {
        for (SequencerDisplay display : displays) {
            display.displayPattern(pattern);
        }
    }
    
    public void displayFill(FillPattern fill) {
        for (SequencerDisplay display : displays) {
            display.displayFill(fill);
        }
    }

    public void displayTrack(Track track, boolean displaySteps) {
        for (SequencerDisplay display : displays) {
            display.displayTrack(track, displaySteps);
        }
    }
    
    public void displayTrack(Track track) {
        for (SequencerDisplay display : displays) {
            display.displayTrack(track);
        }
    }

    public void displayStep(Step step) {
        for (SequencerDisplay display : displays) {
            display.displayStep(step);
        }
    }
    
    public void clearSteps() {
        for (SequencerDisplay display : displays) {
            display.clearSteps();
        }
    }
    
    public void displayPlayingStep(int stepNumber) {
        for (SequencerDisplay display : displays) {
            display.displayPlayingStep(stepNumber);
        }
    }

    public void displayMode(SequencerInterface.Mode mode, boolean isActive) {
        for (SequencerDisplay display : displays) {
            display.displayMode(mode, isActive);
        }
    }
    
    public void displayModes(Map<SequencerInterface.Mode, Boolean> modeIsActiveMap) {
        for (SequencerDisplay display : displays) {
            display.displayModes(modeIsActiveMap);
        }
    }
    
    public void displayModeChoice(SequencerInterface.Mode mode, SequencerInterface.Mode[] modeChoices) {
        for (SequencerDisplay display : displays) {
            display.displayModeChoice(mode, modeChoices);
        }
    }

    public void clearValue() {
        for (SequencerDisplay display : displays) {
            display.clearValue();
        }
    }
    
    public void displayValue(int value, int minValue, int maxValue, SequencerInterface.ValueMode valueMode) {
        for (SequencerDisplay display : displays) {
            display.displayValue(value, minValue, maxValue, valueMode);
        }
    }

    public void selectModule(SequencerInterface.Module module) {
        for (SequencerDisplay display : displays) {
            display.selectModule(module);
        }
    }

    public void displaySwitches(Map<SequencerInterface.Switch, Boolean> switches) {
        for (SequencerDisplay display : displays) {
            display.displaySwitches(switches);
        }
    }

}
