package net.perkowitz.issho.hachi.modules.shihai;

import com.google.common.collect.Sets;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Multitrack;
import net.perkowitz.issho.hachi.modules.Module;
import net.perkowitz.issho.hachi.modules.Muteable;
import org.w3c.dom.stylesheets.DocumentStyle;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.shihai.ShihaiUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class ShihaiDisplay {

    @Setter private GridDisplay display;

    @Setter private boolean settingsView = false;
    @Setter private boolean enableReset = true;
    @Setter private List<Multitrack> multitrackModules = null;


    public ShihaiDisplay(GridDisplay display) {
        this.display = display;
    }

    
    public void redraw(Module[] modules, int minPatternIndex, int maxPatternIndex, int tempoIndex, int tickCount, int measureCount) {

//        if (!playing) {
//            Graphics.setPads(display, Graphics.hachi, COLOR_LOGO);
//            return;
//        }

        drawPatterns(minPatternIndex, maxPatternIndex);
        drawMultitracks();
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

    public void drawMultitracks() {

        int multitrackCount = Math.min(multitrackModules.size(), multitrackControls.size());

        for (int m = 0; m < multitrackCount; m++) {
            drawMultitrack(m);
        }

    }

    public void drawMultitrack(int m) {
        Multitrack module = (Multitrack)multitrackModules.get(m);
        GridControlSet controlSet = multitrackControls.get(m);
        for (int index = 0; index < module.trackCount(); index++ ) {
            GridColor color = Color.OFF;
            if (module.getTrackEnabled(index)) {
                color = module.getEnabledColor();
            }
            controlSet.get(index).draw(display, color);
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

        //if (!playing) return;

        GridControl tickControl = clockControls.get(tickCount % 16);
        GridControl measureControl = clockControls.get(measureCount % 8);

        // draw the 3rd multitrack, if present; it shares space with the clock
        Color defaultColor = COLOR_TICK;
        Multitrack multitrackModule = null;
        if (multitrackControls.size() > 2 && multitrackModules.size() > 2) {
            defaultColor= Color.OFF;
            multitrackModule = (Multitrack)multitrackModules.get(2);
        }

        for (GridControl control : clockControls.getControls()) {
            Color color = defaultColor;
            if (control == measureControl) {
                color = COLOR_MEASURE_HIGHLIGHT;
            } else if (control == tickControl) {
                color = COLOR_TICK_HIGHLIGHT;
            } else if (multitrackModule != null && multitrackModule.getTrackEnabled(control.getIndex())) {
                color = (Color)multitrackModule.getEnabledColor();
            }
            control.draw(display, color);
        }

    }

    public void drawLeftControls() {
        leftControls.draw(display, Color.OFF);
        drawControl(ShihaiUtil.settingsControl, settingsView);
        drawControl(ShihaiUtil.panicControl, false);
        if (enableReset) {
            drawControl(ShihaiUtil.clockResetControl, false);
        }
        drawControl(ShihaiUtil.fillControl, false);
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
