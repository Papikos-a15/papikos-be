package id.ac.ui.cs.advprog.papikosbe.strategy.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NameSearchStrategy implements KosSearchStrategy {
    @Override
    public List<Kos> search(List<Kos> kosList, Object criteria) {
        // Validate criteria type
        if (!(criteria instanceof String)) {
            throw new IllegalArgumentException("Name search criteria must be a String");
        }
        
        String name = (String) criteria;
        
        // Filter kos list by name (case insensitive, partial match)
        return kosList.stream()
                .filter(kos -> kos.getName() != null && 
                        kos.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
}