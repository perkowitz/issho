package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.STEP_COUNT;
import static net.perkowitz.issho.hachi.modules.seq.SeqUtil.SeqMode.BEAT;

/**
 * Created by optic on 2/25/17.
 */
public class SeqPattern implements MemoryObject {

    public static int JUMP_TO_OFF = 0;
    public static int JUMP_TO_RANDOM = 1;

    @Getter @Setter private int index;
    @Getter private List<SeqTrack> tracks = Lists.newArrayList();
    @Getter private List<SeqControlTrack> controlTracks = Lists.newArrayList();
    @Getter private List<SeqPitchStep> pitchTrack = Lists.newArrayList();
    private SeqControlTrack jumpTrack;


    public SeqPattern() {}

    public SeqPattern(int index, SeqUtil.SeqMode mode) {

        this.index = index;

        // for beat mode, create multiple tracks
        if (mode == BEAT) {
            for (int i = 0; i < SeqUtil.BEAT_TRACK_COUNT; i++) {
                tracks.add(new SeqTrack(i, SeqUtil.BEAT_TRACK_NOTES[i]));
            }
        } else {
            tracks.add(new SeqTrack(0, null));
        }

        // create the control tracks
        for (int i = 0; i < SeqUtil.CONTROL_TRACK_COUNT; i++) {
            controlTracks.add(new SeqControlTrack(i));
        }

        // create the pitch track
        for (int i = 0; i < STEP_COUNT; i++) {
            pitchTrack.add(new SeqPitchStep(i));
        }

        jumpTrack = new SeqControlTrack(-1);
    }


    @JsonIgnore public SeqTrack getTrack(int index) {
        if (index >= 0 && index < tracks.size()) {
            return tracks.get(index);
        }
        return null;
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

    @JsonIgnore
    public void setJump(int index, boolean jump) {
        if (index < 0 || index > STEP_COUNT) {
            return;
        }
        if (jumpTrack == null || jumpTrack.getStep(index) == null) {
            jumpTrack = new SeqControlTrack(-1);
        }
        SeqControlStep step = jumpTrack.getStep(index);
        if (jump) {
            step.setValue(JUMP_TO_RANDOM);
        } else {
            step.setValue(JUMP_TO_OFF);
        }
    }

    public void toggleJump(int index) {
        if (index < 0 || index > STEP_COUNT) {
            return;
        }
        if (jumpTrack == null || jumpTrack.getStep(index) == null) {
            jumpTrack = new SeqControlTrack(-1);
        }
        SeqControlStep step = jumpTrack.getStep(index);
        if (step.getValue() == JUMP_TO_RANDOM) {
            step.setValue(JUMP_TO_OFF);
        } else {
            step.setValue(JUMP_TO_RANDOM);
        }
    }

    @JsonIgnore
    public boolean getJump(int index) {
        if (index < 0 || index > STEP_COUNT) {
            return false;
        }
        if (jumpTrack == null || jumpTrack.getStep(index) == null) {
            jumpTrack = new SeqControlTrack(-1);
        }
        return (jumpTrack.getStep(index).getValue() == JUMP_TO_RANDOM);
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
            for (int i = 0; i < pattern.tracks.size(); i++) {
                newPattern.tracks.add(SeqTrack.copy(pattern.tracks.get(i), i));
            }
            for (int i = 0; i < pattern.controlTracks.size(); i++) {
                newPattern.controlTracks.add(SeqControlTrack.copy(pattern.controlTracks.get(i), i));
            }
            List<SeqPitchStep> pitchTrack = Lists.newArrayList();
            for (int i = 0; i < STEP_COUNT; i++) {
                SeqPitchStep seqPitchStep = SeqPitchStep.copy(pattern.getPitchStep(i), i);
                pitchTrack.add(seqPitchStep);
            }
            newPattern.pitchTrack = pitchTrack;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPattern;
    }


}
