package net.perkowitz.issho.hachi.modules.mono;

/**
 * Created by optic on 10/24/16.
 */
public class MonoSession {

    private static int PATTERN_COUNT = 16;

    private MonoPattern[] patterns = new MonoPattern[PATTERN_COUNT];


    public MonoSession() {
        for (int i = 0; i < PATTERN_COUNT; i++) {
            patterns[i] = new MonoPattern();
        }
    }


    public MonoPattern getPattern(int index) {
        return patterns[index];
    }

}
