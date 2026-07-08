package com.etiya.crm.partyservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * TODO: netlestirilecek - lookup-service henuz hazir degil (bkz. BRAIN.md SS3/SS7).
 * CreateIndividualCommand icinde partyTypeId / partyRoleTypeId gelmiyor; PARTY ve
 * PARTY_ROLE olusturulurken bu ID'ler party-service tarafindan atanmak zorunda.
 * BRAIN.md ornek kodlarin ("200=IND", "210=CUST rolu" vb.) UYDURMA oldugunu acikca
 * belirttigi icin burada sabit deger yerine, gercek lookup_value_id'ler netlesince
 * kod degistirmeden guncellenebilsin diye disaridan (application yml) configure
 * edilebilir placeholder degerler kullaniliyor.
 */
@Component
@ConfigurationProperties(prefix = "party-service.lookup")
@Getter
@Setter
public class PartyLookupProperties {

    private Long partyTypeIndividualId;

    private Long partyRoleTypeCustomerId;
}
