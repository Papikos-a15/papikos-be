package id.ac.ui.cs.advprog.papikosbe.repository.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
}
