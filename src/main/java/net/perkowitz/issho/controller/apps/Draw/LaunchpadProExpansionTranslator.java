package net.perkowitz.issho.controller.apps.Draw;

import com.google.common.collect.Maps;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.Pad;
import net.perkowitz.issho.controller.novation.LaunchpadPro;

import java.awt.*;
import java.util.Map;

import static net.perkowitz.issho.controller.Colors.DARK_GRAY;
import static net.perkowitz.issho.controller.Colors.WHITE;

public class LaunchpadProExpansionTranslator implements DrawController, ControllerListener {

    private LaunchpadPro launchpadPro;
    private DrawListener listener;

    private static final int PALETTE_GROUP = LaunchpadPro.BUTTONS_BOTTOM;
    private static final int CANVAS_GROUP = LaunchpadPro.PADS_GROUP;
    private static final int BUTTONS_GROUP = LaunchpadPro.BUTTONS_LEFT;
    private static Map<Draw.ButtonId, Integer> buttonToIndexMap = Maps.newHashMap();
    private static Map<Integer, Draw.ButtonId> indexToButtonMap = Maps.newHashMap();
    static {
        buttonToIndexMap.put(Draw.ButtonId.QUIT, 0);
        buttonToIndexMap.put(Draw.ButtonId.CLEAR, 1);
        buttonToIndexMap.put(Draw.ButtonId.CURRENT_COLOR, 2);
        indexToButtonMap.put(buttonToIndexMap.get(Draw.ButtonId.QUIT), Draw.ButtonId.QUIT);
        indexToButtonMap.put(buttonToIndexMap.get(Draw.ButtonId.CLEAR), Draw.ButtonId.CLEAR);
        indexToButtonMap.put(buttonToIndexMap.get(Draw.ButtonId.CURRENT_COLOR), Draw.ButtonId.CURRENT_COLOR);
    }

    // override certain elements to use for controller mode-switching, invisibly to application
    private static final Color OFF_COLOR = DARK_GRAY;
    private static final Color ON_COLOR = WHITE;
    private Button paletteButton = Button.at(BUTTONS_GROUP, 7);
    private int paletteOffset = 0;


    public LaunchpadProExpansionTranslator(LaunchpadPro launchpadPro, DrawListener listener) {
        this.launchpadPro = launchpadPro;
        this.listener = listener;
    }


    private void drawTranslatorButtons() {
        launchpadPro.setButton(paletteButton, paletteOffset == 0 ? OFF_COLOR : ON_COLOR);
    }

    private void drawPalette() {
        
    }

    /***** DrawController implementation *****/

    public void initialize() {
        launchpadPro.initialize();
        drawTranslatorButtons();
    }

    // setPalette sets the color in the designated button group if within the LPP's index range.
    public void setPalette(int index, Color color) {
        int i = index - paletteOffset;
        if (i >= 0 && i < 8)  {
            launchpadPro.setButton(Button.at(PALETTE_GROUP, i), color);
        }
    }

    // setCanvas sets the color of the designated pad if within the LPP's grid size.
    public void setCanvas(int row, int column, Color color) {
        if (row >= 0 && row < 8 && column >= 0 && column < 8) {
            launchpadPro.setPad(Pad.at(CANVAS_GROUP, row, column), color);
        }
    }

    // setButton sets the color of the designated button, if found.
    public void setButton(Draw.ButtonId buttonId , Color color) {
        Integer index = buttonToIndexMap.get(buttonId);
        if (index != null) {
            launchpadPro.setButton(Button.at(BUTTONS_GROUP, index), color);
        }
    }


    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        // check the translator overridden elements first
        if (element.equals(paletteButton)) {
            paletteOffset = 8 - paletteOffset;
            drawTranslatorButtons();
            listener.drawPalette();
        } else if (element.getType() == Element.Type.BUTTON && element.getGroup() == PALETTE_GROUP) {
            listener.onPalettePressed(element.getIndex() + paletteOffset);
        } else if (element.getType() == Element.Type.PAD && element.getGroup() == CANVAS_GROUP) {
            Pad pad = (Pad) element;
            listener.onCanvasPressed(pad.getRow(), pad.getColumn());
        } else if (element.getType() == Element.Type.BUTTON && element.getGroup() == BUTTONS_GROUP) {
            Draw.ButtonId id = indexToButtonMap.get(element.getIndex());
            if (id != null) {
                listener.onButtonPressed(id);
            }
        }
    }

    public void onElementChanged(Element element, int delta) {}

    public void onElementReleased(Element element) {}

}
