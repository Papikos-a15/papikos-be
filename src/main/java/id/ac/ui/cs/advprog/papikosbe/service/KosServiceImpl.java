package id.ac.ui.cs.advprog.papikosbe.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import id.ac.ui.cs.advprog.papikosbe.model.Kos;
import id.ac.ui.cs.advprog.papikosbe.repository.KosRepository;

import java.util.List;

@Service
public class KosServiceImpl implements KosService {

    @Autowired
    private KosRepository kosRepository;

    @Override
    public Kos addKos(Kos kos) {
        if (kos != null) {
            return kosRepository.save(kos);
        }
        return null;
    }

    @Override
    public List<Kos> getAllKos() {
        return kosRepository.getAllKos();
    }

    @Override
    public Kos getKosById(String id) {
        return kosRepository.getKosById(id);
    }

    @Override
    public Kos updateKos(String id, Kos updatedKos) {
        return kosRepository.updateKos(id, updatedKos);
    }

    @Override
    public void deleteKos(String id) {
    }
}
