package id.ac.ui.cs.advprog.papikosbe.strategy.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PriceRangeSearchStrategy implements KosSearchStrategy {
    @Override
    public List<Kos> search(List<Kos> kosList, Object criteria) {
        // Validate criteria type
        if (!(criteria instanceof Map)) {
            throw new IllegalArgumentException("Price range search criteria must be a Map with min and max values");
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Double> priceRange = (Map<String, Double>) criteria;
        
        // Validate price range map has required keys
        if (!priceRange.containsKey("min") || !priceRange.containsKey("max")) {
            throw new IllegalArgumentException("Price range must contain 'min' and 'max' keys");
        }
        
        Double minPrice = priceRange.get("min");
        Double maxPrice = priceRange.get("max");
        
        // Filter kos list by price range
        return kosList.stream()
                .filter(kos -> kos.getPrice() >= minPrice && kos.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }
}