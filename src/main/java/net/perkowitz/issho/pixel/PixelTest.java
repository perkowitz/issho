package net.perkowitz.issho.pixel;

import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.launchpadpro.Color;

/**
 * Created by mikep on 11/24/16.
 */
public class PixelTest implements Pixelator {

    private PixelDevice device;


    public PixelTest(PixelDevice device) {
        this.device = device;
    }


    public void onPadPressed(GridPad pad) {
        System.out.printf("Pressed: %s\n", pad);
        PixelPad pixelPad = device.getPad(pad.getX(), pad.getY());
        int index = (int)(Math.random() * 64);
        pixelPad.setColor(Color.fromIndex(index));
    }

    public void onPadReleased(GridPad pad) {
    }

    public void onButtonPressed(GridButton button) {

    }

    public void onButtonReleased(GridButton button) {

    }

//    public void clockTick() {}

}
