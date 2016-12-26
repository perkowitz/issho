package net.perkowitz.issho.devices.launchpadpro;

import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.launchpadpro.LaunchpadPro;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sound.midi.Receiver;

import static org.junit.Assert.*;

/**
 * Created by optic on 12/26/16.
 */
public class LaunchpadProTest {

    LaunchpadPro launchpadPro;
    Receiver receiver;
    GridListener listener;

    @Before
    public void setUp() throws Exception {
        receiver = null;
        listener = null;
        launchpadPro = new LaunchpadPro(receiver, listener);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void initialize() throws Exception {

    }

    @Test
    public void setPad() throws Exception {

    }

    @Test
    public void setButton() throws Exception {

    }

}