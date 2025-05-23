package id.ac.ui.cs.advprog.papikosbe.service.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.strategy.kos.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KosSearchServiceImplTest {

    @Mock
    private NameSearchStrategy nameStrategy;

    @Mock
    private PriceRangeSearchStrategy priceRangeStrategy;

    @Mock
    private LocationSearchStrategy locationStrategy;

    @Mock
    private AvailabilitySearchStrategy availabilityStrategy;

    private KosSearchServiceImpl searchService; // Changed to impl type for testing internal state
    private List<Kos> testKosList;
    private Kos kos1, kos2;

    @BeforeEach
    void setUp() {
        // Set up test data
        kos1 = new Kos();
        kos1.setId(UUID.randomUUID());
        kos1.setName("Kos A");
        kos1.setAddress("Jalan Margonda 10");
        kos1.setPrice(1000000.0);
        kos1.setAvailable(true);

        kos2 = new Kos();
        kos2.setId(UUID.randomUUID());
        kos2.setName("Kos B");
        kos2.setAddress("Jalan Margonda 20");
        kos2.setPrice(1500000.0);
        kos2.setAvailable(false);

        testKosList = Arrays.asList(kos1, kos2);

        // Initialize service with mocked strategies
        List<KosSearchStrategy> strategies = Arrays.asList(
                nameStrategy, priceRangeStrategy, locationStrategy, availabilityStrategy
        );

        searchService = new KosSearchServiceImpl(strategies);
    }

    @Test
    void constructor_ShouldMapStrategiesByName() {
        // Use ReflectionTestUtils instead of anonymous subclass for cleaner approach
        @SuppressWarnings("unchecked")
        Map<String, KosSearchStrategy> mappedStrategies =
                (Map<String, KosSearchStrategy>) ReflectionTestUtils.getField(searchService, "strategies");

        // Verify mappings
        assertNotNull(mappedStrategies, "Strategies map should not be null");
        assertEquals(4, mappedStrategies.size(), "Should have mapped all 4 strategies");

        // Check strategy keys exist with correct naming convention
        assertTrue(mappedStrategies.containsKey("name"), "Should contain 'name' strategy");
        assertTrue(mappedStrategies.containsKey("pricerange"), "Should contain 'pricerange' strategy");
        assertTrue(mappedStrategies.containsKey("location"), "Should contain 'location' strategy");
        assertTrue(mappedStrategies.containsKey("availability"), "Should contain 'availability' strategy");

        // Check strategies are mapped to correct implementations
        assertSame(nameStrategy, mappedStrategies.get("name"), "Name strategy should be correctly mapped");
        assertSame(priceRangeStrategy, mappedStrategies.get("pricerange"), "Price range strategy should be correctly mapped");
        assertSame(locationStrategy, mappedStrategies.get("location"), "Location strategy should be correctly mapped");
        assertSame(availabilityStrategy, mappedStrategies.get("availability"), "Availability strategy should be correctly mapped");
    }

    @Test
    void search_ShouldUseCorrectStrategy() {
        // Given: Name strategy returns only kos1 when searching for "Kos A"
        when(nameStrategy.search(eq(testKosList), eq("Kos A")))
                .thenReturn(Collections.singletonList(kos1));

        // When: Searching with name strategy
        List<Kos> result = searchService.search(testKosList, "name", "Kos A");

        // Then: Should get filtered result and only call name strategy
        assertEquals(1, result.size(), "Should return one result");
        assertEquals(kos1.getId(), result.get(0).getId(), "Should return kos1");
        verify(nameStrategy).search(testKosList, "Kos A");
        verifyNoInteractions(priceRangeStrategy, locationStrategy, availabilityStrategy);
    }

    @Test
    void search_ShouldThrowExceptionForInvalidStrategy() {
        // When & Then: Should throw exception for non-existent strategy
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> searchService.search(testKosList, "nonexistent", "some value"),
                "Should throw IllegalArgumentException for invalid strategy name"
        );

        // Verify exception message is helpful
        assertTrue(exception.getMessage().contains("nonexistent"),
                "Exception message should mention the invalid strategy name");
    }

    @Test
    void multiSearch_ShouldApplyStrategiesInOrder() {
        // Given: Search criteria with both name and location
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("name", "Kos");
        criteria.put("location", "Margonda");

        // First filter by name returns both kos
        List<Kos> nameFilterResults = Arrays.asList(kos1, kos2);
        when(nameStrategy.search(eq(testKosList), eq("Kos")))
                .thenReturn(nameFilterResults);

        // Second filter by location returns only kos1 from the name filtered results
        when(locationStrategy.search(eq(nameFilterResults), eq("Margonda")))
                .thenReturn(Collections.singletonList(kos1));

        // When: Multi-search is executed
        List<Kos> result = searchService.multiSearch(testKosList, criteria);

        // Then: Should get results after both filters applied
        assertEquals(1, result.size(), "Should return one result after applying both filters");
        assertEquals(kos1.getId(), result.get(0).getId(), "Should return kos1");

        // Verify strategies were called with correct parameters in expected order
        verify(nameStrategy).search(testKosList, "Kos");
        verify(locationStrategy).search(nameFilterResults, "Margonda");
    }

    @Test
    void multiSearch_WithEmptyCriteria_ShouldReturnOriginalList() {
        // When: Multi-search with no criteria
        List<Kos> result = searchService.multiSearch(testKosList, Collections.emptyMap());

        // Then: Should return original list
        assertEquals(testKosList, result, "Should return the original list when no criteria are provided");
        verifyNoInteractions(nameStrategy, priceRangeStrategy, locationStrategy, availabilityStrategy);
    }

    @Test
    void multiSearch_WithPriceRange_ShouldUseCorrectCriteria() {
        // Given: Price range criteria
        Map<String, Object> criteria = new HashMap<>();
        Map<String, Double> priceRange = new HashMap<>();
        priceRange.put("min", 900000.0);
        priceRange.put("max", 1200000.0);
        criteria.put("pricerange", priceRange);

        // Mock price range filter to return only kos1
        when(priceRangeStrategy.search(eq(testKosList), eq(priceRange)))
                .thenReturn(Collections.singletonList(kos1));

        // When: Multi-search with price range
        List<Kos> result = searchService.multiSearch(testKosList, criteria);

        // Then: Should get price range filtered results
        assertEquals(1, result.size(), "Should return one result after price range filter");
        assertEquals(kos1.getId(), result.get(0).getId(), "Should return kos1");
        verify(priceRangeStrategy).search(testKosList, priceRange);
    }

    @Test
    void multiSearch_WithAvailability_ShouldUseCorrectCriteria() {
        // Given: Availability criteria
        Map<String, Object> criteria = new HashMap<>();
        criteria.put("availability", true);

        // Mock availability filter to return only kos1
        when(availabilityStrategy.search(eq(testKosList), eq(true)))
                .thenReturn(Collections.singletonList(kos1));

        // When: Multi-search with availability
        List<Kos> result = searchService.multiSearch(testKosList, criteria);

        // Then: Should get availability filtered results
        assertEquals(1, result.size(), "Should return one result after availability filter");
        assertEquals(kos1.getId(), result.get(0).getId(), "Should return kos1");
        verify(availabilityStrategy).search(testKosList, true);
    }
}