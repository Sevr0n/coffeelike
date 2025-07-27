package com.gtm007.coffeelike;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Coffee {
    private Long id;
    private String name;

    public Coffee() { }

    public Coffee(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}

