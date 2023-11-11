package micdoodle8.mods.galacticraft.core.client.gui.screen;

import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.settings.*;
import net.minecraft.client.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import org.lwjgl.opengl.*;
import java.text.*;
import net.minecraft.client.renderer.*;
import java.util.*;
import net.minecraft.client.resources.*;

@SideOnly(Side.CLIENT)
public class SmallFontRenderer implements IResourceManagerReloadListener
{
    private static final ResourceLocation[] unicodePageLocations;
    private int[] charWidth;
    public int FONT_HEIGHT;
    public Random fontRandom;
    private byte[] glyphWidth;
    private int[] colorCode;
    private final ResourceLocation locationFontTexture;
    private final TextureManager renderEngine;
    private float posX;
    private float posY;
    private boolean unicodeFlag;
    private boolean bidiFlag;
    private float red;
    private float blue;
    private float green;
    private float alpha;
    private int textColor;
    private boolean randomStyle;
    private boolean boldStyle;
    private boolean italicStyle;
    private boolean underlineStyle;
    private boolean strikethroughStyle;

    public SmallFontRenderer(final GameSettings par1GameSettings, final ResourceLocation par2ResourceLocation, final TextureManager par3TextureManager, final boolean par4) {
        this.charWidth = new int[256];
        this.FONT_HEIGHT = 9;
        this.fontRandom = new Random();
        this.glyphWidth = new byte[65536];
        this.colorCode = new int[32];
        this.locationFontTexture = par2ResourceLocation;
        this.renderEngine = par3TextureManager;
        this.unicodeFlag = true;
        for (int i = 0; i < 32; ++i) {
            final int j = (i >> 3 & 0x1) * 85;
            int k = (i >> 2 & 0x1) * 170 + j;
            int l = (i >> 1 & 0x1) * 170 + j;
            int i2 = (i & 0x1) * 170 + j;
            if (i == 6) {
                k += 85;
            }
            if (par1GameSettings.anaglyph) {
                final int j2 = (k * 30 + l * 59 + i2 * 11) / 100;
                final int k2 = (k * 30 + l * 70) / 100;
                final int l2 = (k * 30 + i2 * 70) / 100;
                k = j2;
                l = k2;
                i2 = l2;
            }
            if (i >= 16) {
                k /= 4;
                l /= 4;
                i2 /= 4;
            }
            this.colorCode[i] = ((k & 0xFF) << 16 | (l & 0xFF) << 8 | (i2 & 0xFF));
        }
        this.readGlyphSizes();
    }

