package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;
import java.util.Random;

import static net.perkowitz.issho.hachi.modules.step.Stage.Marker.*;

/**
 * Created by optic on 12/2/16.
 */
public class Stage implements MemoryObject {

    private static final java.util.Random RANDOM = new Random();

    public enum Marker {
        None, Note, Sharp, Flat, OctaveUp, OctaveDown, VolumeUp, VolumeDown, Longer, Repeat, Skip, Slide, Tie, Random
    }

    private static Marker[] randomMarkers = { Note, OctaveUp, OctaveDown, Longer, Repeat, Skip };

    public static int MAX_MARKERS = 8;

    @Getter @Setter private int index;
    @Getter private Marker[] markers = new Marker[MAX_MARKERS];
    @Getter private List<Step> steps = Lists.newArrayList();
    @Getter private int randomCount = 0;


    public Stage() {}

    public Stage(Integer index) {
        this.index = index;
        for (int i = 0; i < MAX_MARKERS; i++) {
            markers[i] = None;
        }
        steps = Step.fromMarkers(markers);
    }


    public Marker getMarker(int index) {
        return markers[index];
    }

    public void putMarker(int index, Marker marker) {
        if (marker == Random && markers[index] != Random) {
            randomCount++;
        } else if (marker != Random && markers[index] == Random) {
            randomCount--;
        }
        markers[index] = marker;
        computeSteps();
    }

    public void computeSteps() {
        steps = Step.fromMarkers(markers);
    }

    public List<Integer> findMarker(Marker marker) {
        List<Integer> indices = Lists.newArrayList();
        for (int i = 0; i < markers.length; i++) {
            if (marker == markers[i]) {
                indices.add(i);
            }
        }
        return indices;
    }


    /***** overrides *****************************/

    @Override
    public String toString() {
        return String.format("Stage:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        return Lists.newArrayList();
    }

    public void put(int index, MemoryObject memoryObject) {
        System.out.println("Cannot add a MemoryObject to a Stage");
    }

    public boolean nonEmpty() {
        for (Marker marker : markers) {
            if (marker != None) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return Stage.copy(this);
    }

    public String render() {

        String string = "";
        for (Marker marker : markers) {
            switch (marker) {
                case None:
                    string += ".";
                    break;
                case Note:
                    string += "O";
                    break;
                case Sharp:
                    string += "#";
                    break;
                case Flat:
                    string += "b";
                    break;
                case OctaveUp:
                    string += "^";
                    break;
                case OctaveDown:
                    string += "v";
                    break;
                case VolumeUp:
                    string += ">";
                    break;
                case VolumeDown:
                    string += "<";
                    break;
                case Longer:
                    string += "=";
                    break;
                case Repeat:
                    string += "!";
                    break;
                case Skip:
                    string += "X";
                    break;
                case Slide:
                    string += "-";
                    break;
                case Tie:
                    string += "_";
                    break;

            }
        }

        return MemoryUtil.countRender(this, string);
    }


    /***** static methods *******************************/

    public static Stage copy(Stage stage) {
        Stage newStage = new Stage(stage.getIndex());
        for (int i = 0; i < MAX_MARKERS; i++) {
            newStage.markers[i] = stage.getMarker(i);
        }
        newStage.steps = stage.getSteps();
        newStage.randomCount = stage.randomCount;
        return newStage;
    }

    public static Marker randomMarker() {
        return randomMarkers[RANDOM.nextInt(randomMarkers.length)];
    }
}
