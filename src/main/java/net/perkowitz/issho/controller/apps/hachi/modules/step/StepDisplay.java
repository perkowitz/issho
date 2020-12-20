package net.perkowitz.issho.controller.apps.hachi.modules.step;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.controller.apps.hachi.Palette;
import net.perkowitz.issho.controller.apps.hachi.modules.ModuleController;
import net.perkowitz.issho.controller.elements.Button;
import net.perkowitz.issho.controller.elements.Element;
import net.perkowitz.issho.controller.elements.ElementSet;
import net.perkowitz.issho.controller.elements.Pad;

import java.awt.*;

import static net.perkowitz.issho.controller.apps.hachi.modules.step.Stage.Marker.None;
import static net.perkowitz.issho.controller.apps.hachi.modules.step.StepUtil.*;


/**
 * Created by optic on 10/25/16.
 */
public class StepDisplay {

    @Setter private ModuleController controller;
    @Getter @Setter private int currentFileIndex = 0;
    @Getter @Setter private Palette palette = Palette.DEFAULT;

    @Setter private boolean settingsView = false;
    private boolean muted = false;
    @Setter private boolean randomOrder = false;
    @Setter private boolean displayAltControls = false;
    @Setter private Stage.Marker currentMarker = None;


    public StepDisplay(ModuleController controller) {
        this.controller = controller;
    }


    public void draw(StepMemory memory) {
        drawMarkers();
        drawStages(memory);
        drawLeftControls();
        drawPatterns(memory);
    }


    /***** ***********************************************/

    public void initialize() {
        controller.clear();
        controller.flush();
    }

    public void drawActiveNote(int row, int column) {
        if (settingsView) return;
        controller.setPad(row, column, StepUtil.ACTIVE_NOTE_COLOR);
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
        drawLeftControls();
    }

    /***** draw main view ****************************************/

    public void drawMarkers() {
        if (settingsView) return;
        if (!displayAltControls) {
            for (Element element : StepUtil.markerPaletteMap.keySet()) {
                Stage.Marker marker = StepUtil.markerPaletteMap.get(element);
                Color color = StepUtil.MARKER_COLORS.get(marker);
                controller.setButton(element.getGroup(), element.getIndex(), color);
                Log.delay();
            }
        } else {
            for (Element element : StepUtil.markerElements.elements())  {
                controller.setButton(element.getGroup(), element.getIndex(), Colors.OFF);
            }
            controller.setButton(shiftLeftElement.getGroup(), shiftLeftElement.getIndex(), palette.Off);
            controller.setButton(shiftRightElement.getGroup(), shiftRightElement.getIndex(), palette.Off);
            controller.setButton(randomOrderElement.getGroup(), randomOrderElement.getIndex(), palette.Off);
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
        ElementSet padColumn = StepUtil.stageColumns[stageIndex];
        for (int markerIndex = 0; markerIndex < 8; markerIndex++) {
            Stage.Marker marker = stage.getMarker(markerIndex);
            Pad pad = (Pad)padColumn.get(markerIndex);
            controller.setPad(7-pad.getRow(), pad.getColumn(), StepUtil.MARKER_COLORS.get(marker));
        }
    }

    public void drawLeftControls() {
        controller.setButton(currentMarkerDisplayElement.getGroup(), currentMarkerDisplayElement.getIndex(), StepUtil.MARKER_COLORS.get(currentMarker));
        drawButton(StepUtil.altControlsElement, displayAltControls);
        drawButton(StepUtil.copyPatternElement, false);
        drawButton(StepUtil.saveElement, false);
    }

    public void drawPatterns(StepMemory memory) {
        for (Element element : StepUtil.patternElements.elements()) {
            controller.setButton(element.getGroup(), element.getIndex(), Colors.OFF);
        }
        int index = memory.getCurrentPatternIndex() % StepUtil.patternElements.size();
        Button button = (Button)patternElements.get(index);
        controller.setButton(button.getGroup(), button.getIndex(), palette.On);
    }

    public void drawButton(Element element, boolean isOn) {
        Color color = isOn ? palette.On : palette.Key;
        if (muted) {
            color = isOn ? palette.Off : palette.KeyDim;
        }
        controller.setButton(element.getGroup(), element.getIndex(), color);
    }

}
