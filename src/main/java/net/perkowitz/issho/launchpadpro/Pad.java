package net.perkowitz.issho.launchpadpro;

import lombok.Getter;

/**
 * Created by optic on 9/3/16.
 */
public class Pad {

    @Getter private final int x;
    @Getter private final int y;
    @Getter private final int note;

    public Pad(int x, int y) {
        this.x = x;
        this.y = y;
        this.note = y * 10 + x + 11;
    }

    public static Pad fromNote(int note) {
        int x = note % 10 - 1;
        int y = note / 10 - 1;
        return new Pad(x, y);
    }

}
