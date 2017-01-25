package net.perkowitz.issho.hachi;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.perkowitz.issho.hachi.Chord.NoteMapMode.FLOOR;
import static net.perkowitz.issho.hachi.Chord.NoteMapMode.NEAREST;
import static net.perkowitz.issho.hachi.Chord.NoteMapMode.ROUND_ROBIN;
import static net.perkowitz.issho.hachi.Chord.TransposeMode.LOWEST_NOTE;
import static net.perkowitz.issho.hachi.Chord.TransposeMode.NO_TRANSPOSE;

/**
 * Created by optic on 1/14/17.
 */
public class Chord {

    public enum TransposeMode {
        NO_TRANSPOSE, LOWEST_NOTE, LOWEST_BASE_NOTE, FIRST_NOTE
    }

    public enum NoteMapMode {
        NO_MAP, NEAREST, FLOOR, ROUND_ROBIN
    }

    @Setter private TransposeMode transposeMode = NO_TRANSPOSE;
    @Setter private NoteMapMode noteMapMode = ROUND_ROBIN;
    @Getter private List<Integer> baseNotes;
    private List<Integer> notes;
    @Getter private int transpose = 0;
    private Integer[] noteMap = new Integer[12];



    public Chord() {
        this.notes = Lists.newArrayList();
        this.baseNotes = Lists.newArrayList();
        computeTranspose();
        computeNoteMap();
    }

    public Chord(List<Integer> baseNotes) {
        this.notes = baseNotes;
        this.baseNotes = baseNotes;
        computeTranspose();
        computeNoteMap();
    }


    /***** list management *****************************/

    public void add(Integer note) {
        if (note == null || note < 0) return;
        Integer baseNote = note % 12;
        if (!baseNotes.contains(baseNote)) {
            baseNotes.add(baseNote);
            computeTranspose();
            computeNoteMap();
        }
//        System.out.printf("Chord: add %d, transpose=%d, map=%s\n", note, transpose, Arrays.toString(noteMap));
    }

    public void remove(Integer note) {
        Integer baseNote = note % 12;
        baseNotes.remove(baseNote);
        computeTranspose();
        computeNoteMap();

//        System.out.printf("Chord: remove %d, transpose=%d, map=%s\n", note, transpose, Arrays.toString(noteMap));
    }

    public void clear() {
        baseNotes.clear();
        computeTranspose();
        computeNoteMap();
    }

    public boolean isEmpty() {
        return baseNotes.isEmpty();
    }

    public int size() {
        return baseNotes.size();
    }


    /***** private implementation **********************************/

    private void computeTranspose() {

        switch (transposeMode) {
            case NO_TRANSPOSE:
                transpose = 0;
                break;

            case LOWEST_NOTE:
                Integer t = null;
                for (Integer note : notes) {
                    if (t == null || note < t) {
                        t = note;
                    }
                }
                if (t == null) {
                    transpose = 0;
                } else {
                    transpose = t % 12;
                }
                break;

            case LOWEST_BASE_NOTE:
                t = null;
                for (Integer note : notes) {
                    int baseNote = note % 12;
                    if (t == null || baseNote < t) {
                        t = baseNote;
                    }
                }
                if (t == null) {
                    transpose = 0;
                } else {
                    transpose = t % 12;
                }
                break;

            case FIRST_NOTE:
                if (baseNotes.isEmpty()) {
                    transpose = 0;
                } else {
                    transpose = baseNotes.get(0) % 12;
                }
                break;

            default:
                transpose = 0;
                break;

        }
    }

    private void computeNoteMap() {

        if (isEmpty()) {
            for (int i = 0; i < 12; i++) {
                noteMap[i] = i;
            }
            return;
        }

        switch (noteMapMode) {
            case NEAREST:
                noteMap = nearestNoteMap();
                break;
            case FLOOR:
                noteMap = floorNoteMap();
                break;
            case ROUND_ROBIN:
                noteMap = roundRobinNoteMap();
                break;
            case NO_MAP:
            default:
                break;
        }

    }

    public int mapNote(int note) {
        int octave = note / 12;
        int baseNote = note % 12;
        int mappedBaseNote = noteMap[baseNote];
        return (octave * 12) + mappedBaseNote;
    }

    /***** note map types **********************************/

    private Integer[] nearestNoteMap() {
        Integer[] baseNoteMap = new Integer[12];
        for (int baseNote : baseNotes) {
            baseNoteMap[baseNote] = baseNote;
        }

        Integer[] newNoteMap = new Integer[12];
        for (int n = 0; n < 12; n++) {
            int left = n;
            int right = (n + 1) % 12;
            while (baseNoteMap[left] == null && baseNoteMap[right] == null && right != n) {
                left = (left - 1 + 12) % 12;
                right = (right + 1) % 12;
            }
            if (baseNoteMap[left] != null) {
                newNoteMap[n] = baseNoteMap[left];
            } else if (baseNoteMap[right] != null) {
                newNoteMap[n] = baseNoteMap[right];
            } else {
                newNoteMap[n] = n;
            }
        }

        return newNoteMap;
    }

    private Integer[] floorNoteMap() {
        Integer[] newNoteMap = new Integer[12];
        for (int baseNote : baseNotes) {
            newNoteMap[baseNote] = baseNote;
        }

        for (int n = 0; n < 12; n++) {
            if (newNoteMap[n] == null) {
                int left = (n - 1 + 12) % 12;
                while (newNoteMap[left] == null && left != n) {
                    left = (left - 1 + 12) % 12;
                }
                if (newNoteMap[left] != null) {
                    newNoteMap[n] = newNoteMap[left];
                } else {
                    newNoteMap[n] = n;
                }
            }
        }

        return newNoteMap;
    }

    private Integer[] roundRobinNoteMap() {
        Integer[] newNoteMap = new Integer[12];
        for (int n = 0; n < 12; n++) {
            newNoteMap[n] = baseNotes.get(n % baseNotes.size());
        }
        return newNoteMap;
    }


    /***** overrides **********************************/

    @Override
    public String toString() {
        return "Chord:" + baseNotes.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof Chord) {
            Chord chord = (Chord)object;
            return this.baseNotes.equals(chord.baseNotes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }




}
