package com.gtm007.coffeelike;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/coffees")
public class CoffeeController {
    private final Map<Long, Coffee> coffeeMap = new HashMap<>();

    public CoffeeController() {
        coffeeMap.put(1L, new Coffee(1L, "Espresso"));
        coffeeMap.put(2L, new Coffee(2L, "Latte"));
        coffeeMap.put(3L, new Coffee(3L, "Cappuccino"));
        coffeeMap.put(4L, new Coffee(4L, "Americano"));
    }

    @GetMapping
    public Collection<Coffee> getAll() {
        return coffeeMap.values();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Coffee> getById(@PathVariable Long id) {
        Coffee coffee = coffeeMap.get(id);
        return (coffee != null)
                ? ResponseEntity.ok(coffee)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/search")
    public ResponseEntity<Coffee> getByName(@RequestParam String name) {
        return coffeeMap.values().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<String> createCoffee(@RequestBody Coffee coffee) {
        if (coffeeMap.containsKey(coffee.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Coffee with ID " + coffee.getId() + " already exists.");
        }
        coffeeMap.put(coffee.getId(), coffee);
        return ResponseEntity.status(HttpStatus.CREATED).body("Coffee created");
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCoffee(@PathVariable Long id, @RequestBody Coffee coffee) {
        boolean existed = coffeeMap.containsKey(id);
        coffee.setId(id);
        coffeeMap.put(id, coffee);
        return ResponseEntity.status(existed ? HttpStatus.OK : HttpStatus.CREATED)
                .body(existed ? "Updated" : "Created");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCoffee(@PathVariable Long id) {
        if (coffeeMap.remove(id) != null) {
            return ResponseEntity.ok("Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
    }
}
