package micdoodle8.mods.galacticraft.core.client;

import net.minecraft.client.renderer.texture.*;
import java.awt.image.*;

public class DynamicTextureProper extends DynamicTexture
{
    private boolean updateFlag;
    private final int width;
    private final int height;
    
    public DynamicTextureProper(final BufferedImage img) {
        this(img.getWidth(), img.getHeight());
        this.update(img);
    }
    
    public DynamicTextureProper(final int width, final int height) {
        super(width, height);
        this.updateFlag = false;
        this.width = width;
        this.height = height;
    }
    
    public void update(final BufferedImage img) {
        img.getRGB(0, 0, this.width, this.height, this.getTextureData(), 0, this.width);
        this.updateFlag = true;
    }
    
    public int getGlTextureId() {
        if (this.updateFlag) {
            this.updateFlag = false;
            this.updateDynamicTexture();
        }
        return super.getGlTextureId();
    }
}
