package net.perkowitz.issho.hachi.modules.shihai;

import com.google.common.collect.Sets;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.Muteable;

import static net.perkowitz.issho.hachi.modules.shihai.ShihaiUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class ShihaiDisplay {

    @Setter private GridDisplay display;

    @Setter private boolean settingsView = false;


    public ShihaiDisplay(GridDisplay display) {
        this.display = display;
    }

    
    public void redraw(Module[] modules, int minPatternIndex, int maxPatternIndex, int tempoIndex, int tickCount, int measureCount) {

//        if (!playing) {
//            Graphics.setPads(display, Graphics.hachi, COLOR_LOGO);
//            return;
//        }

        drawPatterns(minPatternIndex, maxPatternIndex);
        drawTempo(tempoIndex);
        drawMutes(modules);
        drawClock(tickCount, measureCount);
        drawLeftControls();
    }

    public void drawMutes(Module[] modules) {
        for (GridControl control : muteControls.getControls()) {
            int index = control.getIndex();
            if (index < modules.length) {
                Module module = modules[control.getIndex()];

                Color color = Color.OFF;
                if (module instanceof Muteable) {
                    color = COLOR_UNMUTED;
                    if (((Muteable)module).isMuted()) {
                        color = COLOR_MUTED;
                    }
                }
                control.draw(display, color);
            }
        }
    }

    public void drawPatterns(int minPatternIndex, int maxPatternIndex) {
        for (GridControl control : ShihaiUtil.patternControls.getControls()) {
            Color color = COLOR_PATTERN;
            if (control.getIndex() >= minPatternIndex && control.getIndex() <= maxPatternIndex) {
                color = COLOR_PATTERN_HIGHLIGHT;
            }
            control.draw(display, color);
        }
    }

    public void drawTempo(int tempoIndex) {
        for (GridControl control : tempoControls.getControls()) {
            Color color = COLOR_TEMPO;
            if (control.getIndex() == tempoIndex) {
                color = COLOR_TEMPO_HIGHLIGHT;
            }
            control.draw(display, color);
        }
    }

    public void drawClock(int tickCount, int measureCount) {
//        if (!playing) return;

        GridControl tickControl = tickControls.get(tickCount % 16);
        GridControl measureControl = measureControls.get(measureCount % 8);

        for (GridControl control : tickControls.getControls()) {
            Color color = COLOR_TICK;
            if (control == tickControl) {
                color = COLOR_TICK_HIGHLIGHT;
            }
            control.draw(display, color);
        }

        for (GridControl control : measureControls.getControls()) {
            Color color = COLOR_MEASURE;
            if (control == measureControl) {
                color = COLOR_MEASURE_HIGHLIGHT;
            }
            control.draw(display, color);
        }

    }

    public void drawLeftControls() {
        drawControl(ShihaiUtil.settingsControl, settingsView);
    }

    public void drawControl(GridControl control, boolean isOn) {
        if (isOn) {
            control.draw(display, ShihaiUtil.COLOR_ON);
        } else {
            control.draw(display, ShihaiUtil.COLOR_OFF);
        }
    }


    /***** ***********************************************/

    public void initialize() {
        display.initialize(true, Sets.newHashSet(GridButton.Side.Bottom, GridButton.Side.Right));
    }


    /***** draw main view ****************************************/


}
