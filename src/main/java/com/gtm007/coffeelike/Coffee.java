package com.gtm007.coffeelike;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "coffee_type", schema = "public")
public class Coffee {
    @Id
    private Long id;
    @Column(nullable = false)
    private String name;

    public Coffee() { }

    public Coffee(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

