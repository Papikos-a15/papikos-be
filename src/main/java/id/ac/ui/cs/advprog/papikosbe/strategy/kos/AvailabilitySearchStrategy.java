package id.ac.ui.cs.advprog.papikosbe.strategy.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvailabilitySearchStrategy implements KosSearchStrategy {
    @Override
    public List<Kos> search(List<Kos> kosList, Object criteria) {
        // Validate criteria type
        if (!(criteria instanceof Boolean)) {
            throw new IllegalArgumentException("Availability search criteria must be a Boolean");
        }
        
        Boolean isAvailable = (Boolean) criteria;
        
        // Filter kos list by availability
        return kosList.stream()
                .filter(kos -> kos.isAvailable() == isAvailable)
                .collect(Collectors.toList());
    }
}