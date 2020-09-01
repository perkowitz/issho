package net.perkowitz.issho.controller.apps.Draw;

import java.awt.*;

public interface DrawController {

    public void initialize();
    public void setPalette(int index, Color color);
    public void setCanvas(int row, int column, Color color);
    public void setButton(Draw.ButtonId buttonId , Color color);

}
