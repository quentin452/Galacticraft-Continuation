package micdoodle8.mods.galacticraft.core.util;

import java.util.concurrent.atomic.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import cpw.mods.fml.relauncher.*;
import micdoodle8.mods.galacticraft.core.proxy.*;
import cpw.mods.fml.client.*;
import micdoodle8.mods.galacticraft.core.*;
import micdoodle8.mods.galacticraft.core.network.*;
import net.minecraft.client.renderer.texture.*;
import micdoodle8.mods.galacticraft.core.client.gui.screen.*;
import net.minecraft.world.*;
import net.minecraft.init.*;
import net.minecraft.world.chunk.*;
import net.minecraft.block.*;
import net.minecraft.server.*;
import net.minecraft.entity.player.*;
import org.apache.commons.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import micdoodle8.mods.galacticraft.core.client.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import java.io.*;
import net.minecraft.world.biome.*;
import net.minecraft.block.material.*;
import net.minecraft.util.*;

public class MapUtil
{
    public static AtomicBoolean calculatingMap;
    public static AtomicBoolean resetClientFlag;
    public static boolean doneOverworldTexture;
    public static ArrayList<BlockVec3> biomeColours;
    public static final float[] parabolicField;
    private static MapGen currentMap;
    private static MapGen slowMap;
    private static Random rand;
    private static final int SIZE_STD = 176;
    public static final int SIZE_STD2 = 352;
    private static LinkedList<MapGen> queuedMaps;
    public static LinkedList<String> clientRequests;

    public static void reset() {
        MapUtil.currentMap = null;
        MapUtil.slowMap = null;
        MapUtil.queuedMaps.clear();
        MapUtil.calculatingMap.set(false);
        MapUtil.doneOverworldTexture = false;
    }

    @SideOnly(Side.CLIENT)
    public static void resetClient() {
        MapUtil.resetClientFlag.set(true);
    }

