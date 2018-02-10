package fr.uga.pddl4j.test;

import java.io.File;

/**
 * Static class that contains all shared method for manipulate the benchmark
 * directory structure.
 *
 * @author Cédric Gerard
 * @version 0.1 - 23.06.16
 */
public abstract class Tools {

    /**
     * Check if benchmark are already here.
     * @param path the benchmark directory path
     * @return true if the benchmark file exist
     */
    public static boolean isBenchmarkExist(String path) {
        return new File(path).exists();
    }

}
