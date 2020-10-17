package net.perkowitz.issho.controller.apps.hachi;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.apps.hachi.HachiController;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.Pad;

import java.awt.*;

public class ControllerSwitch implements HachiController {

    private HachiController controller;
    @Getter @Setter private boolean enabled = true;

    public ControllerSwitch(HachiController controller) {
        this.controller = controller;
    }

    public void initialize() {
        if (enabled) {
            controller.initialize();
        }
    }
    public void close() {
        if (enabled) {
            controller.close();
        }
    }

    public void setModuleSelect(int index, Color color) {
        if (enabled) {
            controller.setModuleSelect(index, color);
        }
    }

    public void setModuleMute(int index, Color color) {
        if (enabled) {
            controller.setModuleMute(index, color);
        }
    }

    public void setMainButton(int index, Color color) {
        if (enabled) {
            controller.setMainButton(index, color);
        }
    }

    public void setShihaiButton(int index, Color color) {
        if (enabled) {
            controller.setShihaiButton(index, color);
        }
    }

    public void setKnobValue(int index, int value) {
        if (enabled) {
            controller.setKnobValue(index, value);
        }
    }

    public void setKnobColor(int index, Color color) {
        if (enabled) {
            controller.setKnobColor(index, color);
        }
    }

    public void setKnobModeButton(int index, Color color) {
        if (enabled) {
            controller.setKnobModeButton(index, color);
        }
    }

    public void setPad(Pad pad, Color color) {
        if (enabled) {
            controller.setPad(pad, color);
        }
    }

    public void showClock(int measure, int step, Color measureColor, Color stepColor, Color bothColor) {
        if (enabled) {
            controller.showClock(measure, step, measureColor, stepColor, bothColor);
        }
    }
    
}
