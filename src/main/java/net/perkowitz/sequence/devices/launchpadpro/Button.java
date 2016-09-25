package net.perkowitz.sequence.devices.launchpadpro;

import lombok.Getter;
import net.perkowitz.sequence.devices.GridButton;

import static net.perkowitz.sequence.devices.GridButton.Side.*;

/**
 * Created by optic on 9/3/16.
 */
public class Button implements GridButton {

    @Getter private final GridButton.Side side;
    @Getter private final int index;
    @Getter private final int cc;

    public Button(Side side, int index) {
        this.side = side;
        this.index = index;

        int flippedIndex = 7 - index;
        switch (side) {
            case Top:
                this.cc = 90 + index + 1;
                break;
            case Bottom:
                this.cc = index + 1;
                break;
            case Left:
                this.cc = 10 + flippedIndex * 10;
                break;
            case Right:
                this.cc = 19 + flippedIndex * 10;
                break;
            default:
                this.cc = 100;
        }
    }


    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Button) {
            Button button = (Button) object;
            return this.getSide() == button.getSide() && this.getIndex() == button.getIndex();
        }
        return false;
    }

    @Override
    public String toString() {
        return "Button:" + side + ":" + index;
    }

    /***** static methods ********************************/

    public static Button fromCC(int cc) {

        Side side = Top;
        int index = 0;

        if (cc >= 10 && cc <= 89) {
            index = 7 - (cc / 10 - 1);
            side = (cc % 10 == 0) ? Left : Right;
        } else {
            index = cc % 10 - 1;
            side = (cc < 10) ? Bottom : Top;
        }

        return new Button(side, index);
    }

    public static Button at(Side side, int index) {
        return new Button(side, index);
    }



}
