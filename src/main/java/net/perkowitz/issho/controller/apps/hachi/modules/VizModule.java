package net.perkowitz.issho.controller.apps.hachi.modules;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.apps.hachi.Hachi;
import net.perkowitz.issho.controller.apps.hachi.Palette;

public class VizModule implements Module {

    @Getter @Setter private boolean muted = false;
    @Getter @Setter private Palette palette = Palette.DEFAULT;
    private ModuleController controller;


    public VizModule(ModuleController controller, Palette palette) {
        this.controller = controller;
        this.palette = palette;
    }


    public void flipMuted() {
        muted = !muted;
    }

    public void draw() {
        for (int r = 0; r < Hachi.MAX_ROWS; r++) {
            for (int c = 0; c < Hachi.MAX_COLUMNS; c++) {
                controller.setPad(r, c, Colors.BLACK);
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
        draw();
    }

    public void onStop() {
        System.out.println("MockModule onStop");
    }

    public void onTick() {
        if (!muted) {
            int row = (int) (Math.random() * Hachi.MAX_ROWS);
            int column = (int) (Math.random() * Hachi.MAX_COLUMNS);
            int c = (int) (Math.random() * Colors.rainbow.length);
            controller.setPad(row, column, Colors.rainbow[c]);
        }
    }

    public void onClock(int measure, int beat, int pulse) {

    }


}
