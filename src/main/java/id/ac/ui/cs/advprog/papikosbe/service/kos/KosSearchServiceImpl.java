package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.strategy.kos.KosSearchStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class KosSearchServiceImpl implements KosSearchService {
    // This would store our strategies mapped by name
    private final Map<String, KosSearchStrategy> strategies;

    // Constructor will receive all KosSearchStrategy beans through dependency injection
    public KosSearchServiceImpl(List<KosSearchStrategy> searchStrategies) {
        // Skeleton implementation for TDD - will fail tests
        this.strategies = new HashMap<>();
        
        // We'll properly map the strategies later when implementing
        // Just adding empty map for now
    }
    
    @Override
    public List<Kos> search(List<Kos> kosList, String strategyName, Object criteria) {
        // Skeleton implementation that will fail tests
        // Will be properly implemented later following TDD
        return new ArrayList<>();
    }
    
    @Override
    public List<Kos> multiSearch(List<Kos> kosList, Map<String, Object> searchCriteria) {
        // Skeleton implementation that will fail tests
        // Will be properly implemented later following TDD
        return new ArrayList<>();
    }
    
    // For testing only - you can add this or modify the test
    @Override
    public Map<String, KosSearchStrategy> getStrategies() {
        return this.strategies;
    }
}