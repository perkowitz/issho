package net.perkowitz.issho.hachi.modules.seq;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.MemoryUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by optic on 2/25/17.
 */
public class SeqPattern implements MemoryObject {

    private static int[] notes = new int[] { 49, 37, 39, 51, 42, 44, 46, 50,
            36, 38, 40, 41, 43, 45, 47, 48 };

    @Getter @Setter private int index;
    @Getter private List<SeqTrack> tracks = Lists.newArrayList();

    private Map<String, SeqControlTrack> controlTracksMap = Maps.newHashMap();


    public SeqPattern() {}

    public SeqPattern(int index, List<Integer> defaultControllers, Map<Integer, List<Integer>> controllersByTrack) {

        createControllers(defaultControllers, controllersByTrack);

        this.index = index;
        for (int i = 0; i < SeqUtil.TRACK_COUNT; i++) {
            List<Integer> trackControllers = controllersByTrack.get(i);
            if (trackControllers == null) {
                trackControllers = defaultControllers;
            }
            List<SeqControlTrack> controlTracks = Lists.newArrayList();
            for (int controllerNumber : trackControllers) {
                controlTracks.add(controlTracksMap.get(SeqControlTrack.getControllerString(controllerNumber)));
            }

            tracks.add(new SeqTrack(i, notes[i], controlTracks));
        }


    }

    private void createControllers(List<Integer> defaultControllers, Map<Integer, List<Integer>> controllersByTrack) {

        List<List<Integer>> trackControllerNumbers = Lists.newArrayList();
        trackControllerNumbers.add(defaultControllers);
        trackControllerNumbers.addAll(controllersByTrack.values());

        for (List<Integer> controllerNumbers : controllersByTrack.values()) {
            for (Integer controllerNumber : controllerNumbers) {
                String controlString = SeqControlTrack.getControllerString(controllerNumber);
                if (controlTracksMap.get(controlString) == null) {
                    controlTracksMap.put(controlString, new SeqControlTrack(controllerNumber));
                }
            }
        }

    }

    public SeqTrack getTrack(int index) {
        return tracks.get(index);
    }

    public SeqStep getStep(int trackIndex, int stepIndex) {
        return getTrack(trackIndex).getStep(stepIndex);
    }

    public String toString() {
        return String.format("SeqPattern:%02d", index);
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
        SeqPattern newPattern = new SeqPattern(newIndex);
        try {
            for (int i = 0; i < SeqUtil.TRACK_COUNT; i++) {
                newPattern.tracks.set(i, SeqTrack.copy(pattern.tracks.get(i), i));
            }
            // TODO copy control tracks
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newPattern;
    }


}
