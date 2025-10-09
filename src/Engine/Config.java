package Engine;

import Utils.Colors;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * This class holds some constants like window width/height and resource folder locations
 * Paths to Resources/ and MapFiles/ are resolved at runtime so the application works
 * regardless of the IDE's working directory or how it's launched.
 */
public class Config {
    public static final int TARGET_FPS = 60;

    // At runtime we compute absolute paths for resources and map files so relative file access
    // works even when the IDE launches the JVM with a different working directory.
    public static final String RESOURCES_PATH;
    public static final String MAP_FILES_PATH;

    public static final int GAME_WINDOW_WIDTH = 800;
    public static final int GAME_WINDOW_HEIGHT = 605;
    public static final Color TRANSPARENT_COLOR = Colors.MAGENTA;

    // POWER_SAVER does not hog CPU as much, but can potentially stutter/lag on lower end computers if they cannot handle reaching the target FPS
    // MAX_PERFORMANCE will have the game do whatever it takes to reach the target FPS, even if that means hogging the CPU
    public static final GameLoopType GAME_LOOP_TYPE = GameLoopType.POWER_SAVER;

    static {
        // Try a sequence of likely locations and pick the first that exists.
        String resolvedResources = null;
        String resolvedMapFiles = null;

        try {
            String cwd = System.getProperty("user.dir");

            // Candidate 1: working-directory/Resources
            Path candidate = Paths.get(cwd, "Resources");
            if (Files.exists(candidate)) {
                resolvedResources = candidate.toAbsolutePath().toString() + File.separator;
            }

            // Candidate 2: working-directory/bobcatBrawlers/Resources (common when workspace root is one level up)
            if (resolvedResources == null) {
                Path candidate2 = Paths.get(cwd, "bobcatBrawlers", "Resources");
                if (Files.exists(candidate2)) {
                    resolvedResources = candidate2.toAbsolutePath().toString() + File.separator;
                }
            }

            // Candidate 3: classpath resource (e.g., inside jar or classes folder)
            if (resolvedResources == null) {
                URL resourceUrl = Config.class.getClassLoader().getResource("Resources");
                if (resourceUrl != null) {
                    if ("file".equals(resourceUrl.getProtocol())) {
                        try {
                            URI uri = resourceUrl.toURI();
                            resolvedResources = Paths.get(uri).toAbsolutePath().toString() + File.separator;
                        } catch (URISyntaxException e) {
                            // fallback to default name
                            resolvedResources = "Resources" + File.separator;
                        }
                    } else {
                        // non-file resource (inside jar) - keep logical path
                        resolvedResources = "Resources" + File.separator;
                    }
                }
            }

            // If still unresolved, default to working-dir/Resources (will likely fail but gives predictable path)
            if (resolvedResources == null) {
                resolvedResources = Paths.get(cwd, "Resources").toAbsolutePath().toString() + File.separator;
            }

            // MapFiles candidates, same approach
            Path mapCandidate = Paths.get(cwd, "MapFiles");
            if (Files.exists(mapCandidate)) {
                resolvedMapFiles = mapCandidate.toAbsolutePath().toString() + File.separator;
            } else {
                Path mapCandidate2 = Paths.get(cwd, "bobcatBrawlers", "MapFiles");
                if (Files.exists(mapCandidate2)) resolvedMapFiles = mapCandidate2.toAbsolutePath().toString() + File.separator;
            }

            if (resolvedMapFiles == null) {
                // try classpath resource
                URL mapUrl = Config.class.getClassLoader().getResource("MapFiles");
                if (mapUrl != null && "file".equals(mapUrl.getProtocol())) {
                    try {
                        resolvedMapFiles = Paths.get(mapUrl.toURI()).toAbsolutePath().toString() + File.separator;
                    } catch (URISyntaxException ignored) { }
                }
            }

            if (resolvedMapFiles == null) {
                resolvedMapFiles = Paths.get(cwd, "MapFiles").toAbsolutePath().toString() + File.separator;
            }

        } catch (Exception e) {
            // In unexpected failure cases, fallback to relative paths as before
            resolvedResources = "Resources" + File.separator;
            resolvedMapFiles = "MapFiles" + File.separator;
        }

        RESOURCES_PATH = resolvedResources;
        MAP_FILES_PATH = resolvedMapFiles;
    }

    // prevents Config from being instantiated
    private Config() { }
}
