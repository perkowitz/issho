package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
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
import static net.perkowitz.issho.hachi.HachiUtil.*;

/**
 * Created by optic on 9/12/16.
 */
public class HachiController implements GridListener, Clockable, Receiver {

    private static boolean DEBUG_MODE = true;

    private static int STEP_MIN = 0;
    private static int STEP_MAX = 110;
    private static int RESET_MIN = 111;
    private static int RESET_MAX = 127;
    private static int MIDI_REALTIME_COMMAND = 0xF0;

    private int triggerChannel = 9;//15;
    private int stepNote = 65;//36;
    private int midiClockDivider = 6;
    private int midiClockCount = 0;

    private Module[] modules = null;
    private Module activeModule;
    private GridListener[] moduleListeners = null;
    private GridListener activeListener = null;
    private GridDisplay display;
    private ModuleDisplay[] displays;
    private List<Clockable> clockables = Lists.newArrayList();
    private List<Triggerable> triggerables = Lists.newArrayList();
    private ShihaiModule shihaiModule = null;

    private static CountDownLatch stop = new CountDownLatch(1);
    private static Timer timer = null;
    private boolean clockRunning = false;
    private boolean midiClockRunning = false;
    private int tickCount = 0;

    private int tempo = 120;
    private int tempoIntervalInMillis = 125 * 120 / tempo;


    public HachiController(Module[] modules, GridDisplay display) {

        this.modules = modules;
        moduleListeners = new GridListener[modules.length];
        this.display = display;
        displays = new ModuleDisplay[modules.length];
        for (int i = 0; i < modules.length; i++) {
            System.out.printf("Loading module: %s\n", modules[i]);
            moduleListeners[i] = modules[i].getGridListener();
            ModuleDisplay moduleDisplay = new ModuleDisplay(display);
            displays[i] = moduleDisplay;
            modules[i].setDisplay(moduleDisplay);

            if (modules[i] instanceof Clockable) {
                clockables.add((Clockable)modules[i]);
            }

            if (modules[i] instanceof Triggerable) {
                triggerables.add((Triggerable) modules[i]);
            }

            if (shihaiModule == null && modules[i] instanceof ShihaiModule) {
                shihaiModule = (ShihaiModule)modules[i];
            }
        }

    }

    public void run() {
        System.out.printf("Controller run...\n");
        display.initialize();
        redraw();
        Graphics.setPads(display, Graphics.issho, Color.WHITE);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        System.out.printf("Displaying logo...\n");
        Graphics.setPads(display, Graphics.issho, Color.OFF);
        Graphics.setPads(display, Graphics.hachi, Color.BRIGHT_ORANGE);
        selectModule(0);

        System.out.printf("Starting timer...\n");
        startTimer();

    }

    /***** private implementation ***************/

    private void selectModule(int index) {
        if (index < modules.length && modules[index] != null) {
            selectDisplay(index);
            display.initialize();
            activeModule = modules[index];
            activeListener = moduleListeners[index];
            activeModule.redraw();
            redraw();
        }
    }

    private void selectDisplay(int index) {
        for (int i = 0; i < displays.length; i++) {
            if (i == index) {
                displays[i].setEnabled(true);
            } else {
                displays[i].setEnabled(false);
            }
        }
    }

    private void shutdown() {
        for (Module module : modules) {
            module.shutdown();
        }
        display.initialize();
        System.exit(0);
    }

