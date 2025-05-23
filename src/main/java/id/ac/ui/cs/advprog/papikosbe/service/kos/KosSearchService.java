package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.strategy.kos.KosSearchStrategy;

import java.util.List;
import java.util.Map;

public interface KosSearchService {
    /**
     * Search for kos using a specific search strategy
     * 
     * @param kosList List of kos to search through
     * @param strategyName Name of the search strategy to use
     * @param criteria Search criteria for the strategy
     * @return Filtered list of kos
     */
    List<Kos> search(List<Kos> kosList, String strategyName, Object criteria);
    
    /**
     * Apply multiple search criteria using different strategies
     * 
     * @param kosList List of kos to search through
     * @param searchCriteria Map of strategy names to their criteria
     * @return Filtered list of kos
     */
    List<Kos> multiSearch(List<Kos> kosList, Map<String, Object> searchCriteria);

    public Map<String, KosSearchStrategy> getStrategies();
}