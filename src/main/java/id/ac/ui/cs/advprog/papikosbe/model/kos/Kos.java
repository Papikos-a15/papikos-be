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
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(updatable = false, nullable = false)
    private UUID ownerId;

    @Column(updatable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column()
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
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
