package micdoodle8.mods.miccore;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cpw.mods.fml.common.versioning.ComparableVersion;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.IFMLCallHook;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import micdoodle8.mods.galacticraft.core.util.URLUtil;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
public class DepLoader implements IFMLLoadingPlugin, IFMLCallHook
{
    private static ByteBuffer downloadBuffer;
    private static final String owner = "GalacticraftCore";
    private static DepLoadInst inst;

    public static void load() {
        if (DepLoader.inst == null) {
            (DepLoader.inst = new DepLoadInst()).load();
        }
    }

    public String[] getASMTransformerClass() {
        return null;
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return this.getClass().getName();
    }

    public void injectData(final Map<String, Object> data) {
    }

    public Void call() {
        load();
        return null;
    }

    public String getAccessTransformerClass() {
        return null;
    }

    static {
        DepLoader.downloadBuffer = ByteBuffer.allocateDirect(8388608);
    }

    public static class Downloader extends JOptionPane implements IDownloadDisplay
    {
        private JDialog container;
        private JLabel currentActivity;
        private JProgressBar progress;
        boolean stopIt;
        Thread pokeThread;

        private Box makeProgressPanel() {
            final Box box = Box.createVerticalBox();
            box.add(Box.createRigidArea(new Dimension(0, 10)));
            JLabel welcomeLabel = new JLabel("<html><b><font size='+1'>GalacticraftCore is setting up your minecraft environment</font></b></html>");
            box.add(welcomeLabel);
            welcomeLabel.setAlignmentY(0.0f);
            welcomeLabel = new JLabel("<html>Please wait, GalacticraftCore has some tasks to do before you can play</html>");
            welcomeLabel.setAlignmentY(0.0f);
            box.add(welcomeLabel);
            box.add(Box.createRigidArea(new Dimension(0, 10)));
            box.add(this.currentActivity = new JLabel("Currently doing ..."));
            box.add(Box.createRigidArea(new Dimension(0, 10)));
            (this.progress = new JProgressBar(0, 100)).setStringPainted(true);
            box.add(this.progress);
            box.add(Box.createRigidArea(new Dimension(0, 30)));
            return box;
        }

