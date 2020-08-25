// ButtonElement is an element that you can push and release.
package net.perkowitz.issho.controller.elements;

import lombok.Getter;

public class Button implements Element {

    @Getter private int group;
    @Getter private int index;

    public Button(int group, int index) {
        this.group = group;
        this.index = index;
    }


    public Type getType() {
        return Type.BUTTON;
    }


    /***** overrides ****************************************/

    @Override
    public boolean equals(Object object) {
        if (object instanceof Button) {
            Button b = (Button) object;
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

    public static Button at(int group, int index) {
        return new Button(group, index);
    }

    public static Button to(int group, Button button) {
        return new Button(group, button.getIndex());
    }

    public Button fromElement(Element element) {
        if (element.getType() != Type.BUTTON) {
            return null;
        }
        return (Button) element;
    }

}
