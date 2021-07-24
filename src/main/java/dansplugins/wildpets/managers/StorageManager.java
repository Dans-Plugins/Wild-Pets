package dansplugins.wildpets.managers;

public class StorageManager {

    private static StorageManager instance;

    private StorageManager() {

    }

    public static StorageManager getInstance() {
        if (instance == null) {
            instance = new StorageManager();
        }
        return instance;
    }

    public void save() {
        // TODO
    }

    public void load() {
        // TODO
    }

}
