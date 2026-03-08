package dansplugins.wildpets.data;

import java.util.ArrayList;

/**
 * Interface for objects with owner and access control.
 */
public interface Lockable<T> {
    void setOwner(T toSet);
    T getOwner();
    void addToAccessList(T toAdd);
    void removeFromAccessList(T toRemove);
    boolean hasAccess(T toCheck);
    ArrayList<T> getAccessList();
}
