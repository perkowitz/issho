package net.perkowitz.issho.controller.apps.hachi;

public interface Module {

    public void setMuted(boolean muted);
    public void flipMuted();
    public boolean isMuted();

    public void setPalette(Palette palette);
    public Palette getPalette();

    public void draw();
}
