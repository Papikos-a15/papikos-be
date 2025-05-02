package id.ac.ui.cs.advprog.papikosbe.service;

import id.ac.ui.cs.advprog.papikosbe.model.Kos;

import java.util.List;

public interface KosService {
    Kos addKos(Kos kos);
    List<Kos> getAllKos();
    Kos getKosById(String id);
    Kos updateKos(String id, Kos updatedKos);
    void deleteKos(String id);
}
