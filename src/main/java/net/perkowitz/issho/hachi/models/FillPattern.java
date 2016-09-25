package net.perkowitz.issho.hachi.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class FillPattern extends Pattern {

    @Getter @Setter private int fillInterval = 4;
    @Getter @Setter private int fillPercent = 100;

    public FillPattern() {}

    public FillPattern(int index, int fillInterval) {

        super(index);
        this.fillInterval = fillInterval;

    }

    @Override
    public String toString() {
        return "Fill:" + getIndex();
    }

}
