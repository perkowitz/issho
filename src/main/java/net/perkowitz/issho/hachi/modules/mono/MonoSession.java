package net.perkowitz.issho.hachi.modules.mono;

import lombok.Getter;

/**
 * Created by optic on 10/24/16.
 */
public class MonoSession {

    private static int PATTERN_COUNT = 16;

    @Getter private int index;
    @Getter private MonoPattern[] patterns = new MonoPattern[PATTERN_COUNT];

    public MonoSession() {}

    public MonoSession(int index) {
        this.index = index;
        for (int i = 0; i < PATTERN_COUNT; i++) {
            patterns[i] = new MonoPattern(i);
        }
    }


    public MonoPattern getPattern(int index) {
        return patterns[index];
    }

}
