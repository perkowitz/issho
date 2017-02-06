package net.perkowitz.issho.hachi.modules.minibeat;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 10/24/16.
 */
public class MinibeatMemory {

    @Getter @Setter private int currentSessionIndex = 0;
    @Getter @Setter private int nextSessionIndex = 0;
    @Getter @Setter private int currentPatternIndex = 0;

    @Getter @Setter private int midiChannel = 0;

    @Getter @Setter private boolean someSettingOn = false;


    public MinibeatMemory() {}

}
