package net.perkowitz.issho.controller.apps.draw;

import com.google.common.collect.Maps;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.Pad;
import net.perkowitz.issho.controller.novation.LaunchpadPro;

import java.awt.Color;
import java.util.Map;

public class LaunchpadProPassthruTranslator implements DrawController, ControllerListener {

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

    public LaunchpadProPassthruTranslator(LaunchpadPro launchpadPro, DrawListener listener) {
        this.launchpadPro = launchpadPro;
        this.listener = listener;
    }


    /***** DrawController implementation *****/

    public void initialize() {
        launchpadPro.initialize();
    }

    // setPalette sets the color in the designated button group if within the LPP's index range.
    public void setPalette(int index, Color color) {
        if (index >= 0 && index < 8)  {
            launchpadPro.setButton(Button.at(PALETTE_GROUP, index), color);
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
        if (element.getType() == Element.Type.BUTTON && element.getGroup() == PALETTE_GROUP) {
            listener.onPalettePressed(element.getIndex());
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
