package net.perkowitz.issho.hachi.modules.para;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by optic on 9/29/17.
 */
public class ParaControllerStep {

    @Getter @Setter private boolean enabled;
    @Getter @Setter private int low;
    @Getter @Setter private int high;


    public ParaControllerStep() {
        enabled = false;
        low = 64;
        high = 80;
    }

    public void setValue(int value) {
        low = value;
        high = value;
    }

    public void setValue(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public int getValue() {
        return (int)(Math.random() * (high - low)) + low;
    }


    /***** static methods ***************************************************/

    public static ParaControllerStep copy(ParaControllerStep controllerStep) {
        ParaControllerStep newControllerStep = new ParaControllerStep();
        newControllerStep.enabled = controllerStep.enabled;
        newControllerStep.low = controllerStep.low;
        newControllerStep.high = controllerStep.high;
        return newControllerStep;
    }

}
