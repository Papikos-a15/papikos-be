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

    @PostMapping("/add")
    public ResponseEntity<Kos> addKos(@RequestBody Kos kos) {
        Kos addedKos = kosService.addKos(kos);
        return ResponseEntity.status(201).body(addedKos);
    }

    @GetMapping("/list")
    public ResponseEntity<List<Kos>> getAllKos() {
        List<Kos> kosList = kosService.getAllKos();
        return ResponseEntity.status(200).body(kosList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Kos> getKosById(@PathVariable UUID id) {
        Kos foundKos = kosService.getKosById(id);
        if (foundKos == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(200).body(foundKos);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<Kos> updateKos(@PathVariable UUID id, @RequestBody Kos updatedKos) {
        Kos kosUpdated = kosService.updateKos(id, updatedKos);
        if (kosUpdated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(201).body(kosUpdated);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Nullable> deleteKos(@PathVariable UUID id) {
        kosService.deleteKos(id);
        Kos check = kosService.getKosById(id);
        if (check == null) {
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.notFound().build();
    }
}
