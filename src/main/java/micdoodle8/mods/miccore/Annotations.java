package micdoodle8.mods.miccore;

import java.lang.annotation.*;
import cpw.mods.fml.relauncher.*;

public interface Annotations
{
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface VersionSpecific {
        String version();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD })
    public @interface NetworkedField {
        Side targetSide();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface AltForVersion {
        String version();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    public @interface RuntimeInterface {
        String clazz();
        
        String modID();
        
        String[] altClasses() default {};
    }
}
