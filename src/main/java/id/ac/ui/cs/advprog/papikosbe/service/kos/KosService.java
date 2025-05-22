package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KosService {
    Kos addKos(Kos kos);
    List<Kos> getAllKos();
    Optional<Kos> getKosById(UUID id);
    Optional<Kos> updateKos(UUID id, Kos updatedKos);
    void deleteKos(UUID id);
}
