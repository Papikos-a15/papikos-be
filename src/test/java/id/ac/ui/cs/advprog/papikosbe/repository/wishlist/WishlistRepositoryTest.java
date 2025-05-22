package id.ac.ui.cs.advprog.papikosbe.repository.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class WishlistRepositoryTest {

    @Autowired
    private WishlistRepository wishlistRepository;
    private Wishlist wishlist1;
    private Wishlist wishlist2;
    private UUID kosId1;

    @BeforeEach
    public void setUp() {

        UUID userId1 = UUID.randomUUID();
        kosId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID kosId2 = UUID.randomUUID();

        wishlist1 = new Wishlist(userId1, kosId1);
        wishlist2 = new Wishlist(userId2, kosId2);

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

    @Test
    public void testFindAllByKosId() {
        List<Wishlist> wishlistByKos = wishlistRepository.findByKosId(kosId1);
        assertEquals(1, wishlistByKos.size());
        assertEquals(kosId1, wishlistByKos.getFirst().getKosId());
    }
}
