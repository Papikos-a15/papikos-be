package id.ac.ui.cs.advprog.papikosbe.repository;

import id.ac.ui.cs.advprog.papikosbe.model.Wishlist;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class WishlistRepository {

    public Wishlist save(Wishlist wishlist) {
        return null;
    }

    public Optional<Wishlist> findById(UUID wishlistId) {
        return Optional.empty();
    }

    public void deleteById(UUID wishlistId) {
    }

    public List<Wishlist> findAll() {
        return null;
    }
}
