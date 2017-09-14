package net.perkowitz.issho.hachi.modules.beatbox;

/**
 * Created by optic on 2/25/17.
 */
public class BeatPatternFill extends BeatPattern {

    public static Integer[] noFill = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    public static Integer[] stutter1 = { 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 0, 1, 0, 0, 0, 0 };
    public static Integer[] stutter2 = { 0, 1, 2, 3, 0, 1, 0, 1, 0, 0, 0, 0, null, null, null, null, };
    public static Integer[] half = { 0, null, 1, null, 2, null, 3, null, 4, null, 5, null, 6, null, 7, null };
    public static Integer[] cut1 = { 0, 1, 2, 4, 5, 6, 8, 9, 10, 12, 13, 14, 0, 1, 0, 1 };
    public static Integer[] cut2 = { 0, 2, 4, 6, 0, 2, 4, 6, 8, 10, 12, 14, 8, 10, 12, 14, };
    public static Integer[] backward1 = { 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 0, null, 9, 10 };
    public static Integer[] backward2 = { 12, 13, 14, 15, 8, 9, 10, 11, 4, 5, 6, 7, 0, 1, 2, 3 };
    public static Integer[] drop4 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, null, null, null };
    public static Integer[] drop8 = { 0, 1, 2, 3, 4, 5, 6, 7, 0, null, null, null , null, null, null, null };
    public static Integer[] dropBeat = { null, 1, 2, 3, null, 5, 6, 7, null, 9, 10, 11, null, 13, 14, 15 };
    public static Integer[] onlyBeat = { 0, null, null, null, 4, null, null, null, 8, null, null, null, 12, null, null, null };

    public static Integer[][] mapPool = { stutter1, stutter2, half, cut1, cut2, backward1, backward2, drop4, drop8, dropBeat, onlyBeat };

    private static BeatStep emptyStep = new BeatStep(0);

    private BeatPattern basePattern;
    private Integer[] stepMap = noFill;

    public BeatPatternFill(BeatPattern basePattern, Integer[] stepMap) {
        this.basePattern = basePattern;
        this.stepMap = stepMap;
    }


    public BeatTrack getTrack(int index) {
        return basePattern.getTrack(index);
    }

    public BeatStep getStep(int trackIndex, int stepIndex) {
        Integer mappedIndex = stepMap[stepIndex];
        if (mappedIndex == null || mappedIndex < 0 || mappedIndex >= BeatUtil.STEP_COUNT) {
            return emptyStep;
        } else {
            return basePattern.getStep(trackIndex, mappedIndex);
        }
    }

    public BeatControlStep getControlStep(int stepIndex) {
        return basePattern.getControlStep(stepIndex);
    }


    /***** static methods **************************/

    public static BeatPatternFill copy(BeatPatternFill pattern) {
        BeatPatternFill newPattern = new BeatPatternFill(pattern.basePattern, pattern.stepMap);
        return newPattern;
    }

    public static BeatPatternFill chooseRandom(BeatPattern basePattern) {
        int r = (int)(Math.random() * mapPool.length);
        return new BeatPatternFill(basePattern, mapPool[r]);
    }

    public static BeatPatternFill random(BeatPattern basePattern) {
        Integer[] stepMap = new Integer[BeatUtil.STEP_COUNT];
        for (int i = 0; i < BeatUtil.STEP_COUNT; i++) {
            int r = (int)(Math.random() * (BeatUtil.STEP_COUNT + 1));  // add 1 to have an empty step option
            stepMap[i] = r;
        }
        return new BeatPatternFill(basePattern, stepMap);
    }

    public static BeatPatternFill offset(BeatPattern basePattern, int shift) {
        Integer[] stepMap = new Integer[BeatUtil.STEP_COUNT];
        for (int i = 0; i < BeatUtil.STEP_COUNT; i++) {
            int shiftIndex = (i + shift) % BeatUtil.STEP_COUNT;
            stepMap[i] = shiftIndex;
        }
        return new BeatPatternFill(basePattern, stepMap);
    }

}
