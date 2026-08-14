package com.etiya.crm.orderservice.business.dtos.responses;


//it is not a part of submit flow, to later fill products linked to this billing account table 
public record CustOrdItemResponse(

    Long custOrdItemId,
    Long custOrdId,
    Long prodId,
    String prodNo,
    String prodName,
    Long cmpgId,
    String cmpgNo,
    String cmpgName,
    Long custAcctId
) {

}
