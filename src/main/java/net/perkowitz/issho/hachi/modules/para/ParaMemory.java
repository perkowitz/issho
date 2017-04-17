package net.perkowitz.issho.hachi.modules.para;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.para.ParaUtil.StepEditState.NOTE;

/**
 * Created by optic on 10/24/16.
 */
public class ParaMemory implements MemoryObject {

    private static int SESSION_COUNT = 16;

    @Getter private ParaSession[] sessions = new ParaSession[SESSION_COUNT];
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

    @Getter @Setter ParaUtil.StepEditState stepEditState = NOTE;
    @Getter @Setter ParaUtil.ValueState valueState = ParaUtil.ValueState.VELOCITY;


    public ParaMemory() {
        for (int i = 0; i < SESSION_COUNT; i++) {
            sessions[i] = new ParaSession(i);
        }
    }


    /***** getters for step/pattern/session by index or current **************************/

    public ParaSession currentSession() {
        return sessions[currentSessionIndex];
    }

    public ParaPattern currentPattern() {
        return currentSession().getPattern(currentPatternIndex);
    }

    public ParaStep currentStep() {
        return currentPattern().getStep(currentStepIndex);
    }

    public ParaSession getSession(int index) {
        return sessions[index];
    }

    public ParaPattern getPattern(int index) { return currentSession().getPattern(index); }

    public ParaStep getStep(int index) { return currentPattern().getStep(index); }


    public String toString() {
        return "ParaMemory";
    }


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


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (ParaSession session : sessions) {
            objects.add(session);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof ParaSession) {
            ParaSession session = (ParaSession) memoryObject;
            session.setIndex(index);
            sessions[index] = session;
        } else {
            System.out.printf("Cannot put object %s of type %s in object %s\n", memoryObject, memoryObject.getClass().getSimpleName(), this);
        }
    }

    public boolean nonEmpty() {
        for (MemoryObject object : list()) {
            if (object.nonEmpty()) {
                return true;
            }
        }
        return false;
    }

    public int getIndex() { return 0; }
    public void setIndex(int index) {}

    public MemoryObject clone() {
        return null;
    }

    public String render() { return toString(); }


}
