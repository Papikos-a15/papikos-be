package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Wishlist;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WishlistRepository {

    private final Map<UUID, Wishlist> wishlistStore = new HashMap<>();

    public Wishlist save(Wishlist wishlist) {
        wishlistStore.put(wishlist.getId(), wishlist);
        return wishlist;
    }

    public Optional<Wishlist> findById(UUID wishlistId) {
        return Optional.ofNullable(wishlistStore.get(wishlistId));
    }

    public void deleteById(UUID wishlistId) {
        wishlistStore.remove(wishlistId);
    }

    public List<Wishlist> findAll() {
        return new ArrayList<>(wishlistStore.values());
    }
}
