// KnobElement is an element that can be used to set a value.
package net.perkowitz.issho.controller.elements;

import lombok.Getter;

public class Knob implements Element {

    @Getter private int group;
    @Getter private int index;


    public Knob(int group, int index) {
        this.group = group;
        this.index = index;
    }


    public Type getType() {
        return Type.KNOB;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof Knob) {
            Knob k = (Knob) object;
            return group == k.getGroup() && index == k.getIndex();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Knob:%03d:%03d", group, index);
    }


    /***** static methods *****/

    public static Knob at(int group, int index) {
        return new Knob(group, index);
    }

    public Knob fromElement(Element element) {
        if (element.getType() != Type.KNOB) {
            return null;
        }
        return (Knob) element;
    }


}
