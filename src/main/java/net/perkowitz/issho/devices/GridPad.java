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

    public static GridPad at(int x, int y) {
        return new GridPad(x, y);
    }
}
