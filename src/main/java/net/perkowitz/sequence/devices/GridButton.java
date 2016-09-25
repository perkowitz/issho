package net.perkowitz.sequence.devices;

/**
 * Created by optic on 9/3/16.
 */
public interface GridButton {

    public enum Side {
        Top, Bottom, Left, Right
    }

    public Side getSide();
    public int getIndex();
//    public static GridButton at(Side side, int index);

}
