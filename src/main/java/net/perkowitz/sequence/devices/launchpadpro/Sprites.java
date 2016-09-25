package net.perkowitz.sequence.devices.launchpadpro;

/**
 * Created by optic on 9/4/16.
 */
public class Sprites {

    public static Pad[] hachi = new Pad[] {
            new Pad(3, 0),
            new Pad(4, 0),
            new Pad(3, 2),
            new Pad(4, 2),
            new Pad(3, 3),
            new Pad(4, 3),
            new Pad(3, 4),
            new Pad(4, 4),
            new Pad(3, 6),
            new Pad(4, 6),
            new Pad(3, 7),
            new Pad(4, 7),
    };

    public static Pad[] issho = new Pad[] {
                new Pad(2, 1),
                new Pad(3, 1),
                new Pad(4, 1),
                new Pad(3, 3),
                new Pad(4, 3),
                new Pad(5, 3),
                new Pad(2, 5),
                new Pad(3, 5),
                new Pad(4, 5),
                new Pad(3, 7),
                new Pad(4, 7),
                new Pad(5, 7),
        };


    public static Pad[] nora1 = new Pad[] {
            new Pad(3, 0),
            new Pad(4, 0),
            new Pad(3, 1),
            new Pad(3, 3),
            new Pad(4, 3),
            new Pad(3, 4),
            new Pad(4, 4),
            new Pad(4, 6),
            new Pad(3, 7),
            new Pad(4, 7),
    };

    public static Pad[] nora2 = new Pad[] {
            new Pad(3, 1),
            new Pad(4, 1),
            new Pad(6, 1),
            new Pad(6, 2),
            new Pad(7, 1),
            new Pad(3, 3),
            new Pad(4, 3),
            new Pad(3, 5),
            new Pad(4, 5),
            new Pad(3, 6),
            new Pad(4, 6),
            new Pad(0, 6),
            new Pad(1, 6),
            new Pad(1, 5),
    };

    public static Pad[][] sprites = new Pad[][] { hachi, issho, nora1, nora2 };



}
