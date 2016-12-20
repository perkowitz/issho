package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.step.Step.Mode.*;

/**
 * Created by optic on 12/2/16.
 */
public class Step {

    public enum Mode {
        Play, Tie, Rest, Slide
    }

    public static int DEFAULT_OCTAVE = 5;
    public static int DEFAULT_VELOCITY = 80;
    public static int VELOCITY_INCREMENT = 16;
    public static int[] DEFAULT_SCALE = { 0, 2, 4, 5, 7, 9, 11, 12 };   // C major scale

    @Setter private static int[] scale = DEFAULT_SCALE;

    @Getter private Mode mode = Rest;
    @Getter private int note = 60;
    @Getter private int velocity = 80;


    public Step() {}

    public Step(Mode mode, int note, int velocity) {
        this.mode = mode;
        this.note = note;
        this.velocity = velocity;
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
        int baseNote = 0;
        int octave = DEFAULT_OCTAVE;
        int sharps = 0;
        boolean slide = false;
        int longer = 0;
        int repeats = 1; // we always start with 1
        int ties = 0;

        for (int i = 0; i < markers.length; i++) {
            switch (markers[i]) {
                case Note:
                    baseNote = scale[i];
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

        int note = octave * 12 + baseNote + sharps;
        if (ties > 0) {
            mode = Tie;
        } else if (mode == Play && slide) {
            mode = Slide;
        }

        for (int r = 0; r < repeats; r ++) {
            steps.add(new Step(mode, note, velocity));
            for (int t = 0; t < longer; t ++) {
                steps.add(new Step(Tie, note, velocity));
            }
        }

        return steps;
    }


}
