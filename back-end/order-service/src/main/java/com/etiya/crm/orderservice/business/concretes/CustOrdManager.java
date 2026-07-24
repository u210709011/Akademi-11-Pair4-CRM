package com.etiya.crm.orderservice.business.concretes;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.requests.SubmitOrderRequest;
import com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderItemSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;
import com.etiya.crm.orderservice.business.exceptions.BusinessException;
import com.etiya.crm.orderservice.business.rules.BasketValidationRules;
import com.etiya.crm.orderservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.orderservice.clients.controllers.CustomerClient;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.constants.OrderEventTypes;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterItemRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterSpecRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.CustOrdItemRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.CustOrdRepository;
import com.etiya.crm.orderservice.entities.concretes.BsnInter;
import com.etiya.crm.orderservice.entities.concretes.BsnInterItem;
import com.etiya.crm.orderservice.entities.concretes.BsnInterSpec;
import com.etiya.crm.orderservice.entities.concretes.CustOrd;
import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;
import com.etiya.crm.orderservice.messaging.OrderSubmittedEvent;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustOrdManager implements CustOrdService {

    private static final String NEW_SALE_SPEC_CODE = "NEW_SALE";

    private final CustOrdRepository custOrdRepository;
    private final CustOrdItemRepository custOrdItemRepository;
    private final BsnInterRepository bsnInterRepository;
    private final BsnInterItemRepository bsnInterItemRepository;
    private final BsnInterSpecRepository bsnInterSpecRepository;
    private final CustomerClient customerClient;
    private final ContactAddressClient contactAddressClient;
    private final BasketValidationRules basketValidationRules;
    private final OutboxEventPublisher outboxEventPublisher;


    @Override
    @Transactional
    public OrderSummaryResponse submitOrder(SubmitOrderRequest request) {
        customerClient.getById(request.custId());

        //is account belong to that customer

        List<CustomerAccountResponse> accounts = customerClient.getAccounts(request.custId());
        basketValidationRules.ensureAccountBelongsToCustomer(request.custAcctId(), accounts);
        basketValidationRules.ensureAddressProvided(request);

        BsnInterSpec spec = bsnInterSpecRepository.findByShrtCode(NEW_SALE_SPEC_CODE)
                .orElseThrow(() -> new BusinessException("can't find BSN_INTER_SPEC : " + NEW_SALE_SPEC_CODE));
        //create bsn_inter 
        BsnInter bsnInter = new BsnInter();
        bsnInter.setBsnInterSpec(spec);
        bsnInter.setCustId(request.custId());
        bsnInter.setDescr("New sales order");
        bsnInter = bsnInterRepository.save(bsnInter);
        
        //create order
        CustOrd custOrd = new CustOrd();
        custOrd.setCustId(request.custId());
        custOrd.setBsnInter(bsnInter);
        custOrd.setBsnInterSpec(spec);
        custOrd = custOrdRepository.save(custOrd);

        AddressResponse resolvedAddress = resolveAddress(request, custOrd.getCustOrdId());

        List<OrderItemSummaryResponse> itemResponses = new ArrayList<>();
        for (BasketItemRequest itemRequest : request.items()) {
            
            //create order item
            CustOrdItem item = new CustOrdItem();
            item.setCustOrd(custOrd);
            item.setCustAcctId(request.custAcctId());
            item.setCustId(request.custId());
            item.setProdOfrId(itemRequest.prodOfrId());
            item.setCmpgId(itemRequest.cmpgId());
            // product-service tamamlanınca burada PROD instance olusturulup
            // dogan prodId/prodName/ofrName/price buraya yazilacak.
            item = custOrdItemRepository.save(item);
            
            //create bsn_inter_item
            BsnInterItem bsnInterItem = new BsnInterItem();
            bsnInterItem.setBsnInter(bsnInter);
            bsnInterItem.setRowId(item.getCustOrdItemId());
            bsnInterItemRepository.save(bsnInterItem);

            itemResponses.add(new OrderItemSummaryResponse(
                    item.getCustOrdItemId(), item.getProdId(), item.getProdOfrId(),
                    item.getOfrName(), item.getProdName(), item.getCmpgId(), item.getCmpgName(), null));
        }
        // address object that send to the frontend
        AddressSummaryResponse addressSummary = toAddressSummary(resolvedAddress);
        
        publishOrderSubmittedEvent(custOrd, request.custAcctId());
        
        //return response to frontend
        return new OrderSummaryResponse(

                custOrd.getCustOrdId(),
                custOrd.getOrdStId(),
                itemResponses,
                addressSummary,
                BigDecimal.ZERO);
    }

    /**
     * Ayni transaction icinde outbox tablosuna insert eder; Debezium bu satiri
     * WAL'den okuyup "order-events" topic'ine yayinlar 
     */
    //create event
    private void publishOrderSubmittedEvent(CustOrd custOrd, Long custAcctId) {
        OrderSubmittedEvent payload = new OrderSubmittedEvent(
                UUID.randomUUID(),
                OrderEventTypes.ORDER_SUBMITTED,
                custOrd.getCustOrdId(),
                custOrd.getCustId(),
                custAcctId);

        outboxEventPublisher.publish(OrderEventTypes.AGGREGATE_TYPE, custOrd.getCustOrdId().toString(),
                OrderEventTypes.ORDER_SUBMITTED, payload);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResponse getById(Long custOrdId) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new BusinessException("Can't find order " + custOrdId));

        List<OrderItemSummaryResponse> itemResponses = custOrd.getItems().stream()
                .map(item -> new OrderItemSummaryResponse(
                        item.getCustOrdItemId(),
                        item.getProdId(), 
                        item.getProdOfrId(),
                        item.getOfrName(), 
                        item.getProdName(), 
                        item.getCmpgId(), 
                        item.getCmpgName(), 
                        null))
                .collect(Collectors.toList());

        // Servis adresi burada cozulemiyor: CustOrd hicbir yerde addressId saklamiyor,
        // ADDR.row_id=custOrdId ile bulmak icin de lookup-service'teki CUST_ORD dataTypeId'si
        // gerekiyor -henüz eklenmedi. Product servis bitince tekrar bakılacak

        return new OrderSummaryResponse(custOrd.getCustOrdId(), custOrd.getOrdStId(),
                itemResponses, null, BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustOrdItemResponse> getItemsByCustAcctId(Long custAcctId) {
        return custOrdItemRepository.findByCustAcctId(custAcctId).stream()
                .map(item -> new CustOrdItemResponse(
                        item.getCustOrdItemId(), item.getProdId(), item.getProdName(),
                        item.getCmpgId(), item.getCmpgName(), item.getCustAcctId()))
                .collect(Collectors.toList());
    }

    private AddressResponse resolveAddress(SubmitOrderRequest request, Long custOrdId) {
        if (request.addressId() != null) {
            // Var olan adres secildi - tam detaylarini contact-info-service'ten cekiyoruz
            return contactAddressClient.getById(request.addressId());
        }

        // lookup-service DATA_TYPE grubuna CUST_ORD degeri eklenince
        // buradaki null yerine gercek dataTypeId kullanilacak.

        CreateAddressRequest addressRequest = new CreateAddressRequest(
                custOrdId, null,
                request.newAddress().cityId(),
                request.newAddress().streetName(),
                request.newAddress().buildingName(),
                request.newAddress().addressDesc(),
                true);
        return contactAddressClient.createAddress(addressRequest);
    }

    private AddressSummaryResponse toAddressSummary(AddressResponse address) {
        return new AddressSummaryResponse(
                address.id(),
                address.cityId(),
                address.streetName(),
                address.houseName(),
                address.addrDesc()
        );
    }
}
