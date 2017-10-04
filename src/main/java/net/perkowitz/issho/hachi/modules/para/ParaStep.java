package net.perkowitz.issho.hachi.modules.para;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static net.perkowitz.issho.hachi.modules.para.ParaUtil.Gate.PLAY;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.MAX_NOTE;
import static net.perkowitz.issho.hachi.modules.para.ParaUtil.MIN_NOTE;

/**
 * Created by optic on 10/24/16.
 */
public class ParaStep {

    private static int DEFAULT_NOTE = 60;
    private static int DEFAULT_VELOCITY = 100;
    private static int DEFAULT_CONTROL = 100;
    private static ParaUtil.Gate DEFAULT_GATE = PLAY;

    @Getter @Setter private int index;

    @Getter @Setter private Set<Integer> notes = Sets.newHashSet();
    @Getter @Setter private int velocity;
    @Getter @Setter private Integer controlA;
    @Getter @Setter private ParaUtil.Gate gate;
    @Getter @Setter private boolean enabled = true;
    @Getter @Setter private boolean selected = false;
    @Getter @Setter private ParaControllerStep[] controllerSteps = new ParaControllerStep[ParaMemory.CONTROLLER_COUNT];


    public ParaStep() {}

    public ParaStep(int index) {
        this.index = index;
        this.velocity = DEFAULT_VELOCITY;
        this.gate = DEFAULT_GATE;
        this.controlA = DEFAULT_CONTROL;
        this.enabled = false;
        this.selected = false;
        for (int i = 0; i < controllerSteps.length; i++) {
            controllerSteps[i] = new ParaControllerStep();
        }
    }

    public void addNote(Integer note) {
        notes.add(note);
    }

    public void setNote(Integer note) {
        notes.clear();
        notes.add(note);
    }

    public void addNotes(Collection<Integer> notes) {
        for (Integer note : notes) {
            this.notes.add(note);
        }
    }

    public void removeNote(Integer note) {
        notes.remove(note);
    }

    public void toggleNote(Integer note) {
        if (notes.contains(note)) {
            notes.remove(note);
        } else {
            notes.add(note);
        }
    }

    public void clearNotes() {
        notes.clear();
    }

    public int getControllerValue(int index) {
        if (index >=0 && index < controllerSteps.length && controllerSteps[index] != null) {
            return controllerSteps[index].getValue();
        } else {
            return 0;
        }
    }

    public void setControllerValue(int index, Integer value) {
        if (index >=0 && index < controllerSteps.length && controllerSteps[index] != null) {
            controllerSteps[index].setValue(value);
        }
    }

    public void setControllerValue(int index, Integer low, Integer high) {
        if (index >=0 && index < controllerSteps.length && controllerSteps[index] != null) {
            controllerSteps[index].setValue(low, high);
        }
    }

    public boolean getControllerEnabled(int index) {
        if (index >=0 && index < controllerSteps.length && controllerSteps[index] != null) {
            return controllerSteps[index].isEnabled();
        }
        return false;
    }

    public void setControllerEnabled(int index, boolean enabled) {
        if (index >=0 && index < controllerSteps.length && controllerSteps[index] != null) {
            controllerSteps[index].setEnabled(enabled);
        }
    }

    public void toggleControllerEnabled(int index) {
        if (index >=0 && index < controllerSteps.length && controllerSteps[index] != null) {
            controllerSteps[index].setEnabled(!getControllerEnabled(index));
        }
    }

    public void transpose(int steps) {
        Set<Integer> transposedNotes = Sets.newHashSet();
        for (Integer note : notes) {
            int transposedNote = note + steps;
            while (transposedNote < MIN_NOTE) {
                transposedNote += 12;
            }
            while (transposedNote > MAX_NOTE) {
                transposedNote -= 12;
            }
            transposedNotes.add(transposedNote);
        }
        notes = transposedNotes;
    }

    public void toggleEnabled() {
        enabled = !enabled;
    }

    public String toString() {
        if (enabled) {
            return "ParaStep:" + index + ":O";
        } else {
            return "ParaStep:" + index + ":.";
        }
    }

    public String render() { return toString(); }


    /***** static methods **************************/

    public static ParaStep copy(ParaStep step, int newIndex) {
        ParaStep newStep = new ParaStep();
        newStep.index = newIndex;
        newStep.addNotes(step.getNotes());
        newStep.velocity = step.velocity;
        newStep.gate = step.gate;
        newStep.enabled = step.enabled;
        newStep.selected = step.selected;

        ParaControllerStep[] newControllerSteps = new ParaControllerStep[step.controllerSteps.length];
        for (int i = 0; i < step.controllerSteps.length; i++) {
            newControllerSteps[i] = ParaControllerStep.copy(step.controllerSteps[i]);
        }
        newStep.controllerSteps = newControllerSteps;

        return newStep;
    }
}
