package id.ac.ui.cs.advprog.papikosbe.service.kos;

import org.springframework.stereotype.Service;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.repository.kos.KosRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
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
        return kosRepository.findAll();
    }

    @Override
    public Optional<Kos> getKosById(UUID id) {
        return kosRepository.findById(id);
    }

    @Override
    public Optional<Kos> updateKos(UUID id, Kos updatedKos) {
        Optional<Kos> foundKos = kosRepository.findById(id);
        if (foundKos.isPresent()) {
            foundKos.get().setName(updatedKos.getName());
            foundKos.get().setDescription(updatedKos.getDescription());
            foundKos.get().setAddress(updatedKos.getAddress());
            foundKos.get().setPrice(updatedKos.getPrice());
            foundKos.get().setAvailable(updatedKos.isAvailable());
        }
        return foundKos;
    }

    @Override
    public void deleteKos(UUID id) {
        kosRepository.deleteById(id);
    }
}
