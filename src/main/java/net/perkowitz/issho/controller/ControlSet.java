package net.perkowitz.issho.controller;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ControlSet {

    @Getter private List<Control> controls;


    /***** constructors ****************************************/

    public ControlSet(Collection<Control> controls) {
        this.controls = new ArrayList<Control>(controls);
    }

    public ControlSet(ControlSet controlSet) {
        this.controls = controlSet.getControls();
    }


    /***** public methods *************************************/

    public boolean contains(Control control) {
        return controls.contains(control);
    }

    public int getIndex(Control control) {
        return controls.indexOf(control);
    }


}
