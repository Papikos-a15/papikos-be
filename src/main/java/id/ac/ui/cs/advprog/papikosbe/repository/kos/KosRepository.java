package id.ac.ui.cs.advprog.papikosbe.repository.kos;

import org.springframework.stereotype.Repository;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;

import java.util.*;

@Repository
public class KosRepository {
    // In‑memory store for Kos objects.
    private final Map<UUID, Kos> store = new HashMap<>();

    public Kos save(Kos kos) {
        if (kos.getId() == null) {
            UUID uuid = UUID.randomUUID();
            kos.setId(uuid);
        }
        UUID id = kos.getId();
        store.put(id, kos);
        return store.get(id);
    }

    public Kos getKosById(UUID id) {
        return store.get(id);
    }

    public List<Kos> getAllKos() {
        return new ArrayList<>(store.values());
    }

    public Kos updateKos(UUID id, Kos updatedKos) {
        if (store.containsKey(id)) {
            Kos existingKos = store.get(id);
            existingKos.setName(updatedKos.getName());
            existingKos.setAddress(updatedKos.getAddress());
            existingKos.setDescription(updatedKos.getDescription());
            existingKos.setPrice(updatedKos.getPrice());
            existingKos.setAvailable(updatedKos.isAvailable());
            // Save the updated Kos.
            store.put(id, existingKos);
            return existingKos;
        }
        return null;
    }

    public boolean deleteKos(UUID id) {
        return store.remove(id) != null;
    }
}