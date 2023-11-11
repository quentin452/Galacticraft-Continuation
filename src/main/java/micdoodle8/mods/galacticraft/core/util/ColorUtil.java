package micdoodle8.mods.galacticraft.core.util;

import micdoodle8.mods.galacticraft.api.vector.*;

public class ColorUtil
{
    static Vector3 red;
    static Vector3 orange;
    static Vector3 yellow;
    static Vector3 green;
    static Vector3 cyan;
    static Vector3 blue;
    static Vector3 magenta;
    static Vector3 white;
    static Vector3 black;
    static Vector3 mud;
    static double[] colorwheelAngles;
    static Vector3[] colorwheelColors;
    
    private static Vector3 hue_to_rgb(double deg) {
        deg %= 360.0;
        double previous_angle = ColorUtil.colorwheelAngles[1];
        for (int i = 2; i < ColorUtil.colorwheelAngles.length - 2; ++i) {
            final Double angle = ColorUtil.colorwheelAngles[i];
            if (deg <= angle) {
                return interpolateInArray(ColorUtil.colorwheelColors, i, (angle - deg) / (angle - previous_angle));
            }
            previous_angle = angle;
        }
        return null;
    }
    
    private static double rgb_to_hue(final Vector3 input) {
        final double maxCol = Math.max(Math.max(input.x, input.y), input.z);
        if (maxCol <= 0.0) {
            return 0.0;
        }
        final Vector3 rgb = input.scale(255.0 / maxCol);
        double mindist = 1024.0;
        int mini = 0;
        for (int i = 2; i < ColorUtil.colorwheelAngles.length - 2; ++i) {
            final Vector3 color = ColorUtil.colorwheelColors[i];
            final double separation = color.distance(rgb);
            if (separation < mindist) {
                mindist = separation;
                mini = i;
            }
        }
        final double separation2 = ColorUtil.colorwheelColors[mini - 1].distance(rgb);
        final double separation3 = ColorUtil.colorwheelColors[mini + 1].distance(rgb);
        double hue;
        if (separation2 < separation3) {
            final double separationtot = ColorUtil.colorwheelColors[mini - 1].distance(ColorUtil.colorwheelColors[mini]);
            hue = interpolateInArray(ColorUtil.colorwheelAngles, mini, mindist / separationtot);
            if (hue < 0.0) {
                hue += 360.0;
            }
        }
        else {
            final double separationtot = ColorUtil.colorwheelColors[mini + 1].distance(ColorUtil.colorwheelColors[mini]);
            hue = interpolateInArray(ColorUtil.colorwheelAngles, mini + 1, separation3 / separationtot);
            if (hue > 360.0) {
                hue -= 360.0;
            }
        }
        return hue;
    }
    
    private static double cubicInterpolate(final double y0, final double y1, final double y2, final double y3, final double mu) {
        final double mu2 = mu * mu;
        final double a3 = y3 - y2 - y0 + y1;
        final double a4 = y0 - y1 - a3;
        final double a5 = y2 - y0;
        final double a6 = y1;
        return a3 * mu * mu2 + a4 * mu2 + a5 * mu + a6;
    }
    
    private static Vector3 interpolateInArray(final Vector3[] array, final int i, final double mu) {
        final Vector3 point0 = array[i + 1];
        final Vector3 point2 = array[i];
        final Vector3 point3 = array[i - 1];
        final Vector3 point4 = array[i - 2];
        final double x = cubicInterpolate(point0.x, point2.x, point3.x, point4.x, mu);
        final double y = cubicInterpolate(point0.y, point2.y, point3.y, point4.y, mu);
        final double z = cubicInterpolate(point0.z, point2.z, point3.z, point4.z, mu);
        return new Vector3(x, y, z);
    }
    
    private static double interpolateInArray(final double[] array, final int i, final double mu) {
        return cubicInterpolate(array[i + 1], array[i], array[i - 1], array[i - 2], mu);
    }
    
    public static Vector3 addColorsRealistically(final Vector3 color1, final Vector3 color2) {
        double hue1 = rgb_to_hue(color1);
        double hue2 = rgb_to_hue(color2);
        if (hue1 - hue2 > 180.0) {
            hue2 += 360.0;
        }
        if (hue2 - hue1 > 180.0) {
            hue1 += 360.0;
        }
        double hueresult = (hue1 + hue2) / 2.0;
        if (hueresult > 360.0) {
            hueresult -= 360.0;
        }
        return hue_to_rgb(hueresult).scale(0.00392156862745098);
    }
    
    public static int to32BitColor(int a, int r, int g, final int b) {
        a <<= 24;
        r <<= 16;
        g <<= 8;
        return a | r | g | b;
    }
    
    public static int to32BitColorB(final byte r, final byte g, final byte b) {
        final int rr = (r & 0xFF) << 16;
        final int gg = (g & 0xFF) << 8;
        return rr | gg | (b & 0xFF);
    }
    
    public static int lighten(final int col, final float factor) {
        int gg = col >> 8;
        int rr = gg >> 8;
        gg &= 0xFF;
        int bb = col & 0xFF;
        rr *= (int)(1.0f + factor);
        gg *= (int)(1.0f + factor);
        bb *= (int)(1.0f + factor);
        if (rr > 255) {
            rr = 255;
        }
        if (gg > 255) {
            gg = 255;
        }
        if (bb > 255) {
            bb = 255;
        }
        return rr << 16 | gg << 8 | bb;
    }
    
    public static int toGreyscale(final int col) {
        final int gg = col >> 8;
        int grey = gg >> 8;
        grey += (gg & 0xFF);
        grey += (col & 0xFF);
        grey /= 3;
        return grey << 16 | grey << 8 | (grey & 0xFF);
    }
    
    static {
        ColorUtil.red = new Vector3(255.0, 0.0, 0.0);
        ColorUtil.orange = new Vector3(255.0, 160.0, 0.0);
        ColorUtil.yellow = new Vector3(255.0, 255.0, 0.0);
        ColorUtil.green = new Vector3(0.0, 255.0, 0.0);
        ColorUtil.cyan = new Vector3(0.0, 255.0, 255.0);
        ColorUtil.blue = new Vector3(0.0, 0.0, 255.0);
        ColorUtil.magenta = new Vector3(255.0, 0.0, 255.0);
        ColorUtil.white = new Vector3(255.0, 255.0, 255.0);
        ColorUtil.black = new Vector3(0.0, 0.0, 0.0);
        ColorUtil.mud = new Vector3(94.0, 81.0, 74.0);
        ColorUtil.colorwheelAngles = new double[] { -110.0, -30.0, 0.0, 60.0, 120.0, 180.0, 215.0, 250.0, 330.0, 360.0, 420.0, 480.0 };
        ColorUtil.colorwheelColors = new Vector3[] { ColorUtil.blue, ColorUtil.magenta, ColorUtil.red, ColorUtil.orange, ColorUtil.yellow, ColorUtil.green, ColorUtil.cyan, ColorUtil.blue, ColorUtil.magenta, ColorUtil.red, ColorUtil.orange, ColorUtil.yellow };
    }
}
