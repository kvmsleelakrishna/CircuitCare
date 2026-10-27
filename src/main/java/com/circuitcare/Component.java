
package com.circuitcare;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "components")
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Component_id")
    private Integer componentId;

    @Column(name = "Component_name", nullable = false)
    private String componentName;

    @Column(name = "Current_stock", nullable = false)
    private Integer quantity;

    @Column(name = "Min_stock", nullable = false)
    private Integer minimumStock;

    @Column(name = "Unit_price")
    private BigDecimal unitPrice;

    public Component() {
    }

    public Integer getComponentId() {
        return componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
