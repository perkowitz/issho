package net.perkowitz.issho.controller.apps.hachi;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.elements.Pad;

import java.awt.*;

public class VizModule implements Module {

    @Getter @Setter private boolean muted = false;
    @Getter @Setter private Palette palette = Palette.DEFAULT;
    private HachiController controller;


    public VizModule(HachiController controller, Palette palette) {
        this.controller = controller;
        this.palette = palette;
    }


    public void flipMuted() {
        muted = !muted;
    }

    public void draw() {
        for (int r = 0; r < Hachi.MAX_ROWS; r++) {
            for (int c = 0; c < Hachi.MAX_COLUMNS; c++) {
                controller.setPad(Pad.at(0, r, c), Colors.BLACK);
            }
        }
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
        int row = (int)(Math.random() * Hachi.MAX_ROWS);
        int column = (int)(Math.random() * Hachi.MAX_COLUMNS);
        int c = (int)(Math.random() * Colors.rainbow.length);
        controller.setPad(Pad.at(0, row, column), Colors.rainbow[c]);
    }



}
