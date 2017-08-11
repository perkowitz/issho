package net.perkowitz.issho.devices;

import lombok.Getter;

/**
 * Created by optic on 9/3/16.
 */
public class GridKnob {

    public enum Side {
        Top, Bottom, Left, Right
    }

    @Getter private final Side side;
    @Getter private final int index;

    public GridKnob(Side side, int index) {
        this.side = side;
        this.index = index;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof GridKnob) {
            GridKnob button = (GridKnob) object;
            return side.equals(button.getSide()) && index == button.getIndex();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "GridKnob:" + side + ":" + index;
    }

    /***** static helpers ************************************/

    public static GridKnob at(Side side, int index) {
        return new GridKnob(side, index);
    }


}
