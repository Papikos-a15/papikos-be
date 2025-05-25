package id.ac.ui.cs.advprog.papikosbe.observer;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.observer.event.KosStatusChangedEvent;
import id.ac.ui.cs.advprog.papikosbe.observer.handler.KosStatusChangedHandler;
import id.ac.ui.cs.advprog.papikosbe.repository.wishlist.WishlistRepository;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KosStatusChangedHandlerTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private KosStatusChangedHandler kosStatusChangedHandler;

    @Test
    public void testHandleKosStatusChangedEvent() {
        UUID kosId = UUID.randomUUID();
        String kosName = "Kos 1";

        KosStatusChangedEvent event = new KosStatusChangedEvent(this, kosId, kosName, true);

        Wishlist wishlist1 = new Wishlist(UUID.randomUUID(), kosId);
        Wishlist wishlist2 = new Wishlist(UUID.randomUUID(), kosId);
        wishlistRepository.save(wishlist1);
        wishlistRepository.save(wishlist2);
        List<Wishlist> mockWishlists = List.of(wishlist1, wishlist2);

        when(wishlistRepository.findByKosId(kosId)).thenReturn(mockWishlists);

        kosStatusChangedHandler.handleEvent(event);

        verify(notificationService, times(2)).createNotification(
                any(UUID.class),
                eq("Kos Available"),
                eq("Kos " + kosName + " is now available. Go book it now!"),
                eq(NotificationType.WISHLIST)
        );
    }
}
