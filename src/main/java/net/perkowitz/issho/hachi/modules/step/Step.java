package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

import static net.perkowitz.issho.hachi.modules.step.Step.Mode.*;
import static net.perkowitz.issho.hachi.modules.step.Step.MultiNoteMode.*;

/**
 * Created by optic on 12/2/16.
 */
public class Step {

    public enum Mode {
        Play, Tie, Rest, Slide
    }

    public enum MultiNoteMode {
        CHOOSE_HIGH, CHOOSE_LOW, CHOOSE_RANDOM,
        ORDER_LOW_TO_HIGH, ORDER_HIGH_TO_LOW, ORDER_RANDOM
    }

    public static int DEFAULT_OCTAVE = 5;
    public static int DEFAULT_VELOCITY = 80;
    public static int VELOCITY_INCREMENT = 32;
    public static int[] DEFAULT_SCALE = { 0, 2, 4, 5, 7, 9, 11, 12 };   // C major scale

    public static MultiNoteMode multiNoteMode = ORDER_LOW_TO_HIGH; // TODO: fully implement other modes, make them settable

    @Getter @Setter private static int[] scale = DEFAULT_SCALE;

    @Getter private Mode mode = Rest;
    @Getter private int note = 60;
    @Getter private int velocity = 80;
    @Getter @Setter private Integer highlightedIndex = null; // when the step is played, which pad to highlight


    public Step() {}

    public Step(Mode mode, int note, int velocity) {
        this.mode = mode;
        this.note = note;
        this.velocity = velocity;
    }

    public Step(Mode mode, int note, int velocity, int highlightedIndex) {
        this.mode = mode;
        this.note = note;
        this.velocity = velocity;
        this.highlightedIndex = highlightedIndex;
    }


    /***** overrides *****************************/

    @Override
    public String toString() {
        return "Step: " + mode + ":" + note +  ":" + velocity;
    }


    /***** static factory ********************************/

    public static List<Step> fromMarkers(Stage.Marker[] markers) {

        List<Step> steps = Lists.newArrayList();

        Mode mode = Rest;
        int velocity = DEFAULT_VELOCITY;
        List<Integer> baseNotes = Lists.newArrayList();
        List<Integer> noteIndices = Lists.newArrayList();
        int octave = DEFAULT_OCTAVE;
        int sharps = 0;
        boolean slide = false;
        int longer = 0;
        int repeats = 1; // we always start with 1
        int ties = 0;

        for (int i = 0; i < markers.length; i++) {
            Stage.Marker m = markers[i];
            if (m == Stage.Marker.Random) {
                m = Stage.randomMarker();
            }
            switch (m) {
                case Note:
                    baseNotes.add(scale[i]);
                    noteIndices.add(i);
                    mode = Play;
                    break;
                case Sharp:
                    sharps++;
                    break;
                case Flat:
                    sharps--;
                    break;
                case OctaveUp:
                    octave = Math.min(Math.max(octave + 1, 0), 9);
                    break;
                case OctaveDown:
                    octave = Math.min(Math.max(octave - 1, 0), 9);
                    break;
                case Longer:
                    longer++;
                    break;
                case Repeat:
                    repeats++;
                    break;
                case VolumeUp:
                    velocity = Math.min(Math.max(velocity + VELOCITY_INCREMENT, 0), 127);
                    break;
                case VolumeDown:
                    velocity = Math.min(Math.max(velocity - VELOCITY_INCREMENT, 0), 127);
                    break;
                case Slide:
                    slide = true;
                    break;
                case Tie:
                    ties++;
                    break;
                case Skip:
                    return steps;
            }
        }

        Integer chosenNote = null;
        Integer chosenIndex = null;
        if (baseNotes.size() > 0) {
            switch (multiNoteMode) {
                case ORDER_LOW_TO_HIGH:
                    // notes are already processed low-to-high
                    break;
                case ORDER_HIGH_TO_LOW:
                    // notes are already processed low-to-high
                    Collections.reverse(baseNotes);
                    Collections.reverse(noteIndices);
                    break;
                case ORDER_RANDOM:
                    Collections.shuffle(baseNotes);
                    // noteIndices = shrug emoji
                    break;
                case CHOOSE_LOW:
                    chosenNote = baseNotes.get(0);  // basenotes are added low-to-high
                    chosenIndex = noteIndices.get(0);
                    break;
                case CHOOSE_HIGH:
                    chosenNote = baseNotes.get(baseNotes.size() - 1);  // basenotes are added low-to-high
                    chosenIndex = noteIndices.get(baseNotes.size() - 1);
                    break;
                case CHOOSE_RANDOM:
                    int r = (int) (Math.random() * baseNotes.size());
                    chosenNote = baseNotes.get(r);
                    chosenIndex = noteIndices.get(r);
                    break;
            }
        }

        if (chosenNote != null) {
            baseNotes.clear();
            baseNotes.add(chosenNote);
            noteIndices.clear();
            noteIndices.add(chosenIndex);
        }

        if (mode == Rest) {
            for (int r = 0; r < repeats; r++) {
                steps.add(new Step(mode, 0, velocity));
                for (int t = 0; t < longer; t++) {
                    steps.add(new Step(Tie, 0, velocity));
                }
            }

        } else {
            for (int i = 0; i < baseNotes.size(); i++) {
                Integer bNote = baseNotes.get(i);
                Integer highlight = noteIndices.get(i);
                int note = octave * 12 + bNote + sharps;

                if (ties > 0) {
                    mode = Tie;
                } else if (mode == Play && slide) {
                    mode = Slide;
                }

                for (int r = 0; r < repeats; r++) {
                    steps.add(new Step(mode, note, velocity, highlight));
                    for (int t = 0; t < longer; t++) {
                        steps.add(new Step(Tie, note, velocity, highlight));
                    }
                }
            }
        }

        return steps;
    }


}
