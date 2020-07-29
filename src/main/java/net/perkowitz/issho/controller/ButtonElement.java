// ButtonElement is an element that you can push and release.
package net.perkowitz.issho.controller;

import lombok.Getter;

public class ButtonElement implements Element {

    @Getter private int group;
    @Getter private int index;

    public ButtonElement(int group, int index) {
        this.group = group;
        this.index = index;
    }


    public Type getType() {
        return Type.BUTTON;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof ButtonElement) {
            ButtonElement b = (ButtonElement) object;
            return group == b.getGroup() && index == b.getIndex();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return String.format("Button:%03d:%03d", group, index);
    }


    /***** static methods *****/

    public static ButtonElement at(int group, int index) {
        return new ButtonElement(group, index);
    }

    public ButtonElement fromElement(Element element) {
        if (element.getType() != Type.BUTTON) {
            return null;
        }
        return (ButtonElement) element;
    }

}
