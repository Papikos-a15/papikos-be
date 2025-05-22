package id.ac.ui.cs.advprog.papikosbe.service.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.repository.wishlist.WishlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    public WishlistServiceImpl(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    public Wishlist addWishlist(UUID userId, UUID kosId) {
        Wishlist wishlist = new Wishlist(userId, kosId);
        wishlistRepository.save(wishlist);
        return wishlist;
    }

    public void removeWishlist(UUID wishlistId) {
        wishlistRepository.deleteById(wishlistId);
    }

    public List<Wishlist> getAllWishlists() {
        return wishlistRepository.findAll();
    }
}