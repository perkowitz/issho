/**
 * Translator to implement the BigDraw app on a LaunchpadPro.
 * 
 * This translator allows the smaller launchpad to implement
 * the entire app UI by creating view modes and keeping track
 * of what's currently being viewed.
 *
 * To do this, it has to allocate some buttons for mode-switching
 * and keep track of the state of all buttons/pads so it can
 * redraw them on demand. the app doesn't need to know that
 * multiple modes are being used; it just interacts with palette
 * and canvas.
 *
 */
package net.perkowitz.issho.controller.apps.BigDraw;

import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.Translator;
import net.perkowitz.issho.controller.elements.*;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.novation.LaunchpadPro;

import java.awt.*;

import static net.perkowitz.issho.controller.apps.BigDraw.BigDraw.MAX_COLUMNS;
import static net.perkowitz.issho.controller.apps.BigDraw.BigDraw.MAX_ROWS;

public class ExpansionTranslator implements Translator {

    private static final int PALETTE_GROUP = LaunchpadPro.BUTTONS_BOTTOM;
    private static final int BUTTONS_GROUP = LaunchpadPro.BUTTONS_LEFT;

    private LaunchpadPro launchpad;
    private ControllerListener listener;

    private Color canvas[][] = new Color[MAX_ROWS][MAX_COLUMNS];
    private int rowOffset = 0;
    private int columnOffset = 0;

    private Color palette[] = new Color[BigDraw.palette.length];
    private int paletteOffset = 0;

    public ExpansionTranslator(LaunchpadPro launchpad, ControllerListener listener) {
        this.launchpad = launchpad;
        this.listener = listener;
    }

    private void drawButtons() {
        Color color = Colors.DARK_GRAY;
        if (paletteOffset > 0) {
            color = Colors.WHITE;
        }
        launchpad.setButton(Button.at(BUTTONS_GROUP, 7), color);
    }

    private void drawPalette() {
        for (int i = 0; i < 8; i++) {
            launchpad.setButton(Button.at(PALETTE_GROUP, i), BigDraw.palette[i + paletteOffset]);
        }
    }

    /***** Controller implementation *****/

    public void initialize() {
        launchpad.initialize();
    }

    public void setPad(Pad pad, Color color) {
        if (pad.getGroup() == BigDraw.CANVAS_PADS_GROUP) {
            canvas[pad.getRow()][pad.getColumn()] = color;
            if (pad.getRow() >= rowOffset && pad.getRow() < rowOffset + 8 &&
                    pad.getColumn() >= columnOffset && pad.getColumn() < columnOffset + 8) {
                launchpad.setPad(Pad.at(0, pad.getRow() - rowOffset, pad.getColumn() - columnOffset), color);
            }
        }
    }

    public void setButton(Button button, Color color) {
        if (button.getGroup() == BigDraw.PALETTE_BUTTON_GROUP) {
            palette[button.getIndex()] = color;
            if (button.getIndex() >= paletteOffset && button.getIndex() < paletteOffset + 8) {
                launchpad.setButton(Button.at(PALETTE_GROUP, button.getIndex()-paletteOffset), color);
            }
        }
    }

    public void setKnob(Knob knob, Color color) {}

    public void setLight(Light light, Color color) {}


    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        if (element instanceof Pad && element.getGroup() == BigDraw.CANVAS_PADS_GROUP) {
            Pad pad = (Pad) element;
            Pad newPad = Pad.at(pad.getGroup(), pad.getRow() + rowOffset, pad.getColumn() + columnOffset);
            listener.onElementPressed(newPad, value);
        } else if (element instanceof Button) {
            Button button = (Button) element;
            if (button.getGroup() == PALETTE_GROUP) {
                Button newButton = Button.at(BigDraw.PALETTE_BUTTON_GROUP, button.getIndex() + paletteOffset);
                listener.onElementPressed(newButton, value);
            } else if (button.getGroup() == BUTTONS_GROUP && button.getIndex() == 0) {
                listener.onElementPressed(button, value);
            } else if (button.getGroup() == BUTTONS_GROUP && button.getIndex() == 7) {
                paletteOffset = 8 - paletteOffset;
                drawPalette();
                drawButtons();
            }
        } else {
            listener.onElementPressed(element, value);
        }
    }

    public void onElementChanged(Element element, int delta) {
        listener.onElementChanged(element, delta);
    }

    public void onElementReleased(Element element) {
        listener.onElementReleased(element);
    }

}
