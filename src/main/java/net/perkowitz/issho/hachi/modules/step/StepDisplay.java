package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.*;
import net.perkowitz.issho.devices.launchpadpro.Color;

import java.util.Map;

import static net.perkowitz.issho.hachi.modules.step.Stage.Marker.None;
import static net.perkowitz.issho.hachi.modules.step.StepUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class StepDisplay {

    @Setter private GridDisplay display;
    @Getter @Setter private Map<Integer, Color> palette = StepUtil.PALETTE;
    @Getter @Setter private Map<Stage.Marker, Color> markerPalette = StepUtil.MARKER_COLORS;
    @Getter @Setter private int currentFileIndex = 0;

    @Setter private boolean settingsView = false;
    @Setter private boolean isMuted = false;
    @Setter private boolean randomOrder = false;
    @Setter private boolean displayAltControls = false;
    @Setter private Stage.Marker currentMarker = None;


    public StepDisplay(GridDisplay display) {
        this.display = display;
    }

    
    public void redraw(StepMemory memory) {
        drawMarkers();
        drawStages(memory);
        drawLeftControls();
        drawPatterns(memory);
    }


    /***** ***********************************************/

    public void initialize() {
        display.initialize(true, Sets.newHashSet(GridButton.Side.Bottom, GridButton.Side.Right));
    }

    public void drawActiveNote(GridPad control) {
        if (settingsView) return;
        display.setPad(control, StepUtil.ACTIVE_NOTE_COLOR);
    }

    /***** draw main view ****************************************/

    public void drawMarkers() {
        if (settingsView) return;
        if (!displayAltControls) {
            for (GridControl control : markerPaletteMap.keySet()) {
                control.draw(display, markerPalette.get(markerPaletteMap.get(control)));
            }
        } else {
            markerControls.draw(display, Color.OFF);
            drawControl(StepUtil.shiftLeftControl, false);
            drawControl(StepUtil.shiftRightControl, false);
            drawControl(StepUtil.randomOrderControl, randomOrder);
        }
    }

    public void drawStages(StepMemory memory) {
        if (settingsView) return;
        for (int i = 0; i < StepPattern.STAGE_COUNT; i++) {
            drawStage(memory, i);
        }
    }

    public void drawStage(StepMemory memory, int stageIndex) {
        if (settingsView) return;
        Stage stage = memory.currentPattern().getStage(stageIndex);
        GridControlSet padColumn = StepUtil.stageColumns[stageIndex];
        for (int markerIndex = 0; markerIndex < 8; markerIndex++) {
            Stage.Marker marker = stage.getMarker(markerIndex);
            GridControl control = padColumn.get(7 - markerIndex);
            control.draw(display, markerPalette.get(marker));
        }
    }

    public void drawLeftControls() {
        drawControl(StepUtil.muteControl, isMuted);
        drawControl(StepUtil.settingsControl, settingsView);
        drawControl(StepUtil.saveControl, false);
//        drawControl(StepUtil.panicControl, false);
        StepUtil.currentMarkerDisplayControl.draw(display, markerPalette.get(currentMarker));
        drawControl(StepUtil.altControlsControl, displayAltControls);
        drawControl(StepUtil.savePatternControl, false);
    }

    public void drawPatterns(StepMemory memory) {
        patternControls.draw(display, Color.OFF);
        int index = memory.getCurrentPatternIndex() % patternControls.size();
        patternControls.get(index).draw(display, palette.get(COLOR_ON));
    }

    public void drawControl(GridControl control, boolean isOn) {
        if (isOn) {
            control.draw(display, palette.get(COLOR_ON));
        } else {
            control.draw(display, palette.get(COLOR_OFF));
        }
    }

    


}
