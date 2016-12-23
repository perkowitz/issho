package net.perkowitz.issho.hachi.modules;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Sessionizeable;

import java.util.Map;

import static net.perkowitz.issho.hachi.modules.SettingsUtil.*;


/**
 * Created by optic on 12/7/16.
 */
public class SettingsSubmodule extends BasicModule implements Module, Sessionizeable {

    @Getter @Setter private int currentSessionIndex = 0;
    @Getter @Setter private int nextSessionIndex = 0;
    @Getter @Setter private int currentFileIndex = 0;
    @Getter @Setter private int midiChannel = 0;
    @Getter private SettingsUtil.SettingsChanged settingsChanged = SettingsUtil.SettingsChanged.NONE;

    private Map<Integer, Color> palette = SettingsUtil.PALETTE;

    private boolean includeSessions = true;
    private boolean includeFiles = true;
    private boolean includeMidiChannel = true;


    /***** constructor ***********************************/

    public SettingsSubmodule() {}

    public SettingsSubmodule(boolean includeSessions, boolean includeFiles, boolean includeMidiChannel) {
        this.includeSessions = includeSessions;
        this.includeFiles = includeFiles;
        this.includeMidiChannel = includeMidiChannel;
    }

    /***** Module implementation ***********************************/

    public GridListener getGridListener() { return null; }

    public void redraw() {
        drawSessions();
        drawFiles();
        drawMidiChannel();
        settingsChanged = SettingsUtil.SettingsChanged.NONE;
    }

    public void shutdown() {}


    /***** Sessionizeable implementation *************************************/

    public void selectSession(int index) {
    }

    public void selectPatterns(int firstIndex, int lastIndex) {
    }

    
    /***** listener implementation ***********************************/

    public SettingsUtil.SettingsChanged controlPressed(GridControl control, int velocity) {
        if (SettingsUtil.sessionControls.contains(control)) {
            nextSessionIndex = SettingsUtil.sessionControls.getIndex(control);
            drawSessions();
            return SettingsUtil.SettingsChanged.SELECT_SESSION;

        } else if (loadControls.contains(control)) {
            currentFileIndex = loadControls.getIndex(control);
            drawFiles();
            return SettingsUtil.SettingsChanged.LOAD_FILE;

        } else if (SettingsUtil.saveControls.contains(control)) {
            currentFileIndex = SettingsUtil.saveControls.getIndex(control);
            drawFiles();
            return SettingsUtil.SettingsChanged.SAVE_FILE;

        } else if (SettingsUtil.midiChannelControls.contains(control)) {
            midiChannel = SettingsUtil.midiChannelControls.getIndex(control);
            drawMidiChannel();
            return SettingsUtil.SettingsChanged.SET_MIDI_CHANNEL;
        }

        return SettingsUtil.SettingsChanged.NONE;
    }


    /***** display **************************************/

    public void drawFiles() {
        if (!includeFiles) return;
        for (GridControl control : loadControls.getControls()) {
            Color color = palette.get(COLOR_FILE_LOAD);
            if (control.getIndex() == currentFileIndex) {
                color = palette.get(COLOR_FILE_ACTIVE);
            }
            control.draw(display, color);
        }
        for (GridControl control : saveControls.getControls()) {
            Color color = palette.get(COLOR_FILE_SAVE);
            if (control.getIndex() == currentFileIndex) {
                color = palette.get(COLOR_FILE_ACTIVE);
            }
            control.draw(display, color);
        }
    }

    public void drawSessions() {
        if (!includeSessions) return;
        for (GridControl control : sessionControls.getControls()) {
            Color color = palette.get(COLOR_SESSION);
            if (control.getIndex() == currentSessionIndex) {
                color = palette.get(COLOR_SESSION_ACTIVE);
            } else if (control.getIndex() == nextSessionIndex) {
                color = palette.get(COLOR_SESSION_NEXT);
            }
            control.draw(display, color);
        }
    }

    public void drawMidiChannel() {
        if (!includeMidiChannel) return;
        for (GridControl control : midiChannelControls.getControls()) {
            Color color = palette.get(COLOR_MIDI_CHANNEL);
            if (control.getIndex() == midiChannel) {
                color = palette.get(COLOR_MIDI_CHANNEL_ACTIVE);
            }
            control.draw(display, color);
        }
    }

}
