package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.modules.Module;

import java.util.List;

/**
 * Created by optic on 9/12/16.
 */
public class HachiController implements GridListener, Clockable {

    private Module[] modules = null;
    private Module activeModule;
    private GridListener[] moduleListeners = null;
    private GridListener activeListener = null;
    private GridDisplay display;
    private SwitchableDisplay[] displays;
    private List<Clockable> clockables = Lists.newArrayList();
    private List<Triggerable> triggerables = Lists.newArrayList();

    private GridColor selectedColor = Color.BRIGHT_ORANGE;
    private GridColor unselectedColor = Color.DARK_GRAY;


    public HachiController(Module[] modules, GridDisplay display) {

        this.modules = modules;
        moduleListeners = new GridListener[modules.length];
        this.display = display;
        displays = new SwitchableDisplay[modules.length];
        for (int i = 0; i < modules.length; i++) {
            System.out.printf("Loading module: %s\n", modules[i]);
            moduleListeners[i] = modules[i].getGridListener();
            SwitchableDisplay switchableDisplay = new SwitchableDisplay(display);
            displays[i] = switchableDisplay;
            modules[i].setDisplay(switchableDisplay);

            if (modules[i] instanceof Clockable) {
                clockables.add((Clockable)modules[i]);
            }

            if (modules[i] instanceof Triggerable) {
                triggerables.add((Triggerable) modules[i]);
            }

        }

    }

    public void run() {
        display.initialize();
        redraw();
        Graphics.setPads(display, Graphics.issho, Color.WHITE);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
        Graphics.setPads(display, Graphics.issho, Color.OFF);
        Graphics.setPads(display, Graphics.hachi, Color.BRIGHT_ORANGE);
        selectModule(0);
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

        // exit button
        GridButton button = GridButton.at(HachiUtil.MODULE_BUTTON_SIDE, 7);
        display.setButton(button, unselectedColor);

    }


    /***** GridListener implementation ***************/

    public void onPadPressed(GridPad pad, int velocity) {
//        System.out.printf("Hachi padPressed: %s, %d\n", pad, velocity);
        if (activeListener != null) {
            activeListener.onPadPressed(pad, velocity);
        }
    }

    public void onPadReleased(GridPad pad) {
//        System.out.printf("Hachi padRelease: %s, %d\n", pad);
        if (activeListener != null) {
            activeListener.onPadReleased(pad);
        }
    }

    public void onButtonPressed(GridButton button, int velocity) {
//        System.out.printf("Hachi buttonPressed: %s, %d\n", button, velocity);
        if (button.getSide() == HachiUtil.MODULE_BUTTON_SIDE) {
            // top row used for module switching
            int index = button.getIndex();
            if (index == 7) {
                shutdown();
            } else {
                selectModule(button.getIndex());
            }
        } else {
            // everything else passed through to active module
            if (activeListener != null) {
                activeListener.onButtonPressed(button, velocity);
            }
        }
    }

    public void onButtonReleased(GridButton button) {
//        System.out.printf("Hachi buttonReleased: %s, %d\n", button);
        if (button.getSide() == HachiUtil.MODULE_BUTTON_SIDE) {
            // top row used for module switching
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
