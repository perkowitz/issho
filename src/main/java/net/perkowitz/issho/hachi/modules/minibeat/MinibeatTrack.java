package net.perkowitz.issho.hachi.modules.minibeat;

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
    @Getter @Setter private boolean enabled = true;
    @Getter @Setter private boolean playing = false;
    @Getter private List<MinibeatStep> steps = Lists.newArrayList();


    public MinibeatTrack() {}

    public MinibeatTrack(int index, int noteNumber) {
        this.index = index;
        this.noteNumber = noteNumber;
        for (int i = 0; i < MinibeatUtil.STEP_COUNT; i++) {
            steps.add(new MinibeatStep(i));
            steps.get(i).setVelocity((i + 1) * 7);
        }
    }

    public MinibeatStep getStep(int index) {
        return steps.get(index);
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }

}
