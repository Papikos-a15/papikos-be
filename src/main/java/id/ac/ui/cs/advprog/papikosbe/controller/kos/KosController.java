package id.ac.ui.cs.advprog.papikosbe.controller.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/management")
public class KosController {
    private final KosService kosService;
    private final KosSearchService kosSearchService;

    public KosController(KosService kosService, KosSearchService kosSearchService) {
        this.kosService = kosService;
        this.kosSearchService = kosSearchService;

    }

    @PostMapping("/add")
    public ResponseEntity<Kos> addKos(@RequestBody Kos kos) {
        Kos addedKos = kosService.addKos(kos);
        return ResponseEntity.status(201).body(addedKos);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Kos>> getAllKos() {
        List<Kos> kosList = kosService.getAllKos().join();
        return ResponseEntity.status(200).body(kosList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kos> getKosById(@PathVariable UUID id) {
        Optional<Kos> foundKos = kosService.getKosById(id);
        if (foundKos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return foundKos.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Kos> updateKos(@PathVariable UUID id, @RequestBody Kos updatedKos) {
        Optional<Kos> kosUpdated = kosService.updateKos(id, updatedKos);
        if (kosUpdated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return kosUpdated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/addAvailable")
    public ResponseEntity<Kos> addAvailableRooms(@RequestBody Kos kos) {
        if (kos.getAvailableRooms().equals(kos.getMaxCapacity())) {
            return ResponseEntity.internalServerError().body(kos);
        }

        Optional<Kos> kosUpdated = kosService.addAvailableRoom(kos.getId());
        if (kosUpdated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return kosUpdated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/subtractAvailable")
    public ResponseEntity<Kos> subtractAvailableRooms(@RequestBody Kos kos) {
        if (kos.getAvailableRooms().equals(0)) {
            return ResponseEntity.internalServerError().body(kos);
        }

        Optional<Kos> kosUpdated = kosService.subtractAvailableRoom(kos.getId());
        if (kosUpdated.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return kosUpdated.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Nullable> deleteKos(@PathVariable UUID id) {
        kosService.deleteKos(id);
        Optional<Kos> check = kosService.getKosById(id);
        if (check.isEmpty()) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/search")
    public CompletableFuture<ResponseEntity<List<Kos>>> searchKos(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean availability,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {

        return kosService.getAllKos()
                .thenApply(allKos -> {
                    // If no search parameters provided, return all kos
                    if (name == null && location == null && availability == null
                            && minPrice == null && maxPrice == null) {
                        return ResponseEntity.ok(allKos);
                    }

                    // Build search criteria map
                    Map<String, Object> searchCriteria = new HashMap<>();

                    // Add individual search criteria
                    if (name != null) {
                        searchCriteria.put("name", name);
                    }

                    if (location != null) {
                        searchCriteria.put("location", location);
                    }

                    if (availability != null) {
                        searchCriteria.put("availability", availability);
                    }

                    // Handle price range criteria
                    if (minPrice != null && maxPrice != null) {
                        Map<String, Double> priceRange = new HashMap<>();
                        priceRange.put("min", minPrice);
                        priceRange.put("max", maxPrice);
                        searchCriteria.put("pricerange", priceRange);
                    }

                    // Single name search uses simple search
                    if (searchCriteria.size() == 1 && searchCriteria.containsKey("name")) {
                        return ResponseEntity.ok(
                                kosSearchService.search(allKos, "name", name)
                        );
                    }

                    // For all other cases, use multi-criteria search
                    return ResponseEntity.ok(
                            kosSearchService.multiSearch(allKos, searchCriteria)
                    );
                });
    }
}
