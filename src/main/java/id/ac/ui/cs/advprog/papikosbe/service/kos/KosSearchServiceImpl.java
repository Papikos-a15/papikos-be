package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.strategy.kos.KosSearchStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class KosSearchServiceImpl implements KosSearchService {
    
    // Store strategies mapped by name for easy lookup
    protected final Map<String, KosSearchStrategy> strategies;

    public KosSearchServiceImpl(List<KosSearchStrategy> searchStrategies) {
        // Map each strategy by its class name (without "SearchStrategy" suffix and lowercase)
        this.strategies = searchStrategies.stream()
                .collect(Collectors.toMap(
                        strategy -> strategy.getClass().getSimpleName()
                                .replace("SearchStrategy", "")
                                .toLowerCase(),
                        Function.identity()
                ));
    }

    @Override
    public List<Kos> search(List<Kos> kosList, String strategyName, Object criteria) {
        // Get the requested strategy by name
        KosSearchStrategy strategy = strategies.get(strategyName);
        
        // Throw exception if strategy not found
        if (strategy == null) {
            throw new IllegalArgumentException("Search strategy not found: " + strategyName);
        }
        
        // Apply the strategy to filter the kos list
        return strategy.search(kosList, criteria);
    }

    @Override
    public List<Kos> multiSearch(List<Kos> kosList, Map<String, Object> searchCriteria) {
        // If no criteria provided, return original list
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return kosList;
        }
        
        // Start with the original list
        List<Kos> result = kosList;
        
        // Apply each search criterion sequentially
        for (Map.Entry<String, Object> entry : searchCriteria.entrySet()) {
            String strategyName = entry.getKey();
            Object criteria = entry.getValue();
            
            // Filter the result list with each strategy
            result = search(result, strategyName, criteria);
        }
        
        return result;
    }

    @Override
    public Map<String, KosSearchStrategy> getStrategies() {
        return this.strategies;
    }
}