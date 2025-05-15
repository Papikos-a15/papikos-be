package id.ac.ui.cs.advprog.papikosbe.service.kos;

import org.springframework.stereotype.Service;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.repository.kos.KosRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KosServiceImpl implements KosService {

    private final KosRepository kosRepository;

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
    public Kos getKosById(UUID id) {
        return kosRepository.getKosById(id);
    }

    @Override
    public Kos updateKos(UUID id, Kos updatedKos) {
        return kosRepository.updateKos(id, updatedKos);
    }

    @Override
    public void deleteKos(UUID id) {
        kosRepository.deleteKos(id);
    }
}
