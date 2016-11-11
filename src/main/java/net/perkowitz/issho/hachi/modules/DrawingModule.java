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

/**
 * Created by optic on 9/12/16.
 */
public class DrawingModule extends BasicModule implements Clockable {

    // memory sizes
    private static int FRAMES_PER_PATTERN = 8;
    private static int PATTERNS_PER_SESSION = 8;
    private static int SESSIONS_PER_MEMORY = 8;
    private static int GRID_X_SIZE = 8;
    private static int GRID_Y_SIZE = 8;

    private static final String FILENAME_PREFIX = "drawing-";
    private static final String FILENAME_SUFFIX = ".json";

    // button & color assignments
    private static GridButton.Side PALETTE_SIDE = Bottom;
    private static GridButton.Side FRAME_SIDE = Right;
    private static GridColor OFF_COLOR = Color.DARK_GRAY;
    private static GridColor ON_COLOR = Color.BRIGHT_BLUE;
    private static GridButton currentColorButton = GridButton.at(Left, 7);
    private static GridButton saveButton = GridButton.at(Left, 6);

    // drawing colors
    private GridColor[] palette = { Color.OFF, Color.WHITE, Color.DARK_GRAY,
            Color.BRIGHT_BLUE, Color.BRIGHT_GREEN, Color.BRIGHT_ORANGE, Color.BRIGHT_RED, Color.BRIGHT_YELLOW};
    private GridColor currentColor = palette[1];

    private Memory memory;
    private ObjectMapper objectMapper = new ObjectMapper();


    /***** constructor ****************************************/

    public DrawingModule() {

//        memory = new Memory();
        load(FILENAME_PREFIX + "0" + FILENAME_SUFFIX);

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

        for (int c = 0; c < 8; c++) {
            display.setButton(GridButton.at(PALETTE_SIDE, c), palette[c]);

            if (currentFrameIndex == c) {
                display.setButton(GridButton.at(FRAME_SIDE, c), ON_COLOR);
            } else {
                display.setButton(GridButton.at(FRAME_SIDE, c), OFF_COLOR);
            }

        }

        display.setButton(currentColorButton, currentColor);
        display.setButton(saveButton, OFF_COLOR);

    }


    /***** GridListener interface ****************************************/

    public void onPadPressed(GridPad pad, int velocity) {
        memory.getCurrentFrame().set(pad, (Color)currentColor);
        display.setPad(pad, currentColor);
    }

    public void onPadReleased(GridPad pad) {

    }

    public void onButtonPressed(GridButton button, int velocity) {

        if (button.getSide() == PALETTE_SIDE) {
            selectColor(palette[button.getIndex()]);
            currentColor = palette[button.getIndex()];
        } else if (button.getSide() == FRAME_SIDE) {
            selectFrame(button.getIndex());
        } else if (button.equals(saveButton)) {
            save(FILENAME_PREFIX + "0" + FILENAME_SUFFIX);
        } else if (button.equals(currentColorButton)) {

        }

    }

    public void onButtonReleased(GridButton button) {

    }


    /***** Clockable implementation ****************************************/

    public void start(boolean restart) {
    }

    public void stop() {
    }

    public void tick(boolean andReset) {
        int frameIndex = (memory.getCurrentFrameIndex() + 1) % 8;
        if (andReset) {
            frameIndex = 0;
        }
        selectFrame(frameIndex);
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

        private void set(int x, int y, Color color) {
            pixels[x][y] = color;
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
            return sessions.get(index);
        }

        @JsonIgnore
        public Session currentSession() {
            return sessions.get(currentSessionIndex);
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
