package net.perkowitz.issho.controller;

import net.perkowitz.issho.controller.elements.Element;

public class ControllerListenerTest implements ControllerListener {

    public void onElementPressed(Element element, int value) {
        System.out.printf("Element pressed: %s, %d\n", element, value);
    }
    public void onElementReleased(Element element) {
        System.out.printf("Element release: %s\n", element);
    }

    public void onElementChanged(Element element, int delta) {
        System.out.printf("Element changed: %s, %d\n", element, delta);
    }

}
