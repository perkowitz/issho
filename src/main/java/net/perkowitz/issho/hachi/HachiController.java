package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.shihai.ShihaiModule;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static javax.sound.midi.ShortMessage.*;

/**
 * Created by optic on 9/12/16.
 */
public class HachiController implements Clockable, Receiver {

    public static boolean DEBUG_MODE = true;

    private static int STEP_MIN = 0;
    private static int STEP_MAX = 110;
    private static int RESET_MIN = 111;
    private static int RESET_MAX = 127;
    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private int triggerChannel = 9;//15;
    private int stepNote = 65;//36;
    private int midiClockDivider = 6;
    private int midiClockCount = 0;
    private int clockMeasure = 0;
    private int clockBeat = 0;
    private int clockPulse = 0;
    private int clockPulsesPerBeat = 24;
    private int clockBeatsPerMeasure = 4;

    private Module[] modules = null;
    private GridDevice[] gridDevices;
    private HachiDeviceManager[] hachiDeviceManagers;
    private MultiDisplay[] displays;

    private List<Clockable> clockables = Lists.newArrayList();
    private List<Triggerable> triggerables = Lists.newArrayList();
    private List<Chordable> chordables = Lists.newArrayList();
    private ShihaiModule shihaiModule = null;
    private Receiver midiReceiver;
    @Getter private ChordReceiver chordReceiver;

    private static CountDownLatch stop = new CountDownLatch(1);
    private static Timer timer = null;
    @Getter private boolean clockRunning = false;
    private boolean midiClockRunning = false;
    private int tickCount = 0;
    @Setter private boolean midiContinueAsStart = true;

    private int tempo = 120;
    private int tempoIntervalInMillis = 125 * 120 / tempo;


    public HachiController(Module[] modules, GridDevice[] gridDevices, Receiver midiReceiver) {

        this.midiReceiver = midiReceiver;
        displays = new MultiDisplay[modules.length];

        this.modules = modules;
        for (int i = 0; i < modules.length; i++) {
            System.out.printf("Loading module: %s\n", modules[i]);
            if (modules[i] instanceof Clockable) {
                clockables.add((Clockable)modules[i]);
            }
            if (modules[i] instanceof Triggerable) {
                triggerables.add((Triggerable) modules[i]);
            }
            if (modules[i] instanceof Chordable) {
                chordables.add((Chordable) modules[i]);
            }
            if (shihaiModule == null && modules[i] instanceof ShihaiModule) {
                shihaiModule = (ShihaiModule)modules[i];
            }

            displays[i] = new MultiDisplay(gridDevices);
        }

        chordReceiver = new ChordReceiver(chordables);

        this.gridDevices = gridDevices;
        hachiDeviceManagers = new HachiDeviceManager[gridDevices.length];
        for (int i = 0; i < gridDevices.length; i++) {
            HachiDeviceManager hachiDeviceManager = new HachiDeviceManager(gridDevices[i], modules, this);
            hachiDeviceManagers[i] = hachiDeviceManager;
        }

    }

    public void run() {
        System.out.printf("Controller run...\n");
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.initialize();
        }
        redraw();
//        Graphics.setPads(display, Graphics.issho, Color.WHITE);
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {}
//        System.out.printf("Displaying logo...\n");
//        Graphics.setPads(display, Graphics.issho, Color.OFF);
//        Graphics.setPads(display, Graphics.hachi, Color.BRIGHT_ORANGE);

        for (HachiDeviceManager hachiDeviceManager : hachiDeviceManagers) {
            hachiDeviceManager.selectModule(0);
        }

