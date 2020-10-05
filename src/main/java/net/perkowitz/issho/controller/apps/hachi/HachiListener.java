package net.perkowitz.issho.controller.apps.hachi;


import java.awt.*;

public interface HachiListener {

    /***** Shihai methods *****/
    public void onModuleSelectPressed(int index);
    public void onModuleMutePressed(int index);
    public void onMainButtonPressed(int index);
    public void onShihaiButtonPressed(int index);

    public void onKnobSet(int index, int value);
    public void onKnobModePressed(int index);

    // draw methods
    public void draw();
    public void drawMain();
    public void drawShihai();

}
