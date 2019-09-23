package net.perkowitz.issho.hachi.modules.deprecated.minibeat;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class MinibeatTrack {

    @Getter private int index;
    @Getter private int noteNumber;
    @Getter @Setter private boolean playing = false;
    @Getter private List<MinibeatStep> steps = Lists.newArrayList();


    public MinibeatTrack() {}

    public MinibeatTrack(int index, int noteNumber) {
        this.index = index;
        this.noteNumber = noteNumber;
        for (int i = 0; i < MinibeatUtil.STEP_COUNT; i++) {
            steps.add(new MinibeatStep(i));
        }
    }

    public MinibeatStep getStep(int index) {
        return steps.get(index);
    }


    /***** static methods **************************/

    public static MinibeatTrack copy(MinibeatTrack track, int newIndex) {
        MinibeatTrack newTrack = new MinibeatTrack(newIndex, track.getNoteNumber());
        for (int i = 0; i < MinibeatUtil.STEP_COUNT; i++) {
            newTrack.steps.set(i, MinibeatStep.copy(track.steps.get(i), i));
        }
        return newTrack;
    }

}