        System.out.printf("Starting timer...\n");
        startTimer();

    }

    public void pressPlay() {
        clockRunning = !clockRunning;
        if (clockRunning) {
            start(true);
            for (Clockable clockable: clockables) {
                clockable.start(true);
            }
        } else {
            stop();
            for (Clockable clockable: clockables) {
                clockable.stop();
            }
        }
        redraw();
    }

    public void pressExit() {
        shutdown();
    }

    public MultiDisplay getDisplay(int index) {
        return displays[index];
    }

    /***** private implementation ***************/

    private void shutdown() {
        for (Module module : modules) {
            module.shutdown();
        }
        for (HachiDeviceManager hachiDeviceManager : hachiDeviceManagers) {
            hachiDeviceManager.shutdown();
        }
        for (GridDevice gridDevice : gridDevices) {
            gridDevice.initialize();
        }
        System.exit(0);
    }

    private void redraw() {
        for (HachiDeviceManager hachiDeviceManager : hachiDeviceManagers) {
            hachiDeviceManager.redraw();
        }
    }

    public void startTimer() {

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (clockRunning) {
                    tick(tickCount % 16 == 0);
                    tickCount++;
                }

                if (shihaiModule != null && shihaiModule.tempo() != tempo) {
                    tempo = shihaiModule.tempo();
                    tempoIntervalInMillis = 125 * 120 / tempo;
                    timer.cancel();
                    timer = null;
                    startTimer();
                }

            }
        }, tempoIntervalInMillis, tempoIntervalInMillis);
    }

    public void processCommand(String command) {
        command = command.toLowerCase();

        if (command.equals("end")) {
            shutdown();
        } else if (command.equals("ls")) {
            for (int index = 0; index < modules.length; index++) {
                System.out.printf("%d: %s\n", index, modules[index]);
            }
        }

        System.out.print("> ");
    }


    /***** Clockable implementation ***************/

    public void start(boolean restart) {
        midiClockRunning = true;
        if (restart) {
            tickCount = 0;
        }
        for (Clockable clockable : clockables) {
            clockable.start(restart);
        }
    }

    public void stop() {
        midiClockRunning = false;
        for (Clockable clockable : clockables) {
            clockable.stop();
        }
    }

    public void tick(boolean andReset) {
        if (midiClockRunning) {
            for (Clockable clockable : clockables) {
                clockable.tick(andReset);
            }
        }
    }

    public void clock(int measure, int beat, int pulse) {
        if (midiClockRunning) {
            for (Clockable clockable : clockables) {
                clockable.clock(measure, beat, pulse);
            }
        }

    }


    /***** Receiver implementation ***************/

    public void close() {
    }

    public void send(MidiMessage message, long timeStamp) {

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            byte[] emptyData = new byte[0];

            if (command == MIDI_REALTIME_COMMAND) {
                switch (status) {
                    case START:
                        System.out.println("START");
                        try {
//                            ShortMessage clockMessage = new ShortMessage();
//                            clockMessage.setMessage(0xfe);
//                            midiReceiver.send(clockMessage, -1); // pass clock through to midi out
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        midiClockCount = 0;
                        this.start(true);
                        break;
                    case STOP:
                        System.out.println("STOP");
//                        midiReceiver.send(message, timeStamp); // pass clock through to midi out
                        this.stop();
                        break;
                    case CONTINUE:
                        System.out.println("CONTINUE");
//                        midiReceiver.send(message, timeStamp); // pass clock through to midi out
                        this.start(midiContinueAsStart);
                        break;
                    case TIMING_CLOCK:
//                        midiReceiver.send(message, timeStamp); // pass clock through to midi out
//                        if (midiClockCount % midiClockDivider == 0) {
//                            boolean andReset = (tickCount % 16 == 0);
////                            this.tick(andReset);
//                            tickCount++;
//                        }
//                        midiClockCount++;

                        this.clock(clockMeasure, clockBeat, clockPulse);
                        clockPulse++;
                        if (clockPulse % clockPulsesPerBeat == 0 && clockPulse > 0) {
                            clockPulse = 0;
                            clockBeat++;
                        }
                        if (clockBeat % clockBeatsPerMeasure == 0 && clockBeat > 0) {
                            clockBeat = 0;
                            clockMeasure++;
                        }
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }

            } else {
                switch (command) {
                    case NOTE_ON:
                        System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
                        break;
                    case NOTE_OFF:
//                        System.out.println("NOTE OFF");
                        break;
                    case CONTROL_CHANGE:
//                        System.out.println("MIDI CC");
                        break;
                    default:
//                        System.out.printf("MSG: %d\n", command);
                }
            }
        }

    }



}
