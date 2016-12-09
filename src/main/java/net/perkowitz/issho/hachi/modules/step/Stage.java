package net.perkowitz.issho.hachi.modules.step;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.step.Stage.Marker.None;

/**
 * Created by optic on 12/2/16.
 */
public class Stage {

    public enum Marker {
        None, Note, Sharp, Flat, OctaveUp, OctaveDown, VolumeUp, VolumeDown, Longer, Repeat, Skip, Slide, Tie
    }

    public static int MAX_MARKERS = 8;

    @Getter @Setter
    private Integer index = null;
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
        return "Stage:" + index;
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
