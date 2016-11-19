package net.perkowitz.issho.hachi.modules;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.devices.GridButton;
import net.perkowitz.issho.devices.GridColor;
import net.perkowitz.issho.devices.GridPad;
import net.perkowitz.issho.devices.launchpadpro.Color;
import net.perkowitz.issho.hachi.Clockable;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.File;
import java.util.List;

import static net.perkowitz.issho.devices.GridButton.Side.*;
import static net.perkowitz.issho.hachi.modules.DrawingModule.Mode.EDIT;
import static net.perkowitz.issho.hachi.modules.DrawingModule.Mode.PLAY;

/**
 * Created by optic on 9/12/16.
 */
public class DrawingModule extends BasicModule implements Clockable {

    public enum Mode {
        PLAY, EDIT
    }

    // memory sizes
    private static int FRAMES_PER_PATTERN = 64;
    private static int PATTERNS_PER_SESSION = 1;
    private static int SESSIONS_PER_MEMORY = 1;
    private static int GRID_X_SIZE = 8;
    private static int GRID_Y_SIZE = 8;

    private static final String FILENAME_SUFFIX = ".json";

    // buttons
    private static GridButton.Side PALETTE_SIDE = Bottom;
    private static GridButton.Side FRAME_SIDE = Right;
    private static GridButton playModeButton = GridButton.at(Left, 4);
    private static GridButton editModeButton = GridButton.at(Left, 5);
    private static GridButton saveButton = GridButton.at(Left, 6);
    private static GridButton currentColorButton = GridButton.at(Left, 7);

    // colors
    private static GridColor COLOR_OFF = Color.OFF;
    private static GridColor COLOR_INACTIVE = Color.DIM_RED;
    private static GridColor COLOR_ACTIVE = Color.BRIGHT_RED;
    private GridColor[] palette = { Color.OFF, Color.WHITE, Color.DARK_GRAY,
            Color.BRIGHT_BLUE, Color.BRIGHT_GREEN, Color.BRIGHT_ORANGE, Color.BRIGHT_RED, Color.BRIGHT_YELLOW};
    private GridColor currentColor = palette[1];

    private Memory memory;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String filePrefix = "drawing";
    private Mode currentMode = PLAY;
    private int loopStart = 0;
    private int loopLength = 8;
    private int tickCount = 0;


    /***** constructor ****************************************/

    public DrawingModule(String filePrefix) {

//        memory = new Memory();
        this.filePrefix = filePrefix;
        load(this.filePrefix + "-0" + FILENAME_SUFFIX);

    }


    /***** Module interface ****************************************/

    @Override
    public void redraw() {

        Frame currentFrame = memory.getCurrentFrame();
        int currentFrameIndex = memory.getCurrentFrameIndex();

        for (int x = 0; x < GRID_X_SIZE; x++) {
            for (int y = 0; y < GRID_Y_SIZE; y++) {
                display.setPad(GridPad.at(x, y), currentFrame.get(x, y));
            }
        }

        display.setButton(saveButton, COLOR_INACTIVE);

        if (currentMode == EDIT) {
            for (int c = 0; c < 8; c++) {
                display.setButton(GridButton.at(PALETTE_SIDE, c), palette[c]);

                if (currentFrameIndex == loopStart + c) {
                    display.setButton(GridButton.at(FRAME_SIDE, c), COLOR_ACTIVE);
                } else {
                    display.setButton(GridButton.at(FRAME_SIDE, c), COLOR_INACTIVE);
                }
            }

            display.setButton(currentColorButton, currentColor);
            display.setButton(playModeButton, COLOR_INACTIVE);
            display.setButton(editModeButton, COLOR_ACTIVE);

        } else if (currentMode == PLAY) {
            for (int index = 0; index < 8; index++) {
                if (index == loopStart / 8) {
                    display.setButton(GridButton.at(FRAME_SIDE, index), COLOR_INACTIVE);
                } else {
                    display.setButton(GridButton.at(FRAME_SIDE, index), COLOR_OFF);
                }
                if (index + 1 == loopLength / 8) {
                    display.setButton(GridButton.at(PALETTE_SIDE, index), COLOR_INACTIVE);
                } else {
                    display.setButton(GridButton.at(PALETTE_SIDE, index), COLOR_OFF);
                }

            }
            display.setButton(playModeButton, COLOR_ACTIVE);
            display.setButton(editModeButton, COLOR_INACTIVE);
            display.setButton(currentColorButton, COLOR_OFF);
        }

    }


    /***** GridListener interface ****************************************/

    public void onPadPressed(GridPad pad, int velocity) {
        memory.getCurrentFrame().set(pad, (Color)currentColor);
        display.setPad(pad, currentColor);
    }

    public void onPadReleased(GridPad pad) {

    }

