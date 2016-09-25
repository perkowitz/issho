package net.perkowitz.issho.devices;

import lombok.Getter;

/**
 * Created by optic on 9/3/16.
 */
public class GridButton {

    public enum Side {
        Top, Bottom, Left, Right
    }

    @Getter private final Side side;
    @Getter private final int index;

    public GridButton(Side side, int index) {
        this.side = side;
        this.index = index;
    }

    public static GridButton at(Side side, int index) {
        return new GridButton(side, index);
    }


}
