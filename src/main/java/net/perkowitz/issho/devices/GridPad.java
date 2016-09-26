package net.perkowitz.issho.devices;

import lombok.Getter;

/**
 * Created by optic on 9/3/16.
 */
public class GridPad {

    @Getter private final int x;
    @Getter private final int y;

    public GridPad(int x, int y) {
        this.x = x;
        this.y = y;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof GridPad) {
            GridPad pad = (GridPad) object;
            return x == pad.getX() && y == pad.getY();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "GridPad:" + x + ":" + y;
    }


    /***** static helpers ************************************/

    public static GridPad at(int x, int y) {
        return new GridPad(x, y);
    }

}
