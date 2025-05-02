package id.ac.ui.cs.advprog.papikosbe.controller;

import id.ac.ui.cs.advprog.papikosbe.model.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<Wishlist>> getAllWishlists() {
        List<Wishlist> wishlists = wishlistService.getAllWishlists();
        return new ResponseEntity<>(wishlists, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addWishlist(@RequestBody Wishlist wishlist) {
        Map<String, String> response = new HashMap<>();

        if (wishlist == null) {
            response.put("message", "Wishlist cannot be null");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            wishlistService.addWishlist(wishlist);
            response.put("message", "Wishlist added successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Map<String, String>> removeWishlist(@PathVariable UUID wishlistId) {
        Map<String, String> response = new HashMap<>();

        if (wishlistId == null) {
            response.put("message", "Invalid wishlist ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        wishlistService.removeWishlist(wishlistId);
        response.put("message", "Wishlist removed successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Wishlist>> getWishlistsByUserId(@PathVariable UUID userId) {
        if (userId == null) {
            return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
        }

        List<Wishlist> allWishlists = wishlistService.getAllWishlists();
        List<Wishlist> userWishlists = allWishlists.stream()
                .filter(wishlist -> wishlist.getUserId().equals(userId))
                .collect(Collectors.toList());

        return new ResponseEntity<>(userWishlists, HttpStatus.OK);
    }
}