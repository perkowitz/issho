package net.perkowitz.issho.devices;

import javax.sound.midi.Receiver;

/**
 * Created by optic on 7/29/17.
 */
public interface GridDevice extends Receiver, GridDisplay {

    public void setListener(GridListener listener);

}
