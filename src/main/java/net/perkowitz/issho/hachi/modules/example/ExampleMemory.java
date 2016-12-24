package net.perkowitz.issho.hachi.modules.example;

import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.modules.step.Scale;
import net.perkowitz.issho.hachi.modules.step.StepPattern;
import net.perkowitz.issho.hachi.modules.step.StepSession;

/**
 * Created by optic on 10/24/16.
 */
public class ExampleMemory {

    @Getter @Setter private int currentSessionIndex = 0;
    @Getter @Setter private int nextSessionIndex = 0;
    @Getter @Setter private int currentPatternIndex = 0;

    @Getter @Setter private int midiChannel = 0;

    @Getter @Setter private boolean someSettingOn = false;


    public ExampleMemory() {}

}
