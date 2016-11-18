package net.perkowitz.issho.hachi.modules;

import com.google.common.collect.Sets;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Graphics;
import net.perkowitz.issho.hachi.HachiController;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.devices.GridControlSet;

import java.util.Set;

/**
 * Created by optic on 9/12/16.
 */
public class ShihaiModule extends BasicModule implements Clockable {

    private static int[] tempos = new int[] { 128, 124, 120, 116, 112, 108, 100, 92 };

    private static Color COLOR_LOGO = Color.BRIGHT_RED;
    private static Color COLOR_MUTED = Color.DARK_GRAY;
    private static Color COLOR_UNMUTED = Color.WHITE;
    private static Color COLOR_SESSION = Color.DIM_GREEN;
    private static Color COLOR_SESSION_HIGHLIGHT = Color.LIGHT_GRAY;
    private static Color COLOR_PATTERN = Color.DIM_BLUE;
    private static Color COLOR_PATTERN_HIGHLIGHT = Color.LIGHT_GRAY;
    private static Color COLOR_MEASURE = Color.DIM_RED;
    private static Color COLOR_MEASURE_HIGHLIGHT = Color.LIGHT_GRAY;
    private static Color COLOR_TICK = Color.fromIndex(10);
    private static Color COLOR_TICK_HIGHLIGHT = Color.LIGHT_GRAY;
    private static Color COLOR_TEMPO = Color.DIM_RED;
    private static Color COLOR_TEMPO_HIGHLIGHT = Color.BRIGHT_RED;

    private static GridControlSet muteControls = GridControlSet.buttonSide(GridButton.Side.Bottom);
    private static GridControlSet sessionControls = GridControlSet.padRows(0, 1);
    private static GridControlSet patternControls = GridControlSet.padRows(2, 3);
    private static GridControlSet measureControls = GridControlSet.padRow(5);
    private static GridControlSet tickControls = GridControlSet.padRows(6, 7);
    private static GridControlSet tempoControls = GridControlSet.buttonSide(GridButton.Side.Right);

    @Setter private Module[] modules;
    HachiController hachiController = null;

    private boolean playing = false;
    private int tickCount = 0;
    private int measureCount = 0;
    private int currentSessionIndex = 0;
    private int minPatternIndex = 0;
    private int maxPatternIndex = 0;
    private Set<Integer> patternsPressed = Sets.newHashSet();
    private int patternsReleasedCount = 0;
    private int tempoIndex = 2;


    /***** constructor ****************************************/

    public ShihaiModule() {
    }


    public int tempo() {
        if (tempoIndex < tempos.length && tempoIndex >= 0) {
            return tempos[tempoIndex];
        }
        return 120;
    }

    /***** Module interface ****************************************/

    @Override
    public void redraw() {

        if (!playing) {
            Graphics.setPads(display, Graphics.hachi, COLOR_LOGO);
            return;
        }

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

        for (GridControl control : sessionControls.getControls()) {
            Color color = COLOR_SESSION;
            if (control.getIndex() == currentSessionIndex) {
                color = COLOR_SESSION_HIGHLIGHT;
            }
            control.draw(display, color);
        }

        for (GridControl control : patternControls.getControls()) {
            Color color = COLOR_PATTERN;
            if (control.getIndex() >= minPatternIndex && control.getIndex() <= maxPatternIndex) {
                color = COLOR_PATTERN_HIGHLIGHT;
            }
            control.draw(display, color);
        }

        for (GridControl control : tempoControls.getControls()) {
            Color color = COLOR_TEMPO;
            if (control.getIndex() == tempoIndex) {
                color = COLOR_TEMPO_HIGHLIGHT;
            }
            control.draw(display, color);
        }

        drawClock();

    }

    public void mute(boolean muted) {}



    /***** GridListener interface ****************************************/

    public void onPadPressed(GridPad pad, int velocity) {
        onControlPressed(new GridControl(pad, null), velocity);
    }

    public void onPadReleased(GridPad pad) {
        onControlReleased(new GridControl(pad, null));
    }

    public void onButtonPressed(GridButton button, int velocity) {
        onControlPressed(new GridControl(button, null), velocity);
    }

    public void onButtonReleased(GridButton button) {
        onControlReleased(new GridControl(button, null));
    }

    private void onControlPressed(GridControl control, int velocity) {

        if (muteControls.contains(control)) {
            int index = muteControls.getIndex(control);
            if (modules[index] != null && modules[index] instanceof Muteable) {
                boolean muted = ((Muteable)modules[index]).isMuted();
                ((Muteable)modules[index]).mute(!muted);
                redraw();
            }

        } else if (sessionControls.contains(control)) {
            currentSessionIndex = sessionControls.getIndex(control);
            for (Module module : modules) {
                if (module instanceof Sessionizeable) {
                    Sessionizeable sessionizeable = (Sessionizeable) module;
                    sessionizeable.selectSession(currentSessionIndex);
                }
            }
            redraw();

        } else if (patternControls.contains(control)) {
            Integer index = patternControls.getIndex(control);
            if (index != null) {
                patternsPressed.add(index);
            }

        } else if (tempoControls.contains(control)) {
            Integer index = tempoControls.getIndex(control);
            if (index != null) {
                tempoIndex = index;
                redraw();
            }

        }

    }

    private void onControlReleased(GridControl control) {

        if (patternControls.contains(control)) {

            // releasing a pattern pad
            // don't activate until the last pattern pad is released (so additional releases don't look like a new press/release)
            patternsReleasedCount++;
            if (patternsReleasedCount >= patternsPressed.size()) {
                Integer index = patternControls.getIndex(control);
                patternsPressed.add(index); // just to make sure
                minPatternIndex = index;
                maxPatternIndex = index;
                if (patternsPressed.size() > 1) {
                    for (Integer pattern : patternsPressed) {
                        if (pattern < minPatternIndex) {
                            minPatternIndex = pattern;
                        }
                        if (pattern > maxPatternIndex) {
                            maxPatternIndex = pattern;
                        }
                    }
                }
                patternsPressed.clear();
                patternsReleasedCount = 0;

                for (Module module : modules) {
                    if (module instanceof Sessionizeable) {
                        Sessionizeable sessionizeable = (Sessionizeable) module;
                        sessionizeable.selectPatterns(minPatternIndex, maxPatternIndex);
                    }
                }
                redraw();
            }

        }

    }


    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        playing = true;
        display.initialize();
        redraw();
        tickCount = 0;
        measureCount = 0;
    }

    public void stop() {
        playing = false;
        display.initialize();
        redraw();
        tickCount = 0;
        measureCount = 0;
    }

    public void tick(boolean andReset) {
        if (tickCount % 16 == 0 && tickCount > 0) {
            measureCount++;
        }
        drawClock();

        tickCount++;
    }



    /***** private implementation ****************************************/

    public void drawClock() {
        if (!playing) return;

        GridControl tickControl = tickControls.get(tickCount % 16);
        GridControl measureControl = measureControls.get(measureCount % 8);

        for (GridControl control : tickControls.getControls()) {
            Color color = COLOR_MEASURE;
            if (control == tickControl) {
                color = COLOR_MEASURE_HIGHLIGHT;
            }
            control.draw(display, color);
        }

        for (GridControl control : measureControls.getControls()) {
            Color color = COLOR_TICK;
            if (control == measureControl) {
                color = COLOR_TICK_HIGHLIGHT;
            }
            control.draw(display, color);
        }

    }


}
