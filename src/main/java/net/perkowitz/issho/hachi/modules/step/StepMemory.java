package net.perkowitz.issho.hachi.modules.step;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 10/24/16.
 */
public class StepMemory {

    private static int SESSION_COUNT = 16;

    @Getter private StepSession[] sessions = new StepSession[SESSION_COUNT];

    @Getter @Setter private int currentSessionIndex = 0;
    @Getter @Setter private int nextSessionIndex = 0;
    @Getter private int currentPatternIndex = 0;

    @Getter @Setter private int midiChannel = 0;



    public StepMemory() {
        for (int i = 0; i < SESSION_COUNT; i++) {
            sessions[i] = new StepSession(i);
        }
    }


    /***** getters for step/pattern/session by index or current **************************/

    public StepSession currentSession() {
        return sessions[currentSessionIndex];
    }

    public StepPattern currentPattern() {
        return currentSession().getPattern(currentPatternIndex);
    }

    public Scale currentScale() {
        return currentSession().getScale();
    }

    public void setCurrentPatternIndex(int currentPatternIndex) {
        this.currentPatternIndex = currentPatternIndex % StepSession.PATTERN_COUNT;
    }

    /***** select *******************************************************/


}
