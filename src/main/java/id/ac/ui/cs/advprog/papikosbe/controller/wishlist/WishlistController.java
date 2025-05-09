package id.ac.ui.cs.advprog.papikosbe.controller.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.service.wishlist.WishlistService;
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
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @RequestMapping(value = "/api/v1/wishlists", method = RequestMethod.GET)
    public ResponseEntity<List<Wishlist>> getAllWishlists() {
        try {
            List<Wishlist> wishlists = wishlistService.getAllWishlists();
            return new ResponseEntity<>(wishlists, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error in getting all wishlists!");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/v1/wishlists", method = RequestMethod.POST)
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
        } catch (Exception e) {
            response.put("message", "An error occurred while adding the wishlist");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/v1/wishlists/{wishlistId}", method = RequestMethod.DELETE)
    public ResponseEntity<Map<String, String>> removeWishlist(@PathVariable UUID wishlistId) {
        Map<String, String> response = new HashMap<>();

        if (wishlistId == null) {
            response.put("message", "Invalid wishlist ID");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            wishlistService.removeWishlist(wishlistId);
            response.put("message", "Wishlist removed successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("message", "An error occurred while removing the wishlist");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/api/v1/wishlists/user/{userId}", method = RequestMethod.GET)
    public ResponseEntity<List<Wishlist>> getWishlistsByUserId(@PathVariable UUID userId) {
        if (userId == null) {
            return new ResponseEntity<>(List.of(), HttpStatus.BAD_REQUEST);
        }

        try {
            List<Wishlist> allWishlists = wishlistService.getAllWishlists();
            List<Wishlist> userWishlists = allWishlists.stream()
                    .filter(wishlist -> wishlist.getUserId().equals(userId))
                    .collect(Collectors.toList());

            if (userWishlists.isEmpty()) {
                return new ResponseEntity<>(List.of(), HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(userWishlists, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error in getting wishlists by user ID!");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}