        @Override
        public JDialog makeDialog() {
            if (this.container != null) {
                return this.container;
            }
            this.setMessageType(1);
            this.setMessage(this.makeProgressPanel());
            this.setOptions(new Object[] { "Stop" });
            this.addPropertyChangeListener(evt -> {
                if (evt.getSource() == Downloader.this && evt.getPropertyName() == "value") {
                    Downloader.this.requestClose("This will stop minecraft from launching\nAre you sure you want to do this?");
                }
            });
            (this.container = new JDialog(null, "Hello", Dialog.ModalityType.MODELESS)).setResizable(false);
            this.container.setLocationRelativeTo(null);
            this.container.add(this);
            this.updateUI();
            this.container.pack();
            this.container.setMinimumSize(this.container.getPreferredSize());
            this.container.setVisible(true);
            this.container.setDefaultCloseOperation(0);
            this.container.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(final WindowEvent e) {
                    Downloader.this.requestClose("Closing this window will stop minecraft from launching\nAre you sure you wish to do this?");
                }
            });
            return this.container;
        }

        protected void requestClose(final String message) {
            final int shouldClose = JOptionPane.showConfirmDialog(this.container, message, "Are you sure you want to stop?", 0, 2);
            if (shouldClose == 0) {
                this.container.dispose();
            }
            this.stopIt = true;
            if (this.pokeThread != null) {
                this.pokeThread.interrupt();
            }
        }

        @Override
        public void updateProgressString(final String progressUpdate, final Object... data) {
            if (this.currentActivity != null) {
                this.currentActivity.setText(String.format(progressUpdate, data));
            }
        }

        @Override
        public void resetProgress(final int sizeGuess) {
            if (this.progress != null) {
                this.progress.getModel().setRangeProperties(0, 0, 0, sizeGuess, false);
            }
        }

        @Override
        public void updateProgress(final int fullLength) {
            if (this.progress != null) {
                this.progress.getModel().setValue(fullLength);
            }
        }

        @Override
        public void setPokeThread(final Thread currentThread) {
            this.pokeThread = currentThread;
        }

        @Override
        public boolean shouldStopIt() {
            return this.stopIt;
        }

        @Override
        public void showErrorDialog(final String name, final String url) {
            final JEditorPane ep = new JEditorPane("text/html", "<html>GalacticraftCore was unable to download required library " + name + "<br>Check your internet connection and try restarting or download it manually from<br><a href=\"" + url + "\">" + url + "</a> and put it in your mods folder</html>");
            ep.setEditable(false);
            ep.setOpaque(false);
            ep.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(final HyperlinkEvent event) {
                    try {
                        if (event.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                            Desktop.getDesktop().browse(event.getURL().toURI());
                        }
                    }
                    catch (Exception ex) {}
                }
            });
            JOptionPane.showMessageDialog(null, ep, "A download error has occured", 0);
        }
    }

    public static class DummyDownloader implements IDownloadDisplay
    {
        @Override
        public void resetProgress(final int sizeGuess) {
        }

        @Override
        public void setPokeThread(final Thread currentThread) {
        }

        @Override
        public void updateProgress(final int fullLength) {
        }

        @Override
        public boolean shouldStopIt() {
            return false;
        }

        @Override
        public void updateProgressString(final String string, final Object... data) {
        }

        @Override
        public Object makeDialog() {
            return null;
        }

        @Override
        public void showErrorDialog(final String name, final String url) {
        }
    }

    public static class VersionedFile
    {
        public final Pattern pattern;
        public final String filename;
        public final ComparableVersion version;
        public final String name;

        public VersionedFile(final String filename, final Pattern pattern) {
            this.pattern = pattern;
            this.filename = filename;
            final Matcher m = pattern.matcher(filename);
            if (m.matches()) {
                this.name = m.group(1);
                this.version = new ComparableVersion(m.group(2));
            }
            else {
                this.name = null;
                this.version = null;
            }
        }

        public boolean matches() {
            return this.name != null;
        }
    }

    public static class Dependency
    {
        public String url;
        public VersionedFile file;
        public String existing;
        public boolean coreLib;

        public Dependency(final String url, final VersionedFile file, final boolean coreLib) {
            this.url = url;
            this.file = file;
            this.coreLib = coreLib;
        }
    }

    public static class DepLoadInst
    {
        private final File modsDir;
        private final File v_modsDir;
        private IDownloadDisplay downloadMonitor;
        private JDialog popupWindow;
        private final Map<String, Dependency> depMap;
        private final HashSet<String> depSet;

        public DepLoadInst() {
            this.depMap = new HashMap<>();
            this.depSet = new HashSet<>();
            final String mcVer = (String)FMLInjectionData.data()[4];
            final File mcDir = (File)FMLInjectionData.data()[6];
            this.modsDir = new File(mcDir, "mods");
            this.v_modsDir = new File(mcDir, "mods/" + mcVer);
            System.out.println("MicdoodleCore searching for dependencies in mods file: " + this.modsDir.getAbsolutePath());
            if (!this.v_modsDir.exists() && !this.v_modsDir.mkdirs()) {
                System.err.println("Failed to create mods subdirectory: " + this.v_modsDir.getAbsolutePath() + " !!!");
            }
        }

        private void addClasspath(final String name) {
            try {
                final URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
                final Class sysclass = URLClassLoader.class;
                try {
                    final Method method = sysclass.getDeclaredMethod("addURL", URL.class);
                    method.setAccessible(true);
                    method.invoke(sysloader, new File(this.v_modsDir, name).toURI().toURL());
                }
                catch (Throwable t) {
                    t.printStackTrace();
                    throw new IOException("Error, could not add URL to system classloader");
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void deleteMod(File mod) throws IOException, NoSuchFieldException, IllegalAccessException {
            if (mod.delete()) {
                return;
            }

            ClassLoader classLoader = getClassLoader(DepLoader.class);

            deleteURLFromClassLoader(classLoader, mod.toURI().toURL());

            if (!mod.delete()) {
                mod.deleteOnExit();

                String msg = "GalacticraftCore was unable to delete file " + mod.getPath()
                    + " the game will now try to delete it on exit. If this dialog appears again, delete it manually.";

                System.err.println(msg);

                if (!GraphicsEnvironment.isHeadless()) {
                    JOptionPane.showMessageDialog(null, msg, "An update error has occurred", 0);
                }

                System.exit(1);
            }
        }

        private ClassLoader getClassLoader(Class<?> clazz) {
            return clazz.getClassLoader();
        }

        private void deleteURLFromClassLoader(ClassLoader classLoader, URL url)
            throws IOException, NoSuchFieldException, IllegalAccessException {

            Field ucpField = classLoader.getClass().getDeclaredField("ucp");
            ucpField.setAccessible(true);

            Object ucp = ucpField.get(classLoader);

            Field loadersField = ucp.getClass().getDeclaredField("loaders");
            loadersField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<URLClassLoader> loaders = (List<URLClassLoader>) loadersField.get(ucp);

            for (URLClassLoader loader : loaders) {
                removeURLFromLoader(loader, url);
            }
        }

        private void removeURLFromLoader(URLClassLoader loader, URL url)
            throws NoSuchFieldException, IllegalAccessException, IOException {

            Field loaderMapField = loader.getClass().getDeclaredField("loaderMap");
            loaderMapField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<String, Closeable> loaderMap =
                (Map<String, Closeable>) loaderMapField.get(loader);

            String urlString = URLUtil.urlNoFragString(url);

            Closeable closeable = loaderMap.get(urlString);

            if (closeable != null) {
                closeable.close();
                loaderMap.remove(urlString);
            }
        }

        private void download(final Dependency dep) {
            this.popupWindow = (JDialog)this.downloadMonitor.makeDialog();
            final File libFile = new File(this.v_modsDir, dep.file.filename);
            try {
                final URL libDownload = new URL(dep.url + '/' + dep.file.filename);
                this.downloadMonitor.updateProgressString("Downloading file %s", libDownload.toString());
                System.out.format("Downloading file %s\n", libDownload.toString());
                final URLConnection connection = libDownload.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("User-Agent", "GalacticraftCore Downloader");
                final int sizeGuess = connection.getContentLength();
                this.download(connection.getInputStream(), sizeGuess, libFile);
                this.downloadMonitor.updateProgressString("Download complete");
                System.out.println("Download complete");
                this.scanDepInfo(libFile);
            }
            catch (Exception e) {
                libFile.delete();
                if (this.downloadMonitor.shouldStopIt()) {
                    System.err.println("You have stopped the downloading operation before it could complete");
                    System.exit(1);
                    return;
                }
                this.downloadMonitor.showErrorDialog(dep.file.filename, dep.url + '/' + dep.file.filename);
                throw new RuntimeException("A download error occured", e);
            }
        }

        private void download(final InputStream is, final int sizeGuess, final File target) throws Exception {
            if (sizeGuess > DepLoader.downloadBuffer.capacity()) {
                throw new Exception(String.format("The file %s is too large to be downloaded by GalacticraftCore - the download is invalid", target.getName()));
            }
            DepLoader.downloadBuffer.clear();
            int fullLength = 0;
            this.downloadMonitor.resetProgress(sizeGuess);
            try {
                this.downloadMonitor.setPokeThread(Thread.currentThread());
                final byte[] smallBuffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(smallBuffer)) >= 0) {
                    DepLoader.downloadBuffer.put(smallBuffer, 0, bytesRead);
                    fullLength += bytesRead;
                    if (this.downloadMonitor.shouldStopIt()) {
                        break;
                    }
                    this.downloadMonitor.updateProgress(fullLength);
                }
                is.close();
                this.downloadMonitor.setPokeThread(null);
                DepLoader.downloadBuffer.limit(fullLength);
                DepLoader.downloadBuffer.position(0);
            }
            catch (InterruptedIOException e3) {
                Thread.interrupted();
                throw new Exception("Stop");
            }
            catch (IOException e) {
                throw e;
            }
            try {
                if (!target.exists()) {
                    target.createNewFile();
                }
                DepLoader.downloadBuffer.position(0);
                final FileOutputStream fos = new FileOutputStream(target);
                fos.getChannel().write(DepLoader.downloadBuffer);
                fos.close();
            }
            catch (Exception e2) {
                throw e2;
            }
        }

        private String checkExisting(final Dependency dep) throws IOException, NoSuchFieldException, IllegalAccessException {
            for (final File f : this.modsDir.listFiles()) {
                final VersionedFile vfile = new VersionedFile(f.getName(), dep.file.pattern);
                if (vfile.matches()) {
                    if (vfile.name.equals(dep.file.name)) {
                        if (!f.renameTo(new File(this.v_modsDir, f.getName()))) {
                            this.deleteMod(f);
                        }
                    }
                }
            }
            final File[] listFiles2 = this.v_modsDir.listFiles();
            final int length2 = listFiles2.length;
            int j = 0;
            while (j < length2) {
                final File f = listFiles2[j];
                final VersionedFile vfile = new VersionedFile(f.getName(), dep.file.pattern);
                if (vfile.matches() && vfile.name.equals(dep.file.name)) {
                    final int cmp = vfile.version.compareTo(dep.file.version);
                    if (cmp < 0) {
                        System.out.println("Deleted old version " + f.getName());
                        this.deleteMod(f);
                        return null;
                    }
                    if (cmp > 0) {
                        System.err.println("Warning: version of " + dep.file.name + ", " + vfile.version + " is newer than request " + dep.file.version);
                        return f.getName();
                    }
                    return f.getName();
                }
                else {
                    ++j;
                }
            }
            return null;
        }

        public void load() {
            this.scanDepInfos();
            if (this.depMap.isEmpty()) {
                return;
            }
            this.loadDeps();
            this.activateDeps();
        }

        private void activateDeps() {
            for (final Dependency dep : this.depMap.values()) {
                if (dep.coreLib) {
                    this.addClasspath(dep.existing);
                }
            }
        }

        private void loadDeps() {
            this.downloadMonitor = (FMLLaunchHandler.side().isClient() ? new Downloader() : new DummyDownloader());
            try {
                while (!this.depSet.isEmpty()) {
                    final Iterator<String> it = this.depSet.iterator();
                    final Dependency dep = this.depMap.get(it.next());
                    it.remove();
                    this.load(dep);
                }
            } catch (IOException | NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                if (this.popupWindow != null) {
                    this.popupWindow.setVisible(false);
                    this.popupWindow.dispose();
                }
            }
        }

        private void load(final Dependency dep) throws IOException, NoSuchFieldException, IllegalAccessException {
            dep.existing = this.checkExisting(dep);
            if (dep.existing == null) {
                this.download(dep);
                dep.existing = dep.file.filename;
            }
        }

        private List<File> modFiles() {
            final List<File> list = new LinkedList<File>();
            list.addAll(Arrays.asList(this.modsDir.listFiles()));
            list.addAll(Arrays.asList(this.v_modsDir.listFiles()));
            return list;
        }

        private void scanDepInfos() {
            for (final File file : this.modFiles()) {
                if (!file.getName().endsWith(".jar") && !file.getName().endsWith(".zip")) {
                    continue;
                }
                this.scanDepInfo(file);
            }
        }

        private void scanDepInfo(final File file) {
            try {
                final ZipFile zip = new ZipFile(file);
                ZipEntry e = zip.getEntry("dependanciesGC.info");
                if (e == null) {
                    e = zip.getEntry("dependenciesGC.info");
                }
                if (e != null) {
                    this.loadJSon(zip.getInputStream(e));
                }
                zip.close();
            }
            catch (Exception e2) {
                System.err.println("Failed to load dependanciesGC.info from " + file.getName() + " as JSON");
                e2.printStackTrace();
            }
        }

        private void loadJSon(final InputStream input) throws IOException {
            final InputStreamReader reader = new InputStreamReader(input);
            final JsonElement root = new JsonParser().parse((Reader)reader);
            if (root.isJsonArray()) {
                this.loadJSonArr(root);
            }
            else {
                this.loadJson(root.getAsJsonObject());
            }
            reader.close();
        }

        private void loadJSonArr(final JsonElement root) throws IOException {
            for (final JsonElement node : root.getAsJsonArray()) {
                this.loadJson(node.getAsJsonObject());
            }
        }

        private void loadJson(final JsonObject node) throws IOException {
            final boolean obfuscated = ((LaunchClassLoader)DepLoader.class.getClassLoader()).getClassBytes("net.minecraft.world.World") == null;
            final String testClass = node.get("class").getAsString();
            if (Launch.classLoader.getClassBytes(testClass) != null) {
                return;
            }
            final String repo = node.get("repo").getAsString();
            String filename = node.get("file").getAsString();
            if (!obfuscated && node.has("dev")) {
                filename = node.get("dev").getAsString();
            }
            final boolean coreLib = node.has("coreLib") && node.get("coreLib").getAsBoolean();
            Pattern pattern = null;
            try {
                if (node.has("pattern")) {
                    pattern = Pattern.compile(node.get("pattern").getAsString());
                }
            }
            catch (PatternSyntaxException e) {
                System.err.println("Invalid filename pattern: " + node.get("pattern"));
                e.printStackTrace();
            }
            if (pattern == null) {
                pattern = Pattern.compile("(\\w+).*?([\\d\\.]+)[-\\w]*\\.[^\\d]+");
            }
            final VersionedFile file = new VersionedFile(filename, pattern);
            if (!file.matches()) {
                throw new RuntimeException("Invalid filename format for dependency: " + filename);
            }
            this.addDep(new Dependency(repo, file, coreLib));
        }

        private void addDep(final Dependency newDep) {
            if (this.mergeNew(this.depMap.get(newDep.file.name), newDep)) {
                this.depMap.put(newDep.file.name, newDep);
                this.depSet.add(newDep.file.name);
            }
        }

        private boolean mergeNew(final Dependency oldDep, final Dependency newDep) {
            if (oldDep == null) {
                return true;
            }
            final Dependency newest = (newDep.file.version.compareTo(oldDep.file.version) > 0) ? newDep : oldDep;
            newest.coreLib = (newDep.coreLib || oldDep.coreLib);
            return newest == newDep;
        }
    }

    public interface IDownloadDisplay
    {
        void resetProgress(final int p0);

        void setPokeThread(final Thread p0);

        void updateProgress(final int p0);

        boolean shouldStopIt();

        void updateProgressString(final String p0, final Object... p1);

        Object makeDialog();

        void showErrorDialog(final String p0, final String p1);
    }
}
