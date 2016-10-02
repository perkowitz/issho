package net.perkowitz.issho.hachi.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Track {

    @Getter @Setter private static int stepCount = 16;
    @Getter private Step[] steps;

    @Getter private int index;
    @Getter @Setter private boolean selected = false;
    @Getter @Setter private boolean playing = false;

    @Getter @Setter private int midiChannel = 0;
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
        return "Track:" + getIndex();
    }


}
