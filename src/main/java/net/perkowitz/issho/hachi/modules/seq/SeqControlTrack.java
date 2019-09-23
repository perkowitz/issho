package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class SeqControlTrack {

    public static Integer[] controllersDefault = new Integer[]{ 16, 17, 18, 19, 20, 21, 22, 23, 81, 82, 83, 84, 85, 86, 87, 88 };

    @Getter @Setter private int index;
    @Getter @Setter private boolean playing = true;
    @Getter private List<SeqControlStep> steps = Lists.newArrayList();

    public SeqControlTrack() {}

    public SeqControlTrack(int index) {
        this.index = index;
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            steps.add(new SeqControlStep(i));
        }
    }

    public SeqControlStep getStep(int index) {
        return steps.get(index);
    }

    public String toString() {
        return String.format("SeqCtrlTrack:%02d", index);
    }

    /***** static methods **************************/

    public static SeqControlTrack copy(SeqControlTrack track, int newIndex) {
        SeqControlTrack newTrack = new SeqControlTrack(newIndex);
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            newTrack.steps.set(i, SeqControlStep.copy(track.steps.get(i), i));
        }
        return newTrack;
    }

}
