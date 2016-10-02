package net.perkowitz.issho.hachi.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Step {

    @Getter private int index;
    @Getter @Setter private boolean selected = false;

    @Getter @Setter private boolean on = false;
    @Getter @Setter private int velocity = 100;

    // only used for deserializing JSON; Step should always be created with an index
    public Step() {}

    public Step(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Step:" + getIndex();
    }

}
