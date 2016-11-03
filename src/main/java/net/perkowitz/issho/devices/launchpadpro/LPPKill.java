package net.perkowitz.issho.devices.launchpadpro;

import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridListener;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.util.MidiUtil;
import org.apache.commons.lang3.StringUtils;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.devices.GridButton.Side.*;


public class LPPKill {

    private static MidiDevice lppInput;
    private static MidiDevice lppOutput;
    private static Transmitter transmitter;
    private static Receiver receiver;

    private static CountDownLatch stop = new CountDownLatch(1);
    private Timer timer = null;

    public static void main(String args[]) throws Exception {

        String[] lppNames = new String[] { "Launchpad", "Standalone" };
        lppInput = MidiUtil.findMidiDevice(lppNames, false, true);
        if (lppInput == null) {
            System.err.printf("Unable to find controller input device matching name: %s\n", StringUtils.join(lppNames, ", "));
            System.exit(1);
        }
        lppOutput = MidiUtil.findMidiDevice(lppNames, true, false);
        if (lppOutput == null) {
            System.err.printf("Unable to find controller output device matching name: %s\n", StringUtils.join(lppNames, ", "));
            System.exit(1);
        }

        lppInput.open();
        transmitter = lppInput.getTransmitter();
        lppOutput.open();
        receiver = lppOutput.getReceiver();
        KillListener listener = new KillListener();

        LaunchpadPro lpp = new LaunchpadPro(receiver, listener);
        listener.setLpp(lpp);

        transmitter.setReceiver(lpp);

        lpp.initialize();

        int millis = 1;
        int repeat = 300;

        fastLights(lpp, 100, 0);


        stop.await();

    }

    private static void fastLights(LaunchpadPro lpp, int repeat, int millis)  {
        for (int i = 0; i < repeat; i++) {
            Color color = Color.fromIndex((int)(Math.random() * 127 + 1));
            for (int y = 0; y < 8; y++) {
                for (int x = 0; x < 8; x++) {
//                    lpp.setPad(GridPad.at(x, y), Color.fromIndex((i + y * 8 + x) % 128));
                    lpp.setPad(GridPad.at(x, y), color);
//                    try {
//                        Thread.sleep(millis);
//                    } catch (Exception e) {
//                        System.err.println(e.getStackTrace());
//                        System.exit(1);
//                    }
                }
            }
        }
    }

    static class KillListener implements GridListener {

        @Setter private LaunchpadPro lpp;

        public void onPadPressed(GridPad pad, int velocity) {
            System.out.printf("%s - %d\n", pad, velocity);
            fastLights(lpp, 100, 0);
        }

        public void onPadReleased(GridPad pad) {}
        public void onButtonPressed(GridButton button, int velocity) {
            System.out.printf("%s - %d\n", button, velocity);
            lpp.initialize();
            System.exit(0);
        }
        public void onButtonReleased(GridButton button) {}

    }

}
