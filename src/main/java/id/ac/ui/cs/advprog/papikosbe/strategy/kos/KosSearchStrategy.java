package id.ac.ui.cs.advprog.papikosbe.strategy.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import java.util.List;

public interface KosSearchStrategy {
    List<Kos> search(List<Kos> kosList, Object searchCriteria);
}