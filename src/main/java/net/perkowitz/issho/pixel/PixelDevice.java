package net.perkowitz.issho.pixel;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by mikep on 11/24/16.
 */
public class PixelDevice implements GridListener {

    public enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    public static int X_SIZE = 8;
    public static int Y_SIZE = 8;

    @Setter private Pixelator pixelator;
    private GridDisplay display;
    private PixelPad[][] pads = new PixelPad[X_SIZE][Y_SIZE];
    @Setter private boolean wrappingAllowed = false;

    public PixelDevice(GridDisplay display) {
        this.display = display;
        this.pixelator = null;

        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                pads[x][y] = new PixelPad(this, x, y);
            }
        }
    }


    /***** GridListener implementation **********************************/

    public void onPadPressed(GridPad pad, int velocity) {
        pixelator.onPadPressed(pad);
    }

    public void onPadReleased(GridPad pad) {
        pixelator.onPadReleased(pad);
    }

    public void onButtonPressed(GridButton button, int velocity) {
        pixelator.onButtonPressed(button);
    }

    public void onButtonReleased(GridButton button) {
        pixelator.onButtonReleased(button);
    }


    /***** pads **********************************************************/


    public Set<PixelPad> getPads() {
        Set<PixelPad> padSet = Sets.newHashSet();
        for (int x = 0; x < X_SIZE; x++) {
            for (int y = 0; y < Y_SIZE; y++) {
                padSet.add(pads[x][y]);
            }
        }
        return padSet;
    }


    public PixelPad getPad(int x, int y) {

        if (wrappingAllowed) {
            x = (x + X_SIZE) % X_SIZE;
            y = (y + Y_SIZE) % Y_SIZE;
        }

        if (x >= 0 && x < X_SIZE && y >= 0 && y < Y_SIZE) {
            return pads[x][y];
        }
        return null;
    }

    public PixelPad getNeighbor(PixelPad pad, Direction direction) {

        int x = pad.getX();
        int y = pad.getY();

        switch (direction) {
            case NORTH:
                y += 1;
                break;
            case SOUTH:
                y -= 1;
                break;
            case EAST:
                x += 1;
                break;
            case WEST:
                x -= 1;
                break;
        }

        return getPad(x, y);
    }

    public Set<PixelPad> getNeighbors(PixelPad pad) {

        Set<PixelPad> neighbors = Sets.newHashSet();
        for (Direction direction : Direction.values()) {
            PixelPad neighbor = getNeighbor(pad, direction);
            if (neighbor != null) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }


    /***** drawing ******************************************************************/


    public void initialize() {
        display.initialize();
    }

    public void draw() {
        initialize();
        for (PixelPad pad : getPads()) {
            draw(pad);
        }
    }

    public void draw(PixelPad pad) {
        if (pad != null) {
            display.setPad(GridPad.at(pad.getX(), pad.getY()), pad.getColor());
        }
    }

    public void draw(PixelButton button) {
        if (button != null) {
            display.setButton(GridButton.at(button.getSide(), button.getIndex()), button.getColor());
        }
    }

}
