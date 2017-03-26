package net.perkowitz.issho.pixel;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;
import java.util.Set;

/**
 * Created by mikep on 11/24/16.
 */
public class Rainbow implements Pixelator {

//    private static GridColor[] rainbow = { Color.BRIGHT_RED, Color.BRIGHT_YELLOW, Color.BRIGHT_GREEN, Color.BRIGHT_BLUE };
//    private static GridColor[] rainbow = { Color.fromIndex(19), Color.fromIndex(27), Color.fromIndex(35), Color.fromIndex(43) };  // 43, 35, 27, 19
    private static GridColor[] rainbow = { Color.fromIndex(17), Color.fromIndex(43), Color.fromIndex(51), Color.fromIndex(59) };  // 59, 51, 43, 35, 17

    private PixelDevice device;
    private List<GridColor> colors = Lists.newArrayList(rainbow);


    public Rainbow(PixelDevice device) {

        this.device = device;

        for (PixelPad pad : device.getPads()) {
            GridColor color = colors.get((int)(Math.random() * colors.size()));
            pad.setColor(color);
        }


    }


    public void onPadPressed(GridPad pad) {
        PixelPad pixelPad = device.getPad(pad.getX(), pad.getY());
        int colorIndex = colors.indexOf(pixelPad.getColor());
        if (pixelPad != null) {
            Set<PixelPad> neighbors = pixelPad.getNeighbors();
            for (PixelPad neighbor : neighbors) {
                if (colors.indexOf(neighbor.getColor()) == colorIndex + 1) {
                    pixelPad.setColor(neighbor.getColor());
                }
            }
        }
    }

    public void onPadReleased(GridPad pad) {
    }

    public void onButtonPressed(GridButton button) {

//        device.initialize();
//        System.exit(0);
//        int index = button.getIndex();
//
//        if (button.getSide() == GridButton.Side.Left) {
//            for (int x = 1; x < 8; x++) {
//
//            }
//        }

    }

    public void onButtonReleased(GridButton button) {

    }


//    public void clockTick() {}

}
