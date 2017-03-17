package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.step.Stage.Marker.None;

/**
 * Created by optic on 12/2/16.
 */
public class Stage implements MemoryObject {

    public enum Marker {
        None, Note, Sharp, Flat, OctaveUp, OctaveDown, VolumeUp, VolumeDown, Longer, Repeat, Skip, Slide, Tie
    }

    public static int MAX_MARKERS = 8;

    @Getter @Setter private int index;
    @Getter private Marker[] markers = new Marker[MAX_MARKERS];
    @Getter private List<Step> steps = Lists.newArrayList();


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
        markers[index] = marker;
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

        return String.format("Stage:%02d:%s", index, string);
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


    /***** static methods *******************************/

    public static Stage copy(Stage stage) {
        Stage newStage = new Stage(stage.getIndex());
        for (int i = 0; i < MAX_MARKERS; i++) {
            newStage.markers[i] = stage.getMarker(i);
        }
        newStage.steps = stage.getSteps();
        return newStage;
    }

}
