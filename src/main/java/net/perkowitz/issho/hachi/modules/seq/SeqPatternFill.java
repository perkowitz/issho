package net.perkowitz.issho.hachi.modules.seq;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class SeqPatternFill extends SeqPattern {

    public static Integer[] noFill = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    public static Integer[] stutter1 = { 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 0, 1, 0, 0, 0, 0 };
    public static Integer[] stutter2 = { 0, 1, 2, 3, 0, 1, 0, 1, 0, 0, 0, 0, null, null, null, null, };
    public static Integer[] stutter3 = { 4, 5, 6, 7, 4, 5, 6, 7, 4, 5, 4, 7, 12, 13, 4, 12 };
    public static Integer[] stutter4 = { 2, 3, 4, 5, 10, 11, 12, 13, 2, 3, 4, 1, 10, null, 12, null};
    public static Integer[] half = { 0, null, 1, null, 2, null, 3, null, 4, null, 5, null, 6, null, 7, null };
    public static Integer[] cut1 = { 0, 1, 2, 4, 5, 6, 8, 9, 10, 12, 13, 14, 0, 1, 0, 1 };
    public static Integer[] cut2 = { 0, 2, 4, 6, 0, 2, 4, 6, 8, 10, 12, 14, 8, 10, 12, 14, };
    public static Integer[] backward1 = { 0, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
    public static Integer[] backward2 = { 12, 13, 14, 15, 8, 9, 10, 11, 4, 5, 6, 7, 0, 1, 2, 3 };
    public static Integer[] drop4 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 0, null, null, null };
    public static Integer[] drop8 = { 0, 1, 2, 3, 4, 5, 6, 7, 0, null, null, null , null, null, null, null };
    public static Integer[] dropSeq = { null, 1, 2, 3, null, 5, 6, 7, null, 9, 10, 11, null, 13, 14, 15 };
    public static Integer[] onlySeq = { 0, null, null, null, 4, null, null, null, 8, null, null, null, 12, null, null, null };
    public static Integer[] eightSeq = { 0, null, 2, null, 4, null, 6, null, 8, null, 10, null, 12, null, 14, null };

    public static Integer[][] mapPool = { stutter1, stutter2, stutter3, stutter4, half, cut1, cut2, backward1, backward2, drop4, drop8, dropSeq, onlySeq, eightSeq };

    private static SeqStep emptyStep = new SeqStep(-1);

    private SeqPattern basePattern;
    private Integer[] stepMap = noFill;

    public SeqPatternFill(SeqPattern basePattern, Integer[] stepMap) {
        this.basePattern = basePattern;
        this.stepMap = stepMap;
    }


    public SeqTrack getTrack(int index) {
        return basePattern.getTrack(index);
    }
    public List<SeqTrack> getTracks() { return basePattern.getTracks(); }

    public SeqStep getStep(int trackIndex, int stepIndex) {
        if (stepIndex < 0 || stepIndex >= stepMap.length) {
            return emptyStep;
        }
        Integer mappedIndex = stepMap[stepIndex];
        if (mappedIndex == null || mappedIndex < 0 || mappedIndex >= SeqUtil.STEP_COUNT) {
            return emptyStep;
        } else {
            return basePattern.getStep(trackIndex, mappedIndex);
        }
    }

    // TODO need passthru for control steps as well

    public SeqPitchStep getPitchStep(int stepIndex) {
        return basePattern.getPitchStep(stepIndex);
    }

    public String toString() {
        return "SeqPatternFill:_";
    }

    /***** static methods **************************/

    public static SeqPatternFill copy(SeqPatternFill pattern) {
        SeqPatternFill newPattern = new SeqPatternFill(pattern.basePattern, pattern.stepMap);
        return newPattern;
    }

    public static SeqPatternFill chooseRandom(SeqPattern basePattern) {
        int r = (int)(Math.random() * mapPool.length);
        return new SeqPatternFill(basePattern, mapPool[r]);
    }

    public static SeqPatternFill random(SeqPattern basePattern) {
        Integer[] stepMap = new Integer[SeqUtil.STEP_COUNT];
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            int r = (int)(Math.random() * (SeqUtil.STEP_COUNT + 1));  // add 1 to have an empty step option
            stepMap[i] = r;
        }
        return new SeqPatternFill(basePattern, stepMap);
    }

    public static SeqPatternFill offset(SeqPattern basePattern, int shift) {
        Integer[] stepMap = new Integer[SeqUtil.STEP_COUNT];
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            int shiftIndex = (i + shift) % SeqUtil.STEP_COUNT;
            stepMap[i] = shiftIndex;
        }
        return new SeqPatternFill(basePattern, stepMap);
    }

}
