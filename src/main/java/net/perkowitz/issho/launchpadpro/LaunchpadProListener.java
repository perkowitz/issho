package net.perkowitz.issho.launchpadpro;

/**
 * Created by optic on 9/4/16.
 */
public interface LaunchpadProListener {

    public void onPadPressed(Pad pad, int velocity);
    public void onPadReleased(Pad pad);
    public void onButtonPressed(Button button, int velocity);
    public void onButtonReleased(Button button);

}
