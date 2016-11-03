package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.modules.Module;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

import static net.perkowitz.issho.hachi.HachiUtil.EXIT_BUTTON;
import static net.perkowitz.issho.hachi.HachiUtil.PLAY_BUTTON;

/**
 * Created by optic on 9/12/16.
 */
public class HachiController implements GridListener, Clockable {

    private Module[] modules = null;
    private Module activeModule;
    private GridListener[] moduleListeners = null;
    private GridListener activeListener = null;
    private GridDisplay display;
    private ModuleDisplay[] displays;
    private List<Clockable> clockables = Lists.newArrayList();
    private List<Triggerable> triggerables = Lists.newArrayList();

    private GridColor selectedColor = Color.BRIGHT_ORANGE;
    private GridColor unselectedColor = Color.DARK_GRAY;

    private static CountDownLatch stop = new CountDownLatch(1);
    private static Timer timer = null;
    private boolean clockRunning = false;
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
        selectModule(4);

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
        display.initialize();
        System.exit(0);
    }

    private void redraw() {

        // modules
        for (int index = 0; index < modules.length; index++) {
            GridButton button = GridButton.at(HachiUtil.MODULE_BUTTON_SIDE, index);
            if (modules[index] == activeModule) {
                display.setButton(button, selectedColor);
            } else {
                display.setButton(button, unselectedColor);
            }
        }

        if (clockRunning) {
            display.setButton(PLAY_BUTTON, selectedColor);
        } else {
            display.setButton(PLAY_BUTTON, unselectedColor);
        }

        display.setButton(EXIT_BUTTON, unselectedColor);

    }

    public void startTimer() {

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (clockRunning) {
                    tick();
                    tickCount++;
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
            } else {
                stop();
            }
            redraw();

        } else if (button.equals(EXIT_BUTTON)) {
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
            clockable.start(restart);
        }
    }

    public void stop() {
        for (Clockable clockable : clockables) {
            clockable.stop();
        }
    }

    public void tick() {
        for (Clockable clockable : clockables) {
            clockable.tick();
        }
    }

}
