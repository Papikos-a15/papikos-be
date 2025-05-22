package id.ac.ui.cs.advprog.papikosbe.service.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    Wishlist addWishlist(UUID userId, UUID kosId);
    void removeWishlist(UUID wishlistId);
    List<Wishlist> getAllWishlists();
}