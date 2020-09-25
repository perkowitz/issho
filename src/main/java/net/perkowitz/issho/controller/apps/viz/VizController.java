package net.perkowitz.issho.controller.apps.viz;


import java.awt.*;

public interface VizController {

    public void initialize();
    public void close();
    public void setCanvas(int row, int column, Color color);
    public void setPattern(int index, Color color);
    public void setButton(Viz.ButtonId buttonId, Color color);

}
