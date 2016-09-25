package net.perkowitz.sequence.devices.launchpadpro;

import lombok.Getter;
import net.perkowitz.sequence.devices.GridPad;

/**
 * Created by optic on 9/3/16.
 */
public class Pad implements GridPad {

    @Getter private final int x;
    @Getter private final int y;
    @Getter private final int note;

    public Pad(int x, int y) {
        this.x = x;
        this.y = y;
        this.note = (7-y) * 10 + x + 11;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Pad) {
            Pad pad = (Pad) object;
            return this.getX() == pad.getX() && this.getY() == pad.getY();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Pad:" + x + ":" + y;
    }

    /***** static methods ********************************/

    public static Pad fromNote(int note) {
        int x = note % 10 - 1;
        int y = 7 - (note / 10 - 1);
        return new Pad(x, y);
    }

    public static Pad at(int x, int y) {
        return new Pad(x, y);
    }

}
