package net.perkowitz.issho.controller;

import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.Knob;
import net.perkowitz.issho.controller.elements.Pad;

/**
 * Created by mikep on 7/28/20.
 */
public class TestListener implements ControllerListener {
    public void onElementPressed(Element element, int value) {
        System.out.printf("Element pressed: %s, %d\n", element, value);
    }

    public void onElementReleased(Element element) {
        System.out.printf("Element released: %s\n", element);
    }

    public void onElementChanged(Element element, int delta) {
        System.out.printf("Element changed: %s\n", element);
    }

    public void onButtonPressed(Button button, int velocity) {
        System.out.printf("Button pressed: %s, %d\n", button, velocity);
    }

}
