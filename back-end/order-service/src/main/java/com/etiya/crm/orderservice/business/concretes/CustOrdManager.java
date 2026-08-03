package com.etiya.crm.orderservice.business.concretes;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ProdCharValRequest;
import com.etiya.crm.orderservice.business.dtos.requests.SubmitOrderRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ValidateBasketRequest;
import com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderItemSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;
import com.etiya.crm.orderservice.business.exceptions.BsnInterSpecNotFoundException;
import com.etiya.crm.orderservice.business.exceptions.OrderNotFoundException;
import com.etiya.crm.orderservice.business.rules.BasketValidationRules;
import com.etiya.crm.orderservice.constants.LookupCodes;
import com.etiya.crm.orderservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.orderservice.clients.controllers.CustomerClient;
import com.etiya.crm.orderservice.clients.controllers.LookupClient;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterItemRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterSpecRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.CustOrdCharValRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.CustOrdItemRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.CustOrdRepository;
import com.etiya.crm.orderservice.entities.concretes.BsnInter;
import com.etiya.crm.orderservice.entities.concretes.BsnInterItem;
import com.etiya.crm.orderservice.entities.concretes.BsnInterSpec;
import com.etiya.crm.orderservice.entities.concretes.CustOrd;
import com.etiya.crm.orderservice.entities.concretes.CustOrdCharVal;
import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;
import com.etiya.crm.orderservice.mapper.CustOrdCharValMapper;
import com.etiya.crm.orderservice.mapper.AddressMapper;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.order.OrderEventTypes;
import com.etiya.crm.shared.events.order.OrderSubmittedEvent;
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

    private final AddressMapper addressMapper;
    private final CustOrdCharValMapper  custOrdCharValMapper ;
    private final CustOrdRepository custOrdRepository;
    private final CustOrdItemRepository custOrdItemRepository;
    private final CustOrdCharValRepository custOrdCharValRepository;
    private final BsnInterRepository bsnInterRepository;
    private final BsnInterItemRepository bsnInterItemRepository;
    private final BsnInterSpecRepository bsnInterSpecRepository;
    private final CustomerClient customerClient;
    private final ContactAddressClient contactAddressClient;
    private final LookupClient lookupClient;
    private final BasketValidationRules basketValidationRules;
    private final OutboxEventPublisher outboxEventPublisher;




    @Override
    @Transactional
    public OrderSummaryResponse submitOrder(SubmitOrderRequest request) {
        customerClient.getById(request.custId());

        //is account belong to that customer

        List<CustomerAccountResponse> accounts = customerClient.getAccounts(request.custId(), 1000);
        basketValidationRules.ensureAccountBelongsToCustomer(request.custAcctId(), accounts);
        basketValidationRules.ensureAddressProvided(request);

        BsnInterSpec spec = bsnInterSpecRepository.findByShrtCode(LookupCodes.BSN_INTER_SPEC_NEW_SALE)
                .orElseThrow(() -> new BsnInterSpecNotFoundException(LookupCodes.BSN_INTER_SPEC_NEW_SALE));
        //create bsn_inter 
        BsnInter bsnInter = new BsnInter();
        bsnInter.setBsnInterSpec(spec);
        bsnInter.setCustId(request.custId());
        bsnInter.setDescr("New sales order");
        bsnInter = bsnInterRepository.save(bsnInter);
        
        // FR-021: siparis olusturulunca "Siparis Alindi, Isleniyor" (CUST_ORD/MIDLWARE) statusune alinir
        Long orderStatusId = lookupClient
                .resolveGeneralStatus(GnlStGroups.CUST_ORDER, GnlStCodes.PROCESSING)
                .gnlStId();

        //create order
        CustOrd custOrd = new CustOrd();
        custOrd.setCustId(request.custId());
        custOrd.setOrdStId(orderStatusId);
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

            // FR-015: bu kalem icin girilen urun karakteristiklerini kaydet. Not: CUST_ORD_CHAR_VAL
            // semasi cust_ord_id'ye bagli (cust_ord_item_id yok) - sepette birden fazla kalem varsa
            // hangi karakteristigin hangi kaleme ait oldugu bu tablodan ayirt edilemez.
            if (itemRequest.charVals() != null) {
                for (ProdCharValRequest charValRequest : itemRequest.charVals()) {
                    CustOrdCharVal charVal= custOrdCharValMapper.toEntity(charValRequest);
                    charVal.setCustOrd(custOrd);
                    custOrdCharValRepository.save(charVal);
                }
            }

            itemResponses.add(new OrderItemSummaryResponse(
                    item.getCustOrdItemId(), item.getProdId(), item.getProdOfrId(),
                    item.getOfrName(), item.getProdName(), item.getCmpgId(), item.getCmpgName(), null));
        }
        // address object that send to the frontend
        AddressSummaryResponse addressSummary = addressMapper.toSummaryResponse(resolvedAddress);
        
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

        outboxEventPublisher.publish(KafkaTopics.ORDER_AGGREGATE_TYPE, custOrd.getCustOrdId().toString(),
                OrderEventTypes.ORDER_SUBMITTED, payload);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResponse getById(Long custOrdId) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));

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

    /**
     * FR-017: Offer Selection'da "Next" tiklandiginda Product Configuration'a gecmeden
     * once cagrilir. BR-01/BR-02'ye karsilik gelen (sepet bos olamaz, duplicate urun
     * olamaz) kontrolleri yapar. "Already Active" ve hizmet cakismasi kontrolleri
     * (BR-03/BR-04) product-service'in musteri urun/kampanya verisini sunmasini
     * bekliyor - henuz burada yok.
     */
    @Override
    @Transactional(readOnly = true)
    public void validateBasket(ValidateBasketRequest request) {
        customerClient.getById(request.custId());

        List<CustomerAccountResponse> accounts = customerClient.getAccounts(request.custId(), 1000);
        basketValidationRules.ensureAccountBelongsToCustomer(request.custAcctId(), accounts);
        basketValidationRules.ensureNoDuplicateItems(request.items());
    }

    private AddressResponse resolveAddress(SubmitOrderRequest request, Long custOrdId) {
        if (request.addressId() != null) {
            // Var olan adres secildi - tam detaylarini contact-info-service'ten cekiyoruz
            return contactAddressClient.getById(request.addressId());
        }

        // lookup-service'in type_value seed'inde (V5__seed_general_lookup_data.sql) CUST_ORD icin
        // henuz bir satir yok - dosyadaki not: "ORDER icin henuz gercek deger yok". O satir eklenene
        // kadar bu cagri EntityNotFoundException/404 firlatir ve yeni-adresle siparis verme basarisiz
        // olur (mevcut adres secme akisini etkilemez). Seed eklendiginde bu kod degismeden calisir.
        Long dataTypeId = lookupClient.getTypeValueByTable(LookupCodes.DATA_TYPE_CUST_ORD).fieldName();

        CreateAddressRequest addressRequest = addressMapper.toCreateAddressRequest(request.newAddress(), custOrdId, dataTypeId, true);
        return contactAddressClient.createAddress(addressRequest);

    }


}
