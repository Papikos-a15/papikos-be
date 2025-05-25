package id.ac.ui.cs.advprog.papikosbe.model.wishlist;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class WishlistTest {

    private Wishlist wishlist;
    private UUID dummyUserId;
    private UUID dummyKosId;

    @BeforeEach
    public void setUp() {
        dummyUserId = UUID.randomUUID();
        dummyKosId = UUID.randomUUID();

        wishlist = new Wishlist(dummyUserId, dummyKosId);
        wishlist.setId(UUID.randomUUID());
    }

    @Test
    public void testWishlistInitialization() {
        assertNotNull(wishlist.getId());
        assertEquals(dummyUserId, wishlist.getUserId());
        assertEquals(dummyKosId, wishlist.getKosId());
    }

    @Test
    public void testWishlistIdNotNull() {
        assertNotNull(wishlist.getId(), "Wishlist ID should not be null");
    }

    @Test
    public void testSetKosId() {
        UUID newKosId = UUID.randomUUID();
        wishlist.setKosId(newKosId);
        assertEquals(newKosId, wishlist.getKosId());
    }

    @Test
    public void testSetUserId() {
        UUID newUserId = UUID.randomUUID();
        wishlist.setUserId(newUserId);
        assertEquals(newUserId, wishlist.getUserId());
    }

    @Test
    public void testWishlistConstructorWithNullUserId() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Wishlist(null, dummyKosId);
        });
        assertEquals("User ID and Kos ID cannot be null", thrown.getMessage());
    }

    @Test
    public void testWishlistConstructorWithNullKosId() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Wishlist(dummyUserId, null);
        });
        assertEquals("User ID and Kos ID cannot be null", thrown.getMessage());
    }

    @Test
    public void testWishlistConstructorWithNullUserIdAndKosId() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            new Wishlist(null, null);
        });
        assertEquals("User ID and Kos ID cannot be null", thrown.getMessage());
    }

    @Test
    public void testWishlistNoArgConstructor() {
        Wishlist noArgWishlist = new Wishlist();
        assertNull(noArgWishlist.getUserId(), "User ID should be null by default");
        assertNull(noArgWishlist.getKosId(), "Kos ID should be null by default");
        assertNull(noArgWishlist.getId(), "ID should be null until explicitly set");
    }
}