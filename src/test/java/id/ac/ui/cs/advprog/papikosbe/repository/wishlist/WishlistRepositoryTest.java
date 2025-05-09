package id.ac.ui.cs.advprog.papikosbe.repository.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WishlistRepositoryTest {

    private WishlistRepository wishlistRepository;
    private Wishlist wishlist1;
    private Wishlist wishlist2;

    @BeforeEach
    public void setUp() {
        wishlistRepository = new WishlistRepository();

        UUID wishlistId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID kosId = UUID.randomUUID();

        UUID wishlist2Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UUID kos2Id = UUID.randomUUID();

        wishlist1 = new Wishlist(wishlistId, userId, kosId);
        wishlist1.setWishlistId(UUID.randomUUID());
        wishlist1.setUserId(UUID.randomUUID());
        wishlist1.setKosId(UUID.randomUUID());

        wishlist2 = new Wishlist(wishlist2Id, user2Id, kos2Id);
        wishlist2.setWishlistId(UUID.randomUUID());
        wishlist2.setUserId(UUID.randomUUID());
        wishlist2.setKosId(UUID.randomUUID());

        wishlistRepository.save(wishlist1);
        wishlistRepository.save(wishlist2);
    }

    @Test
    public void testSaveWishlist() {
        List<Wishlist> allWishlist = wishlistRepository.findAll();
        assertEquals(2, allWishlist.size());
        assertTrue(allWishlist.contains(wishlist1));
        assertTrue(allWishlist.contains(wishlist2));
    }

    @Test
    public void testFindByIdSuccess() {
        Optional<Wishlist> found = wishlistRepository.findById(wishlist1.getId());
        assertTrue(found.isPresent());
        assertEquals(wishlist1.getUserId(), found.get().getUserId());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Wishlist> found = wishlistRepository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    public void testDeleteById() {
        wishlistRepository.deleteById(wishlist1.getId());
        Optional<Wishlist> deleted = wishlistRepository.findById(wishlist1.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    public void testFindAll() {
        List<Wishlist> allWishlist = wishlistRepository.findAll();
        assertEquals(2, allWishlist.size());
    }
}