    private void redraw() {

        // modules
        for (int index = 0; index < modules.length; index++) {
            GridButton button = GridButton.at(HachiUtil.MODULE_BUTTON_SIDE, index);
            if (modules[index] == activeModule) {
                display.setButton(button, COLOR_SELECTED);
            } else {
                display.setButton(button, COLOR_UNSELECTED);
            }
        }

        if (clockRunning) {
            display.setButton(PLAY_BUTTON, COLOR_SELECTED);
        } else {
            display.setButton(PLAY_BUTTON, COLOR_UNSELECTED);
        }

        if (DEBUG_MODE) {
            display.setButton(EXIT_BUTTON, COLOR_UNSELECTED);
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



    /***** GridListener implementation ***************/

    public void onPadPressed(GridPad pad, int velocity) {
//        System.out.printf("Hachi padPressed: %s, %d\n", pad, velocity);
        if (activeListener != null) {
            activeListener.onPadPressed(pad, velocity);
        }
    }

    public void onPadReleased(GridPad pad) {
//        System.out.printf("Hachi padRelease: %s\n", pad);
        if (activeListener != null) {
            activeListener.onPadReleased(pad);
        }
    }

    public void onButtonPressed(GridButton button, int velocity) {
//        System.out.printf("Hachi buttonPressed: %s, %d\n", button, velocity);
        if (button.getSide() == HachiUtil.MODULE_BUTTON_SIDE && button.getIndex() < modules.length) {
            // top row used for module switching
            int index = button.getIndex();
            selectModule(button.getIndex());

        } else if (button.equals(PLAY_BUTTON)) {
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

        } else if (button.equals(EXIT_BUTTON) && DEBUG_MODE) {
            shutdown();

        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonPressed(button, velocity);
            }
        }
    }

    public void onButtonReleased(GridButton button) {
//        System.out.printf("Hachi buttonReleased: %s\n", button);
        if (button.getSide() == HachiUtil.MODULE_BUTTON_SIDE) {
            // top row used for module switching
        } else if (button.equals(PLAY_BUTTON)) {
        } else if (button.equals(EXIT_BUTTON)) {
        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonReleased(button);
            }
        }
    }


    /***** Clockable implementation ***************/

    public void start(boolean restart) {
        for (Clockable clockable : clockables) {
            tickCount = 0;
            midiClockRunning = true;
            clockable.start(restart);
        }
    }

    public void stop() {
        for (Clockable clockable : clockables) {
            clockable.stop();
            midiClockRunning = false;
            tickCount = 0;
        }
    }

    public void tick(boolean andReset) {
        if (midiClockRunning) {
            for (Clockable clockable : clockables) {
                clockable.tick(andReset);
            }
        }
    }


    /***** Receiver implementation ***************/

    public void close() {
    }

    public void send(MidiMessage message, long timeStamp) {
//        System.out.printf("MSG (%d, %d): ", message.getLength(), message.getStatus());
//        for (byte b : message.getMessage()) {
//            System.out.printf("%d ", b);
//        }
//        System.out.printf("\n");

        if (message instanceof ShortMessage) {
            ShortMessage shortMessage = (ShortMessage) message;
            int command = shortMessage.getCommand();
            int status = shortMessage.getStatus();

            if (command == MIDI_REALTIME_COMMAND) {
                switch (status) {
                    case START:
//                        System.out.println("START");
                        midiClockCount = 0;
                        this.start(true);
                        break;
                    case STOP:
//                        System.out.println("STOP");
                        midiClockCount = 0;
                        this.stop();
                        break;
                    case TIMING_CLOCK:
//                        System.out.println("TICK");
                        if (midiClockCount % midiClockDivider == 0) {
                            boolean andReset = (tickCount % 16 == 0);
                            this.tick(andReset);
                            tickCount++;
                        }
                        midiClockCount++;
                        break;
                    default:
//                        System.out.printf("REALTIME: %d\n", status);
                        break;
                }


            } else {
                switch (command) {
//                    case NOTE_ON:
////                        System.out.printf("NOTE ON: %d, %d, %d\n", shortMessage.getChannel(), shortMessage.getData1(), shortMessage.getData2());
//                        if (shortMessage.getChannel() == triggerChannel && shortMessage.getData1() == stepNote &&
//                                shortMessage.getData2() >= STEP_MIN && shortMessage.getData2() <= STEP_MAX) {
//                            sequencer.trigger(false);
//                        } else if (shortMessage.getChannel() == triggerChannel && shortMessage.getData1() == stepNote &&
//                                shortMessage.getData2() >= RESET_MIN && shortMessage.getData2() <= RESET_MAX) {
//                            sequencer.trigger(true);
//                        }
//                        break;
//                    case NOTE_OFF:
////                        System.out.println("NOTE OFF");
//                        break;
//                    case CONTROL_CHANGE:
////                        System.out.println("MIDI CC");
//                        break;
//                    default:
////                        System.out.printf("MSG: %d\n", command);
                }
            }
        }

    }



}
