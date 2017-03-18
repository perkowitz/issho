package net.perkowitz.issho.hachi.modules.rhythm.models;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;

import java.util.List;

/**
 * Created by optic on 7/9/16.
 */
public class Track implements MemoryObject {

    @Getter @Setter private static int stepCount = 16;
    @Getter private Step[] steps;

    @Getter @Setter private int index;
    @Getter @Setter private boolean selected = false;
    @Getter @Setter private boolean playing = false;

    @Getter @Setter private int midiChannel = 9;
    @Getter @Setter private int noteNumber = 60;
    @Getter @Setter private boolean enabled = true;


    // only used for deserializing JSON; Track should always be created with an index
    public Track() {}

    public Track(int index) {

        this.index = index;
        this.steps = new Step[stepCount];
        for (int i = 0; i < stepCount; i++) {
            steps[i] = new Step(i);
            steps[i].setVelocity(95);
        }

    }

    public Step getStep(int index) {
        return steps[index % stepCount];
    }

    public void setStep(int position, boolean on, int velocity) {
        steps[position].setOn(on);
        steps[position].setVelocity(velocity);
    }

    public void setStep(int position, boolean on) {
        steps[position].setOn(on);
    }

    public void setStep(int position, int velocity) {
        steps[position].setVelocity(velocity);
    }

    @Override
    public String toString() {
        return String.format("RhythmTrack:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        return Lists.newArrayList();
    }

    public void put(int index, MemoryObject memoryObject) {
        System.out.printf("Cannot put object %s of type %s in object %s\n", memoryObject, memoryObject.getClass().getSimpleName(), this);
    }


    public boolean nonEmpty() {
        for (Step step : steps) {
            if (step.isOn()) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return Track.copy(this, this.index);
    }

    public String render() {

        String binary = "";
        for (int i = stepCount-1; i >= 0; i--) {
            if (steps[i].isOn()) {
                binary += "1";
            } else {
                binary += "0";
            }
        }
        int decimal = Integer.parseInt(binary,2);
        String hexStr = Integer.toString(decimal,16);

        return(hexStr);
    }


    /***** static methods ***********************/

    public static Track copy(Track track, int newIndex) {
        Track newTrack = new Track(newIndex);
        newTrack.midiChannel = track.midiChannel;
        newTrack.noteNumber = track.noteNumber;     // TODO we might want to set this separately when we copy to a diff track #
        for (int index = 0; index < stepCount; index++) {
            newTrack.steps[index] = Step.copy(track.steps[index], index);
        }
        return newTrack;
    }

}
