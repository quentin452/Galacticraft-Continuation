package micdoodle8.mods.galacticraft.core.util;

public class JavaUtil extends SecurityManager
{
    public static JavaUtil instance;
    
    public Class<?> getCaller() {
        return (Class<?>)this.getClassContext()[2];
    }
    
    public boolean isCalledBy(final Class<?> clazz) {
        final Class<?>[] context = (Class<?>[])this.getClassContext();
        for (int imax = Math.min(context.length, 6), i = 2; i < imax; ++i) {
            if (clazz == context[i]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCalledBy(final Class<?> clazz1, final Class<?> clazz2, final Class<?> clazz3) {
        final Class<?>[] context = (Class<?>[])this.getClassContext();
        for (int imax = Math.min(context.length, 6), i = 2; i < imax; ++i) {
            final Class<?> test = context[i];
            if (test == clazz1 || test == clazz2 || test == clazz3) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isCalledBy(final String name) {
        final Class<?>[] context = (Class<?>[])this.getClassContext();
        for (int imax = Math.min(context.length, 6), i = 2; i < imax; ++i) {
            if (context[i].getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        JavaUtil.instance = new JavaUtil();
    }
}
