package id.ac.ui.cs.advprog.papikosbe.model.wishlist;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;

import java.util.UUID;

@Getter
@Setter
@Entity
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private UUID userId;
    private UUID kosId;

    public Wishlist() {}

    public Wishlist(UUID userId, UUID kosId) {
        if (userId == null || kosId == null) {
            throw new IllegalArgumentException("User ID and Kos ID cannot be null");
        }
        this.userId = userId;
        this.kosId = kosId;
    }
}
