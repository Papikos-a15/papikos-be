package id.ac.ui.cs.advprog.papikosbe.strategy.kos;

import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class KosSearchStrategyTest {

    private List<Kos> testKosList;
    private Kos kos1, kos2, kos3, kos4;

    @BeforeEach
    void setUp() {
        // Setup test data using builder and manual field setting
        
        // Kos 1
        kos1 = Kos.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Kos Permata")
                .address("Jl. Margonda Raya No. 10")
                .description("Kos pria dekat kampus")
                .price(1200000.0)
                .maxCapacity(5)
                .build();
        kos1.setAvailableRooms(5);
        kos1.setAvailable(true);
        
        // Kos 2
        kos2 = Kos.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Kos Melati")
                .address("Jl. Pondok Cina No. 5")
                .description("Kos wanita dengan AC")
                .price(1500000.0)
                .maxCapacity(8)
                .build();
        kos2.setAvailableRooms(8);
        kos2.setAvailable(true);
        
        // Kos 3 - Unavailable
        kos3 = Kos.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Kos Dahlia")
                .address("Jl. Margonda Raya No. 25")
                .description("Kos exclusive dengan kamar mandi dalam")
                .price(1800000.0)
                .maxCapacity(10)
                .build();
        kos3.setAvailableRooms(0);  // No rooms available
        kos3.setAvailable(false);
        
        // Kos 4
        kos4 = Kos.builder()
                .id(UUID.randomUUID())
                .ownerId(UUID.randomUUID())
                .name("Kos Permata Biru")
                .address("Jl. Jatiwaringin No. 8")
                .description("Kos pria/wanita")
                .price(1000000.0)
                .maxCapacity(6)
                .build();
        kos4.setAvailableRooms(6);
        kos4.setAvailable(true);
        
        testKosList = Arrays.asList(kos1, kos2, kos3, kos4);
    }

    @Test
    void nameStrategy_ShouldFilterByName() {
        NameSearchStrategy strategy = new NameSearchStrategy();
        
        List<Kos> result1 = strategy.search(testKosList, "Permata");
        assertEquals(2, result1.size());
        assertTrue(result1.contains(kos1));
        assertTrue(result1.contains(kos4));
        
        List<Kos> result2 = strategy.search(testKosList, "Melati");
        assertEquals(1, result2.size());
        assertTrue(result2.contains(kos2));
        
        // Should be case insensitive
        List<Kos> result3 = strategy.search(testKosList, "permata");
        assertEquals(2, result3.size());
    }

    @Test
    void priceRangeStrategy_ShouldFilterByPriceRange() {
        PriceRangeSearchStrategy strategy = new PriceRangeSearchStrategy();
        
        Map<String, Double> priceRange = new HashMap<>();
        priceRange.put("min", 1000000.0);
        priceRange.put("max", 1500000.0);
        
        List<Kos> result = strategy.search(testKosList, priceRange);
        assertEquals(3, result.size());
        assertTrue(result.contains(kos1));
        assertTrue(result.contains(kos2));
        assertTrue(result.contains(kos4));
        assertFalse(result.contains(kos3)); // Too expensive
    }

    @Test
    void locationStrategy_ShouldFilterByLocation() {
        LocationSearchStrategy strategy = new LocationSearchStrategy();
        
        List<Kos> result = strategy.search(testKosList, "Margonda");
        assertEquals(2, result.size());
        assertTrue(result.contains(kos1));
        assertTrue(result.contains(kos3));
        
        // Should be case insensitive
        List<Kos> result2 = strategy.search(testKosList, "margonda");
        assertEquals(2, result2.size());
    }

    @Test
    void availabilityStrategy_ShouldFilterByAvailability() {
        AvailabilitySearchStrategy strategy = new AvailabilitySearchStrategy();
        
        List<Kos> availableResult = strategy.search(testKosList, true);
        assertEquals(3, availableResult.size());
        assertTrue(availableResult.contains(kos1));
        assertTrue(availableResult.contains(kos2));
        assertTrue(availableResult.contains(kos4));
        
        List<Kos> unavailableResult = strategy.search(testKosList, false);
        assertEquals(1, unavailableResult.size());
        assertTrue(unavailableResult.contains(kos3));
    }
}