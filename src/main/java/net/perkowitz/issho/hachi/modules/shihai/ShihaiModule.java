package net.perkowitz.issho.hachi.modules.shihai;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridDisplay;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.hachi.Clockable;
import net.perkowitz.issho.hachi.Sessionizeable;
import net.perkowitz.issho.devices.GridControl;
import net.perkowitz.issho.hachi.modules.*;

import java.util.Set;

import static net.perkowitz.issho.hachi.modules.shihai.ShihaiUtil.*;

/**
 * Created by optic on 9/12/16.
 */
public class ShihaiModule extends BasicModule implements Clockable {

    private static int[] tempos = new int[] { 128, 124, 120, 116, 112, 108, 100, 92 };

    @Getter @Setter private Module[] modules;

    private ShihaiDisplay shihaiDisplay;
    private SettingsSubmodule settingsModule;
    private boolean settingsView = false;

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
        this.shihaiDisplay = new ShihaiDisplay(display);
        this.settingsModule = new SettingsSubmodule(true, false, false);
    }


    public int tempo() {
        if (tempoIndex < tempos.length && tempoIndex >= 0) {
            return tempos[tempoIndex];
        }
        return 120;
    }

    /***** Module interface ****************************************/

    public void setModules(Module[] modules) {
        this.modules = modules;
    }

    @Override
    public void redraw() {
        shihaiDisplay.initialize();
        if (settingsView) {
            settingsModule.redraw();
            shihaiDisplay.drawLeftControls();
        } else {
            shihaiDisplay.redraw(modules, minPatternIndex, maxPatternIndex, tempoIndex, tickCount, measureCount);
        }
    }

    public void mute(boolean muted) {}

    public void setDisplay(GridDisplay display) {
        this.display = display;
        this.shihaiDisplay.setDisplay(display);
        this.settingsModule.setDisplay(display);
    }



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

        if (control.equals(ShihaiUtil.settingsControl)) {
            settingsView = !settingsView;
            shihaiDisplay.setSettingsView(settingsView);
            this.redraw();

        } else if (settingsView) {
            onControlPressedSettings(control, velocity);

        } else if (muteControls.contains(control)) {
            int index = muteControls.getIndex(control);
            if (modules[index] != null && modules[index] instanceof Muteable) {
                boolean muted = ((Muteable)modules[index]).isMuted();
                ((Muteable)modules[index]).mute(!muted);
                shihaiDisplay.drawMutes(modules);
            }

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

        } else if (control.equals(ShihaiUtil.settingsControl)) {
            settingsView = !settingsView;
            shihaiDisplay.setSettingsView(settingsView);
            this.redraw();

        }

    }

    private void onControlReleased(GridControl control) {

        if (settingsView) return;

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

    private void onControlPressedSettings(GridControl control, int velocity) {
        SettingsUtil.SettingsChanged settingsChanged = settingsModule.controlPressed(control, velocity);
        switch (settingsChanged) {
            case SELECT_SESSION:
                int index = settingsModule.getNextSessionIndex();
                settingsModule.setCurrentSessionIndex(index);
                settingsModule.drawSessions();
                for (Module module : modules) {
                    if (module instanceof Sessionizeable) {
                        Sessionizeable sessionizeable = (Sessionizeable) module;
                        sessionizeable.selectSession(index);
                    }
                }
                break;

            case LOAD_FILE:
                break;
            case SAVE_FILE:
                break;
            case SET_MIDI_CHANNEL:
                break;
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
        if (!settingsView) {
            shihaiDisplay.drawClock(tickCount, measureCount);
        }

        tickCount++;
    }



}
