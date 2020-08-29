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

import lombok.Setter;
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
    private static final int CANVAS_GROUP = LaunchpadPro.PADS_GROUP;

    private LaunchpadPro launchpad;
    @Setter private ControllerListener listener;

    // elements the translator overrides to use internally
    private Button translatorPaletteButton = Button.at(LaunchpadPro.BUTTONS_LEFT, 7);
    private Button upButton = Button.at(LaunchpadPro.BUTTONS_TOP, 0);
    private Button downButton = Button.at(LaunchpadPro.BUTTONS_TOP, 1);
    private Button leftButton = Button.at(LaunchpadPro.BUTTONS_TOP, 2);
    private Button rightButton = Button.at(LaunchpadPro.BUTTONS_TOP, 3);
    private ElementSet moveButtons = ElementSet.buttons(LaunchpadPro.BUTTONS_TOP, 0, 3);

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
        launchpad.setButton(translatorPaletteButton, paletteOffset > 0 ? Colors.WHITE : Colors.DARK_GRAY);
        launchpad.setButton(upButton, Colors.DARK_GRAY);
        launchpad.setButton(downButton, Colors.DARK_GRAY);
        launchpad.setButton(leftButton, Colors.DARK_GRAY);
        launchpad.setButton(rightButton, Colors.DARK_GRAY);
    }

    private void drawPalette() {
        for (int i = 0; i < 8; i++) {
            launchpad.setButton(Button.at(PALETTE_GROUP, i), BigDraw.palette[i + paletteOffset]);
        }
    }

    private void drawCanvas() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                launchpad.setPad(Pad.at(CANVAS_GROUP, r, c), canvas[r + rowOffset][c + columnOffset]);
            }
        }
    }

    /***** Controller implementation *****/

    public void initialize() {
        launchpad.initialize();
    }

    public void setPad(Pad pad, Color color) {
        if (BigDraw.canvasPads.contains(pad)) {
            canvas[pad.getRow()][pad.getColumn()] = color;
            if (pad.getRow() >= rowOffset && pad.getRow() < rowOffset + 8 &&
                    pad.getColumn() >= columnOffset && pad.getColumn() < columnOffset + 8) {
                launchpad.setPad(Pad.at(0, pad.getRow() - rowOffset, pad.getColumn() - columnOffset), color);
            }
        }
    }

    public void setButton(Button button, Color color) {
        if (BigDraw.paletteButtons.contains(button)) {
            palette[button.getIndex()] = color;
            if (button.getIndex() >= paletteOffset && button.getIndex() < paletteOffset + 8) {
                launchpad.setButton(Button.at(PALETTE_GROUP, button.getIndex()-paletteOffset), color);
            }
        } else if (BigDraw.miscButtons.contains(button)) {
            launchpad.setButton(Button.at(BUTTONS_GROUP, button.getIndex()), color);
        }
    }

    public void setKnob(Knob knob, Color color) {}

    public void setLight(Light light, Color color) {}


    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        // first check the translator's own overrides
        if (translatorPaletteButton.equals(element)) {
            paletteOffset = 8 - paletteOffset;
            drawPalette();
            drawButtons();
        } else if (moveButtons.contains(element)) {
            switch (element.getIndex()) {
                case 0:
                    rowOffset--;
                    break;
                case 1:
                    rowOffset++;
                    break;
                case 2:
                    columnOffset--;
                    break;
                case 3:
                    columnOffset++;
                    break;
            }
            rowOffset = Math.floorMod(rowOffset, MAX_ROWS);
            columnOffset = Math.floorMod(columnOffset, MAX_COLUMNS);
            drawCanvas();
            drawButtons();
        } else if (LaunchpadPro.pads.contains(element)) {
            Pad pad = (Pad) element;
            Pad newPad = Pad.at(pad.getGroup(), pad.getRow() + rowOffset, pad.getColumn() + columnOffset);
            listener.onElementPressed(newPad, value);
        } else if (LaunchpadPro.bottomButtons.contains(element)) {
            Button button = (Button) element;
            Button newButton = Button.at(BigDraw.PALETTE_BUTTONS_GROUP, button.getIndex() + paletteOffset);
            listener.onElementPressed(newButton, value);
        } else if (LaunchpadPro.leftButtons.contains(element)) {
            Button button = (Button) element;
            Button newButton = Button.at(BigDraw.MISC_BUTTONS_GROUP, button.getIndex());
            listener.onElementPressed(newButton, value);
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
