package net.perkowitz.issho.controller.apps.hachi;

import net.perkowitz.issho.controller.midi.ClockListener;

public interface Module extends ClockListener {

    public void setMuted(boolean muted);
    public void flipMuted();
    public boolean isMuted();

    public void setPalette(Palette palette);
    public Palette getPalette();

    public void draw();
}
