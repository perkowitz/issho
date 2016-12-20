package net.perkowitz.issho.devices;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by optic on 10/27/16.
 */
public class GridControlSet {

    @Getter private List<GridControl> controls;
    private Map<GridPad, GridControl> padMap = Maps.newHashMap();
    private Map<GridButton, GridControl> buttonMap = Maps.newHashMap();
    @Getter @Setter private Enum editStateEnum;
    @Getter @Setter private Enum controlStateEnum;


    /***** constructors ****************************************/

    public GridControlSet(Collection<GridControl> controls) {
        this.controls = new ArrayList<GridControl>(controls);
        computeControlMaps();
    }

    public GridControlSet(GridControlSet controlSet) {
        this.controls = controlSet.getControls();
        computeControlMaps();
    }


    /***** public methods ****************************************/

    public boolean contains(GridPad pad) {
        return padMap.keySet().contains(pad);
    }

    public boolean contains(GridButton button) {
        return buttonMap.keySet().contains(button);
    }

    public boolean contains(GridControl control) {
        return controls.contains(control);
    }

    public GridControl get(GridPad pad) {
        return padMap.get(pad);
    }

    public GridControl get(GridButton button) {
        return buttonMap.get(button);
    }

    public GridControl get(GridControl control) {
        if (control.getPad() != null) {
            return padMap.get(control.getPad());
        } else {
            return buttonMap.get(control.getButton());
        }
    }

    public GridControl get(int index) {
        return controls.get(index);
    }

    public Integer getIndex(GridControl control) {
        GridControl c = get(control);
        if (c != null) {
            return c.getIndex();
        } else {
            return null;
        }
    }

    public void draw(GridDisplay display, Color color) {
        for (GridControl control : controls) {
            control.draw(display, color);
        }
    }

    public void select(GridControl control) {

    }

    public int size() {
        return controls.size();
    }


    /***** private methods ****************************************/

    public void computeControlMaps() {
        for (GridControl control : controls) {
            if (control.getPad() != null) {
                padMap.put(control.getPad(), control);
            } else if (control.getButton() != null) {
                buttonMap.put(control.getButton(), control);
            }
        }
    }




    /***** static factories ****************************************/

    public static GridControlSet buttonSide(GridButton.Side side, int startIndex, int endIndex, boolean invertIndex) {
        List<GridControl> controls = Lists.newArrayList();
        for (int index = startIndex; index <= endIndex; index ++) {
            int buttonIndex = index;
            if (invertIndex) {
                buttonIndex = 7 - index;
            }
            controls.add(new GridControl(GridButton.at(side, buttonIndex), index));
        }
        return new GridControlSet(controls);
    }

    public static GridControlSet buttonSide(GridButton.Side side, int startIndex, int endIndex) {
        return buttonSide(side, startIndex, endIndex, false);
    }

    public static GridControlSet buttonSide(GridButton.Side side) {
        return buttonSide(side, 0, 7, false);
    }

    public static GridControlSet buttonSideInverted(GridButton.Side side) {
        return buttonSide(side, 0, 7, true);
    }

    public static GridControlSet pads(int startY, int endY, int startX, int endX) {
        List<GridControl> controls = Lists.newArrayList();
        int index = 0;
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                controls.add(new GridControl(GridPad.at(x, y), index));
                index++;
            }
        }
        return new GridControlSet(controls);
    }

    public static GridControlSet padRows(int startY, int endY) {
        return pads(startY, endY, 0, 7);
    }

    public static GridControlSet padRow(int row) {
        return padRows(row, row);
    }

    public static GridControlSet padColumns(int startX, int endX) { return pads(0, 7, startX, endX); }

    public static GridControlSet padColumn(int column) {
        return padColumns(column, column);
    }


}
