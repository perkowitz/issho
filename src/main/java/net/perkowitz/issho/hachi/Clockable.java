package net.perkowitz.issho.hachi;

/**
 * Created by optic on 9/19/16.
 */
public interface Clockable {

    public void start(boolean restart);
    public void stop();
    public void tick();

}
