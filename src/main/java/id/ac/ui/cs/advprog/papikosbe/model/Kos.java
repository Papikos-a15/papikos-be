package id.ac.ui.cs.advprog.papikosbe.model;

import lombok.Setter;
import lombok.Getter;

@Getter
public class Kos {

    // Getters
    private Long id;

    @Setter
    private String name;
    @Setter
    private String address;
    @Setter
    private String description;
    @Setter
    private Double price;


    // Constructors
    public Kos() {}

    public Kos(String name, String address, String description, Double price) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.price = price;
    }

}
