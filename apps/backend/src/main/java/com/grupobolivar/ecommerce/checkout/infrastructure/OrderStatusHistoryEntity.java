package com.grupobolivar.ecommerce.checkout.infrastructure;

import com.grupobolivar.ecommerce.checkout.domain.order.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

/** Maps the {@code order_status_history} table. */
@Entity
@Table(name = "order_status_history")
public class OrderStatusHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "order_id")
	private OrderEntity order;

	@Column(name = "status_code")
	@Enumerated(EnumType.STRING)
	private OrderStatus statusCode;

	@Column(name = "changed_at")
	private Instant changedAt;

	private String note;

	protected OrderStatusHistoryEntity() {
	}

	public OrderStatusHistoryEntity(OrderStatus statusCode, Instant changedAt, String note) {
		this.statusCode = statusCode;
		this.changedAt = changedAt;
		this.note = note;
	}

	void setOrder(OrderEntity order) {
		this.order = order;
	}

	public OrderStatus getStatusCode() {
		return statusCode;
	}

	public Instant getChangedAt() {
		return changedAt;
	}

	public String getNote() {
		return note;
	}
}
