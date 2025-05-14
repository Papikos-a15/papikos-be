package id.ac.ui.cs.advprog.papikosbe.service.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;

import java.util.List;
import java.util.UUID;

public interface WishlistService {
    public void addWishlist(Wishlist wishlist);
    public void removeWishlist(UUID wishlistId);
    public List<Wishlist> getAllWishlists();
}