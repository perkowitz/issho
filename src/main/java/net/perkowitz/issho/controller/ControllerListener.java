package net.perkowitz.issho.controller;

import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.Knob;
import net.perkowitz.issho.controller.elements.Pad;

public interface ControllerListener {

    public void onElementPressed(Element element, int value);
    public void onElementChanged(Element element, int delta);
    public void onElementReleased(Element element);

}
