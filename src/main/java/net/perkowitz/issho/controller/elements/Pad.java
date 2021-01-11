// PadElement is like a button that appears in a 2D grid.
package net.perkowitz.issho.controller.elements;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

public class Pad implements Element {

    private static Map<String, Pad> instanceCache = Maps.newHashMap();

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
        return toString(group, row, column);
    }


    /***** static methods *****/

    private static String toString(int group, int row, int column) {
        return String.format("Pad:%03d:%03d/%03d", group, row, column);
    }

    public static Pad at(int group, int row, int column) {
        String s = toString(group, row, column);
        Pad p = instanceCache.get(s);
        if (p == null) {
            p = new Pad(group, row, column);
            instanceCache.put(s, p);
        }
        return p;
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
