package net.perkowitz.issho.controller.apps.draw;

public interface DrawListener {

    public void onPalettePressed(int index);
    public void onCanvasPressed(int row, int column);
    public void onButtonPressed(Draw.ButtonId buttonId);

    public void drawPalette();

}
