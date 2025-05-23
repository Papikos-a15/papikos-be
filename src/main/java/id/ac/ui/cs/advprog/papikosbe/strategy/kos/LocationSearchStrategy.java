package id.ac.ui.cs.advprog.papikosbe.strategy.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocationSearchStrategy implements KosSearchStrategy {
    @Override
    public List<Kos> search(List<Kos> kosList, Object criteria) {
        // Validate criteria type
        if (!(criteria instanceof String)) {
            throw new IllegalArgumentException("Location search criteria must be a String");
        }
        
        String location = (String) criteria;
        
        // Filter kos list by address (case insensitive, partial match)
        return kosList.stream()
                .filter(kos -> kos.getAddress() != null && 
                        kos.getAddress().toLowerCase().contains(location.toLowerCase()))
                .collect(Collectors.toList());
    }
}