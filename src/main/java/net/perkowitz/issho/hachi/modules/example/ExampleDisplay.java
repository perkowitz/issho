package net.perkowitz.issho.hachi.modules.example;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;

import static net.perkowitz.issho.hachi.modules.example.ExampleUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class ExampleDisplay {

    @Setter private GridDisplay display;
    @Getter @Setter private Map<Integer, Color> palette = ExampleUtil.PALETTE;
    @Getter @Setter private int currentFileIndex = 0;
    @Setter private boolean settingsView = false;
    @Setter private boolean someModeIsSet = false;


    public ExampleDisplay(GridDisplay display) {
        this.display = display;
    }


    /**
     * redraw should know how to draw everything
     *
     * @param memory
     */
    public void redraw(ExampleMemory memory) {
        drawButtons();
        drawPads(memory);
        drawLeftControls();
    }


    /**
     * initialize should usually not try to initialize the things that Hachi draws
     * (top row of buttons, top button on left side)
     */
    public void initialize() {
        display.initialize(true, Sets.newHashSet(GridButton.Side.Bottom, GridButton.Side.Right));
    }


    /***** draw main view ****************************************/

    /**
     * a method for just redrawing part of the UI, in case we only change that part
     */
    public void drawButtons() {
        if (settingsView) return;
        for (GridControl button : buttonControls.getControls()) {
            drawControl(button, false);
        }
    }

    /**
     * draw a different part of the UI, based on current memory values
     *
     * @param memory
     */
    public void drawPads(ExampleMemory memory) {
        if (settingsView) return;
        onePadRowControls.draw(display, palette.get(COLOR_PADS));
        twoPadRowControls.draw(display, palette.get(COLOR_MORE_PADS));
        partialPadRowControls.draw(display, palette.get(COLOR_PADS));

        if (memory.isSomeSettingOn()) {
            padControl.draw(display, palette.get(COLOR_ON));
        } else {
            padControl.draw(display, palette.get(COLOR_OFF));
        }
    }

    /**
     * draw some other UI based on some mode setting, not a value in memory
     */
    public void drawLeftControls() {
        drawControl(buttonControl, someModeIsSet);
        drawControl(settingsControl, settingsView);
    }

    public void drawControl(GridControl control, boolean isOn) {
        if (isOn) {
            control.draw(display, palette.get(COLOR_ON));
        } else {
            control.draw(display, palette.get(COLOR_OFF));
        }
    }

}