    @SideOnly(Side.CLIENT)
    public static void resetClientBody() {
        ClientProxyCore.overworldTextureRequestSent = false;
        ClientProxyCore.overworldTexturesValid = false;
        MapUtil.clientRequests.clear();
        final File baseFolder = new File(FMLClientHandler.instance().getClient().mcDataDir, "assets/temp");
        if (baseFolder.exists() && baseFolder.isDirectory()) {
            for (final File f : baseFolder.listFiles()) {
                if (f.isFile()) {
                    f.delete();
                }
            }
        }
        GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_OVERWORLD_IMAGE, new Object[0]));
        DrawGameScreen.reusableMap = new DynamicTexture(352, 352);
        MapUtil.biomeColours.clear();
        setupColours();
    }

    public static void getLocalMap(final World world, final int chunkXPos, final int chunkZPos, final BufferedImage image) {
        for (int x0 = -12; x0 <= 12; ++x0) {
            for (int z0 = -12; z0 <= 12; ++z0) {
                final Chunk chunk = world.getChunkFromChunkCoords(chunkXPos + x0, chunkZPos + z0);
                if (chunk != null) {
                    for (int z2 = 0; z2 < 16; ++z2) {
                        for (int x2 = 0; x2 < 16; ++x2) {
                            int l4 = chunk.getHeightValue(x2, z2) + 1;
                            Block block = Blocks.air;
                            int i5 = 0;
                            if (l4 > 1) {
                                do {
                                    --l4;
                                    block = chunk.getBlock(x2, l4, z2);
                                    i5 = chunk.getBlockMetadata(x2, l4, z2);
                                } while (block.getMapColor(i5) == MapColor.airColor && l4 > 0);
                            }
                            final int col = block.getMapColor(i5).colorValue;
                            image.setRGB(x2 + (x0 + 12) * 16, z2 + (z0 + 12) * 16, col);
                        }
                    }
                }
            }
        }
    }

    public static void makeOverworldTexture() {
        if (MapUtil.doneOverworldTexture) {
            return;
        }
        final World world = WorldUtil.getProviderForDimensionServer(0).worldObj;
        if (world == null) {
            return;
        }
        final File baseFolder = new File(MinecraftServer.getServer().worldServerForDimension(0).getChunkSaveLocation(), "galacticraft/overworldMap");
        if (!baseFolder.exists() && !baseFolder.mkdirs()) {
            GCLog.severe("Base folder(s) could not be created: " + baseFolder.getAbsolutePath());
            MapUtil.doneOverworldTexture = true;
            return;
        }
        if (getBiomeMapForCoords(world, 0, 0, 7, 192, 48, baseFolder)) {
            MapUtil.doneOverworldTexture = true;
        }
    }

    public static void sendOverworldToClient(final EntityPlayerMP client) {
        if (MapUtil.doneOverworldTexture) {
            try {
                final File baseFolder = new File(MinecraftServer.getServer().worldServerForDimension(0).getChunkSaveLocation(), "galacticraft/overworldMap");
                if (!baseFolder.exists()) {
                    GCLog.severe("Base folder missing: " + baseFolder.getAbsolutePath());
                    return;
                }
                File file = new File(baseFolder, "Overworld192.bin");
                if (file.exists()) {
                    GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE, new Object[] { 0, 0, FileUtils.readFileToByteArray(file) }), client);
                }
                file = new File(baseFolder, "Overworld1536.bin");
                if (file.exists()) {
                    GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE, new Object[] { 0, 0, FileUtils.readFileToByteArray(file) }), client);
                }
            }
            catch (Exception ex) {
                System.err.println("Error sending overworld image to player.");
                ex.printStackTrace();
            }
        }
    }

    public static void sendOrCreateMap(final World world, final int cx, final int cz, final EntityPlayerMP client) {
        try {
            final File baseFolder = new File(MinecraftServer.getServer().worldServerForDimension(0).getChunkSaveLocation(), "galacticraft/overworldMap");
            if (!baseFolder.exists()) {
                GCLog.severe("Base folder missing: " + baseFolder.getAbsolutePath());
                return;
            }
            final File file = getFile(baseFolder, cx, cz);
            if (!file.exists()) {
                getBiomeMapForCoords(world, cx, cz, 1, 176, 176, baseFolder);
                return;
            }
            GalacticraftCore.packetPipeline.sendTo((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.C_SEND_OVERWORLD_IMAGE, new Object[] { cx, cz, FileUtils.readFileToByteArray(file) }), client);
        }
        catch (Exception ex) {
            System.err.println("Error sending map image to player.");
            ex.printStackTrace();
        }
    }

    public static boolean buildMaps(final World world, final int x, final int z) {
        final File baseFolder = new File(MinecraftServer.getServer().worldServerForDimension(0).getChunkSaveLocation(), "galacticraft/overworldMap");
        if (!baseFolder.exists() && !baseFolder.mkdirs()) {
            GCLog.severe("Base folder(s) could not be created: " + baseFolder.getAbsolutePath());
            return false;
        }
        final int cx = convertMap(x);
        final int cz = convertMap(z);
        getBiomeMapForCoords(world, cx, cz, 1, 176, 176, baseFolder);
        getBiomeMapForCoords(world, cx + 352, cz, 1, 176, 176, baseFolder);
        getBiomeMapForCoords(world, cx, cz + 352, 1, 176, 176, baseFolder);
        getBiomeMapForCoords(world, cx - 352, cz, 1, 176, 176, baseFolder);
        getBiomeMapForCoords(world, cx, cz - 352, 1, 176, 176, baseFolder);
        getBiomeMapForCoords(world, cx + 352, cz + 352, 1, 176, 176, baseFolder);
        getBiomeMapForCoords(world, cx - 352, cz + 352, 1, 176, 176, baseFolder);
        getBiomeMapForCoords(world, cx - 352, cz - 352, 1, 176, 176, baseFolder);
        getBiomeMapForCoords(world, cx + 352, cz - 352, 1, 176, 176, baseFolder);
        return true;
    }

    private static int convertMap(final int x) {
        int cx = x + 176;
        if (cx < 0) {
            cx -= 351;
        }
        cx /= 352;
        return cx * 352;
    }

    public static boolean getBiomeMapForCoords(final World world, final int cx, final int cz, final int scale, final int sizeX, final int sizeZ, final File baseFolder) {
        File outputFile;
        if (sizeX != sizeZ) {
            outputFile = new File(baseFolder, "Overworld" + sizeX + ".bin");
            if (sizeX == 1536) {
                return false;
            }
        }
        else {
            outputFile = getFile(baseFolder, cx, cz);
        }
        final MapGen newGen = new MapGen(world, sizeX, sizeZ, cx, cz, 1 << scale, outputFile);
        if (newGen.calculatingMap) {
            if (MapUtil.calculatingMap.getAndSet(true)) {
                MapUtil.queuedMaps.add(newGen);
            }
            else {
                MapUtil.currentMap = newGen;
            }
            return false;
        }
        return true;
    }

    public static void BiomeMapNextTick() {
        boolean doingSlow = false;
        MapGen map;
        if (MapUtil.currentMap != null) {
            map = MapUtil.currentMap;
        }
        else {
            if (MapUtil.slowMap == null) {
                return;
            }
            map = MapUtil.slowMap;
            doingSlow = true;
        }
        final long end = System.nanoTime() + 4500000L;
        while (System.nanoTime() < end) {
            if (map.BiomeMapOneTick()) {
                map.writeOutputFile(true);
                if (map.biomeMapFile.getName().equals("Overworld192.bin")) {
                    MapUtil.doneOverworldTexture = true;
                }
                if (doingSlow) {
                    MapUtil.slowMap = null;
                }
                else {
                    MapUtil.currentMap = null;
                    if (MapUtil.queuedMaps.size() > 0) {
                        MapUtil.currentMap = MapUtil.queuedMaps.removeFirst();
                    }
                }
                if (MapUtil.currentMap == null && MapUtil.slowMap == null) {
                    MapUtil.calculatingMap.set(false);
                }
            }
        }
    }

    public static BufferedImage convertTo12pxTexture(final BufferedImage overworldImage, final BufferedImage paletteImage) {
        final BufferedImage result = new BufferedImage(overworldImage.getWidth(), overworldImage.getHeight(), 1);
        final TreeMap<Integer, Integer> mapColPos = new TreeMap<Integer, Integer>();
        final TreeMap<Integer, Integer> mapColPosB = new TreeMap<Integer, Integer>();
        int count = 0;
        for (int x = 0; x < overworldImage.getWidth(); x += 4) {
            for (int z = 0; z < overworldImage.getHeight(); z += 4) {
                int r = 0;
                int g = 0;
                int b = 0;
                for (int xx = 0; xx < 4; ++xx) {
                    for (int zz = 0; zz < 4; ++zz) {
                        final int col = overworldImage.getRGB(xx + x, zz + z);
                        r += col >> 16;
                        g += (col >> 8 & 0xFF);
                        b += (col & 0xFF);
                    }
                }
                while (mapColPos.containsKey(g - b)) {
                    ++g;
                }
                mapColPos.put(g - b, count);
                if (x < overworldImage.getHeight()) {
                    final int col2 = paletteImage.getRGB(x + 1, z + 1);
                    r = col2 >> 16;
                    for (g = (col2 >> 8 & 0xFF), b = (col2 & 0xFF); mapColPosB.containsKey(g - b); ++g) {}
                    mapColPosB.put(g - b, col2);
                }
                ++count;
            }
        }
        count = 0;
        int newCol = 0;
        final Iterator<Integer> it = mapColPosB.keySet().iterator();
        final Iterator<Integer> itt = mapColPos.keySet().iterator();
        final int modulus = overworldImage.getHeight() / 4;
        final int mod2 = overworldImage.getWidth() / overworldImage.getHeight();
        for (int x2 = 0; x2 < overworldImage.getWidth() / 4; ++x2) {
            for (int z2 = 0; z2 < modulus; ++z2) {
                if (count % mod2 == 0) {
                    newCol = mapColPosB.get(it.next());
                }
                final int position = mapColPos.get(itt.next());
                final int xx2 = position / modulus;
                final int zz2 = position % modulus;
                for (int xxx = 0; xxx < 4; ++xxx) {
                    for (int zzz = 0; zzz < 4; ++zzz) {
                        result.setRGB(xx2 * 4 + xxx, zz2 * 4 + zzz, newCol);
                    }
                }
                ++count;
            }
        }
        return result;
    }

    public static BufferedImage readImage(final Object source) throws IOException {
        final ImageInputStream stream = ImageIO.createImageInputStream(source);
        final ImageReader reader = ImageIO.getImageReaders(stream).next();
        reader.setInput(stream);
        final ImageReadParam param = reader.getDefaultReadParam();
        ImageTypeSpecifier typeToUse = null;
        final Iterator i = reader.getImageTypes(0);
        while (i.hasNext()) {
            final ImageTypeSpecifier type = (ImageTypeSpecifier) i.next();
            if (type.getColorModel().getColorSpace().isCS_sRGB()) {
                typeToUse = type;
            }
        }
        if (typeToUse != null) {
            param.setDestinationType(typeToUse);
        }
        final BufferedImage b = reader.read(0, param);
        reader.dispose();
        stream.close();
        return b;
    }

    @SideOnly(Side.CLIENT)
    public static void writeImgToFile(final BufferedImage img, final String name) {
        if (GalacticraftCore.enableJPEG) {
            final File folder = new File(FMLClientHandler.instance().getClient().mcDataDir, "assets/temp");
            try {
                final ImageOutputStream outputStreamA = new FileImageOutputStream(new File(folder, name));
                GalacticraftCore.jpgWriter.setOutput(outputStreamA);
                GalacticraftCore.jpgWriter.write(null, new IIOImage(img, null, null), GalacticraftCore.writeParam);
                outputStreamA.close();
            }
            catch (Exception ex) {}
        }
    }

    @SideOnly(Side.CLIENT)
    public static void getOverworldImageFromRaw(final File folder, final int cx, final int cz, final byte[] raw) throws IOException {
        if (raw.length == 1179648) {
            final File file0 = new File(folder, "overworldRaw.bin");
            if (!file0.exists() || (file0.canRead() && file0.canWrite())) {
                FileUtils.writeByteArrayToFile(file0, raw);
            }
            else {
                System.err.println("Cannot read/write to file %minecraftDir%/assets/temp/overworldRaw.bin");
            }
            final BufferedImage worldImageLarge = new BufferedImage(3072, 768, 1);
            final ArrayList<Integer> cols = new ArrayList<Integer>();
            final int lastcol = -1;
            final int idx = 0;
            for (int x = 0; x < 1536; ++x) {
                for (int z = 0; z < 384; ++z) {
                    final int arrayIndex = (x * 384 + z) * 2;
                    int biome = raw[arrayIndex] & 0xFF;
                    final int height = raw[arrayIndex + 1] & 0xFF;
                    if (height < 63 && biome != 10) {
                        biome = 0;
                    }
                    if (height < 56 && biome == 0) {
                        biome = 24;
                    }
                    worldImageLarge.setRGB(x * 2, z * 2, convertBiomeColour(biome, height));
                    worldImageLarge.setRGB(x * 2, z * 2 + 1, convertBiomeColour(biome, height));
                    worldImageLarge.setRGB(x * 2 + 1, z * 2, convertBiomeColour(biome, height));
                    worldImageLarge.setRGB(x * 2 + 1, z * 2 + 1, convertBiomeColour(biome, height));
                }
            }
            if (ClientProxyCore.overworldTextureLarge == null) {
                ClientProxyCore.overworldTextureLarge = new DynamicTextureProper(768, 192);
            }
            ClientProxyCore.overworldTextureLarge.update(worldImageLarge);
            if (GalacticraftCore.enableJPEG) {
                final ImageOutputStream outputStream = new FileImageOutputStream(new File(folder, "large.jpg"));
                GalacticraftCore.jpgWriter.setOutput(outputStream);
                GalacticraftCore.jpgWriter.write(null, new IIOImage(worldImageLarge, null, null), GalacticraftCore.writeParam);
                outputStream.close();
            }
        }
        else if (raw.length == 18432) {
            final BufferedImage worldImage = new BufferedImage(192, 48, 1);
            final ArrayList<Integer> cols2 = new ArrayList<Integer>();
            final int lastcol2 = -1;
            final int idx2 = 0;
            for (int x2 = 0; x2 < 192; ++x2) {
                for (int z2 = 0; z2 < 48; ++z2) {
                    final int arrayIndex2 = (x2 * 48 + z2) * 2;
                    int biome2 = raw[arrayIndex2] & 0xFF;
                    final int height2 = raw[arrayIndex2 + 1] & 0xFF;
                    if (height2 < 63 && biome2 != 10) {
                        biome2 = 0;
                    }
                    if (height2 < 56 && biome2 == 0) {
                        biome2 = 24;
                    }
                    worldImage.setRGB(x2, z2, convertBiomeColour(biome2, height2));
                }
            }
            final IResourceManager rm = Minecraft.getMinecraft().getResourceManager();
            BufferedImage paletteImage = null;
            try {
                final InputStream in = rm.getResource(new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/gui/celestialbodies/earth.png")).getInputStream();
                paletteImage = ImageIO.read(in);
                in.close();
                paletteImage.getHeight();
            }
            catch (Exception e) {
                e.printStackTrace();
                return;
            }
            final BufferedImage result = convertTo12pxTexture(worldImage, paletteImage);
            if (result != null) {
                if (ClientProxyCore.overworldTextureWide == null) {
                    ClientProxyCore.overworldTextureWide = new DynamicTextureProper(192, 48);
                }
                if (ClientProxyCore.overworldTextureClient == null) {
                    ClientProxyCore.overworldTextureClient = new DynamicTextureProper(48, 48);
                }
                ClientProxyCore.overworldTextureWide.update(result);
                ClientProxyCore.overworldTextureClient.update(result);
                ClientProxyCore.overworldTexturesValid = true;
            }
        }
        else {
            final File file0 = getFile(folder, cx, cz);
            if (!file0.exists() || (file0.canRead() && file0.canWrite())) {
                FileUtils.writeByteArrayToFile(file0, raw);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean getMap(final int[] image, final World world, final int xCoord, final int zCoord) {
        final int cx = convertMap(xCoord);
        final int cz = convertMap(zCoord);
        final File baseFolder = new File(FMLClientHandler.instance().getClient().mcDataDir, "assets/temp");
        if (!baseFolder.exists() && !baseFolder.mkdirs()) {
            GCLog.severe("Base folder(s) could not be created: " + baseFolder.getAbsolutePath());
            return false;
        }
        final int dim = world.provider.dimensionId;
        boolean result = true;
        if (makeRGBimage(image, baseFolder, cx - 352, cz - 352, 0, 0, xCoord, zCoord, dim, result)) {
            result = false;
        }
        if (makeRGBimage(image, baseFolder, cx - 352, cz, 0, 176, xCoord, zCoord, dim, result)) {
            result = false;
        }
        if (makeRGBimage(image, baseFolder, cx - 352, cz + 352, 0, 352, xCoord, zCoord, dim, result)) {
            result = false;
        }
        if (makeRGBimage(image, baseFolder, cx, cz - 352, 176, 0, xCoord, zCoord, dim, result)) {
            result = false;
        }
        if (makeRGBimage(image, baseFolder, cx, cz, 176, 176, xCoord, zCoord, dim, result)) {
            result = false;
        }
        if (makeRGBimage(image, baseFolder, cx, cz + 352, 176, 352, xCoord, zCoord, dim, result)) {
            result = false;
        }
        if (makeRGBimage(image, baseFolder, cx + 352, cz - 352, 352, 0, xCoord, zCoord, dim, result)) {
            result = false;
        }
        if (makeRGBimage(image, baseFolder, cx + 352, cz, 352, 176, xCoord, zCoord, dim, result)) {
            result = false;
        }
        if (makeRGBimage(image, baseFolder, cx + 352, cz + 352, 352, 352, xCoord, zCoord, dim, result)) {
            result = false;
        }
        return result;
    }

    private static boolean makeRGBimage(final int[] array, final File baseFolder, final int cx, final int cz, final int offsetX, final int offsetZ, final int xCoord, final int zCoord, final int dim, final boolean prevResult) {
        final File filename = getFile(baseFolder, cx, cz);
        if (!filename.exists()) {
            if (MapUtil.clientRequests.contains(filename.getName())) {
                GCLog.debug("Still waiting for file " + filename.getName());
            }
            else {
                MapUtil.clientRequests.add(filename.getName());
                GCLog.debug("Client requested file" + filename.getName());
                GalacticraftCore.packetPipeline.sendToServer((IPacket)new PacketSimple(PacketSimple.EnumSimplePacket.S_REQUEST_MAP_IMAGE, new Object[] { dim, cx, cz }));
            }
            return true;
        }
        if (!prevResult) {
            return true;
        }
        final int ox = (convertMap(xCoord) - xCoord - 176) / 2;
        final int oz = (convertMap(zCoord) - zCoord - 176) / 2;
        byte[] raw = null;
        try {
            raw = FileUtils.readFileToByteArray(filename);
        }
        catch (IOException e) {
            GCLog.severe("Problem reading map file: " + baseFolder.getAbsolutePath() + filename.getName());
            return true;
        }
        if (raw == null || raw.length != 61952) {
            GCLog.debug("map size is " + raw.length);
            return true;
        }
        final int xstart = Math.max(0, -offsetX - ox);
        final int zstart = Math.max(0, -offsetZ - oz);
        for (int x = xstart; x < 176; ++x) {
            final int imagex = x + offsetX + ox;
            if (imagex >= 352) {
                break;
            }
            for (int z = zstart; z < 176; ++z) {
                final int imageZ = z + oz + offsetZ;
                if (imageZ >= 352) {
                    break;
                }
                final int arrayIndex = (x * 176 + z) * 2;
                int biome = raw[arrayIndex] & 0xFF;
                final int height = raw[arrayIndex + 1] & 0xFF;
                if (height < 63 && biome != 10) {
                    biome = 0;
                }
                if (height < 56 && biome == 0) {
                    biome = 24;
                }
                if (imagex < 0 || imageZ < 0) {
                    GCLog.debug("Outside image " + imagex + "," + imageZ + " - x=" + x + " z=" + z + " offsetX=" + offsetX + " offsetZ = " + offsetZ + " ox=" + ox + " oz=" + oz);
                }
                else {
                    array[imagex + 352 * imageZ] = convertBiomeColour(biome, height) - 16777216;
                }
            }
        }
        return false;
    }

    private static File getFile(final File folder, final int cx, final int cz) {
        return new File(folder, "overworld" + cx / 352 + "_" + cz / 352 + ".bin");
    }

    public static int convertBiomeColour(int in, final int height) {
        final int s = MapUtil.biomeColours.size();
        if (in >= 128 && in < 128 + s) {
            in -= 128;
        }
        int rv;
        if (in >= s) {
            rv = BiomeGenBase.getBiome(in).color;
        }
        else {
            final BlockVec3 bv = MapUtil.biomeColours.get(in);
            if (bv == null) {
                rv = BiomeGenBase.getBiome(in).color;
            }
            else if (bv.z > 0 && MapUtil.rand.nextInt(100) < bv.z) {
                rv = bv.y;
            }
            else {
                rv = bv.x;
            }
        }
        if (rv == 10232868 && MapUtil.rand.nextInt(2) == 0) {
            rv = 12559236;
        }
        if (height < 63) {
            return rv;
        }
        if (height > 92 && (in == 3 || in == 20 || in == 31 || in == 33 || in == 34) && MapUtil.rand.nextInt(8) > 98 - height) {
            rv = Material.snow.getMaterialMapColor().colorValue;
        }
        final float factor = (height - 68.0f) / 114.0f;
        return ColorUtil.lighten(rv, factor);
    }

    private static void setupColours() {
        MapUtil.biomeColours.add(new BlockVec3(Material.water.getMaterialMapColor().colorValue, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(4813878, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(13946264, Material.cactus.getMaterialMapColor().colorValue, 3));
        MapUtil.biomeColours.add(new BlockVec3(5072204, Material.rock.getMaterialMapColor().colorValue, 15));
        MapUtil.biomeColours.add(new BlockVec3(3962145, 2708502, 45));
        MapUtil.biomeColours.add(new BlockVec3(6454881, 1518103, 18));
        MapUtil.biomeColours.add(new BlockVec3(4412443, 1118985, 25));
        MapUtil.biomeColours.add(new BlockVec3(4813878, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(0, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(0, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(Material.ice.getMaterialMapColor().colorValue, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(Material.ice.getMaterialMapColor().colorValue, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(Material.snow.getMaterialMapColor().colorValue, 4813878, 3));
        MapUtil.biomeColours.add(new BlockVec3(Material.snow.getMaterialMapColor().colorValue, Material.ice.getMaterialMapColor().colorValue, 5));
        MapUtil.biomeColours.add(new BlockVec3(6510175, 8131604, 10));
        MapUtil.biomeColours.add(new BlockVec3(6971494, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(Material.sand.getMaterialMapColor().colorValue, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(13946264, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(3962145, 2708502, 35));
        MapUtil.biomeColours.add(new BlockVec3(6454881, 1518103, 14));
        MapUtil.biomeColours.add(new BlockVec3(5072204, 4813878, 50));
        MapUtil.biomeColours.add(new BlockVec3(1534979, 1000706, 25));
        MapUtil.biomeColours.add(new BlockVec3(1534979, 1000706, 25));
        MapUtil.biomeColours.add(new BlockVec3(1534979, 1000706, 25));
        MapUtil.biomeColours.add(new BlockVec3(3092436, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(Material.rock.getMaterialMapColor().colorValue, 0, 0));
        MapUtil.biomeColours.add(new BlockVec3(Material.sand.getMaterialMapColor().colorValue, Material.snow.getMaterialMapColor().colorValue, 75));
        MapUtil.biomeColours.add(new BlockVec3(5335862, 4813878, 65));
        MapUtil.biomeColours.add(new BlockVec3(5335862, 4813878, 55));
        MapUtil.biomeColours.add(new BlockVec3(10232868, 1977880, 98));
        MapUtil.biomeColours.add(new BlockVec3(Material.snow.getMaterialMapColor().colorValue, 1518103, 12));
        MapUtil.biomeColours.add(new BlockVec3(Material.snow.getMaterialMapColor().colorValue, 1518103, 12));
        MapUtil.biomeColours.add(new BlockVec3(1518103, 7228981, 12));
        MapUtil.biomeColours.add(new BlockVec3(1518103, 7228981, 12));
        MapUtil.biomeColours.add(new BlockVec3(8092024, 4813878, 10));
        MapUtil.biomeColours.add(new BlockVec3(5657897, 2499332, 20));
        MapUtil.biomeColours.add(new BlockVec3(5657897, 2499332, 14));
        MapUtil.biomeColours.add(new BlockVec3(10506783, 7417635, 14));
        MapUtil.biomeColours.add(new BlockVec3(10506783, 7417635, 17));
        MapUtil.biomeColours.add(new BlockVec3(10506783, 7417635, 20));
    }

    public static void makeVanillaMap(final int dim, final int chunkXPos, final int chunkZPos, final File baseFolder, final BufferedImage image) {
        for (int x0 = -12; x0 <= 12; ++x0) {
            for (int z0 = -12; z0 <= 12; ++z0) {
                final Chunk chunk = MinecraftServer.getServer().worldServerForDimension(dim).getChunkFromChunkCoords(chunkXPos + x0, chunkZPos + z0);
                if (chunk != null) {
                    for (int z2 = 0; z2 < 16; ++z2) {
                        for (int x2 = 0; x2 < 16; ++x2) {
                            int l4 = chunk.getHeightValue(x2, z2) + 1;
                            Block block = Blocks.air;
                            int i5 = 0;
                            if (l4 > 1) {
                                do {
                                    --l4;
                                    block = chunk.getBlock(x2, l4, z2);
                                    i5 = chunk.getBlockMetadata(x2, l4, z2);
                                } while (block.getMapColor(i5) == MapColor.airColor && l4 > 0);
                            }
                            final int col = block.getMapColor(i5).colorValue;
                            image.setRGB(x2 + (x0 + 12) * 16, z2 + (z0 + 12) * 16, col);
                        }
                    }
                }
            }
        }
        try {
            final File outputFile = new File(baseFolder, dim + "_" + chunkXPos + "_" + chunkZPos + ".jpg");
            if (!outputFile.exists() || (outputFile.canWrite() && outputFile.canRead())) {
                ImageIO.write(image, "jpg", outputFile);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        MapUtil.calculatingMap = new AtomicBoolean();
        MapUtil.resetClientFlag = new AtomicBoolean();
        MapUtil.doneOverworldTexture = false;
        MapUtil.biomeColours = new ArrayList<BlockVec3>(40);
        parabolicField = new float[25];
        MapUtil.currentMap = null;
        MapUtil.slowMap = null;
        MapUtil.rand = new Random();
        MapUtil.queuedMaps = new LinkedList<MapGen>();
        MapUtil.clientRequests = new LinkedList<String>();
        setupColours();
        for (int j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                final float f = 10.0f / MathHelper.sqrt_float(j * j + k * k + 0.2f);
                MapUtil.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }
    }
}
