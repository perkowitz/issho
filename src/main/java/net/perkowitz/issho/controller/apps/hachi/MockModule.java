package net.perkowitz.issho.controller.apps.hachi;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Controller;
import net.perkowitz.issho.controller.elements.Pad;

import java.awt.*;

public class MockModule implements Module {

    @Getter @Setter private boolean muted = false;
    @Getter @Setter private Palette palette = Palette.DEFAULT;
    private HachiController controller;


    public MockModule(HachiController controller, Palette palette) {
        this.controller = controller;
        this.palette = palette;
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
                controller.setPad(Pad.at(0, r, c), color);
            }
        }

    }

}
