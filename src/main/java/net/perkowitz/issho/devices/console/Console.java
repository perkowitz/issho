package net.perkowitz.issho.devices.console;

import net.perkowitz.issho.devices.*;

/**
 * Created by mperkowi on 9/20/16.
 */
public class Console implements GridListener, GridDisplay {


    /***** GridDisplay implementation ************************************/

    public void onPadPressed(GridPad pad, int velocity) {
        System.out.printf("[][][][] PadPressed: %s, %d\n", pad, velocity);
    }

    public void onPadReleased(GridPad pad) {
        System.out.printf("[][][][] PadReleased: %s\n", pad);

    }

    public void onButtonPressed(GridButton button, int velocity) {
        System.out.printf("[][][][] ButtonPressed: %s, %d\n", button, velocity);
    }

    public void onButtonReleased(GridButton button) {
        System.out.printf("[][][][] ButtonReleased: %s\n", button);
    }


    /***** GridDisplay implementation ************************************/

    public void initialize() {}

    public void setPad(GridPad pad, GridColor color) {
        System.out.printf("[][][][] SetPad: %s, %s\n", pad, color);
    }

    public void setButton(GridButton button, GridColor color) {
        System.out.printf("[][][][] SetButton: %s, %s\n", button, color);
    }




}
