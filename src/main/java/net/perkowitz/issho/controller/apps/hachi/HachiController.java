package net.perkowitz.issho.controller.apps.hachi;


import net.perkowitz.issho.controller.elements.Pad;

import java.awt.*;

public interface HachiController {

    public void initialize();
    public void close();

    /***** Main and Shihai methods *****/
    public void setModuleSelect(int index, Color color);
    public void setModuleMute(int index, Color color);
    public void setMainButton(int index, Color color);
    public void setShihaiButton(int index, Color color);

    public void setKnobValue(int index, int value);
    public void setKnobColor(int index, Color color);
    public void setKnobModeButton(int index, Color color);

    public void setPad(Pad pad, Color color);

    public void showClock(int measure, int step, Color measureColor, Color stepColor, Color bothColor);
}
