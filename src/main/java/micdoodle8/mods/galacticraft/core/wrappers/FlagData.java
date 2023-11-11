package micdoodle8.mods.galacticraft.core.wrappers;

import micdoodle8.mods.galacticraft.api.vector.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import java.awt.image.*;
import java.util.*;

public class FlagData
{
    private int height;
    private int width;
    private byte[][][] color;
    
    public FlagData(final int width, final int height) {
        this.height = height;
        this.width = width;
        this.color = new byte[width][height][3];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                this.color[i][j][0] = 127;
                this.color[i][j][1] = 127;
                this.color[i][j][2] = 127;
            }
        }
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public void setHeight(final int height) {
        this.height = height;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public void setWidth(final int width) {
        this.width = width;
    }
    
    public Vector3 getColorAt(final int posX, final int posY) {
        if (posX >= this.width || posY >= this.height) {
            return new Vector3(0.0, 0.0, 0.0);
        }
        return new Vector3((this.color[posX][posY][0] + 128) / 256.0, (this.color[posX][posY][1] + 128) / 256.0, (this.color[posX][posY][2] + 128) / 256.0);
    }
    
    public void setColorAt(final int posX, final int posY, final Vector3 colorVec) {
        this.color[posX][posY][0] = (byte)(colorVec.intX() - 128);
        this.color[posX][posY][1] = (byte)(colorVec.intY() - 128);
        this.color[posX][posY][2] = (byte)(colorVec.intZ() - 128);
    }
    
    public static FlagData readFlagData(final NBTTagCompound nbt) {
        if (nbt.hasKey("FlagWidth")) {
            final int width = nbt.getInteger("FlagWidth");
            final int height = nbt.getInteger("FlagHeight");
            final FlagData flagData = new FlagData(width, height);
            for (int i = 0; i < width; ++i) {
                for (int j = 0; j < height; ++j) {
                    flagData.color[i][j][0] = nbt.getByte("ColorR-X" + i + "-Y" + j);
                    flagData.color[i][j][1] = nbt.getByte("ColorG-X" + i + "-Y" + j);
                    flagData.color[i][j][2] = nbt.getByte("ColorB-X" + i + "-Y" + j);
                }
            }
            return flagData;
        }
        final int width = nbt.getInteger("FWidth");
        final int height = nbt.getInteger("FHeight");
        final FlagData flagData = new FlagData(width, height);
        for (int i = 0; i < height; ++i) {
            final int[] colorRow = nbt.getIntArray("FRow" + i);
            for (int k = 0; k < width; ++k) {
                final int color = colorRow[k];
                flagData.color[k][i][0] = (byte)(color >> 16);
                flagData.color[k][i][1] = (byte)(color >> 8 & 0xFF);
                flagData.color[k][i][2] = (byte)(color & 0xFF);
            }
        }
        return flagData;
    }
    
    public void saveFlagData(final NBTTagCompound nbt) {
        nbt.setInteger("FWidth", this.width);
        nbt.setInteger("FHeight", this.height);
        for (int i = 0; i < this.height; ++i) {
            final int[] colorRow = new int[this.width];
            for (int j = 0; j < this.width; ++j) {
                final byte[] arrayColor = this.color[j][i];
                colorRow[j] = ColorUtil.to32BitColorB(arrayColor[0], arrayColor[1], arrayColor[2]);
            }
            nbt.setIntArray("FRow" + i, colorRow);
        }
    }
    
    public BufferedImage toBufferedImage() {
        final BufferedImage image = new BufferedImage(this.width, this.height, 1);
        for (int i = 0; i < this.width; ++i) {
            for (int j = 0; j < this.height; ++j) {
                final int col = this.color[i][j][0] + 128 << 16 | this.color[i][j][1] + 128 << 8 | this.color[i][j][2] + 128;
                image.setRGB(i, j, col);
            }
        }
        return image;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final FlagData flagData = (FlagData)o;
        return this.height == flagData.height && this.width == flagData.width && Arrays.deepEquals(this.color, flagData.color);
    }
}
