package net.perkowitz.issho.hachi;

import java.util.List;

/**
 * Created by optic on 3/13/17.
 */
public interface MemoryObject {

    public List<MemoryObject> list();
    public boolean nonEmpty();
}
