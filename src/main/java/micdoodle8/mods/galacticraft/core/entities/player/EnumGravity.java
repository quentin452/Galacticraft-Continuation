package micdoodle8.mods.galacticraft.core.entities.player;

public enum EnumGravity
{
    down(0, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f), 
    up(1, 1.0f, 0.0f, 0.0f, -1.0f, 0.0f, -1.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f), 
    west(2, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f), 
    east(3, 0.0f, 1.0f, -1.0f, 0.0f, 0.0f, 0.0f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f), 
    south(4, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.5f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f), 
    north(5, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f);
    
    private final int intValue;
    private final float pitchGravityX;
    private final float pitchGravityY;
    private final float yawGravityX;
    private final float yawGravityY;
    private final float yawGravityZ;
    private final float thetaX;
    private final float thetaZ;
    private final float sneakVecX;
    private final float sneakVecY;
    private final float sneakVecZ;
    private final float eyeVecX;
    private final float eyeVecY;
    private final float eyeVecZ;
    private static final EnumGravity[] GDirections;
    
    private EnumGravity(final int value, final float pitchX, final float pitchY, final float yawX, final float yawY, final float yawZ, final float thetaX, final float thetaZ, final float sneakX, final float sneakY, final float sneakZ, final float eyeX, final float eyeY, final float eyeZ) {
        this.intValue = value;
        this.pitchGravityX = pitchX;
        this.pitchGravityY = pitchY;
        this.yawGravityX = yawX;
        this.yawGravityY = yawY;
        this.yawGravityZ = yawZ;
        this.thetaX = thetaX;
        this.thetaZ = thetaZ;
        this.sneakVecX = sneakX;
        this.sneakVecY = sneakY;
        this.sneakVecZ = sneakZ;
        this.eyeVecX = eyeX;
        this.eyeVecY = eyeY;
        this.eyeVecZ = eyeZ;
    }
    
    public int getIntValue() {
        return this.intValue;
    }
    
    public float getPitchGravityX() {
        return this.pitchGravityX;
    }
    
    public float getPitchGravityY() {
        return this.pitchGravityY;
    }
    
    public float getYawGravityX() {
        return this.yawGravityX;
    }
    
    public float getYawGravityY() {
        return this.yawGravityY;
    }
    
    public float getYawGravityZ() {
        return this.yawGravityZ;
    }
    
    public float getThetaX() {
        return this.thetaX;
    }
    
    public float getThetaZ() {
        return this.thetaZ;
    }
    
    public float getSneakVecX() {
        return this.sneakVecX;
    }
    
    public float getSneakVecY() {
        return this.sneakVecY;
    }
    
    public float getSneakVecZ() {
        return this.sneakVecZ;
    }
    
    public float getEyeVecX() {
        return this.eyeVecX;
    }
    
    public float getEyeVecY() {
        return this.eyeVecY;
    }
    
    public float getEyeVecZ() {
        return this.eyeVecZ;
    }
    
    public static EnumGravity[] getGDirections() {
        return EnumGravity.GDirections;
    }
    
    static {
        GDirections = new EnumGravity[] { EnumGravity.down, EnumGravity.up, EnumGravity.west, EnumGravity.east, EnumGravity.south, EnumGravity.north };
    }
}
