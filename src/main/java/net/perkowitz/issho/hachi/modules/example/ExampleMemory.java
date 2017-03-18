package net.perkowitz.issho.hachi.modules.example;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.perkowitz.issho.hachi.MemoryObject;
import net.perkowitz.issho.hachi.modules.mono.MonoSession;
import net.perkowitz.issho.hachi.modules.step.Scale;
import net.perkowitz.issho.hachi.modules.step.StepPattern;
import net.perkowitz.issho.hachi.modules.step.StepSession;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public class ExampleMemory {

    @Getter @Setter private int currentSessionIndex = 0;
    @Getter @Setter private int nextSessionIndex = 0;
    @Getter @Setter private int currentPatternIndex = 0;

//    private ExampleSession[] sessions;

    @Getter @Setter private int midiChannel = 0;

    @Getter @Setter private boolean someSettingOn = false;


    public ExampleMemory() {}


    /***** MemoryObject implementation ***********************/

    /***********************************************************************
     * MemoryObject implementation
     *
     * implementing MemoryObject for the modules various data objects allows them to
     * be examined and changed by the MemoryApp, a tool for managing module data
     *
     */

    /**
     * returns a list of things in memory (usually sessions)
     * @return
     */
    public List<MemoryObject> list() {
        List<MemoryObject> objects = Lists.newArrayList();
        return objects;
    }

    /**
     * update the value of a particular MemoryObject in this object's list
     * @param index
     * @param memoryObject
     */
    public void put(int index, MemoryObject memoryObject) {
//        if (memoryObject instanceof ExampleSession) {
//            ExampleSession session = (ExampleSession) memoryObject;
//            session.setIndex(index);
//            sessions[index] = session;
//        } else {
//            System.out.printf("Cannot put object %s of type %s in object %s\n", memoryObject, memoryObject.getClass().getSimpleName(), this);
//        }
    }

    /**
     * check to see if the MemoryObject has any data in it or is empty (or in an initialized state)
     * @return
     */
    public boolean nonEmpty() {
        for (MemoryObject object : list()) {
            if (object.nonEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * These aren't usually defined for a full memory, but should be for sessions, patterns, etc
     * @return
     */
    public int getIndex() { return 0; }
    public void setIndex(int index) {}
    public MemoryObject clone() {
        return null;
    }

    public String render() { return toString(); }



}