    private void readFontTexture() {
        BufferedImage bufferedimage;
        try {
            bufferedimage = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(this.locationFontTexture).getInputStream());
        }
        catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }
        final int i = bufferedimage.getWidth();
        final int j = bufferedimage.getHeight();
        final int[] aint = new int[i * j];
        bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
        final int k = j / 16;
        final int l = i / 16;
        final byte b0 = 1;
        final float f = 8.0f / l;
        for (int i2 = 0; i2 < 256; ++i2) {
            final int j2 = i2 % 16;
            final int k2 = i2 / 16;
            if (i2 == 32) {
                this.charWidth[i2] = 3 + b0;
            }
            int l2;
            for (l2 = l - 1; l2 >= 0; --l2) {
                final int i3 = j2 * l + l2;
                boolean flag = true;
                for (int j3 = 0; j3 < k && flag; ++j3) {
                    final int k3 = (k2 * l + j3) * i;
                    if ((aint[i3 + k3] >> 24 & 0xFF) != 0x0) {
                        flag = false;
                    }
                }
                if (!flag) {
                    break;
                }
            }
            ++l2;
            this.charWidth[i2] = (int)(0.5 + l2 * f) + b0;
        }
    }

    private void readGlyphSizes() {
        try {
            final InputStream inputstream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("font/glyph_sizes.bin")).getInputStream();
            inputstream.read(this.glyphWidth);
        }
        catch (IOException ioexception) {
            throw new RuntimeException(ioexception);
        }
    }

    private float renderCharAtPos(final int par1, final char par2, final boolean par3) {
        return (par2 == ' ') ? 4.0f : ((par1 > 0 && !this.unicodeFlag) ? this.renderDefaultChar(par1 + 32, par3) : this.renderUnicodeChar(par2, par3));
    }

    private float renderDefaultChar(final int par1, final boolean par2) {
        final float f = (float)(par1 % 16 * 8);
        final float f2 = (float)(par1 / 16 * 8);
        final float f3 = par2 ? 1.0f : 0.0f;
        this.renderEngine.bindTexture(this.locationFontTexture);
        final float f4 = this.charWidth[par1] - 0.01f;
        GL11.glBegin(5);
        GL11.glTexCoord2f(f / 128.0f, f2 / 128.0f);
        GL11.glVertex3f(this.posX + f3, this.posY, 0.0f);
        GL11.glTexCoord2f(f / 128.0f, (f2 + 7.99f) / 128.0f);
        GL11.glVertex3f(this.posX - f3, this.posY + 7.99f, 0.0f);
        GL11.glTexCoord2f((f + f4 - 1.0f) / 128.0f, f2 / 128.0f);
        GL11.glVertex3f(this.posX + f4 - 1.0f + f3, this.posY, 0.0f);
        GL11.glTexCoord2f((f + f4 - 1.0f) / 128.0f, (f2 + 7.99f) / 128.0f);
        GL11.glVertex3f(this.posX + f4 - 1.0f - f3, this.posY + 7.99f, 0.0f);
        GL11.glEnd();
        return (float)this.charWidth[par1];
    }

    private ResourceLocation getUnicodePageLocation(final int par1) {
        if (SmallFontRenderer.unicodePageLocations[par1] == null) {
            SmallFontRenderer.unicodePageLocations[par1] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", par1));
        }
        return SmallFontRenderer.unicodePageLocations[par1];
    }

    private void loadGlyphTexture(final int par1) {
        this.renderEngine.bindTexture(this.getUnicodePageLocation(par1));
    }

    private float renderUnicodeChar(final char par1, final boolean par2) {
        if (this.glyphWidth[par1] == 0) {
            return 0.0f;
        }
        final int i = par1 / '\u0100';
        this.loadGlyphTexture(i);
        final int j = this.glyphWidth[par1] >>> 4;
        final int k = this.glyphWidth[par1] & 0xF;
        final float f1 = (float)(k + 1);
        final float f2 = (float)(par1 % '\u0010' * 16 + j);
        final float f3 = (float)((par1 & '\u00ff') / 16 * 16);
        final float f4 = f1 - j - 0.02f;
        final float f5 = par2 ? 1.0f : 0.0f;
        GL11.glBegin(5);
        GL11.glTexCoord2f(f2 / 256.0f, f3 / 256.0f);
        GL11.glVertex3f(this.posX + f5, this.posY, 0.0f);
        GL11.glTexCoord2f(f2 / 256.0f, (f3 + 15.98f) / 256.0f);
        GL11.glVertex3f(this.posX - f5, this.posY + 7.99f, 0.0f);
        GL11.glTexCoord2f((f2 + f4) / 256.0f, f3 / 256.0f);
        GL11.glVertex3f(this.posX + f4 / 2.0f + f5, this.posY, 0.0f);
        GL11.glTexCoord2f((f2 + f4) / 256.0f, (f3 + 15.98f) / 256.0f);
        GL11.glVertex3f(this.posX + f4 / 2.0f - f5, this.posY + 7.99f, 0.0f);
        GL11.glEnd();
        return (f1 - j) / 2.0f + 1.0f;
    }

    public int drawStringWithShadow(final String par1Str, final int par2, final int par3, final int par4) {
        return this.drawString(par1Str, par2, par3, par4, true);
    }

    public int drawString(final String par1Str, final int par2, final int par3, final int par4) {
        return this.drawString(par1Str, par2, par3, par4, false);
    }

    public int drawString(String par1Str, final int par2, final int par3, final int par4, final boolean par5) {
        this.resetStyles();
        if (this.bidiFlag) {
            par1Str = this.bidiReorder(par1Str);
        }
        int l;
        if (par5) {
            l = this.renderString(par1Str, par2 + 1, par3 + 1, par4, true);
            l = Math.max(l, this.renderString(par1Str, par2, par3, par4, false));
        }
        else {
            l = this.renderString(par1Str, par2, par3, par4, false);
        }
        return l;
    }

    private String bidiReorder(final String par1Str) {
        if (par1Str != null && Bidi.requiresBidi(par1Str.toCharArray(), 0, par1Str.length())) {
            final Bidi bidi = new Bidi(par1Str, -2);
            final byte[] abyte = new byte[bidi.getRunCount()];
            final String[] astring = new String[abyte.length];
            for (int j = 0; j < abyte.length; ++j) {
                final int k = bidi.getRunStart(j);
                final int i = bidi.getRunLimit(j);
                final int l = bidi.getRunLevel(j);
                final String s1 = par1Str.substring(k, i);
                abyte[j] = (byte)l;
                astring[j] = s1;
            }
            final String[] astring2 = astring.clone();
            Bidi.reorderVisually(abyte, 0, astring, 0, abyte.length);
            final StringBuilder stringbuilder = new StringBuilder();
            for (int i = 0; i < astring.length; ++i) {
                byte b0 = abyte[i];
                for (int i2 = 0; i2 < astring2.length; ++i2) {
                    if (astring2[i2].equals(astring[i])) {
                        b0 = abyte[i2];
                        break;
                    }
                }
                if ((b0 & 0x1) == 0x0) {
                    stringbuilder.append(astring[i]);
                }
                else {
                    for (int i2 = astring[i].length() - 1; i2 >= 0; --i2) {
                        char c0 = astring[i].charAt(i2);
                        if (c0 == '(') {
                            c0 = ')';
                        }
                        else if (c0 == ')') {
                            c0 = '(';
                        }
                        stringbuilder.append(c0);
                    }
                }
            }
            return stringbuilder.toString();
        }
        return par1Str;
    }

    private void resetStyles() {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    private void renderStringAtPos(final String par1Str, final boolean par2) {
        for (int i = 0; i < par1Str.length(); ++i) {
            final char c0 = par1Str.charAt(i);
            if (c0 == '�' && i + 1 < par1Str.length()) {
                int j = "0123456789abcdefklmnor".indexOf(par1Str.toLowerCase().charAt(i + 1));
                if (j < 16) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    if (j < 0 || j > 15) {
                        j = 15;
                    }
                    if (par2) {
                        j += 16;
                    }
                    final int k = this.colorCode[j];
                    this.textColor = k;
                    GL11.glColor4f((k >> 16) / 255.0f, (k >> 8 & 0xFF) / 255.0f, (k & 0xFF) / 255.0f, this.alpha);
                }
                else if (j == 16) {
                    this.randomStyle = true;
                }
                else if (j == 17) {
                    this.boldStyle = true;
                }
                else if (j == 18) {
                    this.strikethroughStyle = true;
                }
                else if (j == 19) {
                    this.underlineStyle = true;
                }
                else if (j == 20) {
                    this.italicStyle = true;
                }
                else if (j == 21) {
                    this.randomStyle = false;
                    this.boldStyle = false;
                    this.strikethroughStyle = false;
                    this.underlineStyle = false;
                    this.italicStyle = false;
                    GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
                }
                ++i;
            }
            else {
                int j = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8�\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1����������\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261�\u2265\u2264\u2320\u2321\u00f7\u2248�\u2219�\u221a\u207f�\u25a0\u0000".indexOf(c0);
                if (this.randomStyle && j != -1) {
                    int k;
                    do {
                        k = this.fontRandom.nextInt(this.charWidth.length);
                    } while (this.charWidth[j] != this.charWidth[k]);
                    j = k;
                }
                final float f = this.unicodeFlag ? 0.5f : 1.0f;
                final boolean flag1 = (c0 == '\0' || j == -1 || this.unicodeFlag) && par2;
                if (flag1) {
                    this.posX -= f;
                    this.posY -= f;
                }
                float f2 = this.renderCharAtPos(j, c0, this.italicStyle);
                if (flag1) {
                    this.posX += f;
                    this.posY += f;
                }
                if (this.boldStyle) {
                    this.posX += f;
                    if (flag1) {
                        this.posX -= f;
                        this.posY -= f;
                    }
                    this.renderCharAtPos(j, c0, this.italicStyle);
                    this.posX -= f;
                    if (flag1) {
                        this.posX += f;
                        this.posY += f;
                    }
                    ++f2;
                }
                if (this.strikethroughStyle) {
                    final Tessellator tessellator = Tessellator.instance;
                    GL11.glDisable(3553);
                    tessellator.startDrawingQuads();
                    tessellator.addVertex((double)this.posX, (double)(this.posY + this.FONT_HEIGHT / 2), 0.0);
                    tessellator.addVertex((double)(this.posX + f2), (double)(this.posY + this.FONT_HEIGHT / 2), 0.0);
                    tessellator.addVertex((double)(this.posX + f2), (double)(this.posY + this.FONT_HEIGHT / 2 - 1.0f), 0.0);
                    tessellator.addVertex((double)this.posX, (double)(this.posY + this.FONT_HEIGHT / 2 - 1.0f), 0.0);
                    tessellator.draw();
                    GL11.glEnable(3553);
                }
                if (this.underlineStyle) {
                    final Tessellator tessellator = Tessellator.instance;
                    GL11.glDisable(3553);
                    tessellator.startDrawingQuads();
                    final int l = this.underlineStyle ? -1 : 0;
                    tessellator.addVertex((double)(this.posX + l), (double)(this.posY + this.FONT_HEIGHT), 0.0);
                    tessellator.addVertex((double)(this.posX + f2), (double)(this.posY + this.FONT_HEIGHT), 0.0);
                    tessellator.addVertex((double)(this.posX + f2), (double)(this.posY + this.FONT_HEIGHT - 1.0f), 0.0);
                    tessellator.addVertex((double)(this.posX + l), (double)(this.posY + this.FONT_HEIGHT - 1.0f), 0.0);
                    tessellator.draw();
                    GL11.glEnable(3553);
                }
                this.posX += (int)f2;
            }
        }
    }

    private int renderStringAligned(String par1Str, int par2, final int par3, final int par4, final int par5, final boolean par6) {
        if (this.bidiFlag) {
            par1Str = this.bidiReorder(par1Str);
            final int i1 = this.getStringWidth(par1Str);
            par2 = par2 + par4 - i1;
        }
        return this.renderString(par1Str, par2, par3, par5, par6);
    }

    private int renderString(final String par1Str, final int par2, final int par3, int par4, final boolean par5) {
        if (par1Str == null) {
            return 0;
        }
        if ((par4 & 0xFC000000) == 0x0) {
            par4 |= 0xFF000000;
        }
        if (par5) {
            par4 = ((par4 & 0xFCFCFC) >> 2 | (par4 & 0xFF000000));
        }
        this.red = (par4 >> 16 & 0xFF) / 255.0f;
        this.blue = (par4 >> 8 & 0xFF) / 255.0f;
        this.green = (par4 & 0xFF) / 255.0f;
        this.alpha = (par4 >> 24 & 0xFF) / 255.0f;
        GL11.glColor4f(this.red, this.blue, this.green, this.alpha);
        this.posX = (float)par2;
        this.posY = (float)par3;
        this.renderStringAtPos(par1Str, par5);
        return (int)this.posX;
    }

    public int getStringWidth(final String par1Str) {
        if (par1Str == null) {
            return 0;
        }
        int i = 0;
        boolean flag = false;
        for (int j = 0; j < par1Str.length(); ++j) {
            char c0 = par1Str.charAt(j);
            int k = this.getCharWidth(c0);
            if (k < 0 && j < par1Str.length() - 1) {
                ++j;
                c0 = par1Str.charAt(j);
                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag = false;
                    }
                }
                else {
                    flag = true;
                }
                k = 0;
            }
            i += k;
            if (flag) {
                ++i;
            }
        }
        return i;
    }

    public int getCharWidth(final char par1) {
        if (par1 == '�') {
            return -1;
        }
        if (par1 == ' ') {
            return 4;
        }
        final int i = "\u00c0\u00c1\u00c2\u00c8\u00ca\u00cb\u00cd\u00d3\u00d4\u00d5\u00da\u00df\u00e3\u00f5\u011f\u0130\u0131\u0152\u0153\u015e\u015f\u0174\u0175\u017e\u0207\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8�\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1����������\u2591\u2592\u2593\u2502\u2524\u2561\u2562\u2556\u2555\u2563\u2551\u2557\u255d\u255c\u255b\u2510\u2514\u2534\u252c\u251c\u2500\u253c\u255e\u255f\u255a\u2554\u2569\u2566\u2560\u2550\u256c\u2567\u2568\u2564\u2565\u2559\u2558\u2552\u2553\u256b\u256a\u2518\u250c\u2588\u2584\u258c\u2590\u2580\u03b1\u03b2\u0393\u03c0\u03a3\u03c3\u03bc\u03c4\u03a6\u0398\u03a9\u03b4\u221e\u2205\u2208\u2229\u2261�\u2265\u2264\u2320\u2321\u00f7\u2248�\u2219�\u221a\u207f�\u25a0\u0000".indexOf(par1);
        if (par1 > '\0' && i != -1 && !this.unicodeFlag) {
            return this.charWidth[i];
        }
        if (this.glyphWidth[par1] != 0) {
            int j = this.glyphWidth[par1] >>> 4;
            int k = this.glyphWidth[par1] & 0xF;
            if (k > 7) {
                k = 15;
                j = 0;
            }
            return (++k - j) / 2 + 1;
        }
        return 0;
    }

    public String trimStringToWidth(final String par1Str, final int par2) {
        return this.trimStringToWidth(par1Str, par2, false);
    }

    public String trimStringToWidth(final String par1Str, final int par2, final boolean par3) {
        final StringBuilder stringbuilder = new StringBuilder();
        int j = 0;
        final int k = par3 ? (par1Str.length() - 1) : 0;
        final int l = par3 ? -1 : 1;
        boolean flag1 = false;
        boolean flag2 = false;
        for (int i1 = k; i1 >= 0 && i1 < par1Str.length() && j < par2; i1 += l) {
            final char c0 = par1Str.charAt(i1);
            final int j2 = this.getCharWidth(c0);
            if (flag1) {
                flag1 = false;
                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag2 = false;
                    }
                }
                else {
                    flag2 = true;
                }
            }
            else if (j2 < 0) {
                flag1 = true;
            }
            else {
                j += j2;
                if (flag2) {
                    ++j;
                }
            }
            if (j > par2) {
                break;
            }
            if (par3) {
                stringbuilder.insert(0, c0);
            }
            else {
                stringbuilder.append(c0);
            }
        }
        return stringbuilder.toString();
    }

    private String trimStringNewline(String par1Str) {
        while (par1Str != null && par1Str.endsWith("\n")) {
            par1Str = par1Str.substring(0, par1Str.length() - 1);
        }
        return par1Str;
    }

    public void drawSplitString(String par1Str, final int par2, final int par3, final int par4, final int par5) {
        this.resetStyles();
        this.textColor = par5;
        par1Str = this.trimStringNewline(par1Str);
        this.renderSplitString(par1Str, par2, par3, par4, false);
    }

    private void renderSplitString(final String par1Str, final int par2, int par3, final int par4, final boolean par5) {
        final List<?> list = this.listFormattedStringToWidth(par1Str, par4);
        for (final Object s1 : list) {
            this.renderStringAligned((String) s1, par2, par3, par4, this.textColor, par5);
            par3 += this.FONT_HEIGHT;
        }
    }

    public int splitStringWidth(final String par1Str, final int par2) {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(par1Str, par2).size();
    }

    public void setUnicodeFlag(final boolean par1) {
        this.unicodeFlag = par1;
    }

    public boolean getUnicodeFlag() {
        return this.unicodeFlag;
    }

    public void setBidiFlag(final boolean par1) {
        this.bidiFlag = par1;
    }

    public List<?> listFormattedStringToWidth(final String par1Str, final int par2) {
        return Arrays.asList((Object[])this.wrapFormattedStringToWidth(par1Str, par2).split("\n"));
    }

    String wrapFormattedStringToWidth(final String par1Str, final int par2) {
        final int j = this.sizeStringToWidth(par1Str, par2);
        if (par1Str.length() <= j) {
            return par1Str;
        }
        final String s1 = par1Str.substring(0, j);
        final char c0 = par1Str.charAt(j);
        final boolean flag = c0 == ' ' || c0 == '\n';
        final String s2 = getFormatFromString(s1) + par1Str.substring(j + (flag ? 1 : 0));
        return s1 + "\n" + this.wrapFormattedStringToWidth(s2, par2);
    }

    private int sizeStringToWidth(final String par1Str, final int par2) {
        final int j = par1Str.length();
        int k = 0;
        int l = 0;
        int i1 = -1;
        boolean flag = false;
        while (l < j) {
            final char c0 = par1Str.charAt(l);
            Label_0164: {
                switch (c0) {
                    case '\n': {
                        --l;
                        break Label_0164;
                    }
                    case '�': {
                        if (l < j - 1) {
                            ++l;
                            final char c2 = par1Str.charAt(l);
                            if (c2 != 'l' && c2 != 'L') {
                                if (c2 == 'r' || c2 == 'R' || isFormatColor(c2)) {
                                    flag = false;
                                }
                            }
                            else {
                                flag = true;
                            }
                        }
                        break Label_0164;
                    }
                    case ' ': {
                        i1 = l;
                        break;
                    }
                }
                k += this.getCharWidth(c0);
                if (flag) {
                    ++k;
                }
            }
            if (c0 == '\n') {
                i1 = ++l;
                break;
            }
            if (k > par2) {
                break;
            }
            ++l;
        }
        return (l != j && i1 != -1 && i1 < l) ? i1 : l;
    }

    private static boolean isFormatColor(final char par0) {
        return (par0 >= '0' && par0 <= '9') || (par0 >= 'a' && par0 <= 'f') || (par0 >= 'A' && par0 <= 'F');
    }

    private static boolean isFormatSpecial(final char par0) {
        return (par0 >= 'k' && par0 <= 'o') || (par0 >= 'K' && par0 <= 'O') || par0 == 'r' || par0 == 'R';
    }

    private static String getFormatFromString(final String par0Str) {
        String s1 = "";
        int i = -1;
        final int j = par0Str.length();
        while ((i = par0Str.indexOf(167, i + 1)) != -1) {
            if (i < j - 1) {
                final char c0 = par0Str.charAt(i + 1);
                if (isFormatColor(c0)) {
                    s1 = "�" + c0;
                }
                else {
                    if (!isFormatSpecial(c0)) {
                        continue;
                    }
                    s1 = s1 + "�" + c0;
                }
            }
        }
        return s1;
    }

    public boolean getBidiFlag() {
        return this.bidiFlag;
    }

    public void onResourceManagerReload(final IResourceManager var1) {
        this.readFontTexture();
    }

    static {
        unicodePageLocations = new ResourceLocation[256];
    }
}
