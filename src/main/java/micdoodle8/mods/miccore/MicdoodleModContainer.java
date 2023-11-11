package micdoodle8.mods.miccore;

import com.google.common.eventbus.*;
import cpw.mods.fml.common.*;
import java.util.*;
import cpw.mods.fml.common.versioning.*;

public class MicdoodleModContainer extends DummyModContainer
{
    public MicdoodleModContainer() {
        super(new ModMetadata());
        final ModMetadata meta = this.getMetadata();
        meta.modId = "Micdoodlecore";
        meta.name = "Micdoodle8 Core";
        meta.updateUrl = "http://www.micdoodle8.com/";
        meta.description = "Provides core features of Micdoodle8's mods";
        meta.authorList = Arrays.asList("micdoodle8, radfast");
        meta.url = "http://www.micdoodle8.com/";
    }
    
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        bus.register((Object)this);
        return true;
    }
    
    public List<ArtifactVersion> getDependencies() {
        final LinkedList<ArtifactVersion> deps = new LinkedList<ArtifactVersion>();
        deps.add(VersionParser.parseVersionReference("required-after:Forge@[10.12.2.1147,10.13.4.1614]"));
        return deps;
    }
    
    public VersionRange acceptableMinecraftVersionRange() {
        return VersionParser.parseRange("[1.7.2],[1.7.10]");
    }
}
