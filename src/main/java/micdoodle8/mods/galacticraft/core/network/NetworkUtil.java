package micdoodle8.mods.galacticraft.core.network;

import io.netty.buffer.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.galacticraft.core.energy.tile.*;
import net.minecraft.entity.*;
import micdoodle8.mods.galacticraft.api.vector.*;
import micdoodle8.mods.galacticraft.core.wrappers.*;
import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import net.minecraft.world.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import micdoodle8.mods.galacticraft.core.util.*;
import net.minecraftforge.fluids.*;
import com.google.common.math.*;

public class NetworkUtil
{
    public static void encodeData(final ByteBuf buffer, final Collection<Object> sendData) throws IOException {
        for (final Object dataValue : sendData) {
            if (dataValue instanceof Integer) {
                buffer.writeInt((int)dataValue);
            }
            else if (dataValue instanceof Float) {
                buffer.writeFloat((float)dataValue);
            }
            else if (dataValue instanceof Double) {
                buffer.writeDouble((double)dataValue);
            }
            else if (dataValue instanceof Byte) {
                buffer.writeByte((int)(byte)dataValue);
            }
            else if (dataValue instanceof Boolean) {
                buffer.writeBoolean((boolean)dataValue);
            }
            else if (dataValue instanceof String) {
                ByteBufUtils.writeUTF8String(buffer, (String)dataValue);
            }
            else if (dataValue instanceof Short) {
                buffer.writeShort((int)(short)dataValue);
            }
            else if (dataValue instanceof Long) {
                buffer.writeLong((long)dataValue);
            }
            else if (dataValue instanceof EnergyStorage) {
                final EnergyStorage storage = (EnergyStorage)dataValue;
                buffer.writeFloat(storage.getCapacityGC());
                buffer.writeFloat(storage.getMaxReceive());
                buffer.writeFloat(storage.getMaxExtract());
                buffer.writeFloat(storage.getEnergyStoredGC());
            }
            else if (dataValue instanceof NBTTagCompound) {
                writeNBTTagCompound((NBTTagCompound)dataValue, buffer);
            }
            else if (dataValue instanceof FluidTank) {
                writeFluidTank((FluidTank)dataValue, buffer);
            }
            else if (dataValue instanceof Entity) {
                buffer.writeInt(((Entity)dataValue).getEntityId());
            }
            else if (dataValue instanceof Vector3) {
                buffer.writeDouble(((Vector3)dataValue).x);
                buffer.writeDouble(((Vector3)dataValue).y);
                buffer.writeDouble(((Vector3)dataValue).z);
            }
            else if (dataValue instanceof BlockVec3) {
                buffer.writeInt(((BlockVec3)dataValue).x);
                buffer.writeInt(((BlockVec3)dataValue).y);
                buffer.writeInt(((BlockVec3)dataValue).z);
            }
            else if (dataValue instanceof byte[]) {
                buffer.writeInt(((byte[])dataValue).length);
                for (int i = 0; i < ((byte[])dataValue).length; ++i) {
                    buffer.writeByte((int)((byte[])dataValue)[i]);
                }
            }
            else if (dataValue instanceof UUID) {
                buffer.writeLong(((UUID)dataValue).getLeastSignificantBits());
                buffer.writeLong(((UUID)dataValue).getMostSignificantBits());
            }
            else if (dataValue instanceof Collection) {
                encodeData(buffer, (Collection<Object>)dataValue);
            }
            else if (dataValue instanceof FlagData) {
                buffer.writeInt(((FlagData)dataValue).getWidth());
                buffer.writeInt(((FlagData)dataValue).getHeight());
                for (int i = 0; i < ((FlagData)dataValue).getWidth(); ++i) {
                    for (int j = 0; j < ((FlagData)dataValue).getHeight(); ++j) {
                        final Vector3 vec = ((FlagData)dataValue).getColorAt(i, j);
                        buffer.writeByte((int)(byte)(vec.x * 256.0 - 128.0));
                        buffer.writeByte((int)(byte)(vec.y * 256.0 - 128.0));
                        buffer.writeByte((int)(byte)(vec.z * 256.0 - 128.0));
                    }
                }
            }
            else if (dataValue instanceof Integer[]) {
                final Integer[] array = (Integer[])dataValue;
                buffer.writeInt(array.length);
                for (int k = 0; k < array.length; ++k) {
                    buffer.writeInt((int)array[k]);
                }
            }
            else if (dataValue instanceof String[]) {
                final String[] array2 = (String[])dataValue;
                buffer.writeInt(array2.length);
                for (int k = 0; k < array2.length; ++k) {
                    ByteBufUtils.writeUTF8String(buffer, array2[k]);
                }
            }
            else if (dataValue instanceof Footprint[]) {
                final Footprint[] array3 = (Footprint[])dataValue;
                buffer.writeInt(array3.length);
                for (int k = 0; k < array3.length; ++k) {
                    buffer.writeInt(array3[k].dimension);
                    buffer.writeFloat((float)array3[k].position.x);
                    buffer.writeFloat((float)array3[k].position.y + 1.0f);
                    buffer.writeFloat((float)array3[k].position.z);
                    buffer.writeFloat(array3[k].rotation);
                    buffer.writeShort((int)array3[k].age);
                    ByteBufUtils.writeUTF8String(buffer, array3[k].owner);
                }
            }
            else {
                if (dataValue == null) {
                    GCLog.severe("Cannot construct PacketSimple with null data, this is a bug.");
                }
                GCLog.info("Could not find data type to encode!: " + dataValue);
            }
        }
    }
    
