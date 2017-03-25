package net.perkowitz.issho.hachi.modules.beatbox;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class BeatTrack {

    @Getter private int index;
    @Getter private int noteNumber;
    @Getter @Setter private boolean playing = false;
    @Getter private List<BeatStep> steps = Lists.newArrayList();


    public BeatTrack() {}

    public BeatTrack(int index, int noteNumber) {
        this.index = index;
        this.noteNumber = noteNumber;
        for (int i = 0; i < BeatUtil.STEP_COUNT; i++) {
            steps.add(new BeatStep(i));
        }
    }

    public BeatStep getStep(int index) {
        return steps.get(index);
    }


    /***** static methods **************************/

    public static BeatTrack copy(BeatTrack track, int newIndex) {
        BeatTrack newTrack = new BeatTrack(newIndex, track.getNoteNumber());
        for (int i = 0; i < BeatUtil.STEP_COUNT; i++) {
            newTrack.steps.set(i, BeatStep.copy(track.steps.get(i), i));
        }
        return newTrack;
    }

}
