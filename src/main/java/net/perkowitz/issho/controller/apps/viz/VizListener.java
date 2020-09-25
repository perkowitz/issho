package net.perkowitz.issho.controller.apps.viz;


public interface VizListener {

    public void onCanvasPressed(int row, int column);
    public void onPatternPressed(int index);
    public void onButtonPressed(Viz.ButtonId buttonId);

}
