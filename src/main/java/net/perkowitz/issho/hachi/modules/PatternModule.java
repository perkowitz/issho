package net.perkowitz.issho.hachi.modules;


/**
 * Created by optic on 10/24/16.
 */
public interface PatternModule extends Module {

    public void selectSession(int index);
    public void selectPatterns(int firstIndex, int lastIndex);

}
