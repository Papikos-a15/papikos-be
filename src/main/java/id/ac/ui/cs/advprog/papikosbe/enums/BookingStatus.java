package id.ac.ui.cs.advprog.papikosbe.enums;

public enum BookingStatus {
    PENDING_PAYMENT,  // Booking telah dibuat, namun pembayaran belum dilakukan (can edit)
    PAID,             // Pembayaran sudah diterima, menunggu approval dari pemilik (can edit)
    APPROVED,         // Booking telah disetujui oleh pemilik kos (cannot edit anymore)
    ACTIVE,
    INACTIVE,
    CANCELLED         // Booking dibatalkan (terminal state)
}