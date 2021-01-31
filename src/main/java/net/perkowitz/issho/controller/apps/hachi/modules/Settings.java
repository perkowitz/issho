package net.perkowitz.issho.controller.apps.hachi.modules;

import net.perkowitz.issho.controller.Colors;
import net.perkowitz.issho.controller.Log;
import net.perkowitz.issho.controller.apps.hachi.modules.step.Step;
import net.perkowitz.issho.controller.apps.hachi.modules.step.StepModule;
import net.perkowitz.issho.hachi.Saveable;

import java.awt.*;

public class Settings implements ModuleListener {

    private static final int LOG_LEVEL = Log.OFF;

    private static final int SESSION_ROW = 0;
    private static final int SAVEABLE_LOAD_ROW = 2;
    private static final int SAVEABLE_SAVE_ROW = 3;
    private static final int SAVEABLE_INDEX_COUNT = 8;
    private static final int MIDIMODULE_CHANNEL_ROW = 7;
    private static final int STEP_STAGE_ROW = 5;
    private static final int STEP_STAGE_COLUMN = 0;

    private static final Color SELECTED_COLOR = Colors.WHITE;
    private static final Color SESSION_COLOR = Colors.SKY_BLUE;
    private static final Color SAVEABLE_LOAD_COLOR = Colors.BRIGHT_GREEN;
    private static final Color SAVEABLE_SAVE_COLOR = Colors.BRIGHT_RED;
    private static final Color MIDI_CHANNEL_COLOR = Colors.DARK_GRAY;

    private ModuleController controller;
    private Module module = null;
    private SaveableModule saveableModule = null;
    private MidiModule midiModule = null;
    private StepModule stepModule = null;

    private boolean lengthSettable = false;


    public Settings(ModuleController controller, Module module) {
        this.controller = controller;
        this.module = module;
        if (module instanceof SaveableModule) {
            saveableModule = (SaveableModule) module;
        }
        if (module instanceof MidiModule) {
            midiModule = (MidiModule) module;
        }
        if (module instanceof StepModule) {
            stepModule = (StepModule) module;
        }
    }

    public void draw() {
        controller.clearPads();
        drawSaveable();
        drawMidi();
        drawStep();
    }

    private void drawSaveable() {
        if (saveableModule != null) {
            for (int c = 0; c < SAVEABLE_INDEX_COUNT; c++) {
                controller.setPad(SAVEABLE_LOAD_ROW, c, SAVEABLE_LOAD_COLOR);
                controller.setPad(SAVEABLE_SAVE_ROW, c, SAVEABLE_SAVE_COLOR);
            }
            controller.setPad(SAVEABLE_LOAD_ROW, saveableModule.getFileIndex(), SELECTED_COLOR);
            controller.setPad(SAVEABLE_SAVE_ROW, saveableModule.getFileIndex(), SELECTED_COLOR);
        }
    }

    private void drawMidi() {
        if (midiModule != null) {
            for (int c = 0; c < 16; c++) {
                controller.setPad(MIDIMODULE_CHANNEL_ROW, c, MIDI_CHANNEL_COLOR);
            }
            controller.setPad(MIDIMODULE_CHANNEL_ROW, midiModule.getMidiChannel(), SELECTED_COLOR);
        }
    }

    private void drawStep() {
        if (stepModule != null) {
            Color color = Colors.DIM_PURPLE;
            if (stepModule.getStageCount() != 8) {
                color = SELECTED_COLOR;
            }
            controller.setPad(STEP_STAGE_ROW, STEP_STAGE_COLUMN, color);
        }
    }


    /***** ModuleListener implementation *****/

    public void onPadPressed(int row, int column, int value) {
        if (row == SAVEABLE_LOAD_ROW && saveableModule != null) {
            saveableModule.setFileIndex(column);
            saveableModule.load();
            drawSaveable();

        } else if (row == SAVEABLE_SAVE_ROW && saveableModule != null) {
            saveableModule.setFileIndex(column);
            saveableModule.save();
            drawSaveable();

        } else if (row == MIDIMODULE_CHANNEL_ROW && midiModule != null) {
            midiModule.setMidiChannel(column);
            drawMidi();

        } else if (row == STEP_STAGE_ROW && column == STEP_STAGE_COLUMN && stepModule != null) {
            stepModule.toggleStageCount();
            drawStep();
        }
    }

    public void onPadReleased(int row, int column) { }

    public void onButtonPressed(int group, int index, int value) { }

    public void onButtonReleased(int group, int index) { }

    public void onKnob(int index, int value) { }

}
