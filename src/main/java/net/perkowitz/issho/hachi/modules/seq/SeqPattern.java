package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

/**
 * Created by optic on 2/25/17.
 */
public class SeqPattern implements MemoryObject {

    private static int[] notes = new int[] { 49, 37, 39, 51, 42, 44, 46, 50,
            36, 38, 40, 41, 43, 45, 47, 48 };

    @Getter @Setter private int index;
    @Getter private List<SeqTrack> tracks = Lists.newArrayList();
    @Getter private List<SeqControlTrack> controlTracks = Lists.newArrayList();
    @Getter private List<SeqPitchStep> pitchTrack = Lists.newArrayList();


    public SeqPattern() {}

    public SeqPattern(int index) {

        this.index = index;

        // create each track
        for (int i = 0; i < SeqUtil.TRACK_COUNT; i++) {
            tracks.add(new SeqTrack(i, notes[i]));
        }

        // create the control tracks
        for (int i = 0; i < SeqUtil.CONTROL_TRACK_COUNT; i++) {
            controlTracks.add(new SeqControlTrack(i));
        }

        // create the pitch track
        for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
            pitchTrack.add(new SeqPitchStep(i));
        }

    }


    @JsonIgnore public SeqTrack getTrack(int index) {
        return tracks.get(index);
    }

    @JsonIgnore public SeqStep getStep(int trackIndex, int stepIndex) {
        return getTrack(trackIndex).getStep(stepIndex);
    }

    @JsonIgnore public SeqControlTrack getControlTrack(int index) { return controlTracks.get(index); }

    public String toString() {
        return String.format("SeqPattern:%02d", index);
    }

    public SeqPitchStep getPitchStep(int stepIndex) {
        return pitchTrack.get(stepIndex);
    }

    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (SeqTrack track : tracks) {
            objects.add(track);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof SeqTrack) {
            SeqTrack track = (SeqTrack) memoryObject;
            track.setIndex(index);
            tracks.set(index, track);
        } else {
            System.out.printf("Cannot put object %s of type %s in object %s\n", memoryObject, memoryObject.getClass().getSimpleName(), this);
        }
    }


    public boolean nonEmpty() {
        for (MemoryObject object : list()) {
            if (object.nonEmpty()) {
                return true;
            }
        }
        return false;
    }

    public MemoryObject clone() {
        return SeqPattern.copy(this, this.index);
    }

    public String render() {
        return MemoryUtil.countRender(this);
    }


    /***** static methods **************************/

    public static SeqPattern copy(SeqPattern pattern, int newIndex) {
        SeqPattern newPattern = new SeqPattern();
        newPattern.setIndex(newIndex);
        try {
            for (int i = 0; i < SeqUtil.TRACK_COUNT; i++) {
                newPattern.tracks.add(SeqTrack.copy(pattern.tracks.get(i), i));
            }
            List<SeqPitchStep> pitchTrack = Lists.newArrayList();
            for (int i = 0; i < SeqUtil.STEP_COUNT; i++) {
                SeqPitchStep seqPitchStep = SeqPitchStep.copy(pattern.getPitchStep(i), i);
                pitchTrack.add(seqPitchStep);
            }
            newPattern.pitchTrack = pitchTrack;
            // TODO copy control tracks and pitch track
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPattern;
    }


}
