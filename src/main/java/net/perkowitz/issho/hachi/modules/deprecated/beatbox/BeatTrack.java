package net.perkowitz.issho.hachi.modules.deprecated.beatbox;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class BeatTrack implements MemoryObject {

    @Getter @Setter int index;
    @Getter private int noteNumber;
    @Getter private int midiChannel;
    @Getter @Setter private boolean playing = false;
    @Getter private List<BeatStep> steps = Lists.newArrayList();


    public BeatTrack() {}

    public BeatTrack(int index, int noteNumber) {
        this.index = index;
        this.noteNumber = noteNumber;
        for (int i = 0; i < BeatUtil.STEP_COUNT; i++) {
            steps.add(new BeatStep(i));
        }
    }

    public BeatStep getStep(int index) {
        return steps.get(index);
    }

    public String toString() {
        return String.format("BeatTrack:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        return Lists.newArrayList();
    }

    public void put(int index, MemoryObject memoryObject) {
        System.out.printf("Cannot put object %s of type %s in object %s\n", memoryObject, memoryObject.getClass().getSimpleName(), this);
    }


    public boolean nonEmpty() {
        for (BeatStep step : steps) {
            if (step.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return BeatTrack.copy(this, this.index);
    }

    public String render() {

        String string = "";

        for (BeatStep step : steps) {
            if (step.isEnabled()) {
                string += "O";
            } else {
                string += ".";
            }
        }

        return(MemoryUtil.countRender(this, string));
    }


    /***** static methods **************************/

    public static BeatTrack copy(BeatTrack track, int newIndex) {
        BeatTrack newTrack = new BeatTrack(newIndex, track.getNoteNumber());
        for (int i = 0; i < BeatUtil.STEP_COUNT; i++) {
            newTrack.steps.set(i, BeatStep.copy(track.steps.get(i), i));
        }
        return newTrack;
    }

}
