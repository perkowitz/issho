package net.perkowitz.issho.hachi.modules.rhythm.models;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.modules.mono.MonoPattern;
import net.perkowitz.issho.hachi.modules.mono.MonoStep;

import java.util.List;

import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.Gate.REST;

/**
 * Created by optic on 7/9/16.
 */
public class Pattern implements MemoryObject {

    // lay out the note numbers across the tracks like a keyboard octave
    private static int[] noteNumbers = new int[] { 49, 37, 39, 51, 42, 44, 46, 50,
                                                   36, 38, 40, 41, 43, 45, 47, 48 };

    @Getter @Setter private int index;
    @Getter @Setter private boolean selected = false;
    @Getter @Setter private boolean playing = false;
    @Getter @Setter private boolean chained = false;

    @Getter @Setter private static int trackCount = 16;
    @Getter private Track[] tracks;

    // only used for deserializing JSON; Pattern should always be created with an index
    public Pattern() {}

    public Pattern(int index) {

        this.index = index;

        this.tracks = new Track[trackCount];
        for (int i = 0; i < trackCount; i++) {
            tracks[i] = new Track(i);
            tracks[i].setMidiChannel(9);
            tracks[i].setNoteNumber(noteNumbers[i]);
        }

    }

    public Track getTrack(int index) {
        return tracks[index % trackCount];
    }

    public void selectTrack(int index) {
        for (int i = 0; i < trackCount; i++) {
            tracks[i].setSelected(false);
        }
        tracks[index].setSelected(true);
    }

    public void copyMutes(Pattern pattern) {
        for (int i = 0; i < trackCount; i++) {
            tracks[i].setEnabled(pattern.getTrack(i).isEnabled());
        }
    }

    @Override
    public String toString() {

        String string = "";
        for (Track track : tracks) {
            if (!string.equals("")) {
                string += "-";
            }
            string += track;
        }

        return String.format("Pattern:%02d:%s", index, string);
    }


    /***** MemoryObject implementation ***********************/

    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        for (Track track : tracks) {
            objects.add(track);
        }
        return objects;
    }

    public void put(int index, MemoryObject memoryObject) {
        if (memoryObject instanceof Track) {
            Track track = (Track) memoryObject;
            track.setIndex(index);
            tracks[index] = track;
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
        return Pattern.copy(this, this.index);
    }

    public String render() { return toString(); }


    /***** static methods ***********************/

    public static Pattern copy(Pattern pattern, int newIndex) {
        Pattern newPattern = new Pattern(newIndex);
        for (int index = 0; index < trackCount; index++) {
            newPattern.tracks[index] = Track.copy(pattern.tracks[index], index);
        }
        return newPattern;
    }

}
