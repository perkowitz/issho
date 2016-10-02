package net.perkowitz.issho.hachi;

import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;

/**
 * Created by optic on 9/4/16.
 */
public class Graphics {

    public static GridPad[] hachi = new GridPad[] {
            new GridPad(3, 0),
            new GridPad(4, 0),
            new GridPad(3, 2),
            new GridPad(4, 2),
            new GridPad(3, 3),
            new GridPad(4, 3),
            new GridPad(3, 4),
            new GridPad(4, 4),
            new GridPad(3, 6),
            new GridPad(4, 6),
            new GridPad(3, 7),
            new GridPad(4, 7),
    };

    public static GridPad[] issho = new GridPad[] {
            new GridPad(3, 1),
            new GridPad(4, 1),
            new GridPad(5, 1),
            new GridPad(2, 3),
            new GridPad(3, 3),
            new GridPad(4, 3),
            new GridPad(3, 5),
            new GridPad(4, 5),
            new GridPad(5, 5),
            new GridPad(2, 7),
            new GridPad(3, 7),
            new GridPad(4, 7)
        };


    public static GridPad[] nora1 = new GridPad[] {
            new GridPad(3, 0),
            new GridPad(4, 0),
            new GridPad(3, 1),
            new GridPad(3, 3),
            new GridPad(4, 3),
            new GridPad(3, 4),
            new GridPad(4, 4),
            new GridPad(4, 6),
            new GridPad(3, 7),
            new GridPad(4, 7),
    };

    public static GridPad[] nora2 = new GridPad[] {
            new GridPad(3, 1),
            new GridPad(4, 1),
            new GridPad(6, 1),
            new GridPad(6, 2),
            new GridPad(7, 1),
            new GridPad(3, 3),
            new GridPad(4, 3),
            new GridPad(3, 5),
            new GridPad(4, 5),
            new GridPad(3, 6),
            new GridPad(4, 6),
            new GridPad(0, 6),
            new GridPad(1, 6),
            new GridPad(1, 5),
    };

    public static GridPad[][] sprites = new GridPad[][] { hachi, issho, nora1, nora2 };

    public static void setPads(GridDisplay display, GridPad[] pads, GridColor color) {
        for (GridPad pad : pads) {
            display.setPad(pad, color);
        }
    }


}
