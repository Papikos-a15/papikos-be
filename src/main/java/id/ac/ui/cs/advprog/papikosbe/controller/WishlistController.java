package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.model.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<List<Wishlist>> getAllWishlists() {
        // TODO
        return null;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addWishlist(@RequestBody Wishlist wishlist) {
        // TODO
        return null;
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Map<String, String>> removeWishlist(@PathVariable UUID wishlistId) {
        // TODO
        return null;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Wishlist>> getWishlistsByUserId(@PathVariable UUID userId) {
        // TODO
        return null;
    }
}