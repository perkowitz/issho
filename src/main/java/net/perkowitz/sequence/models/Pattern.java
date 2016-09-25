package net.perkowitz.sequence.models;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 7/9/16.
 */
public class Pattern {

    // lay out the note numbers across the tracks like a keyboard octave
    private static int[] noteNumbers = new int[] { 49, 37, 39, 51, 42, 44, 46, 50,
                                                   36, 38, 40, 41, 43, 45, 47, 48 };

    @Getter private int index;
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
        return "Pattern:" + getIndex();
    }

}
