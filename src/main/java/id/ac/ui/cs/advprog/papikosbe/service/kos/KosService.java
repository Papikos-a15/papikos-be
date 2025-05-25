package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface KosService {
    Kos addKos(Kos kos);
    CompletableFuture<List<Kos>> getAllKos();
    Optional<Kos> getKosById(UUID id);
    Optional<Kos> updateKos(UUID id, Kos updatedKos);
    Optional<Kos> addAvailableRoom(UUID id);
    Optional<Kos> subtractAvailableRoom(UUID id);
    void deleteKos(UUID id);
}
