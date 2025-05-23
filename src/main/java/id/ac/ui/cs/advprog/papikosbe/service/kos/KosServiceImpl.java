package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.observer.KosStatusChangedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.repository.kos.KosRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KosServiceImpl implements KosService {

    private final KosRepository kosRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Kos addKos(Kos kos) {
        if (kos != null) {
            kos.setAvailableRooms(kos.getMaxCapacity());
            kos.setAvailable(true);
            return kosRepository.save(kos);
        }
        return null;
    }

    @Async
    @Override
    public CompletableFuture<List<Kos>> getAllKos() {
        List<Kos> allKos = kosRepository.findAll();
        return CompletableFuture.completedFuture(allKos);
    }

    @Override
    public Optional<Kos> getKosById(UUID id) {
        return kosRepository.findById(id);
    }

    @Override
    public Optional<Kos> updateKos(UUID id, Kos updatedKos) {
        Optional<Kos> foundKos = kosRepository.findById(id);
        System.out.println("ini update kos");
        if (foundKos.isPresent()) {
            boolean avail = foundKos.get().isAvailable();
            System.out.println("avail: " + avail);

            foundKos.get().setName(updatedKos.getName());
            foundKos.get().setDescription(updatedKos.getDescription());
            foundKos.get().setAddress(updatedKos.getAddress());
            foundKos.get().setPrice(updatedKos.getPrice());
            foundKos.get().setAvailable(updatedKos.isAvailable());

            if (!avail && foundKos.get().isAvailable()) {
                System.out.println("yeah");
                eventPublisher.publishEvent(new KosStatusChangedEvent(
                        this,
                        foundKos.get().getId(),
                        foundKos.get().getName(),
                        true));
            }
        }

        return foundKos;
    }

    @Override
    public Optional<Kos> addAvailableRoom(UUID id) {
        Optional<Kos> foundKos = kosRepository.findById(id);
        if (foundKos.isPresent()) {
            Kos kos = foundKos.get();
            if (kos.getAvailableRooms() < kos.getMaxCapacity()) {
                kos.setAvailableRooms(kos.getAvailableRooms() + 1);
            }
            return foundKos;
        }
        return foundKos;
    }

    @Override
    public Optional<Kos> subtractAvailableRoom(UUID id) {
        Optional<Kos> foundKos = kosRepository.findById(id);
        if (foundKos.isPresent()) {
            Kos kos = foundKos.get();
            if (kos.getAvailableRooms() > 0) {
                kos.setAvailableRooms(kos.getAvailableRooms() - 1);
            }
            else if (kos.getAvailableRooms() == 0) {
                kos.setAvailable(false);
            }
            return foundKos;
        }
        return foundKos;
    }

    @Override
    public void deleteKos(UUID id) {
        kosRepository.deleteById(id);
    }
}
