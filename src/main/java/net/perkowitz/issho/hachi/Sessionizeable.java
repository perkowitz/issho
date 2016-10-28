package net.perkowitz.issho.hachi;


import net.perkowitz.issho.hachi.modules.Module;

/**
 * Created by optic on 10/24/16.
 */
public interface Sessionizeable extends Module {

    public void selectSession(int index);
    public void selectPatterns(int firstIndex, int lastIndex);

}