    public static ArrayList<Object> decodeData(final Class<?>[] types, final ByteBuf buffer) {
        final ArrayList<Object> objList = new ArrayList<Object>();
        for (final Class clazz : types) {
            if (clazz.equals(Integer.class)) {
                objList.add(buffer.readInt());
            }
            else if (clazz.equals(Float.class)) {
                objList.add(buffer.readFloat());
            }
            else if (clazz.equals(Double.class)) {
                objList.add(buffer.readDouble());
            }
            else if (clazz.equals(Byte.class)) {
                objList.add(buffer.readByte());
            }
            else if (clazz.equals(Boolean.class)) {
                objList.add(buffer.readBoolean());
            }
            else if (clazz.equals(String.class)) {
                objList.add(ByteBufUtils.readUTF8String(buffer));
            }
            else if (clazz.equals(Short.class)) {
                objList.add(buffer.readShort());
            }
            else if (clazz.equals(Long.class)) {
                objList.add(buffer.readLong());
            }
            else if (clazz.equals(byte[].class)) {
                final byte[] bytes = new byte[buffer.readInt()];
                for (int i = 0; i < bytes.length; ++i) {
                    bytes[i] = buffer.readByte();
                }
                objList.add(bytes);
            }
            else if (clazz.equals(EnergyStorage.class)) {
                final EnergyStorage storage = new EnergyStorage(buffer.readFloat(), buffer.readFloat(), buffer.readFloat());
                storage.setEnergyStored(buffer.readFloat());
                objList.add(storage);
            }
            else if (clazz.equals(NBTTagCompound.class)) {
                try {
                    objList.add(readNBTTagCompound(buffer));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (clazz.equals(BlockVec3.class)) {
                objList.add(new BlockVec3(buffer.readInt(), buffer.readInt(), buffer.readInt()));
            }
            else if (clazz.equals(UUID.class)) {
                objList.add(new UUID(buffer.readLong(), buffer.readLong()));
            }
            else if (clazz.equals(Vector3.class)) {
                objList.add(new Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble()));
            }
            else if (clazz.equals(FlagData.class)) {
                final int width = buffer.readInt();
                final int height = buffer.readInt();
                final FlagData flagData = new FlagData(width, height);
                for (int j = 0; j < width; ++j) {
                    for (int k = 0; k < height; ++k) {
                        flagData.setColorAt(j, k, new Vector3((double)(buffer.readByte() + 128), (double)(buffer.readByte() + 128), (double)(buffer.readByte() + 128)));
                    }
                }
                objList.add(flagData);
            }
            else if (clazz.equals(Integer[].class)) {
                for (int size = buffer.readInt(), i = 0; i < size; ++i) {
                    objList.add(buffer.readInt());
                }
            }
            else if (clazz.equals(String[].class)) {
                for (int size = buffer.readInt(), i = 0; i < size; ++i) {
                    objList.add(ByteBufUtils.readUTF8String(buffer));
                }
            }
            else if (clazz.equals(Footprint[].class)) {
                for (int size = buffer.readInt(), i = 0; i < size; ++i) {
                    objList.add(new Footprint(buffer.readInt(), new Vector3((double)buffer.readFloat(), (double)buffer.readFloat(), (double)buffer.readFloat()), buffer.readFloat(), buffer.readShort(), ByteBufUtils.readUTF8String(buffer)));
                }
            }
        }
        return objList;
    }
    
    public static Object getFieldValueFromStream(final Field field, final ByteBuf buffer, final World world) throws IOException {
        final Class<?> dataValue = field.getType();
        if (dataValue.equals(Integer.TYPE)) {
            return buffer.readInt();
        }
        if (dataValue.equals(Float.TYPE)) {
            return buffer.readFloat();
        }
        if (dataValue.equals(Double.TYPE)) {
            return buffer.readDouble();
        }
        if (dataValue.equals(Byte.TYPE)) {
            return buffer.readByte();
        }
        if (dataValue.equals(Boolean.TYPE)) {
            return buffer.readBoolean();
        }
        if (dataValue.equals(String.class)) {
            return ByteBufUtils.readUTF8String(buffer);
        }
        if (dataValue.equals(Short.TYPE)) {
            return buffer.readShort();
        }
        if (dataValue.equals(Long.class)) {
            return buffer.readLong();
        }
        if (dataValue.equals(NBTTagCompound.class)) {
            return readNBTTagCompound(buffer);
        }
        if (dataValue.equals(FluidTank.class)) {
            return readFluidTank(buffer);
        }
        if (dataValue.equals(Vector3.class)) {
            return new Vector3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
        }
        if (dataValue.equals(BlockVec3.class)) {
            return new BlockVec3(buffer.readInt(), buffer.readInt(), buffer.readInt());
        }
        if (dataValue.equals(UUID.class)) {
            return new UUID(buffer.readLong(), buffer.readLong());
        }
        if (dataValue.equals(byte[].class)) {
            final byte[] bytes = new byte[buffer.readInt()];
            for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = buffer.readByte();
            }
            return bytes;
        }
        if (dataValue.equals(EnergyStorage.class)) {
            final float capacity = buffer.readFloat();
            final float maxReceive = buffer.readFloat();
            final float maxExtract = buffer.readFloat();
            final EnergyStorage storage = new EnergyStorage(capacity, maxReceive, maxExtract);
            storage.setEnergyStored(buffer.readFloat());
            return storage;
        }
        for (Class<?> c = dataValue; c != null; c = c.getSuperclass()) {
            if (c.equals(Entity.class)) {
                return world.getEntityByID(buffer.readInt());
            }
        }
        throw new NullPointerException("Field type not found: " + field.getType().getSimpleName());
    }
    
