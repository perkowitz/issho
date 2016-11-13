package net.perkowitz.issho.hachi.modules.mono;

import lombok.Getter;
import lombok.Setter;

import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.StepEditState.NOTE;

/**
 * Created by optic on 10/24/16.
 */
public class MonoMemory {

    private static int SESSION_COUNT = 16;

    @Getter private MonoSession[] sessions = new MonoSession[SESSION_COUNT];
    @Getter @Setter private int currentSessionIndex = 0;
    @Getter private int currentPatternIndex = 0;
    @Getter private int currentStepIndex = 0;
    @Getter @Setter private int keyboardOctave;

    @Getter private int playingPatternIndex;// the currently playing pattern (which might not be in the chain, if a new one has been selected)
    @Getter private int patternChainMin;    // the index of the first of the playing pattern chain
    @Getter private int patternChainMax;    // the index of the last of the pattern chain
    @Getter private int patternChainNextIndex;  // the index of the NEXT pattern to play

    @Getter @Setter private Integer nextSessionIndex = null;

    @Getter @Setter private int midiChannel = 0;

    @Getter @Setter MonoUtil.StepEditState stepEditState = NOTE;
    @Getter @Setter MonoUtil.ValueState valueState = MonoUtil.ValueState.VELOCITY;


    public MonoMemory() {
        for (int i = 0; i < SESSION_COUNT; i++) {
            sessions[i] = new MonoSession(i);
        }
    }


    /***** getters for step/pattern/session by index or current **************************/

    public MonoSession currentSession() {
        return sessions[currentSessionIndex];
    }

    public MonoPattern currentPattern() {
        return currentSession().getPattern(currentPatternIndex);
    }

    public MonoStep currentStep() {
        return currentPattern().getStep(currentStepIndex);
    }

    public MonoSession getSession(int index) {
        return sessions[index];
    }

    public MonoPattern getPattern(int index) { return currentSession().getPattern(index); }

    public MonoStep getStep(int index) { return currentPattern().getStep(index); }


    /***** select *******************************************************/

    public void selectStep(int index) {
        currentStep().setSelected(false);
        currentStepIndex = index;
        currentStep().setSelected(true);
    }

    public void selectNextPattern() {
        currentPatternIndex = patternChainNextIndex;
        patternChainNextIndex++;
        if (patternChainNextIndex > patternChainMax) {
            patternChainNextIndex = patternChainMin;
        }
    }

    // TODO make this multiple
    public void selectPatternChain(int minIndex, int maxIndex) {
        patternChainNextIndex = minIndex;
        patternChainMin = minIndex;
        patternChainMax = maxIndex;

    }


}
