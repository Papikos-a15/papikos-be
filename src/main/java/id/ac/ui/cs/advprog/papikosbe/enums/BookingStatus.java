package id.ac.ui.cs.advprog.papikosbe.enums;

public enum BookingStatus {
    PENDING_PAYMENT, // Booking telah dibuat tapi pembayaran belum dilakukan
    PAID,            // Pembayaran sudah diterima
    ACTIVE,          // Booking aktif/sedang berlangsung
    CANCELLED        // Booking dibatalkan
}
