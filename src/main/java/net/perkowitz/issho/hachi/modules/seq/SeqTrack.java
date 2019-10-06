package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class SeqTrack implements MemoryObject {

    @Getter @Setter int index;
    @Getter private Integer noteNumber = null; // this is set for a beat track, which just triggers one note; null for melody tracks
    @Getter private Integer midiChannel = null;
    @Getter @Setter private boolean playing = false;
    @Getter private List<SeqStep> steps = Lists.newArrayList();


    public SeqTrack() {}

    public SeqTrack(int index, Integer noteNumber) {
        this.index = index;
        this.noteNumber = noteNumber;
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            steps.add(new SeqStep(i));
        }
    }

    // TODO: add a current step marker and a way to advance/reset, to support ratchet etc

    public SeqStep getStep(int index) {
        return steps.get(index);
    }

    public String toString() {
        return String.format("SeqTrack:%02d", index);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        return Lists.newArrayList();
    }

    public void put(int index, MemoryObject memoryObject) {
        System.out.printf("Cannot put object %s of type %s in object %s\n", memoryObject, memoryObject.getClass().getSimpleName(), this);
    }


    public boolean nonEmpty() {
        for (SeqStep step : steps) {
            if (step.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return SeqTrack.copy(this, this.index);
    }

    public String render() {

        String string = "";

        for (SeqStep step : steps) {
            if (step.isEnabled()) {
                string += "O";
            } else {
                string += ".";
            }
        }

        return(MemoryUtil.countRender(this, string));
    }


    /***** static methods **************************/

    public static SeqTrack copy(SeqTrack track, int newIndex) {
        SeqTrack newTrack = new SeqTrack(newIndex, track.getNoteNumber());
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            newTrack.steps.set(i, SeqStep.copy(track.steps.get(i), i));
        }
        return newTrack;
    }

}
