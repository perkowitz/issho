package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class SeqControlTrack {

    public static String CONTROL_PREFIX = "CC";

    @Getter @Setter private boolean playing = false;
    @Getter private List<SeqControlStep> steps = Lists.newArrayList();
    @Getter private int controllerNumber;

    public SeqControlTrack() {}

    public SeqControlTrack(int controllerNumber) {
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            steps.add(new SeqControlStep(i));
        }
        this.controllerNumber = controllerNumber;
    }

    public SeqControlStep getStep(int index) {
        return steps.get(index);
    }

    public String controllerString() {
        return controllerString(controllerNumber);
    }


    /***** static methods **************************/

    public static SeqControlTrack copy(SeqControlTrack track) {
        SeqControlTrack newTrack = new SeqControlTrack(track.controllerNumber);
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            newTrack.steps.set(i, SeqControlStep.copy(track.steps.get(i), i));
        }
        return newTrack;
    }

    public static String controllerString(int controllerNumber) {
        return CONTROL_PREFIX + String.format("%03d", controllerNumber);
    }
}
