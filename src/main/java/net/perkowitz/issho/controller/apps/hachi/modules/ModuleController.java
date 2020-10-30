/**
 * ModuleController is a controller interface for Hachi modules.
 * It limits the module to access to the parts of the controller
 * available to modules.
 *
 */
package net.perkowitz.issho.controller.apps.hachi.modules;

import java.awt.*;

public interface ModuleController {

    public void clear();
    public void setButton(int group, int index, Color color);
    public void setPad(int row, int column, Color color);
    public void setKnobValue(int index, int value);
    public void setKnobColor(int index, Color color);
}
