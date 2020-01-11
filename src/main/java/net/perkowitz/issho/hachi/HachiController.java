package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.shihai.ShihaiModule;
import net.perkowitz.issho.util.Terminal;

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
public class HachiController implements Clockable, Receiver, ValueSettable {

    @Getter @Setter private static boolean debugMode = false;
    @Getter @Setter private static boolean sendMidiRealtime = false;

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
    private Receiver outputReceiver = null;

    private List<Clockable> clockables = Lists.newArrayList();
    private List<Triggerable> triggerables = Lists.newArrayList();
    private List<Chordable> chordables = Lists.newArrayList();
    private ShihaiModule shihaiModule = null;
    @Getter private ChordReceiver chordReceiver;

    private static CountDownLatch stop;
    private static Timer timer = null;
    @Getter private boolean clockRunning = false;
    private boolean midiClockRunning = false;
    private int tickCount = 0;
    @Setter private boolean midiContinueAsStart = true;

    private int tempo = 120;
    private int tempoIntervalInMillis = 125 * 120 / tempo;


    public HachiController(Module[] modules, GridDevice[] gridDevices, CountDownLatch stop, Receiver outputReceiver) {

        displays = new MultiDisplay[modules.length];

        this.modules = modules;
        for (int i = 0; i < modules.length; i++) {
//            GridColor color = Color.WHITE;
//            if (modules[i] instanceof Multitrack) {
//                Multitrack m = (Multitrack) modules[i];
//                color = m.getEnabledColor();
//            }
//            Console.fg(Console.fromGrid((net.perkowitz.issho.devices.launchpadpro.Color)color), false);
//            System.out.printf("Loading module: %s\n", modules[i].name());
//            Console.reset();
            System.out.printf("Loading module (%d): %s\n", i, modules[i].name());
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

        this.stop = stop;
        this.outputReceiver = outputReceiver;
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
        timer.cancel();
        stop.countDown();
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
            for (HachiDeviceManager hachiDeviceManager : hachiDeviceManagers) {
                hachiDeviceManager.textClock("");
            }
        }
    }

    public void clock(int measure, int beat, int pulse) {
        if (midiClockRunning) {
            for (Clockable clockable : clockables) {
                clockable.clock(measure, beat, pulse);
            }
            for (HachiDeviceManager hachiDeviceManager : hachiDeviceManagers) {
                hachiDeviceManager.textClock(String.format("%03d:%02d:%02d", measure, measure % 8, beat));
            }
        }

    }


    /***** ValueSettable implementation ***********************************/

    public void setValue(int value) {
        // will send value to any ValueSettable module that's active for some grid device
        for (HachiDeviceManager hachiDeviceManager : hachiDeviceManagers) {
            Module module = hachiDeviceManager.getActiveModule();
            if (module instanceof ValueSettable) {
                ((ValueSettable)module).setValue(value);
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

            if (command == MIDI_REALTIME_COMMAND) {
                // echo realtime commands to the midi output
                if (sendMidiRealtime) {
                    outputReceiver.send(message, timeStamp);
                }
                switch (status) {
                    case START:
//                        System.out.println("START");
                        midiClockCount = 0;
                        clockMeasure = clockBeat = clockPulse = 0;
                        this.start(true);
                        break;
                    case STOP:
//                        System.out.println("STOP");
                        this.stop();
                        break;
                    case CONTINUE:
//                        System.out.println("CONTINUE");
                        midiClockCount = 0;
                        clockMeasure = clockBeat = clockPulse = 0;
                        this.start(midiContinueAsStart);
                        break;
                    case TIMING_CLOCK:
//                        System.out.println("TICK");
//                        System.out.printf("  Tick: %d, %d\n", midiClockCount, tickCount);
                        if (midiClockCount % midiClockDivider == 0) {
                            boolean andReset = (tickCount % 16 == 0);
//                            this.tick(andReset);
//                            System.out.printf("  Sending tick, reset=%s\n", andReset);
                            tickCount++;
                        }

                        this.clock(clockMeasure, clockBeat, clockPulse);
//                        if (clockPulse == 0) {
//                            System.out.printf("  Clock: %04d:%02d:%02d\n", clockMeasure, clockBeat, clockPulse);
//                        }
                        clockPulse++;
                        if (clockPulse % clockPulsesPerBeat == 0 && clockPulse > 0) {
                            clockPulse = 0;
                            clockBeat++;
//                            System.out.println("  Incrementing clockBeat");
                        }
                        if (clockBeat % clockBeatsPerMeasure == 0 && clockBeat > 0) {
                            clockBeat = 0;
                            clockMeasure++;
//                            System.out.println("  Incrementing clockMeasure");
                        }


                        midiClockCount++;
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }

            } else {
                switch (command) {
                    case NOTE_ON:
//                        System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
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
