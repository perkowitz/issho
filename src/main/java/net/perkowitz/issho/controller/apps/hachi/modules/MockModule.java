package net.perkowitz.issho.controller.apps.hachi.modules;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.apps.hachi.Hachi;
import net.perkowitz.issho.controller.apps.hachi.Palette;

import java.awt.*;

public class MockModule implements Module {

    @Getter @Setter private boolean muted = false;
    @Getter @Setter private Palette palette = Palette.DEFAULT;
    private boolean random = false;
    private ModuleController controller;


    public MockModule(ModuleController controller, Palette palette) {
        this.controller = controller;
        this.palette = palette;
    }

    public MockModule(ModuleController controller, Palette palette, boolean random) {
        this.controller = controller;
        this.palette = palette;
        this.random = random;
    }


    public void flipMuted() {
        muted = !muted;
    }

    public void draw() {
        Color color = palette.Key;
        if (muted) {
            color = palette.KeyDim;
        }
        for (int r = 0; r < Hachi.MAX_ROWS; r++) {
            for (int c = 0; c < Hachi.MAX_COLUMNS; c++) {
                if (random && !muted) {
                    int i = (int)(Math.random() * Colors.standardPalette.length);
                    controller.setPad(r, c, Colors.standardPalette[i]);
                } else {
                    controller.setPad(r, c, color);
                }
            }
        }

        for (int g = 0; g < 4; g++) {
            for (int i = 0; i < 16; i++) {
                controller.setButton(g, i, palette.KeyDim);
            }
        }

    }


    /***** ModuleListener implementation *****/

    public void onPadPressed(int row, int column, int value) {
    }

    public void onPadReleased(int row, int column) {
    }

    public void onButtonPressed(int group, int index, int value) {
    }

    public void onButtonReleased(int group, int index) {
    }

    public void onKnob(int index, int value) {
    }


    /***** ClockListener implementation *****/

    public void onStart(boolean restart) {
        System.out.println("MockModule onStart");
    }

    public void onStop() {
        System.out.println("MockModule onStop");
    }
    public void onTick() {}

    public void onClock(int measure, int beat, int pulse) {}


}
