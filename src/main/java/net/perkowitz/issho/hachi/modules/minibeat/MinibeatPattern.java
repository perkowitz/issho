package net.perkowitz.issho.hachi.modules.minibeat;

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

}
