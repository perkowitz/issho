// PadElement is like a button that appears in a 2D grid.
package net.perkowitz.issho.controller.elements;

import lombok.Getter;

public class Pad implements Element {

    @Getter private int row;
    @Getter private int column;

    public Pad(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Type getType() {
        return Element.Type.PAD;
    }

    public int getGroup() { return row; }
    public int getIndex() { return column; }

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
        return String.format("Pad:%03d:%03d", row, column);
    }


    /***** static methods *****/

    public static Pad at(int row, int column) {
        return new Pad(row, column);
    }

    public Pad fromElement(Element element) {
        if (element.getType() != Type.PAD) {
            return null;
        }
        return (Pad) element;
    }



}
