package net.perkowitz.issho.pixel;

import lombok.Getter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Set;

/**
 * Created by mikep on 11/24/16.
 */
public class PixelButton {

    PixelDevice device;

    @Getter private GridButton.Side side;
    @Getter private int index;
    @Getter private GridColor color;


    public PixelButton(PixelDevice device, GridButton.Side side, int index) {
        this.device = device;
        this.side = side;
        this.index = index;
        this.color = Color.OFF;
    }


    public void setColor(GridColor color) {
        this.color = color;
        device.draw(this);
    }



}
