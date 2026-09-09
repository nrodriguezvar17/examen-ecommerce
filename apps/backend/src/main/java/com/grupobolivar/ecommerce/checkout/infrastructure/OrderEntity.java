package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.checkout.domain.order.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Maps the {@code orders} table plus its lines, discount breakdown and status history. */
@Entity
@Table(name = "orders")
public class OrderEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String radicado;

	@Column(name = "customer_id")
	private Long customerId;

	@Column(name = "status_code")
	@Enumerated(EnumType.STRING)
	private OrderStatus status;

	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "coupon_code")
	private String couponCode;

	@Column(name = "original_total")
	private BigDecimal originalTotal;

	@Column(name = "total_discount")
	private BigDecimal totalDiscount;

	@Column(name = "effective_rate")
	private BigDecimal effectiveRate;

	@Column(name = "final_total")
	private BigDecimal finalTotal;

	@Column(name = "cap_reached")
	private boolean capReached;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<OrderLineEntity> lines = new ArrayList<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<OrderDiscountEntity> discounts = new ArrayList<>();

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<OrderStatusHistoryEntity> statusHistory = new ArrayList<>();

	protected OrderEntity() {
	}

	public OrderEntity(String radicado, Long customerId, OrderStatus status, Instant createdAt,
			String couponCode, BigDecimal originalTotal, BigDecimal totalDiscount,
			BigDecimal effectiveRate, BigDecimal finalTotal, boolean capReached) {
		this.radicado = radicado;
		this.customerId = customerId;
		this.status = status;
		this.createdAt = createdAt;
		this.couponCode = couponCode;
		this.originalTotal = originalTotal;
		this.totalDiscount = totalDiscount;
		this.effectiveRate = effectiveRate;
		this.finalTotal = finalTotal;
		this.capReached = capReached;
	}

	public void addLine(OrderLineEntity line) {
		line.setOrder(this);
		lines.add(line);
	}

	public void addDiscount(OrderDiscountEntity discount) {
		discount.setOrder(this);
		discounts.add(discount);
	}

	public void addStatusChange(OrderStatusHistoryEntity change) {
		change.setOrder(this);
		statusHistory.add(change);
	}

	public String getRadicado() {
		return radicado;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public BigDecimal getOriginalTotal() {
		return originalTotal;
	}

	public BigDecimal getTotalDiscount() {
		return totalDiscount;
	}

	public BigDecimal getEffectiveRate() {
		return effectiveRate;
	}

	public BigDecimal getFinalTotal() {
		return finalTotal;
	}

	public boolean isCapReached() {
		return capReached;
	}

	public List<OrderLineEntity> getLines() {
		return lines;
	}

	public List<OrderDiscountEntity> getDiscounts() {
		return discounts;
	}
}
