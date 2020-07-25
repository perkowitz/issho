package net.perkowitz.issho.controller;

import lombok.Getter;

public class Pad implements Control {

    @Getter private int row;
    @Getter private int column;

    public Pad(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Type getType() {
        return Type.PAD;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof Pad) {
            Pad p = (Pad) object;
            return row == p.getRow() && column == p.getColumn();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "Pad:" + row + ":" + column;
    }


    /***** static methods *****/

    public Pad fromControl(Control control) {
        if (control.getType() != Type.PAD) {
            return null;
        }
        return (Pad)control;
    }



}
