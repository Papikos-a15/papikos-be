package id.ac.ui.cs.advprog.papikosbe.controller.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/management")
public class KosController {
    private final KosService kosService;

    public KosController(KosService kosService) {this.kosService = kosService;}

    @PostMapping
    public ResponseEntity<Kos> addKos(@RequestBody Kos kos) {
        Kos addedKos = kosService.addKos(kos);
        return ResponseEntity.status(201).body(addedKos);
    }

    @GetMapping
    public ResponseEntity<List<Kos>> getAllKos() {
        List<Kos> kosList = kosService.getAllKos();
        return ResponseEntity.status(200).body(kosList);
    }

    @GetMapping
    public ResponseEntity<Kos> getKosById(@RequestParam("id") UUID id) {
        Kos foundKos = kosService.getKosById(id);
        if (foundKos == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(200).body(foundKos);
    }

    @PatchMapping
    public ResponseEntity<Kos> updateKos(@RequestParam("id") UUID id, @RequestParam("updatedKos") Kos updatedKos) {
        Kos kosUpdated = kosService.updateKos(id, updatedKos);
        if (kosUpdated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(201).body(kosUpdated);
    }

    @DeleteMapping
    public ResponseEntity<Nullable> deleteKos(@RequestParam("id") UUID id) {
        kosService.deleteKos(id);
        Kos check = kosService.getKosById(id);
        if (check == null) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.notFound().build();
    }
}
