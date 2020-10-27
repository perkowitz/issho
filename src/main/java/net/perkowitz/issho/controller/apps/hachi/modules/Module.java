package net.perkowitz.issho.controller.apps.hachi.modules;

import net.perkowitz.issho.controller.apps.hachi.Palette;
import net.perkowitz.issho.controller.midi.ClockListener;

public interface Module extends ModuleListener, ClockListener {

    public void setMuted(boolean muted);
    public void flipMuted();
    public boolean isMuted();

    public void setPalette(Palette palette);
    public Palette getPalette();

    public void draw();
}