    public static ItemStack readItemStack(final ByteBuf buffer) throws IOException {
        ItemStack itemstack = null;
        final short itemID = buffer.readShort();
        if (itemID >= 0) {
            final byte stackSize = buffer.readByte();
            final short meta = buffer.readShort();
            itemstack = new ItemStack(Item.getItemById((int)itemID), (int)stackSize, (int)meta);
            itemstack.stackTagCompound = readNBTTagCompound(buffer);
        }
        return itemstack;
    }
    
    public static void writeItemStack(final ItemStack itemStack, final ByteBuf buffer) throws IOException {
        if (itemStack == null) {
            buffer.writeShort(-1);
        }
        else {
            buffer.writeShort(Item.getIdFromItem(itemStack.getItem()));
            buffer.writeByte(itemStack.stackSize);
            buffer.writeShort(itemStack.getItemDamage());
            NBTTagCompound nbttagcompound = null;
            if (itemStack.getItem().isDamageable() || itemStack.getItem().getShareTag()) {
                nbttagcompound = itemStack.stackTagCompound;
            }
            writeNBTTagCompound(nbttagcompound, buffer);
        }
    }
    
    public static NBTTagCompound readNBTTagCompound(final ByteBuf buffer) throws IOException {
        final short dataLength = buffer.readShort();
        if (dataLength < 0) {
            return null;
        }
        final byte[] compressedNBT = new byte[dataLength];
        buffer.readBytes(compressedNBT);
        return VersionUtil.decompressNBT(compressedNBT);
    }
    
    public static void writeNBTTagCompound(final NBTTagCompound nbt, final ByteBuf buffer) throws IOException {
        if (nbt == null) {
            buffer.writeShort(-1);
        }
        else {
            final byte[] compressedNBT = CompressedStreamTools.compress(nbt);
            buffer.writeShort((int)(short)compressedNBT.length);
            buffer.writeBytes(compressedNBT);
        }
    }
    
