package net.perkowitz.issho.hachi;

/**
 * Created by optic on 11/12/16.
 */
public interface Saveable {

    public void setFilePrefix(String filePrefix);
    public String getFilePrefix();
    public void save(int index);
    public void load(int index);

}
