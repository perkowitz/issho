package net.perkowitz.issho.hachi;


import net.perkowitz.issho.hachi.modules.Module;

import java.util.List;

/**
 * Created by optic on 10/24/16.
 */
public interface Chordable extends Module {

    public void setChordNotes(List<Integer> notes);

}
