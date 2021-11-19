package dansplugins.wildpets.objects;

import preponderous.ponder.modifiers.Savable;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class PetRecord implements Savable {
    private UUID uniqueID;
    String name;
    private UUID ownerUUID;
    private int assignedID;

    public PetRecord(Pet pet) {
        uniqueID = pet.getUniqueID();
        name = pet.getName();
        ownerUUID = pet.getOwnerUUID();
        assignedID = pet.getAssignedID();
    }

    public UUID getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(UUID uniqueID) {
        this.uniqueID = uniqueID;
    }

    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    public int getAssignedID() {
        return assignedID;
    }

    public void setAssignedID(int assignedID) {
        this.assignedID = assignedID;
    }

    @Override
    public Map<String, String> save() {
        // TODO: implement
        return null;
    }

    @Override
    public void load(Map<String, String> map) {
        // TODO: implement
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PetRecord petRecord = (PetRecord) o;
        return uniqueID.equals(petRecord.uniqueID);
    }
}
