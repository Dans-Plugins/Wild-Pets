package dansplugins.wildpets.pet.record;

import dansplugins.wildpets.exceptions.PetRecordNotFoundException;
import dansplugins.wildpets.pet.Pet;

import java.util.HashSet;
import java.util.UUID;

public class PetRecordRepository {
    private final HashSet<PetRecord> petRecords = new HashSet<>();

    public HashSet<PetRecord> getPetRecords() {
        return petRecords;
    }

    public boolean addPetRecord(Pet pet) {
        return petRecords.add(new PetRecord(pet));
    }

    public boolean removePetRecord(UUID entityUUID) {
        PetRecord recordToRemove = null;
        for (PetRecord record : petRecords) {
            if (record.getUniqueID().equals(entityUUID)) {
                recordToRemove = record;
                break;
            }
        }
        if (recordToRemove == null) {
            return false;
        }
        return petRecords.remove(recordToRemove);
    }

    public PetRecord getPetRecord(UUID entityUUID) throws PetRecordNotFoundException {
        for (PetRecord record : petRecords) {
            if (record.getUniqueID().equals(entityUUID)) {
                return record;
            }
        }
        throw new PetRecordNotFoundException("Pet record for entity with UUID " + entityUUID + " not found.");
    }

    /**
     * Returns the existing record for this pet, creating one from its current
     * state first if none exists yet.
     */
    public PetRecord getOrCreatePetRecord(Pet pet) throws PetRecordNotFoundException {
        try {
            return getPetRecord(pet.getUniqueID());
        } catch (PetRecordNotFoundException e) {
            addPetRecord(pet);
            return getPetRecord(pet.getUniqueID());
        }
    }
}
