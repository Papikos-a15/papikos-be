package id.ac.ui.cs.advprog.papikosbe.controller.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.service.wishlist.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/wishlists")
public class WishlistController {

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    public ResponseEntity<List<Wishlist>> getAllWishlists() {
        try {
            List<Wishlist> wishlists = wishlistService.getAllWishlists();
            return ResponseEntity.ok(wishlists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addWishlist(@RequestBody(required = false) Wishlist wishlist) {
        Map<String, String> response = new HashMap<>();

        if (wishlist == null) {
            response.put("message", "Wishlist cannot be null");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Wishlist newWishlist = wishlistService.addWishlist(wishlist);
            response.put("wishlistId", newWishlist.getId().toString());
            response.put("message", "Wishlist added successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while adding the wishlist");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Map<String, String>> removeWishlist(@PathVariable UUID wishlistId) {
        Map<String, String> response = new HashMap<>();

        if (wishlistId == null) {
            response.put("message", "Invalid wishlist ID");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            wishlistService.removeWishlist(wishlistId);
            response.put("message", "Wishlist removed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "An error occurred while removing the wishlist");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Wishlist>> getWishlistsByUserId(@PathVariable UUID userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body(List.of());
        }

        try {
            List<Wishlist> allWishlists = wishlistService.getAllWishlists();
            List<Wishlist> userWishlists = allWishlists.stream()
                    .filter(wishlist -> userId.equals(wishlist.getUserId()))
                    .toList();

            if (userWishlists.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NO_CONTENT);
            }

            return ResponseEntity.ok(userWishlists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}