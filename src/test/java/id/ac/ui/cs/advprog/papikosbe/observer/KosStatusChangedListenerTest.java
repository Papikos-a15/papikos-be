package id.ac.ui.cs.advprog.papikosbe.observer;

import id.ac.ui.cs.advprog.papikosbe.enums.NotificationType;
import id.ac.ui.cs.advprog.papikosbe.model.kos.Kos;
import id.ac.ui.cs.advprog.papikosbe.model.wishlist.Wishlist;
import id.ac.ui.cs.advprog.papikosbe.repository.kos.KosRepository;
import id.ac.ui.cs.advprog.papikosbe.repository.wishlist.WishlistRepository;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosService;
import id.ac.ui.cs.advprog.papikosbe.service.kos.KosServiceImpl;
import id.ac.ui.cs.advprog.papikosbe.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class KosStatusChangedListenerTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private KosStatusChangedListener listener;

    @InjectMocks
    private KosServiceImpl kosService;

    @Autowired
    private KosRepository kosRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        wishlistRepository = mock(WishlistRepository.class);
        kosRepository = mock(KosRepository.class);
    }

    @Test
    void testHandleRoomStatusChanged_sendsNotifications() {
        notificationService = mock(NotificationService.class);
        listener = new KosStatusChangedListener(wishlistRepository, notificationService);
        eventPublisher = mock(ApplicationEventPublisher.class);

        // Arrange
        UUID kosId = UUID.randomUUID();
        String kosName = "Kos A";
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        Wishlist wishlist1 = new Wishlist(userId1, kosId);
        Wishlist wishlist2 = new Wishlist(userId2, kosId);
        wishlistRepository.save(wishlist1);
        wishlistRepository.save(wishlist2);

        when(wishlistRepository.findByKosId(kosId)).thenReturn(List.of(wishlist1, wishlist2));

        KosStatusChangedEvent event = new KosStatusChangedEvent(this, kosId, kosName, true);

        // Act
        listener.handleRoomStatusChanged(event);

        // Assert
        verify(notificationService, times(1)).createNotification(
                eq(userId1),
                eq("Kos Available"),
                eq("Kos " + kosName + " is now available. Go book it now!"),
                eq(NotificationType.WISHLIST)
        );

        verify(notificationService, times(1)).createNotification(
                eq(userId2),
                eq("Kos Available"),
                eq("Kos " + kosName + " is now available. Go book it now!"),
                eq(NotificationType.WISHLIST)
        );

        verifyNoMoreInteractions(notificationService);
    }
}
