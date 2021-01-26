package net.perkowitz.issho.controller.apps.hachi;


public interface HachiListener {

    /***** Shihai methods *****/
    public void onModuleSelectPressed(int index);
    public void onModuleMutePressed(int index);
    public void onMainButtonPressed(int index);
    public void onMainButtonReleased(int index);
    public void onShihaiButtonPressed(int index);

    public void onKnobSet(int index, int value);
    public void onKnobModePressed(int index);

    public void onModuleButtonPressed(int group, int index, int value);
    public void onModuleButtonReleased(int group, int index);
    public void onModulePadPressed(int row, int column, int value);
    public void onModulePadReleased(int row, int column);

    // draw methods
    public void draw();
    public void drawMain();
    public void drawShihai();

}
