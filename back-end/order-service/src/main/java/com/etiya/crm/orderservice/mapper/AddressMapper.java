package com.etiya.crm.orderservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    /*streetId ve cityId zaten isim olarak eşleştiği için mapping yapmadık, otomatik eşleştiriyor
    sourceİd burada zaten kaynakvar veri ismi farklı onlar eşleşmeli o nedenle ekledik*/
    @Mapping(target = "addressId", source = "id")
    @Mapping(target = "buildingName", source = "houseName")
    @Mapping(target = "addressDesc", source = "addrDesc")
    //cityName lookup-service'ten ayrica cekiliyor (AddressResponse'ta sadece cityId var), managerda set edilir
    @Mapping(target = "cityName", ignore = true)
    AddressSummaryResponse toSummaryResponse(AddressResponse address);

    @Mapping(target = "houseName", source = "request.buildingName")
    @Mapping(target = "addrDesc", source = "request.addressDesc")
    CreateAddressRequest toCreateAddressRequest(AddressInfoRequest request, Long rowId, Long dataTypeId, boolean primary);
    /*createaddressrequest custorditem gibi mutable bir entity değil, toEntityde yaptığımız gibiignore true yapıp sonra managerda setleyemeyiz
     onun yerine mappera parametreleri verdik, mapstruct parametre adı hedefle aynıysa otomatik eşleştirir*/
}
