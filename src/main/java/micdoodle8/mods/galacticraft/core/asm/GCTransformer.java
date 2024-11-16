package micdoodle8.mods.galacticraft.core.asm;

import static micdoodle8.mods.galacticraft.core.asm.GCLoadingPlugin.debugOutputDir;
import static org.objectweb.asm.Opcodes.ASM5;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import com.google.common.collect.ImmutableMap;

public class GCTransformer implements IClassTransformer {

    private static final boolean DEBUG = Boolean.getBoolean("glease.debugasm");
    private static final ConcurrentMap<String, Integer> transformCounts = new ConcurrentHashMap<>();
    private final Map<String, TransformerFactory> transformers = ImmutableMap.<String, TransformerFactory>builder()
        .put(
            "net.minecraft.server.management.ServerConfigurationManager",
            new TransformerFactory(ServerConfigurationManagerVisitor::new))
        .build();

    static void catching(Exception e) {
        GCLoadingPlugin.LOGGER.fatal("Something went very wrong with class transforming! Aborting!!!", e);
        throw new RuntimeException("Transforming class", e);
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        final TransformerFactory factory = this.transformers.get(name);
        if (factory == null || factory.isInactive()) {
            return basicClass;
        }
        GCLoadingPlugin.LOGGER.info("Transforming class {}", name);
        final ClassReader cr = new ClassReader(basicClass);
        final ClassWriter cw = new ClassWriter(factory.isExpandFrames() ? ClassWriter.COMPUTE_FRAMES : 0);
        // we are very probably the last one to run.
        byte[] transformedBytes = null;
        if (DEBUG) {
            final int curCount = transformCounts.compute(transformedName, (k, v) -> v == null ? 0 : v + 1);
            final String infix = curCount == 0 ? "" : "_" + curCount;
            try (PrintWriter origOut = new PrintWriter(new File(debugOutputDir, name + infix + "_orig.txt"), "UTF-8");
                PrintWriter tranOut = new PrintWriter(new File(debugOutputDir, name + infix + "_tran.txt"), "UTF-8")) {
                cr.accept(
                    new TraceClassVisitor(factory.apply(ASM5, new TraceClassVisitor(cw, tranOut)), origOut),
                    factory.isExpandFrames() ? ClassReader.SKIP_FRAMES : 0);
                transformedBytes = cw.toByteArray();
            } catch (final Exception e) {
                GCLoadingPlugin.LOGGER
                    .warn("Unable to transform with debug output on. Now retrying without debug output.", e);
            }
        }
        if (transformedBytes == null || transformedBytes.length == 0) {
            try {
                cr.accept(factory.apply(ASM5, cw), factory.isExpandFrames() ? ClassReader.SKIP_FRAMES : 0);
                transformedBytes = cw.toByteArray();
            } catch (final Exception e) {
                catching(e);
            }
        }
        if (transformedBytes == null || transformedBytes.length == 0) {
            if (!DEBUG) {
                GCLoadingPlugin.LOGGER.fatal(
                    "Null or empty byte array created. Transforming will rollback as a last effort attempt to make things work! However features will not function!");
                return basicClass;
            }
            catching(new RuntimeException("Null or empty byte array created. This will not work well!"));
        }
        return transformedBytes;
    }
}
