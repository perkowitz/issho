package net.perkowitz.issho.pixel;

import lombok.Getter;

/**
 * Created by mikep on 11/24/16.
 */
public class PixelColor {

    public static PixelColor RED = PixelColor.fromRGB(1.0, 0.0, 0.0);

    @Getter private Double r = 0.0;
    @Getter private Double g = 0.0;
    @Getter private Double b = 0.0;


    public PixelColor(Double r, Double g, Double b) {
        this.r = Math.min(Math.max(r, 0), 1);
        this.g = Math.min(Math.max(g, 0), 1);
        this.b = Math.min(Math.max(b, 0), 1);
    }


    /***** overrides **************************************************/

    @Override
    public boolean equals(Object o) {
        if (o instanceof PixelColor) {
            PixelColor color = (PixelColor)o;
            return (this.r.equals(color.r)) && (this.g.equals(color.g)) && (this.b.equals(color.b));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return("(" + r + "," + g + "," + b + ")");
    }


    /***** static constructors **********************************/

    public static PixelColor fromRGB(Double r, Double g, Double b) {
        return new PixelColor(r, g, b);
    }

}
