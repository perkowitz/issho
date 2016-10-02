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


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof GridButton) {
            GridButton button = (GridButton) object;
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
        return "GridButton:" + side + ":" + index;
    }

    /***** static helpers ************************************/

    public static GridButton at(Side side, int index) {
        return new GridButton(side, index);
    }


}
