package net.perkowitz.issho.hachi;

import java.util.List;

/**
 * MemoryObject
 *
 * A piece of module data; a thing that can be part of a module's memory.
 * Because modules can have very different memory structures (session/pattern/track/step vs session/pattern/step for example),
 * this is a very generic object interface that just has listable children and an index, and can be cloned.
 * This interface allows for the implementation of a simple memory management tool where memory contents
 * can be listed and copied.
 *
 *
 * Created by optic on 3/13/17.
 */
public interface MemoryObject {

    public List<MemoryObject> list();
    public void put(int index, MemoryObject memoryObject);
    public boolean nonEmpty();
    public MemoryObject clone();
    public int getIndex();
    public void setIndex(int index);
    public String render();

}
