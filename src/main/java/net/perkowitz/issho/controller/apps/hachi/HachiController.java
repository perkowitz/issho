/**
 * HachiController is a controller interface for the Hachi app. It extends
 * ModuleController, because the app has access to everything modules have
 * access to, as well as the "main control" elements.
 */
package net.perkowitz.issho.controller.apps.hachi;


import java.awt.*;

public interface HachiController  {

    public void initialize();
    public void close();

    /***** Main elements *****/
    public void setModuleSelect(int index, Color color);
    public void setModuleMute(int index, Color color);
    public void setMainButton(int index, Color color);
    public void setShihaiButton(int index, Color color);
    public void setKnobValue(int index, int value);
    public void setKnobColor(int index, Color color);
    public void setKnobModeButton(int index, Color color);

    public void setModulePad(int row, int column, Color color);
    public void setModuleButton(int group, int index, Color color);

    public void showClock(int measure, int step, Color measureColor, Color stepColor, Color bothColor);

    /***** sizes *****/
    public int MODULE_COUNT();   // max number of modules in select/mute buttons
    public int MAIN_COUNT();
    public int SHIHAI_COUNT();
    public int KNOB_COUNT();
    public int KNOB_MODE_COUNT();
    public int PAD_ROWS_COUNT();
    public int PAD_COLUMNS_COUNT();
    public int BUTTON_GROUPS_COUNT();
    public int BUTTONS_COUNT(int group);

}
