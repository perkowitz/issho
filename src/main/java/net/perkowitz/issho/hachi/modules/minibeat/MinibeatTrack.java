package net.perkowitz.issho.hachi.modules.minibeat;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class MinibeatTrack {

    private int index;
    private int noteNumber;
    @Getter @Setter private boolean enabled = true;
    private List<MinibeatStep> steps = Lists.newArrayList();


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

}
