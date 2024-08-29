package org.fabricmcpatcher.resource;


import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;
import org.fabricmcpatcher.FabricMcPatcher;
import org.fabricmcpatcher.utils.MCLogger;
import org.fabricmcpatcher.utils.MCPatcherUtils;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceList {
    private static final MCLogger logger = MCLogger.getLogger("Texture Pack");

    private static ResourceList instance;
    //private static final Map<ResourcePack, Integer> resourcePackOrder = new WeakHashMap<ResourcePack, Integer>();

    //private final ResourcePack resourcePack;
    //private final Set<IdentifierWithSource> allResources = new TreeSet<IdentifierWithSource>(new IdentifierWithSource.Comparator1());

    public static ResourceList getInstance() {
        if (instance == null) {
            /*List<ResourcePack> resourcePacks = TexturePackAPI.getResourcePacks(null);
            int order = resourcePacks.size();
            resourcePackOrder.clear();
            for (ResourcePack resourcePack : resourcePacks) {
                resourcePackOrder.put(resourcePack, order);
                order--;
            }*/
            instance = new ResourceList();
        }
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }
    /*
    public static int getResourcePackOrder(ResourcePack resourcePack) {
        Integer i = resourcePackOrder.get(resourcePack);
        return i == null ? Integer.MAX_VALUE : i;
    }

    private ResourceList() {
        this.resourcePack = null;
        for (ResourcePack resourcePack : TexturePackAPI.getResourcePacks(null)) {
            ResourceList sublist;
            if (resourcePack instanceof FileResourcePack) {
                sublist = new ResourceList((FileResourcePack) resourcePack);
            } else if (resourcePack instanceof DefaultResourcePack) {
                sublist = new ResourceList((DefaultResourcePack) resourcePack);
            } else if (resourcePack instanceof AbstractResourcePack) {
                sublist = new ResourceList((AbstractResourcePack) resourcePack);
            } else {
                continue;
            }
            allResources.removeAll(sublist.allResources);
            allResources.addAll(sublist.allResources);
        }
        logger.fine("new %s", this);
        if (logger.isLoggable(Level.FINEST)) {
            for (IdentifierWithSource resource : allResources) {
                logger.finest("%s -> %s", resource, resource.getSource().getName());
            }
        }
    }

    private ResourceList(FileResourcePack resourcePack) {
        this.resourcePack = resourcePack;
        scanZipFile(resourcePack.zipFile);
        logger.fine("new %s", this);
    }

    private ResourceList(DefaultResourcePack resourcePack) {
        this.resourcePack = resourcePack;
        String version = MCPatcherUtils.getMinecraftVersion();
        File jar = MCPatcherUtils.getMinecraftPath("versions", version, version + ".jar");
        if (jar.isFile()) {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(jar);
                scanZipFile(zipFile);
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                MCPatcherUtils.close(zipFile);
            }
        }
        try {
            Map<String, File> map = resourcePack.map;
            if (map != null) {
                for (Map.Entry<String, File> entry : map.entrySet()) {
                    String key = entry.getKey();
                    File file = entry.getValue();
                    Identifier resource = new Identifier(key);
                    addResource(resource, file.isFile(), file.isDirectory());
                }
            }
        } catch (NoSuchFieldError e) {
            // field not present in 1.5
        }
        if (!allResources.isEmpty()) {
            logger.fine("new %s", this);
        }
    }

    private ResourceList(AbstractResourcePack resourcePack) {
        this.resourcePack = resourcePack;
        File directory = resourcePack.file;
        if (directory == null || !directory.isDirectory()) {
            return;
        }
        Set<String> allFiles = new HashSet<String>();
        listAllFiles(directory, "", allFiles);
        for (String path : allFiles) {
            Identifier resource = TexturePackAPI.parsePath(path);
            if (resource != null) {
                File file = new File(directory, path);
                addResource(resource, file.isFile(), file.isDirectory());
            }
        }
        logger.fine("new %s", this);
    }

    private void scanZipFile(ZipFile zipFile) {
        if (zipFile == null) {
            return;
        }
        for (ZipEntry entry : Collections.list(zipFile.entries())) {
            String path = entry.getName();
            Identifier resource = TexturePackAPI.parsePath(path);
            if (resource != null) {
                addResource(resource, !entry.isDirectory(), entry.isDirectory());
            }
        }
    }*/

    private static void listAllFiles(File base, String subdir, Set<String> files) {
        File[] entries = new File(base, subdir).listFiles();
        if (entries == null) {
            return;
        }
        for (File file : entries) {
            String newPath = subdir + file.getName();
            if (files.add(newPath)) {
                if (file.isDirectory()) {
                    listAllFiles(base, subdir + file.getName() + '/', files);
                }
            }
        }
    }
/*
    private void addResource(Identifier resource, boolean isFile, boolean isDirectory) {
        if (isFile) {
            allResources.add(new IdentifierWithSource(resourcePack, resource));
        } else if (isDirectory) {
            if (!resource.getPath().endsWith("/")) {
                resource = new Identifier(resource.getNamespace(), resource.getPath() + '/');
            }
            allResources.add(new IdentifierWithSource(resourcePack, resource));
        }
    }
*/
    public List<Identifier> listMcPatcherResources(String directory, String suffix, boolean sortByFilename) {

        List<Identifier> ret = new ArrayList<>();

        for (String folder : FabricMcPatcher.CHECK_FOLDERS) {
            ret.addAll(listResources(folder+directory, suffix, true, false, sortByFilename));
        }

        return ret;
    }
    public List<Identifier> listResources(String directory, String suffix, boolean sortByFilename) {
        return listResources(directory, suffix, true, false, sortByFilename);
    }

    public List<Identifier> listResources(String directory, String suffix, boolean recursive, boolean directories, boolean sortByFilename) {
        return listResources(null, directory, suffix, recursive, directories, sortByFilename);
    }

    public List<Identifier> listResources(String namespace, String directory, String suffix, boolean recursive, boolean directories, final boolean sortByFilename) {
        if (suffix == null) {
            suffix = "";
        }
        if (MCPatcherUtils.isNullOrEmpty(directory)) {
            directory = "";
        } else if (directory.endsWith("/")) {
            directory.substring(0,directory.length()-1);
        }

        //MinecraftClient.getInstance().getResourceManager().findResources()

        assert !directories;

        String finalSuffix = suffix;
        Map<Identifier,Resource> resList = MinecraftClient.getInstance().getResourceManager().findResources(directory,(s)->{
            return s.getPath().endsWith(finalSuffix)
                    && (namespace==null || s.getNamespace().equals(namespace))
                    && (recursive || !s.getPath().contains("/"));
        });

        List<Identifier> list = new ArrayList<>(resList.keySet());

        if(sortByFilename) {
            list.sort(Identifier::compareTo);
        }
        return list;

        /*
        Set<Identifier> tmpList = new TreeSet<Identifier>(
            new IdentifierWithSource.Comparator1(true, sortByFilename ? suffix : null)
        );
        boolean allNamespaces = MCPatcherUtils.isNullOrEmpty(namespace);
        for (IdentifierWithSource resource : allResources) {
            if (directories != resource.isDirectory()) {
                continue;
            }
            if (!allNamespaces && !namespace.equals(resource.getNamespace())) {
                continue;
            }
            String path = resource.getPath();
            if (!path.endsWith(suffix)) {
                continue;
            }
            if (!path.startsWith(directory)) {
                continue;
            }
            if (!recursive) {
                String subpath = path.substring(directory.length());
                if (subpath.contains("/")) {
                    continue;
                }
            }
            tmpList.add(resource);
        }

        return new ArrayList<Identifier>(tmpList);*/
    }
/*
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ResourceList: ");
        if (resourcePack == null) {
            sb.append("(combined) ");
        } else {
            sb.append(resourcePack.getName()).append(' ');
        }
        int fileCount = 0;
        int directoryCount = 0;
        Set<String> namespaces = new HashSet<String>();
        for (IdentifierWithSource resource : allResources) {
            if (resource.isDirectory()) {
                directoryCount++;
            } else {
                fileCount++;
            }
            namespaces.add(resource.getNamespace());
        }
        sb.append(fileCount).append(" files, ");
        sb.append(directoryCount).append(" directories in ");
        sb.append(namespaces.size()).append(" namespaces");
        return sb.toString();
    }*/
}