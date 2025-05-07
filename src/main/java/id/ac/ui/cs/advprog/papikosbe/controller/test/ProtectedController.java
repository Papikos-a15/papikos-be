package id.ac.ui.cs.advprog.papikosbe.controller.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dummy controller untuk TDD JWT-filter.
 */
@RestController
public class ProtectedController {

    @GetMapping("/protected")
    public String protectedEndpoint() {
        // TODO: ubah menjadi logika bisnis sebenarnya jika perlu
        return "ok";
    }
}
