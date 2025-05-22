package id.ac.ui.cs.advprog.papikosbe.controller.wishlist;

import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.service.wishlist.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    private Wishlist testWishlist;
    private final UUID wishlistId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final UUID kosId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testWishlist = new Wishlist(userId, kosId);
    }

    @Test
    void testGetAllWishlists() {
        List<Wishlist> wishlists = new ArrayList<>();
        wishlists.add(testWishlist);
        when(wishlistService.getAllWishlists()).thenReturn(wishlists);

        ResponseEntity<List<Wishlist>> response = wishlistController.getAllWishlists();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(wishlistService, times(1)).getAllWishlists();
    }

    @Test
    void testGetAllWishlists_InternalServerError() {
        when(wishlistService.getAllWishlists()).thenThrow(new RuntimeException("Internal error"));

        ResponseEntity<List<Wishlist>> response = wishlistController.getAllWishlists();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(wishlistService, times(1)).getAllWishlists();
    }

    @Test
    void testAddWishlist() {
        ResponseEntity<Map<String, String>> response = wishlistController.addWishlist(testWishlist);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wishlist added successfully", response.getBody().get("message"));
        verify(wishlistService, times(1)).addWishlist(testWishlist);
    }

    @Test
    void testAddWishlist_WithIllegalArgumentException() {
        doThrow(new IllegalArgumentException("Invalid wishlist fields"))
                .when(wishlistService).addWishlist(any(Wishlist.class));

        ResponseEntity<Map<String, String>> response = wishlistController.addWishlist(testWishlist);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid wishlist fields", response.getBody().get("message"));
        verify(wishlistService, times(1)).addWishlist(testWishlist);
    }

    @Test
    void testAddWishlist_WithNullWishlist() {
        ResponseEntity<Map<String, String>> response = wishlistController.addWishlist(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wishlist cannot be null", response.getBody().get("message"));
        verify(wishlistService, never()).addWishlist(any());
    }

    @Test
    void testAddWishlist_WithException() {
        Map<String, String> response = new HashMap<>();
        doThrow(new RuntimeException("Unexpected error"))
                .when(wishlistService).addWishlist(any(Wishlist.class));

        ResponseEntity<Map<String, String>> result = wishlistController.addWishlist(testWishlist);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("An error occurred while adding the wishlist", result.getBody().get("message"));
        verify(wishlistService, times(1)).addWishlist(testWishlist);
    }

    @Test
    void testRemoveWishlist() {
        doNothing().when(wishlistService).removeWishlist(wishlistId);

        ResponseEntity<Map<String, String>> response = wishlistController.removeWishlist(wishlistId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wishlist removed successfully", response.getBody().get("message"));
        verify(wishlistService, times(1)).removeWishlist(wishlistId);
    }

    @Test
    void testRemoveWishlist_InvalidUUID() {
        ResponseEntity<Map<String, String>> response = wishlistController.removeWishlist(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid wishlist ID", response.getBody().get("message"));
        verify(wishlistService, never()).removeWishlist(any());
    }

    @Test
    void testRemoveWishlist_InternalServerError() {
        doThrow(new RuntimeException("Internal error")).when(wishlistService).removeWishlist(wishlistId);

        ResponseEntity<Map<String, String>> response = wishlistController.removeWishlist(wishlistId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("An error occurred while removing the wishlist", response.getBody().get("message"));
        verify(wishlistService, times(1)).removeWishlist(wishlistId);
    }

    @Test
    void testGetWishlistsByUserId() {
        List<Wishlist> wishlists = new ArrayList<>();
        wishlists.add(testWishlist);
        when(wishlistService.getAllWishlists()).thenReturn(wishlists);

        ResponseEntity<List<Wishlist>> response = wishlistController.getWishlistsByUserId(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(userId, response.getBody().get(0).getUserId());
    }

    @Test
    void testGetWishlistsByUserId_IdNull() {
        ResponseEntity<List<Wishlist>> response = wishlistController.getWishlistsByUserId(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());  // Expecting BAD_REQUEST
        assertNotNull(response.getBody());  // The body should not be null
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetWishlistsByUserId_NoResults() {
        List<Wishlist> wishlists = new ArrayList<>();
        wishlists.add(testWishlist);
        when(wishlistService.getAllWishlists()).thenReturn(wishlists);

        ResponseEntity<List<Wishlist>> response = wishlistController.getWishlistsByUserId(UUID.randomUUID());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
        assertEquals(0, response.getBody().size());
    }

    @Test
    void testGetWishlistsByUserId_InternalServerError() {
        when(wishlistService.getAllWishlists()).thenThrow(new RuntimeException("Internal error"));

        ResponseEntity<List<Wishlist>> response = wishlistController.getWishlistsByUserId(userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(wishlistService, times(1)).getAllWishlists();
    }
}