package net.perkowitz.issho.hachi.modules.beatbox;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class BeatPattern implements MemoryObject {

    private static int[] notes = new int[] { 49, 37, 39, 51, 42, 44, 46, 50,
            36, 38, 40, 41, 43, 45, 47, 48 };

    @Getter @Setter private int index;
    @Getter private List<BeatTrack> tracks = Lists.newArrayList();
    @Getter private BeatControlTrack controlTrack = new BeatControlTrack(0);


    public BeatPattern() {}

    public BeatPattern(int index) {
        this.index = index;
        for (int i = 0; i < BeatUtil.TRACK_COUNT; i++) {
            tracks.add(new BeatTrack(i, notes[i]));
        }
    }


    public BeatTrack getTrack(int index) {
        return tracks.get(index);
    }

    public BeatStep getStep(int trackIndex, int stepIndex) {
        return getTrack(trackIndex).getStep(stepIndex);
    }

    public BeatControlStep getControlStep(int stepIndex) {
        return getControlTrack().getStep(stepIndex);
    }

    public String toString() {
        return String.format("BeatPattern:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (BeatTrack track : tracks) {
            objects.add(track);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof BeatTrack) {
            BeatTrack track = (BeatTrack) memoryObject;
            track.setIndex(index);
            tracks.set(index, track);
        } else {
            System.out.printf("Cannot put object %s of type %s in object %s\n", memoryObject, memoryObject.getClass().getSimpleName(), this);
        }
    }


    public boolean nonEmpty() {
        for (MemoryObject object : list()) {
            if (object.nonEmpty()) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return BeatPattern.copy(this, this.index);
    }

    public String render() {
        return MemoryUtil.countRender(this);
    }


    /***** static methods **************************/

    public static BeatPattern copy(BeatPattern pattern, int newIndex) {
        BeatPattern newPattern = new BeatPattern(newIndex);
        try {
            for (int i = 0; i < BeatUtil.TRACK_COUNT; i++) {
                newPattern.tracks.set(i, BeatTrack.copy(pattern.tracks.get(i), i));
            }
            newPattern.controlTrack = BeatControlTrack.copy(pattern.controlTrack, pattern.controlTrack.getIndex());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPattern;
    }


}
