/**
 * Translates from the ModuleController interface to a HachiController.
 * Can be enabled/disabled when a module is active/inactive.
 * This translator can be replaced for individual module/controller
 * combinations if complex remapping is needed.
 */
package net.perkowitz.issho.controller.apps.hachi;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.controller.apps.hachi.modules.ModuleController;

import java.awt.*;

public class ModuleTranslator implements ModuleController {

    private HachiController controller;
    @Getter @Setter private boolean enabled = true;

    public ModuleTranslator(HachiController controller) {
        this.controller = controller;
    }


    public void clear() {
        if (enabled) {
            clearButtons();
            clearPads();
        }
    }

    private void clearButtons() {
        for (int group = 0; group < controller.BUTTON_GROUPS_COUNT(); group++) {
            for (int index = 0; index < controller.BUTTONS_COUNT(group); index++) {
                controller.setModuleButton(group, index, Colors.OFF);
            }
        }
    }

    public void clearPads() {
        for (int row=0; row < controller.PAD_ROWS_COUNT(); row++) {
            for (int column = 0; column < controller.PAD_COLUMNS_COUNT(); column++) {
                setPad(row, column, Colors.OFF);
            }
        }
    }

    public void setButton(int group, int index, Color color) {
        if (enabled) {
            controller.setModuleButton(group, index, color);
        }
    }

    public void setPad(int row, int column, Color color) {
        if (enabled) {
            controller.setModulePad(row, column, color);
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

    public void flush() {
        controller.flush();
    }

}
