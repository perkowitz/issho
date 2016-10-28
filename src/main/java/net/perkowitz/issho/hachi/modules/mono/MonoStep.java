package net.perkowitz.issho.hachi.modules.mono;

import lombok.Getter;
import lombok.Setter;

import static net.perkowitz.issho.hachi.modules.mono.MonoUtil.Gate.HOLD;

/**
 * Created by optic on 10/24/16.
 */
public class MonoStep {

    private static int DEFAULT_NOTE = 60;
    private static int DEFAULT_VELOCITY = 100;
    private static MonoUtil.Gate DEFAULT_MODE = HOLD;

    @Getter private int index;

    @Getter @Setter private int note;
    @Getter @Setter private int velocity;
    @Getter @Setter private int length;
    @Getter @Setter private MonoUtil.Gate mode;
    @Getter @Setter private boolean enabled = false;
    @Getter @Setter private boolean selected = false;


    public MonoStep(int index) {
        this.index = index;
        this.note = DEFAULT_NOTE;
        this.velocity = DEFAULT_VELOCITY;
        this.length = 1;
        this.mode = DEFAULT_MODE;
        this.enabled = false;
        this.selected = false;

        this.note = DEFAULT_NOTE + index;
        this.velocity = 50 + index * 4;
        if (index % 2 == 0) {
            this.enabled = true;
        }
    }


}
