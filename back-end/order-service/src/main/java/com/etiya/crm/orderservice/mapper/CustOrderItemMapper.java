package com.etiya.crm.orderservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderItemSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.ProdCharValResponse;
import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;

@Mapper(componentModel = "spring")
public interface CustOrderItemMapper {

    //createOrder VE getById'de aynı satırlar tekrarlanıyordu
    //price/prodSpecId: entity'deki alanla ayni isimde oldugu icin MapStruct otomatik esler
    //charVals: entity'de yok, ikinci parametreden (buildSummary'de custOrdItemId'ye gore
    //gruplanmis, ayri sorguyla cekilmis liste) parametre adi eslesmesiyle otomatik alinir
    //serviceStartDate: FR'da ayri bir alan yok, item'in olusturuldugu an (cdate) kullanilir
    @Mapping(target = "serviceStartDate", source = "item.cdate")
    OrderItemSummaryResponse toSummaryResponse(CustOrdItem item, List<ProdCharValResponse> charVals);
    //getItemsByCustAcctId
    CustOrdItemResponse toItemResponse(CustOrdItem item);

    //bu alanlar requestte yok managerda set edeceğiz o nedenle ignore = true
    @Mapping(target= "custOrd", ignore = true)
    @Mapping(target= "custAcctId", ignore = true)
    @Mapping(target= "custId", ignore = true)
    @Mapping(target= "custOrdItemId", ignore = true)

    //BaseEntity audit alanlari - JwtAuditorAware/JPA auditing tarafindan otomatik doldurulur
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    
    //product-service'ten manager'da cekilip elle set edilen alanlar (ofrName/prodSpecId/price)
    @Mapping(target = "newCustAcctId", ignore = true)
    @Mapping(target = "newCustId", ignore = true)
    @Mapping(target = "prodId", ignore = true)
    @Mapping(target = "ofrName", ignore = true)
    @Mapping(target = "prodName", ignore = true)
    @Mapping(target = "prodSpecId", ignore = true)
    @Mapping(target = "cmpgName", ignore = true)
    @Mapping(target = "bsnInter", ignore = true)
    @Mapping(target = "isNeedShpmt", ignore = true)
    @Mapping(target = "price", ignore = true)
    CustOrdItem toEntity(BasketItemRequest request);
}