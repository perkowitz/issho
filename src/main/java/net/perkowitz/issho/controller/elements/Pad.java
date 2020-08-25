// PadElement is like a button that appears in a 2D grid.
package net.perkowitz.issho.controller.elements;

import lombok.Getter;

public class Pad implements Element {

    @Getter private int group;
    @Getter private int row;
    @Getter private int column;

    public Pad(int group, int row, int column) {
        this.group = group;
        this.row = row;
        this.column = column;
    }

    public Type getType() {
        return Element.Type.PAD;
    }

    public int getIndex() { return -1; }

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
        return String.format("Pad:%03d:%03d/%03d", group, row, column);
    }


    /***** static methods *****/

    public static Pad at(int group, int row, int column) {
        return new Pad(group, row, column);
    }

    public static Pad to(int group, Pad pad) {
        return new Pad(group, pad.getRow(), pad.getColumn());
    }
    
    public Pad fromElement(Element element) {
        if (element.getType() != Type.PAD) {
            return null;
        }
        return (Pad) element;
    }



}
