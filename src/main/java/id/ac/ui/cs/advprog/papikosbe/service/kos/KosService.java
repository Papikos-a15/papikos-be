package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;

import java.util.List;
import java.util.UUID;

public interface KosService {
    Kos addKos(Kos kos);
    List<Kos> getAllKos();
    Kos getKosById(UUID id);
    Kos updateKos(UUID id, Kos updatedKos);
    void deleteKos(UUID id);
}
