package net.perkowitz.issho.hachi.modules.mono2;

import lombok.Getter;
import lombok.Setter;

import static net.perkowitz.issho.hachi.modules.mono2.MonoUtil.Gate.HOLD;

/**
 * Created by optic on 10/24/16.
 */
public class MonoStep {

    private static int DEFAULT_NOTE = 60;
    private static int DEFAULT_VELOCITY = 100;
    private static MonoUtil.Gate DEFAULT_MODE = HOLD;

    @Getter private int index;

    @Getter @Setter private int octaveNote;
    @Getter @Setter private int octave;
    @Getter @Setter private int velocity;
    @Getter @Setter private int length;
    @Getter @Setter private MonoUtil.Gate mode;
    @Getter @Setter private boolean enabled = false;
    @Getter @Setter private boolean selected = false;


    public MonoStep(int index) {
        this.index = index;
        this.octaveNote = DEFAULT_NOTE % 12;
        this.octave = DEFAULT_NOTE / 12;
        this.velocity = DEFAULT_VELOCITY;
        this.length = 1;
        this.mode = DEFAULT_MODE;
        this.enabled = false;
        this.selected = false;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }

    public int getNote() {
        return octave * 12 + octaveNote;
    }

    public void setNote(int note) {
        octaveNote = note % 12;
        octave = note / 12;
    }

}
