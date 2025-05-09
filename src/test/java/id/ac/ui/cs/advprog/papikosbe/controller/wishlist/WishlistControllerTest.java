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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        testWishlist = new Wishlist(wishlistId, userId, kosId);
    }

    @Test
    void testGetAllWishlists() {
        // Arrange
        List<Wishlist> wishlists = new ArrayList<>();
        wishlists.add(testWishlist);
        when(wishlistService.getAllWishlists()).thenReturn(wishlists);

        // Act
        ResponseEntity<List<Wishlist>> response = wishlistController.getAllWishlists();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(wishlistService, times(1)).getAllWishlists();
    }

    @Test
    void testAddWishlist() {
        // Arrange
        doNothing().when(wishlistService).addWishlist(any(Wishlist.class));

        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.addWishlist(testWishlist);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wishlist added successfully", response.getBody().get("message"));
        verify(wishlistService, times(1)).addWishlist(testWishlist);
    }

    @Test
    void testAddWishlist_WithIllegalArgumentException() {
        // Arrange
        doThrow(new IllegalArgumentException("Invalid wishlist fields"))
                .when(wishlistService).addWishlist(any(Wishlist.class));

        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.addWishlist(testWishlist);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid wishlist fields", response.getBody().get("message"));
        verify(wishlistService, times(1)).addWishlist(testWishlist);
    }

    @Test
    void testRemoveWishlist() {
        // Arrange
        doNothing().when(wishlistService).removeWishlist(wishlistId);

        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.removeWishlist(wishlistId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wishlist removed successfully", response.getBody().get("message"));
        verify(wishlistService, times(1)).removeWishlist(wishlistId);
    }

    @Test
    void testRemoveWishlist_InvalidUUID() {
        // make sure the test handles the null UUID correctly

        // Act - The controller will handle the null UUID
        ResponseEntity<Map<String, String>> response = wishlistController.removeWishlist(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid wishlist ID", response.getBody().get("message"));
        verify(wishlistService, never()).removeWishlist(any());
    }

    @Test
    void testAddWishlist_WithNullWishlist() {
        // Act
        ResponseEntity<Map<String, String>> response = wishlistController.addWishlist(null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Wishlist cannot be null", response.getBody().get("message"));
        verify(wishlistService, never()).addWishlist(any());
    }

    @Test
    void testGetWishlistsByUserId() {
        // Arrange
        List<Wishlist> wishlists = new ArrayList<>();
        wishlists.add(testWishlist);
        when(wishlistService.getAllWishlists()).thenReturn(wishlists);

        // Act
        ResponseEntity<List<Wishlist>> response = wishlistController.getWishlistsByUserId(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(userId, response.getBody().get(0).getUserId());
    }

    @Test
    void testGetWishlistsByUserId_NoResults() {
        // Arrange
        List<Wishlist> wishlists = new ArrayList<>();
        wishlists.add(testWishlist);
        when(wishlistService.getAllWishlists()).thenReturn(wishlists);

        // Act
        ResponseEntity<List<Wishlist>> response = wishlistController.getWishlistsByUserId(UUID.randomUUID());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }
}