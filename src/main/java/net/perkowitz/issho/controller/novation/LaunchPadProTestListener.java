package net.perkowitz.issho.controller.novation;

import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.elements.Element;

import java.awt.*;

/**
 * Created by mikep on 7/28/20.
 */
public class LaunchPadProTestListener implements ControllerListener {

    private Color color = Colors.BLACK;
    @Setter private Controller controller;

    public void onElementPressed(Element element, int value) { System.out.printf("Pressed:%s %d\n", element, value); }

    public void onElementChanged(Element element, int delta) {
        System.out.printf("Changed:%s %d\n", element, delta);
    }

    public void onElementReleased(Element element) {
        System.out.printf("Released:%s\n", element);
    }

}
