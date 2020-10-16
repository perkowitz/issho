package net.perkowitz.issho.controller.apps.viz;

import com.google.common.collect.Maps;
import net.perkowitz.issho.controller.ControllerListener;
import net.perkowitz.issho.controller.apps.draw.Draw;
import net.perkowitz.issho.controller.apps.draw.DrawController;
import net.perkowitz.issho.controller.apps.draw.DrawListener;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.Pad;
import net.perkowitz.issho.controller.yaeltex.YaeltexHachiXL;

import java.awt.*;
import java.util.Map;


public class YaeltexHachiTranslator implements VizController, ControllerListener {

    private static final int CANVAS_GROUP = YaeltexHachiXL.PADS_GROUP;
    private static final int BUTTONS_GROUP = YaeltexHachiXL.BUTTONS_LEFT;
    private static final int PATTERNS_GROUP = YaeltexHachiXL.BUTTONS_BOTTOM;
    private static Map<Viz.ButtonId, Integer> buttonToIndexMap = Maps.newHashMap();
    private static Map<Integer, Viz.ButtonId> indexToButtonMap = Maps.newHashMap();
    static {
        buttonToIndexMap.put(Viz.ButtonId.START, 0);
        buttonToIndexMap.put(Viz.ButtonId.QUIT, 1);
        buttonToIndexMap.put(Viz.ButtonId.CLEAR, 2);
        indexToButtonMap.put(buttonToIndexMap.get(Viz.ButtonId.START), Viz.ButtonId.START);
        indexToButtonMap.put(buttonToIndexMap.get(Viz.ButtonId.QUIT), Viz.ButtonId.QUIT);
        indexToButtonMap.put(buttonToIndexMap.get(Viz.ButtonId.CLEAR), Viz.ButtonId.CLEAR);
    }


    private YaeltexHachiXL hachi;
    private VizListener listener;

    public YaeltexHachiTranslator(YaeltexHachiXL hachi, VizListener listener) {
        this.hachi = hachi;
        this.listener = listener;
    }



    /***** VizController implementation *****/

    public void initialize() {
        hachi.initialize();
    }

    public void close() {
    }

    public void setCanvas(int row, int column, Color color) {
        hachi.setPad(Pad.at(YaeltexHachiXL.PADS_GROUP, row, column), color);
    }

    public void setPattern(int index, Color color) {
        hachi.setButton(Button.at(PATTERNS_GROUP, index), color);
    }

    public void setButton(Viz.ButtonId buttonId, Color color) {
        Integer index = buttonToIndexMap.get(buttonId);
        if (index != null) {
            hachi.setButton(Button.at(BUTTONS_GROUP, index), color);
        }
    }

    /***** ControllerListener implementation *****/

    public void onElementPressed(Element element, int value) {
        if (element.getType() == Element.Type.PAD && element.getGroup() == CANVAS_GROUP) {
            Pad pad = (Pad) element;
            listener.onCanvasPressed(pad.getRow(), pad.getColumn());
        } else if (element.getType() == Element.Type.BUTTON && element.getGroup() == PATTERNS_GROUP) {
            listener.onPatternPressed(element.getIndex());
        } else if (element.getType() == Element.Type.BUTTON && element.getGroup() == BUTTONS_GROUP) {
            Viz.ButtonId id = indexToButtonMap.get(element.getIndex());
            if (id != null) {
                listener.onButtonPressed(id);
            }
        }
    }


    public void onElementChanged(Element element, int delta) {}

    public void onElementReleased(Element element) {}

}
