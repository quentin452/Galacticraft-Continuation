package micdoodle8.mods.galacticraft.core.util;

public enum EnumColor
{
    BLACK("�0", "black", new int[] { 0, 0, 0 }), 
    DARK_BLUE("�1", "darkBlue", new int[] { 0, 0, 170 }), 
    DARK_GREEN("�2", "darkGreen", new int[] { 0, 170, 0 }), 
    DARK_AQUA("�3", "darkAqua", new int[] { 0, 170, 170 }), 
    DARK_RED("�4", "darkRed", new int[] { 170, 0, 0 }), 
    PURPLE("�5", "purple", new int[] { 170, 0, 170 }), 
    ORANGE("�6", "orange", new int[] { 255, 170, 0 }), 
    GREY("�7", "grey", new int[] { 170, 170, 170 }), 
    DARK_GREY("�8", "darkGrey", new int[] { 85, 85, 85 }), 
    INDIGO("�9", "indigo", new int[] { 85, 85, 255 }), 
    BRIGHT_GREEN("�a", "brightGreen", new int[] { 85, 255, 85 }), 
    AQUA("�b", "aqua", new int[] { 85, 255, 255 }), 
    RED("�c", "red", new int[] { 255, 85, 85 }), 
    PINK("�d", "pink", new int[] { 255, 85, 255 }), 
    YELLOW("�e", "yellow", new int[] { 255, 255, 85 }), 
    WHITE("�f", "white", new int[] { 255, 255, 255 });
    
    private final String code;
    private final int[] rgbCode;
    private final String unlocalizedName;
    
    private EnumColor(final String s, final String n, final int[] rgb) {
        this.code = s;
        this.unlocalizedName = n;
        this.rgbCode = rgb;
    }
    
    public String getCode() {
        return this.code;
    }
    
    public String getLocalizedName() {
        return GCCoreUtil.translate("color." + this.unlocalizedName);
    }
    
    public String getName() {
        return this.code + this.getLocalizedName();
    }
    
    public float getColor(final int index) {
        return this.rgbCode[index] / 255.0f;
    }
    
    @Override
    public String toString() {
        return this.code;
    }
}
