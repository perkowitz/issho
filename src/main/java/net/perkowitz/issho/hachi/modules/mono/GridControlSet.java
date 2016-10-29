package net.perkowitz.issho.hachi.modules.mono;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by optic on 10/27/16.
 */
public class GridControlSet {

    private List<GridControl> controls;
    private Map<GridPad, GridControl> padMap = Maps.newHashMap();
    private Map<GridButton, GridControl> buttonMap = Maps.newHashMap();


    /***** constructors ****************************************/

    public GridControlSet(List<GridControl> controls) {
        this.controls = controls;

        for (GridControl control : controls) {
            if (control.getPad() != null) {
                padMap.put(control.getPad(), control);
            } else if (control.getButton() != null) {
                buttonMap.put(control.getButton(), control);
            }
        }

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

    public void draw(GridDisplay display) {
        for (GridControl control : controls) {
            control.draw(display);
        }
    }


    /***** static factories ****************************************/

    public static GridControlSet buttonSide(GridButton.Side side) {
        List<GridControl> controls = Lists.newArrayList();
        for (int index = 0; index < 8; index++) {
            controls.add(new GridControl(GridButton.at(side, index)));
        }
        return new GridControlSet(controls);
    }

    public static GridControlSet padRows(int startRow, int endRow) {
        List<GridControl> controls = Lists.newArrayList();
        for (int row = startRow; row <= endRow; row++) {
            for (int index = 0; index < 8; index++) {
                controls.add(new GridControl(GridPad.at(index, row)));
            }
        }
        return new GridControlSet(controls);
    }

    public static GridControlSet padRow(int row) {
        return padRows(row, row);
    }


}
