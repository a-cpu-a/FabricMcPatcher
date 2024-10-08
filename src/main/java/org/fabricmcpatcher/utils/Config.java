package org.fabricmcpatcher.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class Config {
    private static Config instance = new Config();

    private static File jsonFile;
    private static boolean readOnly;

    public static final String MCPATCHER_PROPERTIES = "mcpatcher.properties";
    public static final String MCPATCHER_JSON = "mcpatcher.json";
    public static final String LAUNCHER_JSON = "launcher_profiles.json";
    public static final String VERSIONS_JSON = "versions.json";

    static final String TAG_MINECRAFT_VERSION = "minecraftVersion";
    static final String TAG_PATCHER_VERSION = "patcherVersion";
    static final String TAG_PRE_PATCH_STATE = "prePatchState";
    static final String TAG_MODIFIED_CLASSES = "modifiedClasses";
    static final String TAG_ADDED_CLASSES = "addedClasses";

    static final String VAL_BUILTIN = "builtIn";
    static final String VAL_EXTERNAL_ZIP = "externalZip";
    static final String VAL_EXTERNAL_JAR = "externalJar";

    static final String TAG_MAL_VERSION = ".MALVersion";

    private static final String TAG_SELECTED_PROFILE = "selectedProfile";

    public static final String MCPATCHER_PROFILE_NAME = "MCPatcher";

    private static final int VAL_FORMAT_CURRENT = 1;
    private static final int VAL_FORMAT_MIN = 1;
    private static final int VAL_FORMAT_MAX = 1;

    transient String selectedProfile = MCPATCHER_PROFILE_NAME;

    int format = VAL_FORMAT_CURRENT;
    String patcherVersion;
    int uiX = -1;
    int uiY = -1;
    int uiW = -1;
    int uiH = -1;
    int uiFlags;
    boolean betaWarningShown;
    boolean selectPatchedProfile = true;
    boolean fetchRemoteVersionList = true;
    boolean extraProfiling;
    String lastModDirectory;
    int floodMessageLimit = 1000;
    LinkedHashMap<String, String> logging = new LinkedHashMap<String, String>();
    LinkedHashMap<String, ProfileEntry> profiles = new LinkedHashMap<String, ProfileEntry>();

    static boolean load(File minecraftDir, boolean isGame) {
        jsonFile = new File(minecraftDir, MCPATCHER_JSON);
        instance = JsonUtils.parseJson(jsonFile, Config.class);
        if (instance == null || instance.format <= 0) {
            instance = new Config();
            if (isGame) {
                System.out.printf("WARNING: configuration file %s not found, using defaults\n", jsonFile);
            }
            save();
        } else if (instance.format < VAL_FORMAT_MIN) {
            instance.format = VAL_FORMAT_CURRENT;
            save();
        } else if (instance.format > VAL_FORMAT_MAX) {
            setReadOnly(true); // don't overwrite newer file
        }
        String profile = getSelectedLauncherProfile(minecraftDir);
        if (MCPatcherUtils.isNullOrEmpty(profile)) {
            if (isGame) {
                System.out.printf("WARNING: could not determine selected profile, defaulting to %s\n", MCPATCHER_PROFILE_NAME);
            }
            profile = MCPATCHER_PROFILE_NAME;
        } else if (!instance.profiles.containsKey(profile)) {
            if (isGame) {
                System.out.printf("WARNING: selected profile '%s' not found, using defaults\n", profile);
            }
        }
        instance.selectedProfile = profile;
        return true;
    }

    static boolean save() {
        boolean success = false;
        if (jsonFile != null && !readOnly) {
            success = JsonUtils.writeJson(instance, jsonFile);
        }
        return success;
    }

    private static String getSelectedLauncherProfile(File minecraftDir) {
        File path = new File(minecraftDir, LAUNCHER_JSON);
        JsonObject json = JsonUtils.parseJson(path);
        if (json != null) {
            JsonElement element = json.get(TAG_SELECTED_PROFILE);
            if (element != null && element.isJsonPrimitive()) {
                return element.getAsString();
            }
        }
        return null;
    }

    public static Config getInstance() {
        return instance;
    }

    public static void setReadOnly(boolean readOnly) {
        Config.readOnly = readOnly;
    }

    static Level getLogLevel(String category) {
        Level level = Level.INFO;
        String value = instance.logging.get(category);
        if (value != null) {
            try {
                level = Level.parse(value.trim().toUpperCase());
            } catch (Throwable e) {
            }
        }
        setLogLevel(category, level);
        return level;
    }

    static void setLogLevel(String category, Level level) {
        instance.logging.put(category, level.toString().toUpperCase());
    }

    /**
     * Gets a value from mcpatcher.json.
     *
     * @param tag          property name
     * @param defaultValue default value if not found in profile
     * @return String value
     */
    public static String getString(String mod, String tag, Object defaultValue) {
        LinkedHashMap<String, String> modConfig = instance.getModConfig(mod);
        String value = modConfig.get(tag);
        if (value == null) {
            modConfig.put(tag, defaultValue.toString());
            return defaultValue.toString();
        } else {
            return value;
        }
    }

    /**
     * Gets a value from mcpatcher.json.
     *
     * @param mod          name of mod
     * @param tag          property name
     * @param defaultValue default value if not found in profile
     * @return int value or 0
     */
    public static int getInt(String mod, String tag, int defaultValue) {
        int value;
        try {
            value = Integer.parseInt(getString(mod, tag, defaultValue));
        } catch (NumberFormatException e) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Gets a value from mcpatcher.json.
     *
     * @param mod          name of mod
     * @param tag          property name
     * @param defaultValue default value if not found in profile
     * @return boolean value
     */
    public static boolean getBoolean(String mod, String tag, boolean defaultValue) {
        String value = getString(mod, tag, defaultValue).toLowerCase();
        if (value.equals("false")) {
            return false;
        } else if (value.equals("true")) {
            return true;
        } else {
            return defaultValue;
        }
    }

    /**
     * Sets a value in mcpatcher.json.
     *
     * @param mod   name of mod
     * @param tag   property name
     * @param value property value (must support toString())
     */
    public static void set(String mod, String tag, Object value) {
        if (value == null) {
            remove(mod, tag);
            return;
        }
        instance.getModConfig(mod).put(tag, value.toString());
    }

    /**
     * Remove a value from mcpatcher.json.
     *
     * @param mod name of mod
     * @param tag property name
     */
    public static void remove(String mod, String tag) {
        instance.getModConfig(mod).remove(tag);
    }

    public static File getOptionsTxt(File dir, String name) {
        File origFile = new File(dir, name);
        if (name.endsWith(".txt")) {
            String version = MCPatcherUtils.getMinecraftVersion();
            while (!MCPatcherUtils.isNullOrEmpty(version)) {
                File newFile = new File(dir, name.replace(".txt", "." + version + ".txt"));
                if (newFile.isFile()) {
                    System.out.printf("Using %s instead of %s\n", newFile.getName(), name);
                    return newFile;
                }
                int dot = version.lastIndexOf('.');
                if (dot > 0) {
                    version = version.substring(0, dot);
                } else if (version.matches("\\d+w\\d+[a-z]")) {
                    version = version.substring(0, version.length() - 1);
                } else {
                    break;
                }
            }
        }
        return origFile;
    }

    String getSelectedProfileName() {
        if (MCPatcherUtils.isNullOrEmpty(selectedProfile)) {
            selectedProfile = MCPATCHER_PROFILE_NAME;
        }
        return selectedProfile;
    }

    ProfileEntry getSelectedProfile() {
        ProfileEntry profile = profiles.get(getSelectedProfileName());
        if (profile == null) {
            profile = new ProfileEntry();
            profiles.put(selectedProfile, profile);
        }
        return profile;
    }

    VersionEntry getSelectedVersion() {
        ProfileEntry profile = getSelectedProfile();
        VersionEntry version = profile.versions.get(profile.version);
        if (version == null) {
            version = new VersionEntry();
            profile.versions.put(profile.version, version);
        }
        return version;
    }

    ModEntry getModEntry(String mod) {
        return getSelectedVersion().mods.get(mod);
    }

    Collection<ModEntry> getModEntries() {
        return getSelectedVersion().mods.values();
    }

    private LinkedHashMap<String, String> getModConfig(String mod) {
        return getSelectedProfile().getModConfig(mod);
    }

    void removeMod(String mod) {
        getSelectedProfile().config.remove(mod);
        getSelectedVersion().mods.remove(mod);
    }

    void removeProfile(String name) {
        if (!name.equals(selectedProfile)) {
            profiles.remove(name);
        }
    }

    void removeVersion(String name) {
        if (!name.equals(getSelectedProfile().version)) {
            getSelectedProfile().versions.remove(name);
        }
    }

    Map<String, String> getPatchedVersionMap() {
        Map<String, String> map = new HashMap<String, String>();
        for (ProfileEntry profile : profiles.values()) {
            profile.versions.remove(null);
            profile.versions.remove("");
            for (Map.Entry<String, VersionEntry> entry : profile.versions.entrySet()) {
                String patchedVersion = entry.getKey();
                String unpatchedVersion = entry.getValue().original;
                map.put(patchedVersion, unpatchedVersion);
            }
        }
        return map;
    }

    boolean haveUICoords() {
        return uiX >= 0 && uiY >= 0 && uiW > 0 && uiH > 0;
    }

    static class ProfileEntry {
        String original;
        String version;
        LinkedHashMap<String, LinkedHashMap<String, String>> config = new LinkedHashMap<String, LinkedHashMap<String, String>>();
        LinkedHashMap<String, VersionEntry> versions = new LinkedHashMap<String, VersionEntry>();

        private LinkedHashMap<String, String> getModConfig(String mod) {
            LinkedHashMap<String, String> map = config.get(mod);
            if (map == null) {
                map = new LinkedHashMap<String, String>();
                config.put(mod, map);
            }
            return map;
        }
    }

    static class VersionEntry {
        String original;
        LinkedHashMap<String, ModEntry> mods = new LinkedHashMap<String, ModEntry>();
    }

    static class ModEntry {
        String type;
        boolean enabled;
        String path;
        String className;
        List<FileEntry> files;
    }

    static class FileEntry {
        String from;
        String to;

        private FileEntry() {
        }

        FileEntry(String from, String to) {
            this.from = from;
            this.to = to;
        }
    }
}