package id.ac.ui.cs.advprog.papikosbe.model;

import java.util.UUID;

public class Wishlist {

    private UUID wishlistId;
    private UUID userId;
    private UUID kosId;

    public Wishlist(UUID wishlistId, UUID userId, UUID kosId) {
        if (wishlistId == null || userId == null || kosId == null) {
            throw new IllegalArgumentException("Wishlist ID, User ID, and Kos ID cannot be null");
        }
        this.wishlistId = wishlistId;
        this.userId = userId;
        this.kosId = kosId;
    }

    public UUID getId() {
        return wishlistId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getKosId() {
        return kosId;
    }

    public void setWishlistId(UUID wishlistId) {
        this.wishlistId = wishlistId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setKosId(UUID kosId) {
        this.kosId = kosId;
    }
}
