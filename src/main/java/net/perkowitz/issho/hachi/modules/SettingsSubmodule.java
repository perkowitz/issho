package net.perkowitz.issho.hachi.modules;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Sessionizeable;

import java.util.List;
import java.util.Map;

import static net.perkowitz.issho.hachi.modules.SettingsSubmodule.Operation.CLEAR;
import static net.perkowitz.issho.hachi.modules.SettingsSubmodule.Operation.COPY;
import static net.perkowitz.issho.hachi.modules.SettingsUtil.*;


/**
 * Created by optic on 12/7/16.
 */
public class SettingsSubmodule extends BasicModule implements Module, Sessionizeable {

    public enum Operation {
        COPY, CLEAR
    }

    @Getter @Setter private int currentSessionIndex = 0;
    @Getter @Setter private int nextSessionIndex = 0;
    @Getter @Setter private int currentFileIndex = 0;
    @Getter @Setter private int midiChannel = 0;
    @Getter @Setter private int swingOffset = 0;
    @Getter private SettingsUtil.SettingsChanged settingsChanged = SettingsUtil.SettingsChanged.NONE;

    private Map<Integer, Color> palette = SettingsUtil.PALETTE;

    private boolean includeSessions = true;
    private boolean includeFiles = true;
    private boolean includeMidiChannel = true;
    private boolean includeSwing = false;

    private boolean performingOperation = false;
    @Getter private Operation operationPerformed = null;
    private List<GridControl> operationTargets = Lists.newArrayList();
    @Getter private Integer copyFromSessionIndex = null;
    @Getter private Integer copyToSessionIndex = null;
    @Getter private Integer copyToFileIndex = null;
    @Getter private Integer clearSessionIndex = null;


    /***** constructor ***********************************/

    public SettingsSubmodule() {
    }

    public SettingsSubmodule(boolean includeSessions, boolean includeFiles, boolean includeMidiChannel, boolean includeSwing) {
        this.includeSessions = includeSessions;
        this.includeFiles = includeFiles;
        this.includeMidiChannel = includeMidiChannel;
        this.includeSwing = includeSwing;
    }

    /***** Module implementation ***********************************/

    public GridListener getGridListener() {
        return null;
    }

    public void redraw() {
        display.initialize();
        drawSessions();
        drawFiles();
        drawMidiChannel();
        drawSwing();
        settingsChanged = SettingsUtil.SettingsChanged.NONE;
    }

    public void shutdown() {
    }


    /***** Sessionizeable implementation *************************************/

    public void selectSession(int index) {
    }

    public void selectPatterns(int firstIndex, int lastIndex) {
    }


    /***** listener implementation ***********************************/

    public SettingsUtil.SettingsChanged controlPressed(GridControl control, int velocity) {

        if (performingOperation && !operationControls.contains(control)) {
            // needs to be first because it redefines the meaning of other control presses
            if (sessionControls.contains(control)) {
                int index = sessionControls.getIndex(control);
                operationTargets.add(new GridControl(control, index));
            }
            if (saveControls.contains(control)) {
                int index = saveControls.getIndex(control);
                operationTargets.add(new GridControl(control, index));
            }
            return SettingsChanged.NONE;

        } else if (SettingsUtil.sessionControls.contains(control)) {
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

        } else if (SettingsUtil.swingControls.contains(control)) {
            swingOffset = SettingsUtil.swingControls.getIndex(control) - 3;
            drawSwing();
            return SettingsChanged.SET_SWING;

        } else if (SettingsUtil.operationControls.contains(control)) {

            // if we hit another operation button in the midst of an operation, the old one is replaced
            performingOperation = true;
            if (control.equals(copyControl)) {
                operationPerformed = COPY;
            } else if (control.equals(clearControl)) {
                operationPerformed = CLEAR;
            } else {
                operationPerformed = null;
            }
            operationTargets.clear();
            copyFromSessionIndex = copyToSessionIndex = copyToFileIndex = clearSessionIndex = null;
            drawOperationsControls(operationPerformed);

            return SettingsChanged.OPERATION_STARTED;
        }

        return SettingsUtil.SettingsChanged.NONE;
    }

    public SettingsUtil.SettingsChanged controlReleased(GridControl control) {

        copyFromSessionIndex = copyToSessionIndex = copyToFileIndex = clearSessionIndex = null;

        if (control.equals(clearControl)) {
            // for a CLEAR to happen, you must press CLEAR, press exactly one session, then release CLEAR
            if (performingOperation == true && operationPerformed == CLEAR
                    && operationTargets.size() == 1
                    && sessionControls.contains(operationTargets.get(0))) {
                clearSessionIndex = operationTargets.get(0).getIndex();
                performingOperation = false;
                drawOperationsControls(null);
                return SettingsChanged.CLEAR_SESSION;
            }
            performingOperation = false;
            drawOperationsControls(null);

        } else if (control.equals(copyControl)) {
            // for a COPY to happen, you must press COPY, press exactly two sessions, then release COPY
            if (performingOperation == true && operationPerformed == COPY
                    && operationTargets.size() == 2
                    && sessionControls.contains(operationTargets.get(0))
                    && sessionControls.contains(operationTargets.get(1))) {
                copyFromSessionIndex = operationTargets.get(0).getIndex();
                copyToSessionIndex = operationTargets.get(1).getIndex();
                performingOperation = false;
                drawOperationsControls(null);
                return SettingsChanged.COPY_SESSION;
            } else if (performingOperation == true && operationPerformed == COPY
                    // OR you press COPY, press a session, then a file save, then another session, then release COPY
                    && operationTargets.size() == 3
                    && sessionControls.contains(operationTargets.get(0))
                    && saveControls.contains(operationTargets.get(1))
                    && sessionControls.contains(operationTargets.get(2))) {
                copyFromSessionIndex = operationTargets.get(0).getIndex();
                copyToFileIndex = operationTargets.get(1).getIndex();
                copyToSessionIndex = operationTargets.get(2).getIndex();
                performingOperation = false;
                drawOperationsControls(null);
                return SettingsChanged.COPY_SESSION_TO_FILE;
            }
            performingOperation = false;
            drawOperationsControls(null);
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
        drawOperationsControls(null);
    }

    public void drawOperationsControls(Operation operation) {
        if (!includeSessions) return;
        for (GridControl control : operationControls.getControls()) {
            if (control.equals(copyControl) && operation == COPY) {
                control.draw(display, palette.get(COLOR_ON));
            } else if (control.equals(clearControl) && operation == CLEAR){
                control.draw(display, palette.get(COLOR_ON));
            } else {
                control.draw(display, palette.get(COLOR_OFF));
            }
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

    public void drawSwing() {
        if (!includeSwing) return;
        for (GridControl control : swingControls.getControls()) {
            Color color = palette.get(COLOR_SWING);
            if (control.getIndex() == swingOffset + 3) {
                color = palette.get(COLOR_SWING_ACTIVE);
            }
            control.draw(display, color);
        }
    }

}
