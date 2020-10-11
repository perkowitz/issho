package net.perkowitz.issho.controller.midi;

/**
 * Created by optic on 9/19/16.
 */
public interface Clockable {

    public void start(boolean restart);
    public void stop();
    public void tick(boolean andReset);
    public void clock(int measure, int beat, int pulse);

}