    public static void writeFluidTank(final FluidTank fluidTank, final ByteBuf buffer) throws IOException {
        if (fluidTank == null) {
            buffer.writeInt(0);
            buffer.writeInt(-1);
            buffer.writeInt(0);
        }
        else {
            buffer.writeInt(fluidTank.getCapacity());
            buffer.writeInt((fluidTank.getFluid() == null) ? -1 : FluidUtil.getFluidID(fluidTank.getFluid()));
            buffer.writeInt(fluidTank.getFluidAmount());
        }
    }
    
    public static FluidTank readFluidTank(final ByteBuf buffer) throws IOException {
        final int capacity = buffer.readInt();
        final int fluidID = buffer.readInt();
        final FluidTank fluidTank = new FluidTank(capacity);
        final int amount = buffer.readInt();
        if (fluidID == -1) {
            fluidTank.setFluid((FluidStack)null);
        }
        else {
            final Fluid fluid = FluidRegistry.getFluid(fluidID);
            fluidTank.setFluid(new FluidStack(fluid, amount));
        }
        return fluidTank;
    }
    
    public static boolean fuzzyEquals(final Object a, final Object b) {
        if (a == null != (b == null)) {
            return false;
        }
        if (a == null) {
            return true;
        }
        if (a instanceof Float && b instanceof Float) {
            final float af = (float)a;
            final float bf = (float)b;
            return af == bf || Math.abs(af - bf) < 0.01f;
        }
        if (a instanceof Double && b instanceof Double) {
            return DoubleMath.fuzzyEquals((double)a, (double)b, 0.01);
        }
        if (a instanceof Entity && b instanceof Entity) {
            final Entity a2 = (Entity)a;
            final Entity b2 = (Entity)b;
            return fuzzyEquals(a2.getEntityId(), b2.getEntityId());
        }
        if (a instanceof Vector3 && b instanceof Vector3) {
            final Vector3 a3 = (Vector3)a;
            final Vector3 b3 = (Vector3)b;
            return fuzzyEquals(a3.x, b3.x) && fuzzyEquals(a3.y, b3.y) && fuzzyEquals(a3.z, b3.z);
        }
        if (a instanceof EnergyStorage && b instanceof EnergyStorage) {
            final EnergyStorage a4 = (EnergyStorage)a;
            final EnergyStorage b4 = (EnergyStorage)b;
            return fuzzyEquals(a4.getEnergyStoredGC(), b4.getEnergyStoredGC()) && fuzzyEquals(a4.getCapacityGC(), b4.getCapacityGC()) && fuzzyEquals(a4.getMaxReceive(), b4.getMaxReceive()) && fuzzyEquals(a4.getMaxExtract(), b4.getMaxExtract());
        }
        if (a instanceof FluidTank && b instanceof FluidTank) {
            final FluidTank a5 = (FluidTank)a;
            final FluidTank b5 = (FluidTank)b;
            final FluidStack fluidA = a5.getFluid();
            final FluidStack fluidB = b5.getFluid();
            return fuzzyEquals(a5.getCapacity(), b5.getCapacity()) && fuzzyEquals((fluidA != null) ? FluidUtil.getFluidID(fluidA) : -1, (fluidB != null) ? FluidUtil.getFluidID(fluidB) : -1) && fuzzyEquals(a5.getFluidAmount(), b5.getFluidAmount());
        }
        return a.equals(b);
    }
    
    public static Object cloneNetworkedObject(final Object a) {
        if (a instanceof EnergyStorage) {
            final EnergyStorage prevStorage = (EnergyStorage)a;
            final EnergyStorage storage = new EnergyStorage(prevStorage.getCapacityGC(), prevStorage.getMaxReceive(), prevStorage.getMaxExtract());
            storage.setEnergyStored(prevStorage.getEnergyStoredGC());
            return storage;
        }
        if (a instanceof FluidTank) {
            final FluidTank prevTank = (FluidTank)a;
            FluidStack prevFluid = prevTank.getFluid();
            prevFluid = ((prevFluid == null) ? null : prevFluid.copy());
            final FluidTank tank = new FluidTank(prevFluid, prevTank.getCapacity());
            return tank;
        }
        return a;
    }
}
