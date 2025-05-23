package id.ac.ui.cs.advprog.papikosbe.strategy.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NameSearchStrategy implements KosSearchStrategy {
    @Override
    public List<Kos> search(List<Kos> kosList, Object criteria) {
        // Skeleton implementation that will fail tests
        return new ArrayList<>();
    }
}