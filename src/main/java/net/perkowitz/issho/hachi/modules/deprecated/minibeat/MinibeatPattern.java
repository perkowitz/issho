package net.perkowitz.issho.hachi.modules.deprecated.minibeat;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class MinibeatPattern {

    private int[] notes = new int[] { 0, 2, 3, 6, 10, 5, 7, 12 };

    @Getter private int index;
    @Getter private List<MinibeatTrack> tracks = Lists.newArrayList();


    public MinibeatPattern() {}

    public MinibeatPattern(int index) {
        this.index = index;
        for (int i = 0; i < MinibeatUtil.TRACK_COUNT; i++) {
            tracks.add(new MinibeatTrack(i, 36 + notes[i]));
        }
    }


    public MinibeatTrack getTrack(int index) {
        return tracks.get(index);
    }


    /***** static methods **************************/

    public static MinibeatPattern copy(MinibeatPattern pattern, int newIndex) {
        MinibeatPattern newPattern = new MinibeatPattern(newIndex);
        try {
            for (int i = 0; i < MinibeatUtil.TRACK_COUNT; i++) {
                newPattern.tracks.set(i, MinibeatTrack.copy(pattern.tracks.get(i), i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPattern;
    }


}
