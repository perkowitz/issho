package net.perkowitz.issho.hachi.modules.rhythm.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class FillPattern extends Pattern {

    @Getter @Setter private int fillInterval = 4;
    @Getter @Setter private int fillPercent = 100;

    public FillPattern() {}

    public FillPattern(int index, int fillInterval) {

        super(index);
        this.fillInterval = fillInterval;

    }

    @Override
    public String toString() {
        return String.format("FillPattern:%02d", getIndex());
    }

    /***** static methods ***********************/

    public static FillPattern copy(FillPattern pattern, int newIndex) {
        FillPattern newPattern = new FillPattern(newIndex, pattern.getFillInterval());
        newPattern.setFillPercent(pattern.getFillPercent());
        for (int index = 0; index < Pattern.trackCount; index++) {
            newPattern.tracks[index] = Track.copy(pattern.tracks[index], index);
        }
        return newPattern;
    }


}
