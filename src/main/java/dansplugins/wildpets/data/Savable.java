package dansplugins.wildpets.data;

import java.util.Map;

/**
 * Interface for objects that can be serialized/deserialized to/from a map of strings.
 */
public interface Savable {

    /**
     * Saves the object in the form of a map of strings to strings.
     * @return The saved data.
     */
    Map<String, String> save();

    /**
     * Takes a map of strings to strings and loads values in from it.
     * @param data The data to load.
     */
    void load(Map<String, String> data);
}
