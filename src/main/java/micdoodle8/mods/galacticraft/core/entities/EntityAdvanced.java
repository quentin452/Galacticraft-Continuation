package micdoodle8.mods.galacticraft.core.entities;

import net.minecraft.entity.*;
import java.lang.reflect.*;
import net.minecraft.world.*;
import micdoodle8.mods.galacticraft.core.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.player.*;
import cpw.mods.fml.common.network.*;
import micdoodle8.mods.miccore.*;
import java.lang.annotation.*;
import micdoodle8.mods.galacticraft.core.network.*;
import java.util.*;
import io.netty.buffer.*;

public abstract class EntityAdvanced extends Entity implements IPacketReceiver
{
    protected long ticks;
    private LinkedHashSet<Field> fieldCacheClient;
    private LinkedHashSet<Field> fieldCacheServer;
    private Map<Field, Object> lastSentData;
    private boolean networkDataChanged;
    
    public EntityAdvanced(final World world) {
        super(world);
        this.ticks = 0L;
        this.lastSentData = new HashMap<Field, Object>();
        this.networkDataChanged = false;
        if (world != null && world.isRemote) {
            this.fieldCacheServer = new LinkedHashSet<Field>();
            GalacticraftCore.packetPipeline.sendToServer(new PacketDynamic(this));
        }
    }
    
    public abstract boolean isNetworkedEntity();
    
    public abstract int getPacketCooldown(final Side p0);
    
    public abstract void onPacketClient(final EntityPlayer p0);
    
    public abstract void onPacketServer(final EntityPlayer p0);
    
    public abstract double getPacketRange();
    
    public void onUpdate() {
        super.onUpdate();
        if (this.ticks >= Long.MAX_VALUE) {
            this.ticks = 1L;
        }
        ++this.ticks;
        if (this.isNetworkedEntity()) {
            if (!this.worldObj.isRemote && this.ticks % this.getPacketCooldown(Side.CLIENT) == 0L) {
                if (this.fieldCacheClient == null) {
                    try {
                        this.initFieldCache();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                final PacketDynamic packet = new PacketDynamic(this);
                if (this.networkDataChanged) {
                    GalacticraftCore.packetPipeline.sendToAllAround(packet, new NetworkRegistry.TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, this.getPacketRange()));
                }
            }
            if (this.worldObj.isRemote && this.ticks % this.getPacketCooldown(Side.SERVER) == 0L) {
                if (this.fieldCacheClient == null) {
                    try {
                        this.initFieldCache();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                final PacketDynamic packet = new PacketDynamic(this);
                if (this.networkDataChanged) {
                    GalacticraftCore.packetPipeline.sendToServer(packet);
                }
            }
        }
    }
    
    private void initFieldCache() throws IllegalArgumentException, IllegalAccessException {
        this.fieldCacheClient = new LinkedHashSet<Field>();
        this.fieldCacheServer = new LinkedHashSet<Field>();
        for (final Field field : this.getClass().getFields()) {
            if (field.isAnnotationPresent(Annotations.NetworkedField.class)) {
                final Annotations.NetworkedField f = field.getAnnotation(Annotations.NetworkedField.class);
                if (f.targetSide() == Side.CLIENT) {
                    this.fieldCacheClient.add(field);
                }
                else {
                    this.fieldCacheServer.add(field);
                }
            }
        }
    }
    
    public void getNetworkedData(final ArrayList<Object> sendData) {
        Set<Field> fieldList = null;
        boolean changed = false;
        if (this.worldObj.isRemote) {
            fieldList = this.fieldCacheServer;
        }
        else {
            fieldList = this.fieldCacheClient;
        }
        for (final Field f : fieldList) {
            boolean fieldChanged = false;
            try {
                final Object data = f.get(this);
                final Object lastData = this.lastSentData.get(f);
                if (!NetworkUtil.fuzzyEquals(lastData, data)) {
                    fieldChanged = true;
                }
                sendData.add(data);
                if (fieldChanged) {
                    this.lastSentData.put(f, NetworkUtil.cloneNetworkedObject(data));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            changed |= fieldChanged;
        }
        this.networkDataChanged = changed;
    }
    
    public void decodePacketdata(final ByteBuf buffer) {
        Label_0026: {
            if (this.fieldCacheClient != null) {
                if (this.fieldCacheServer != null) {
                    break Label_0026;
                }
            }
            try {
                this.initFieldCache();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (this.worldObj.isRemote && this.fieldCacheClient.size() == 0) {
            return;
        }
        if (!this.worldObj.isRemote && this.fieldCacheServer.size() == 0) {
            return;
        }
        Set<Field> fieldSet = null;
        if (this.worldObj.isRemote) {
            fieldSet = this.fieldCacheClient;
        }
        else {
            fieldSet = this.fieldCacheServer;
        }
        for (final Field field : fieldSet) {
            try {
                final Object obj = NetworkUtil.getFieldValueFromStream(field, buffer, this.worldObj);
                field.set(this, obj);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }
    
    public void handlePacketData(final Side side, final EntityPlayer player) {
    }
}
