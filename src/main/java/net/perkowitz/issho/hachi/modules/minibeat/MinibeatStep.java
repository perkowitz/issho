package net.perkowitz.issho.hachi.modules.minibeat;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class MinibeatStep {

    private int DEFAULT_VELOCITY = 100;

    private int index;
    private int velocity;
    @Getter @Setter private boolean enabled = false;


    public MinibeatStep(int index) {
        this.index = index;
        this.velocity = DEFAULT_VELOCITY;
    }

}
