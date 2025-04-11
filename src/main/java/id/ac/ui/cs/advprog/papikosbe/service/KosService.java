package id.ac.ui.cs.advprog.papikosbe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import id.ac.ui.cs.advprog.papikosbe.model.Kos;
import id.ac.ui.cs.advprog.papikosbe.repository.KosRepository;

import java.util.List;
import java.util.Optional;

@Service
public class KosService {

    @Autowired
    private KosRepository kosRepository;

    public Kos addKos(Kos kos) {
    }

    public List<Kos> getAllKos() {
    }

    public Optional<Kos> getKosById(Long id) {
    }

    public Kos updateKos(Long id, Kos updatedKos) {
    }

    public void deleteKos(Long id) {
    }
}
