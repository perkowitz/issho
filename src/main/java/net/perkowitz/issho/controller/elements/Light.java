// LightElement is an element that just lights up in a color; there is no input from a Light.
package net.perkowitz.issho.controller.elements;

import lombok.Getter;

public class Light implements Element {

    @Getter private int group;
    @Getter private int index;

    public Light(int group, int index) {
        this.group = group;
        this.index = index;
    }


    public Type getType() {
        return Type.LIGHT;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof Light) {
            Light l = (Light) object;
            return group == l.getGroup() && index == l.getIndex();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Light:%03d:%03d", group, index);
    }


    /***** static methods *****/

    public static Light at(int group, int index) {
        return new Light(group, index);
    }

    public Light fromElement(Element element) {
        if (element.getType() != Type.LIGHT) {
            return null;
        }
        return (Light) element;
    }


}
