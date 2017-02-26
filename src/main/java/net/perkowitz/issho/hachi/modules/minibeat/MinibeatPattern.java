package net.perkowitz.issho.hachi.modules.minibeat;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class MinibeatPattern {

    private int index;
    private List<MinibeatTrack> tracks = Lists.newArrayList();


    public MinibeatPattern(int index) {
        this.index = index;
        for (int i = 0; i < MinibeatUtil.TRACK_COUNT; i++) {
            tracks.add(new MinibeatTrack(i, 36 + i));
        }
    }


    public MinibeatTrack getTrack(int index) {
        return tracks.get(index);
    }

}
