package net.perkowitz.issho.hachi.modules;

import com.google.common.collect.Sets;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Clockable;

import java.util.Set;

/**
 * Created by optic on 9/12/16.
 */
public class ClockModule extends BasicModule implements Clockable {

    private static final GridColor offColor = Color.DARK_GRAY;
    private static final GridColor measureColor = Color.WHITE;
    private static final GridColor stepColor = Color.WHITE;

    private static final int MEASURE_ROW = 0;
    private static final int STEP_MIN_ROW = 1;
    private static final int STEP_MAX_ROW = 2;

    private int stepCount;
    private Set<GridPad> pads = Sets.newHashSet();

    public ClockModule() {
        stepCount = 0;
    }


    /***** private implementation ****************************************/

    private void drawClock() {

        int measure = (stepCount / 16) % 8;
        int step = stepCount % 16;

        for (GridPad pad : pads) {
            display.setPad(pad, offColor);
        }

        pads.clear();
        GridPad pad = GridPad.at(measure, MEASURE_ROW);
        pads.add(pad);
        display.setPad(pad, measureColor);

        pad = GridPad.at(step % 8, STEP_MIN_ROW + (step / 8));
        pads.add(pad);
        display.setPad(pad, stepColor);

        pad = GridPad.at((int)(Math.random() * 8), (int)(Math.random() * 8));
        pads.add(pad);
        display.setPad(pad, Color.fromIndex((int)(Math.random() * 64)));

    }


    /***** Module implementation ****************************************/

    @Override
    public void redraw() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                display.setPad(GridPad.at(x, y), offColor);
            }
        }
        drawClock();
    }

    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        if (restart) {
            stepCount = 0;
        }
    }

    public void stop() {
    }

    public void tick() {
        stepCount++;
        drawClock();
    }


}
