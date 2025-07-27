package com.gtm007.coffeelike;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@RestController
@RequestMapping("/api/coffees")
public class CoffeeController {
    private final CoffeeRepository coffeeRepository;


    public CoffeeController(CoffeeRepository coffeeRepository) {
        this.coffeeRepository = coffeeRepository;
    }

    @Operation(summary = "Get all coffee types")
    @GetMapping
    public Collection<Coffee> getAll() {
        return coffeeRepository.findAll();
    }

    @Operation(summary = "Get coffee by ID")
    @GetMapping("/{id}")
    public ResponseEntity<Coffee> getById(@PathVariable Long id) {
        return coffeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(summary = "Get coffee by name")
    @GetMapping("/search")
    public ResponseEntity<Coffee> getByName(@RequestParam String name) {
        return coffeeRepository.findByNameIgnoreCase(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @Operation(summary = "Create new coffee")
    @PostMapping
    public ResponseEntity<String> createCoffee(@RequestBody Coffee coffee) {
        if (coffeeRepository.existsById(coffee.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Coffee with ID " + coffee.getId() + " already exists.");
        }
        coffeeRepository.save(coffee);
        return ResponseEntity.status(HttpStatus.CREATED).body("Coffee created");
    }
    @Operation(summary = "Update or create coffee")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateCoffee(@PathVariable Long id, @RequestBody Coffee coffee) {
        boolean existed = coffeeRepository.existsById(id);
        coffee.setId(id);
        coffeeRepository.save(coffee);
        return ResponseEntity.status(existed ? HttpStatus.OK : HttpStatus.CREATED)
                .body(existed ? "Updated" : "Created");
    }
    @Operation(summary = "Delete coffee by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCoffee(@PathVariable Long id) {
        if (coffeeRepository.existsById(id)) {
            coffeeRepository.deleteById(id);
            return ResponseEntity.ok("Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
    }
}
