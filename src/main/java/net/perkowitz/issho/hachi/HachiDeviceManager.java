package net.perkowitz.issho.hachi;

import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.hachi.modules.Module;

import static net.perkowitz.issho.hachi.HachiUtil.*;

/**
 * Created by optic on 9/12/16.
 */
public class HachiDeviceManager implements GridListener {

    private Module[] modules = null;
    private Module activeModule;
    private GridListener[] moduleListeners = null;
    private GridListener activeListener = null;
    private GridDisplay display;
    private GridDevice gridDevice;
    private ModuleDisplay[] displays;
    private HachiController hachiController;


    public HachiDeviceManager(GridDevice gridDevice, Module[] modules, HachiController hachiController) {

        this.modules = modules;
        moduleListeners = new GridListener[modules.length];
        this.gridDevice = gridDevice;
        gridDevice.setListener(this);
        this.display = gridDevice;
        this.hachiController = hachiController;
        displays = new ModuleDisplay[modules.length];
        for (int i = 0; i < modules.length; i++) {
            moduleListeners[i] = modules[i].getGridListener();
            ModuleDisplay moduleDisplay = new ModuleDisplay(display);
            displays[i] = moduleDisplay;
            modules[i].setDisplay(moduleDisplay);
        }

    }


    /***** private implementation ***************/

    public void selectModule(int index) {
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

    public void shutdown() {
        display.initialize();
    }

    public void redraw() {

        // modules
        for (int index = 0; index < modules.length; index++) {
            GridButton button = GridButton.at(HachiUtil.MODULE_BUTTON_SIDE, index);
            if (modules[index] == activeModule) {
                display.setButton(button, COLOR_SELECTED);
            } else {
                display.setButton(button, COLOR_UNSELECTED);
            }
        }

        if (hachiController.isClockRunning()) {
            display.setButton(PLAY_BUTTON, COLOR_SELECTED);
        } else {
            display.setButton(PLAY_BUTTON, COLOR_UNSELECTED);
        }

        if (HachiController.DEBUG_MODE) {
            display.setButton(EXIT_BUTTON, COLOR_UNSELECTED);
        }

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
            hachiController.pressPlay();
            redraw();

        } else if (button.equals(EXIT_BUTTON) && HachiController.DEBUG_MODE) {
            hachiController.pressExit();
            //shutdown();

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

    public void onKnobChanged(GridKnob knob, int delta) {}
    public void onKnobSet(GridKnob knob, int value) {}


}
