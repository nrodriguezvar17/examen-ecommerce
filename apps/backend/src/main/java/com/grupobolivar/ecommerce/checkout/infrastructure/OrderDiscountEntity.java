package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.checkout.domain.discount.DiscountType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/** Maps the {@code order_discounts} table (raw discount per rule, before the cap). */
@Entity
@Table(name = "order_discounts")
public class OrderDiscountEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_id")
	private OrderEntity order;

	@Enumerated(EnumType.STRING)
	private DiscountType type;

	private BigDecimal rate;

	private BigDecimal amount;

	protected OrderDiscountEntity() {
	}

	public OrderDiscountEntity(DiscountType type, BigDecimal rate, BigDecimal amount) {
		this.type = type;
		this.rate = rate;
		this.amount = amount;
	}

	void setOrder(OrderEntity order) {
		this.order = order;
	}

	public DiscountType getType() {
		return type;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public BigDecimal getAmount() {
		return amount;
	}
}
