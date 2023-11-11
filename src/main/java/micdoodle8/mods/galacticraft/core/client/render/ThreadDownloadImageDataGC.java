package micdoodle8.mods.galacticraft.core.client.render;

import cpw.mods.fml.relauncher.*;
import java.util.concurrent.atomic.*;
import net.minecraft.client.renderer.*;
import java.awt.image.*;
import net.minecraft.util.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.*;
import cpw.mods.fml.common.*;
import javax.imageio.*;
import java.io.*;
import java.net.*;
import org.apache.commons.io.*;
import org.apache.logging.log4j.*;

@SideOnly(Side.CLIENT)
public class ThreadDownloadImageDataGC extends SimpleTexture
{
    private static final Logger logger;
    private static final AtomicInteger threadDownloadCounter;
    private final File field_152434_e;
    private final String imageUrl;
    private final IImageBuffer imageBuffer;
    private BufferedImage bufferedImage;
    private Thread imageThread;
    private boolean textureUploaded;
    
    public ThreadDownloadImageDataGC(final File p_i1049_1_, final String p_i1049_2_, final ResourceLocation p_i1049_3_, final IImageBuffer p_i1049_4_) {
        super(p_i1049_3_);
        this.field_152434_e = p_i1049_1_;
        this.imageUrl = p_i1049_2_;
        this.imageBuffer = p_i1049_4_;
    }
    
    private void checkTextureUploaded() {
        if (!this.textureUploaded && this.bufferedImage != null) {
            if (this.textureLocation != null) {
                this.deleteGlTexture();
            }
            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
            this.textureUploaded = true;
        }
    }
    
    public int getGlTextureId() {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }
    
    public void setBufferedImage(final BufferedImage p_147641_1_) {
        this.bufferedImage = p_147641_1_;
    }
    
    public void loadTexture(final IResourceManager p_110551_1_) {
        try {
            if (this.bufferedImage == null && this.textureLocation != null) {
                super.loadTexture(p_110551_1_);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.imageThread == null) {
            if (this.field_152434_e != null && this.field_152434_e.isFile()) {
                FMLLog.fine("Loading http texture from local cache (%s)", new Object[] { this.field_152434_e });
                try {
                    this.bufferedImage = ImageIO.read(this.field_152434_e);
                    if (this.imageBuffer != null) {
                        this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
                    }
                }
                catch (IOException ioexception) {
                    ThreadDownloadImageDataGC.logger.error("Couldn't load skin " + this.field_152434_e, (Throwable)ioexception);
                    this.func_152433_a();
                }
            }
            else {
                this.func_152433_a();
            }
        }
    }
    
    protected void func_152433_a() {
        (this.imageThread = new Thread("Texture Downloader #" + ThreadDownloadImageDataGC.threadDownloadCounter.incrementAndGet()) {
            @Override
            public void run() {
                HttpURLConnection httpurlconnection = null;
                FMLLog.fine("Downloading http texture from %s to %s", new Object[] { ThreadDownloadImageDataGC.this.imageUrl, ThreadDownloadImageDataGC.this.field_152434_e });
                try {
                    httpurlconnection = (HttpURLConnection)new URL(ThreadDownloadImageDataGC.this.imageUrl).openConnection();
                    httpurlconnection.setDoInput(true);
                    httpurlconnection.setDoOutput(false);
                    httpurlconnection.connect();
                    if (httpurlconnection.getResponseCode() / 100 == 2) {
                        BufferedImage bufferedimage;
                        if (ThreadDownloadImageDataGC.this.field_152434_e != null) {
                            FileUtils.copyInputStreamToFile(httpurlconnection.getInputStream(), ThreadDownloadImageDataGC.this.field_152434_e);
                            bufferedimage = ImageIO.read(ThreadDownloadImageDataGC.this.field_152434_e);
                        }
                        else {
                            bufferedimage = ImageIO.read(httpurlconnection.getInputStream());
                        }
                        if (ThreadDownloadImageDataGC.this.imageBuffer != null) {
                            bufferedimage = ThreadDownloadImageDataGC.this.imageBuffer.parseUserSkin(bufferedimage);
                        }
                        ThreadDownloadImageDataGC.this.setBufferedImage(bufferedimage);
                    }
                }
                catch (Exception exception) {
                    ThreadDownloadImageDataGC.logger.error("Couldn't download http texture", (Throwable)exception);
                }
                finally {
                    if (httpurlconnection != null) {
                        httpurlconnection.disconnect();
                    }
                }
            }
        }).setDaemon(true);
        this.imageThread.start();
    }
    
    static {
        logger = LogManager.getLogger();
        threadDownloadCounter = new AtomicInteger(0);
    }
}
