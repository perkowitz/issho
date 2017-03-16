package net.perkowitz.issho.hachi;

import java.util.List;

/**
 * Created by optic on 3/13/17.
 */
public interface MemoryObject {

    public List<MemoryObject> list();
    public void put(int index, MemoryObject memoryObject);
    public boolean nonEmpty();
    public MemoryObject clone();
    public int getIndex();
    public void setIndex(int index);
}
