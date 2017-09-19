package net.perkowitz.issho.hachi.modules.beatbox;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.util.MidiUtil;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class BeatControlTrack {

    @Getter private int index;
    @Getter @Setter private boolean playing = false;
    @Getter private List<BeatControlStep> steps = Lists.newArrayList();
    @Getter private BeatControlStep clearStep = new BeatControlStep(0);


    public BeatControlTrack() {}

    public BeatControlTrack(int index) {
        this.index = index;
        for (int i = 0; i < BeatUtil.STEP_COUNT; i++) {
            steps.add(new BeatControlStep(i));
        }
    }

    public BeatControlStep getStep(int index) {
        return steps.get(index);
    }


    /***** static methods **************************/

    public static BeatControlTrack copy(BeatControlTrack track, int newIndex) {
        BeatControlTrack newTrack = new BeatControlTrack(newIndex);
        for (int i = 0; i < BeatUtil.STEP_COUNT; i++) {
            newTrack.steps.set(i, BeatControlStep.copy(track.steps.get(i), i));
        }
        return newTrack;
    }

}
