package id.ac.ui.cs.advprog.papikosbe.enums;

/**
 * Defines what context data a ValidationRule requires
 */
public enum ValidationRequirement {
    BOOKING_ONLY,           // Only needs booking
    BOOKING_AND_KOS,        // Needs booking + kos
    BOOKING_AND_REQUESTER,  // Needs booking + requester
    FULL_CONTEXT           // Needs all: booking + kos + requester
}