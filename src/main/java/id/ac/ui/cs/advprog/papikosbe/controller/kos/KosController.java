package id.ac.ui.cs.advprog.papikosbe.controller.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/management")
public class KosController {
    private final KosService kosService;

    public KosController(KosService kosService) {this.kosService = kosService;}

    @PostMapping
    public ResponseEntity<String> addKos(@RequestBody Kos kos) {}

    @GetMapping
    public List<Kos> getAllKos() {}

    @GetMapping
    public Kos getKosById(@RequestParam("id") UUID id) {}

    @PatchMapping
    public Kos updateKos(@RequestParam("id") UUID id, @RequestParam("updatedKos") Kos updatedKos) {}

    @DeleteMapping
    public void deleteKos(@RequestParam("id") UUID id) {}
}
