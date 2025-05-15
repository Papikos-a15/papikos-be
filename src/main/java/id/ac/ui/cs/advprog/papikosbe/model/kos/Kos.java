package id.ac.ui.cs.advprog.papikosbe.model.kos;

import lombok.*;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "kos")
@Builder
@Setter @Getter
public class Kos {

    @Id
    @GeneratedValue
    @Column(name = "kos_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "owner_id", updatable = false, nullable = false)
    private UUID ownerId;

    @Column(name = "tenant_id", updatable = false, nullable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;


    // Constructors
    public Kos() {}

    public Kos(UUID id, String name, String address, String description, Double price, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    }

    public Kos(UUID id, UUID ownerId, UUID tenantId, String name, String address, String description, Double price, boolean isAvailable) {
        this.id = id;
        this.ownerId = ownerId;
        this.tenantId = tenantId;
        this.name = name;
        this.address = address;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    }
}
