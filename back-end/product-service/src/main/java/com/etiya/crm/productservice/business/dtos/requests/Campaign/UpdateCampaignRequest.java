package com.etiya.crm.productservice.business.dtos.requests.Campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCampaignRequest {

    @NotBlank(message = "İsim zorunludur.")
    @Size(max = 100, message = "Ad en fazla 100 karakter olabilir.")
    private String name;

    @NotBlank(message = "Aciklama zorunludur.")
    @Size(max = 100, message = "Aciklama en fazla 100 karakter olabilir.")
    private String descr;

    @NotBlank(message = "Kampanya Kodu alanı zorunludur.")
    private String campaignCode;

    private LocalDate activityEndDate;

    @NotBlank(message = "Durum kodu zorunludur.")
    private String statusCode;

    @NotNull(message = "penalty alanı boş bırakılamaz")
    private Boolean penalty;

}
