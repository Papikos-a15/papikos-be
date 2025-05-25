package id.ac.ui.cs.advprog.papikosbe.repository.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByKosId(UUID kosId);
}