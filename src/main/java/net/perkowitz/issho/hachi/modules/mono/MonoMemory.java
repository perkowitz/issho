package net.perkowitz.issho.hachi.modules.mono;

import lombok.Getter;

/**
 * Created by optic on 10/24/16.
 */
public class MonoMemory {

    private static int SESSION_COUNT = 16;

    @Getter private MonoSession[] sessions = new MonoSession[SESSION_COUNT];
    @Getter private int currentSessionIndex = 0;
    @Getter private int currentPatternIndex = 0;


    public MonoMemory() {
        for (int i = 0; i < SESSION_COUNT; i++) {
            sessions[i] = new MonoSession();
        }
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
