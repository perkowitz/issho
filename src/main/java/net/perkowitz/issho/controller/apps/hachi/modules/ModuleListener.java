package net.perkowitz.issho.controller.apps.hachi.modules;

public interface ModuleListener {

    public void onPadPressed(int row, int column, int value);
    public void onPadReleased(int row, int column);
    public void onButtonPressed(int group, int index, int value);
    public void onButtonReleased(int group, int index);
    public void onKnob(int index, int value);
}
