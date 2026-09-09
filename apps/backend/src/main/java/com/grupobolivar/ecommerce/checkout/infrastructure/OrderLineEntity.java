package com.grupobolivar.ecommerce.checkout.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Maps the {@code order_lines} table (snapshot of a purchased line). */
@Entity
@Table(name = "order_lines")
public class OrderLineEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_id")
	private OrderEntity order;

	@Column(name = "product_id")
	private long productId;

	@Column(name = "product_name")
	private String productName;

	@Column(name = "unit_price")
	private BigDecimal unitPrice;

	@Column(name = "category_name")
	private String categoryName;

	private int quantity;

	@Column(name = "line_total")
	private BigDecimal lineTotal;

	protected OrderLineEntity() {
	}

	public OrderLineEntity(long productId, String productName, BigDecimal unitPrice,
			String categoryName, int quantity, BigDecimal lineTotal) {
		this.productId = productId;
		this.productName = productName;
		this.unitPrice = unitPrice;
		this.categoryName = categoryName;
		this.quantity = quantity;
		this.lineTotal = lineTotal;
	}

	void setOrder(OrderEntity order) {
		this.order = order;
	}

	public long getProductId() {
		return productId;
	}

	public String getProductName() {
		return productName;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public int getQuantity() {
		return quantity;
	}

	public BigDecimal getLineTotal() {
		return lineTotal;
	}
}
