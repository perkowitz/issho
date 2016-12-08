package net.perkowitz.issho.hachi.modules.step;

import lombok.Setter;

/**
 * Created by optic on 12/4/16.
 */
public class Scale {

    public static int MIN_SCALE_SIZE = 8;
    public static int[] DEFAULT_SCALE = {0, 2, 4, 5, 7, 9, 11, 12};   // C major scale

    @Setter private int[] scale = DEFAULT_SCALE;


    public Scale(int[] scale) {

        if (scale.length >= MIN_SCALE_SIZE) {
            this.scale = scale;
        } else {
            // if scale is smaller than min size, pad it out with same notes up an octave (or more)
            this.scale = new int[MIN_SCALE_SIZE];
            int inc = 0;
            for (int i = 0; i < MIN_SCALE_SIZE; i++) {
                if (i > 0 && i % scale.length == 0) {
                    inc += 12;
                }
                int note = scale[i % scale.length] + inc;
                this.scale[i] = note;
            }
        }



    }


    public int get(int index) {
        return scale[index % scale.length];
    }


    /***** static factories *********************************/

    public static Scale cMajor() {
        int[] notes = {0, 2, 4, 5, 7, 9, 11, 12};
        return new Scale(notes);
    }

    public static Scale cMinor() {
        int[] notes = {0, 2, 3, 5, 7, 9, 10, 12};
        return new Scale(notes);
    }


}