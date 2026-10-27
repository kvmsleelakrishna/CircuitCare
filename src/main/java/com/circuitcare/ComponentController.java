
package com.circuitcare;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/components")
@CrossOrigin(origins = {
    "http://127.0.0.1:5500",
    "http://localhost:5500"
})
public class ComponentController {

    private final ComponentRepository componentRepository;

    public ComponentController(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    // READ: Get all components
    @GetMapping
    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    // READ: Get one component by ID
    @GetMapping("/{id}")
    public ResponseEntity<Component> getComponentById(
            @PathVariable Integer id) {

        return componentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // CREATE: Add a component
    @PostMapping
    public ResponseEntity<?> addComponent(
            @RequestBody ComponentRequest request) {

        if (!isValid(request)) {
            return ResponseEntity.badRequest()
                    .body("Enter a name, non-negative quantity and "
                        + "minimum stock, and a valid price.");
        }

        Component component = new Component();
        copyDetails(request, component);

        Component saved = componentRepository.save(component);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // UPDATE: Update component details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComponent(
            @PathVariable Integer id,
            @RequestBody ComponentRequest request) {

        if (!isValid(request)) {
            return ResponseEntity.badRequest()
                    .body("Enter valid component details.");
        }

        return componentRepository.findById(id)
                .<ResponseEntity<?>>map(component -> {
                    copyDetails(request, component);

                    Component updated =
                            componentRepository.save(component);

                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE: Remove a component
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComponent(
            @PathVariable Integer id) {

        if (!componentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        componentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Find components that need restocking
    @GetMapping("/low-stock")
    public List<Component> getLowStockComponents() {
        return componentRepository.findLowStockComponents();
    }

    private void copyDetails(
            ComponentRequest request, Component component) {

        component.setComponentName(request.getComponentName().trim());
        component.setQuantity(request.getQuantity());
        component.setMinimumStock(request.getMinimumStock());
        component.setUnitPrice(request.getUnitPrice());
    }

    private boolean isValid(ComponentRequest request) {
        return request != null
                && request.getComponentName() != null
                && !request.getComponentName().isBlank()
                && request.getQuantity() != null
                && request.getQuantity() >= 0
                && request.getMinimumStock() != null
                && request.getMinimumStock() >= 0
                && request.getUnitPrice() != null
                && request.getUnitPrice().compareTo(BigDecimal.ZERO) >= 0;
    }
}
