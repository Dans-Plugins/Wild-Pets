package dansplugins.wildpets.objects;

import java.util.HashSet;

public abstract class AbstractFamilialEntity {
    protected HashSet<Integer> parentIDs = new HashSet<>();
    protected HashSet<Integer> childIDs = new HashSet<>();

    public HashSet<Integer> getParentIDs() {
        return parentIDs;
    }

    public boolean addParent(int ID) {
        return parentIDs.add(ID);
    }

    public boolean removeParent(int ID) {
        return parentIDs.remove(ID);
    }

    public HashSet<Integer> getChildIDs() {
        return childIDs;
    }

    public boolean addChild(int ID) {
        return childIDs.add(ID);
    }

    public boolean removeChild(int ID) {
        return childIDs.remove(ID);
    }
}
