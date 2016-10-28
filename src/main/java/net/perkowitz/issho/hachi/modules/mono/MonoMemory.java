package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.StepEditMode.NOTE;

/**
 * Created by optic on 10/24/16.
 */
public class MonoMemory {

    private static int SESSION_COUNT = 16;

    @Getter private MonoSession[] sessions = new MonoSession[SESSION_COUNT];
    @Getter private int currentSessionIndex = 0;
    @Getter private int currentPatternIndex = 0;
    @Getter @Setter private MonoUtil.StepEditMode stepEditMode = NOTE;


    public MonoMemory() {
        for (int i = 0; i < SESSION_COUNT; i++) {
            sessions[i] = new MonoSession();
        }

        stepEditMode = NOTE;
    }


    public MonoSession getSession(int index) {
        return sessions[index];
    }

    public MonoSession currentSession() {
        return sessions[currentSessionIndex];
    }

    public MonoPattern currentPattern() {
        return currentSession().getPattern(currentPatternIndex);
    }


}
