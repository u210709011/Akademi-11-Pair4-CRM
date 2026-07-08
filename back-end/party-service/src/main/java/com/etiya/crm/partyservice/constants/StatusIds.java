package com.etiya.crm.partyservice.constants;

/**
 * BRAIN.md SS3 "netlesen kurallar": lookup STATUS grubu icin sabitlenen degerler
 * (1=ACTIVE, 2=PASSIVE, 3=PENDING). lookup-service hazir olana kadar bu Long
 * degerler dogrudan kullanilir; PENDING bu kapsamda kullanilmiyor.
 */
public final class StatusIds {

    public static final Long ACTIVE = 1L;
    public static final Long PASSIVE = 2L;

    private StatusIds() {
    }
}
