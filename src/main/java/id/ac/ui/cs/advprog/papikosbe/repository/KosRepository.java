package id.ac.ui.cs.advprog.papikosbe.repository;

import org.springframework.stereotype.Repository;
import id.ac.ui.cs.advprog.papikosbe.model.Kos;

import java.util.*;

@Repository
public class KosRepository {
    // In‑memory store for Kos objects.
    private final Map<Long, Kos> store = new HashMap<>();


    public Kos save(Kos kos) {
        String uuid = UUID.randomUUID().toString();
        kos.setId(uuid);
        store.put(Long.parseLong(uuid), kos);
        return kos;
    }

    public Kos getKosById(Long id) {
        return store.get(id);
    }

    public List<Kos> getAllKos() {
        return new ArrayList<>(store.values());
    }

    public Kos updateKos(Long id, Kos updatedKos) {
        if (store.containsKey(id)) {
            Kos existingKos = store.get(id);
            existingKos.setName(updatedKos.getName());
            existingKos.setAddress(updatedKos.getAddress());
            existingKos.setDescription(updatedKos.getDescription());
            existingKos.setPrice(updatedKos.getPrice());
            // Save the updated Kos.
            store.put(id, existingKos);
            return existingKos;
        }
        return null;
    }

    public boolean deleteKos(Long id) {
        return store.remove(id) != null;
    }
}