    public void onButtonPressed(GridButton button, int velocity) {

        if (button.equals(saveButton)) {
            save(filePrefix + "-" + "0" + FILENAME_SUFFIX);
        } else if (button.equals(currentColorButton)) {

        } else if (button.equals(editModeButton)) {
            currentMode = EDIT;
            redraw();
        } else if (button.equals(playModeButton)) {
            currentMode = PLAY;
            redraw();
        } else if (currentMode == EDIT) {
            if (button.getSide() == PALETTE_SIDE) {
                selectColor(palette[button.getIndex()]);
                currentColor = palette[button.getIndex()];
            } else if (button.getSide() == FRAME_SIDE) {
                selectFrame(loopStart + button.getIndex());
            }
        } else if (currentMode == PLAY) {
            if (button.getSide() == PALETTE_SIDE) {
                loopLength = (button.getIndex() + 1) * 8;
                redraw();
            } else if (button.getSide() == FRAME_SIDE) {
                loopStart = button.getIndex() * 8;
                loopLength = 8;
                redraw();
            }
        }

    }

    public void onButtonReleased(GridButton button) {

    }


    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
        tickCount = 0;
    }

    public void stop() {
        tickCount = 0;
    }

    public void tick(boolean andReset) {
        int frameIndex = (loopStart + (tickCount % loopLength)) % FRAMES_PER_PATTERN;
        if (andReset) {
            frameIndex = loopStart;
        }
//        System.out.printf("Tick: reset=%s, tickCount=%d, frameIndex=%d\n", andReset, tickCount, frameIndex);
        selectFrame(frameIndex);
        tickCount++;
    }


    /***** private implementation ****************************************/

    private void selectColor(GridColor color) {
        currentColor = color;
        display.setButton(currentColorButton, color);
    }

    private void clear(Frame frame) {
        frame.clear();
    }

    private void selectFrame(int index) {
        memory.getCurrentPattern().setCurrentFrameIndex(index);
        redraw();
    }

    private void save(String filename) {

        try {

            File file = new File(filename);
            if (file.exists()) {
                // make a backup, but will overwrite any previous backups
                Files.copy(file, new File(filename + ".backup"));
            }

            objectMapper.writeValue(file, memory);
//            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(memory);
//            System.out.println(json);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void load(String filename) {

        try {
            File file = new File(filename);

            if (file.exists()) {
                memory = objectMapper.readValue(file, Memory.class);
            } else {
                memory = new Memory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /***** private class ****************************************/

    private static class Frame {

        @Getter @Setter private Color[][] pixels;

        public Frame() {
            pixels = new Color[GRID_X_SIZE][GRID_Y_SIZE];
            clear();
        }

        private GridColor get(int x, int y) {
            return pixels[x][y];
        }

        private void set(GridPad pad, Color color) {
            pixels[pad.getX()][pad.getY()] = color;
        }

        private void clear() {
            for (int x = 0; x < GRID_X_SIZE; x++) {
                for (int y = 0; y < GRID_Y_SIZE; y++) {
                    pixels[x][y] = Color.OFF;
                }
            }
        }
    }

    private static class Pattern {

        @JsonSerialize @JsonDeserialize private List<Frame> frames;
        @Getter @Setter private int currentFrameIndex;

        public Pattern() {
            frames = Lists.newArrayList();
            for (int f = 0; f < FRAMES_PER_PATTERN; f++) {
                frames.add(new Frame());
            }
            currentFrameIndex = 0;
        }

        public Frame get(int index) {
            return frames.get(index);
        }

        @JsonIgnore
        public Frame getCurrentFrame() {
            return frames.get(currentFrameIndex);
        }


    }

    private static class Session {

        @JsonSerialize @JsonDeserialize private List<Pattern> patterns;
        @Getter @Setter private int currentPatternIndex;

        public Session() {
            patterns = Lists.newArrayList();
            for (int f = 0; f < PATTERNS_PER_SESSION; f++) {
                patterns.add(new Pattern());
            }
            currentPatternIndex = 0;
        }

        public Pattern get(int index) {
            return patterns.get(index);
        }

        @JsonIgnore
        public Pattern getCurrentPattern() {
            return patterns.get(currentPatternIndex);
        }


    }

    private static class Memory {

        @JsonSerialize @JsonDeserialize private List<Session> sessions;
        @Getter @Setter private int currentSessionIndex;

        public Memory() {
            sessions = Lists.newArrayList();
            for (int f = 0; f < SESSIONS_PER_MEMORY; f++) {
                sessions.add(new Session());
            }
            currentSessionIndex = 0;
        }

        public Session get(int index) {
            return sessions.get(index % SESSIONS_PER_MEMORY);
        }

        @JsonIgnore
        public Session currentSession() {
            return sessions.get(currentSessionIndex % SESSIONS_PER_MEMORY);
        }

        @JsonIgnore
        public Pattern getCurrentPattern() {
            return currentSession().getCurrentPattern();
        }

        @JsonIgnore
        public Frame getCurrentFrame() {
            return getCurrentPattern().getCurrentFrame();
        }

        @JsonIgnore
        public int getCurrentPatternIndex() {
            return currentSession().getCurrentPatternIndex();
        }

        @JsonIgnore
        public int getCurrentFrameIndex() {
            return getCurrentPattern().getCurrentFrameIndex();
        }

    }




}
