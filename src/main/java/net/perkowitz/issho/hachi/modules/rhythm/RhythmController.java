package net.perkowitz.issho.hachi.modules.rhythm;


import net.perkowitz.issho.devices.GridListener;

/**
 * Created by mperkowi on 7/15/16.
 */
public interface RhythmController extends GridListener {

    public void setSequencer(RhythmInterface sequencer);

}
