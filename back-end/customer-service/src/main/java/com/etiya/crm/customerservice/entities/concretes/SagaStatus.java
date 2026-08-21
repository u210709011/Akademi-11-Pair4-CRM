package com.etiya.crm.customerservice.entities.concretes;

/** CustomerDeletionSaga'nin durum makinesi. */
public enum SagaStatus {

	/** Silme yayinlandi, en az bir adimin sonucu bekleniyor. */
	STARTED,

	/** Iki adim da basarili - saga normal tamamlandi. */
	COMPLETED,

	/** En az bir adim kalici olarak basarisiz oldu, telafi (compensation) tetiklendi/surdu. */
	COMPENSATING,

	/** Compensation tamamlandi: musteri yerel olarak reactivate edildi, compensating command yayinlandi. */
	COMPENSATED
}
