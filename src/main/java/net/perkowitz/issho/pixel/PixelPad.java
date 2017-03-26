package net.perkowitz.issho.pixel;

import lombok.Getter;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.List;
import java.util.Set;

/**
 * Created by mikep on 11/24/16.
 */
public class PixelPad {

    PixelDevice device;

    @Getter private int x;
    @Getter private int y;
    @Getter private GridColor color;


    public PixelPad(PixelDevice device, int x, int y) {
        this.device = device;
        this.x = x;
        this.y = y;
        this.color = Color.OFF;
    }


    public Set<PixelPad> getNeighbors() {
        return device.getNeighbors(this);
    }

    public PixelPad getNeighbor(PixelDevice.Direction direction) {
        return device.getNeighbor(this, direction);
    }

    public void setColor(GridColor color) {
        this.color = color;
        device.draw(this);
    }



}
