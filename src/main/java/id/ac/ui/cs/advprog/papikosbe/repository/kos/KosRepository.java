package id.ac.ui.cs.advprog.papikosbe.repository.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface KosRepository extends JpaRepository<Kos, UUID> {
    // In‑memory store for Kos objects.
    public Kos save(Kos kos);
    public Kos getKosById(UUID id);
    public List<Kos> getAllKos();
    public Kos updateKos(UUID id, Kos updatedKos);
    public boolean deleteKos(UUID id);
}