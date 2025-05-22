package id.ac.ui.cs.advprog.papikosbe.service.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.repository.wishlist.WishlistRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WishlistServiceImplTest {

    private WishlistServiceImpl wishlistServiceImpl;
    private WishlistRepository wishlistRepository;

    @BeforeEach
    public void setUp() {
        wishlistRepository = mock(WishlistRepository.class);
        wishlistServiceImpl = new WishlistServiceImpl(wishlistRepository);
    }

    @Test
    public void testAddWishlist() {
        UUID wishlistId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID kosId = UUID.randomUUID();

        Wishlist wishlist = new Wishlist(userId, kosId);
        wishlistServiceImpl.addWishlist(wishlist);

        verify(wishlistRepository, times(1)).save(wishlist);
    }

    @Test
    public void testRemoveWishlist() {
        UUID wishlistId = UUID.randomUUID();
        wishlistServiceImpl.removeWishlist(wishlistId);

        verify(wishlistRepository, times(1)).deleteById(wishlistId);
    }

    @Test
    public void testGetAllWishlists() {
        UUID wishlistId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID kosId = UUID.randomUUID();

        UUID wishlist2Id = UUID.randomUUID();
        UUID user2Id = UUID.randomUUID();
        UUID kos2Id = UUID.randomUUID();

        List<Wishlist> dummyWishlists = List.of(new Wishlist(userId, kosId), new Wishlist(user2Id, kos2Id));
        when(wishlistRepository.findAll()).thenReturn(dummyWishlists);

        List<Wishlist> result = wishlistServiceImpl.getAllWishlists();
        assertEquals(2, result.size());
        verify(wishlistRepository, times(1)).findAll();
    }
}