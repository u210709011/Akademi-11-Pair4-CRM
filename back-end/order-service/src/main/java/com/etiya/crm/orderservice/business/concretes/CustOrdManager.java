package com.etiya.crm.orderservice.business.concretes;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.abstracts.LookupCacheService;
import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.requests.CreateOrderRequest;
import com.etiya.crm.orderservice.business.dtos.requests.OrderConfigurationRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ProdCharValRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ValidateBasketRequest;
import com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderItemSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.ProdCharValResponse;
import com.etiya.crm.orderservice.business.exceptions.BsnInterSpecNotFoundException;
import com.etiya.crm.orderservice.business.exceptions.OrderNotEditableException;
import com.etiya.crm.orderservice.business.exceptions.OrderNotFoundException;
import com.etiya.crm.orderservice.business.exceptions.ServiceAddressMissingException;
import com.etiya.crm.orderservice.business.rules.BasketValidationRules;
import com.etiya.crm.orderservice.constants.LookupCodes;
import com.etiya.crm.orderservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.orderservice.clients.controllers.CustomerClient;
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
import com.etiya.crm.orderservice.mapper.CustOrderItemMapper;
import com.etiya.crm.orderservice.mapper.AddressMapper;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.contracts.typevalue.TypeValueTables;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.order.OrderEventTypes;
import com.etiya.crm.shared.events.order.OrderSubmittedEvent;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustOrdManager implements CustOrdService {

    private final AddressMapper addressMapper;
    private final CustOrdCharValMapper  custOrdCharValMapper ;
    private final CustOrderItemMapper custOrderItemMapper;
    private final CustOrdRepository custOrdRepository;
    private final CustOrdItemRepository custOrdItemRepository;
    private final CustOrdCharValRepository custOrdCharValRepository;
    private final BsnInterRepository bsnInterRepository;
    private final BsnInterItemRepository bsnInterItemRepository;
    private final BsnInterSpecRepository bsnInterSpecRepository;
    private final CustomerClient customerClient;
    private final ContactAddressClient contactAddressClient;
    private final LookupCacheService lookupCacheService;
    private final BasketValidationRules basketValidationRules;
    private final OutboxEventPublisher outboxEventPublisher;

    @Override
    @Transactional(readOnly = true)
    public void validateBasket(ValidateBasketRequest request) {
        customerClient.getById(request.custId());

        List<CustomerAccountResponse> accounts = customerClient.getAccounts(request.custId(), 1000).content();
        basketValidationRules.ensureAccountBelongsToCustomer(request.custAcctId(), accounts);
        basketValidationRules.ensureNoDuplicateItems(request.items());
    }

    /**
     * FR-012/FR-013: Offer Selection'da sepet dogrulandiktan sonra cagrilir. CustOrd/BsnInter'i
     * WAIT durumunda olusturur - boylece Order/Business Interaction numaralari Configuration
     * ekraninda (henuz Finish'e basilmadan) gorunur olur. charVals/adres burada yok, onlar
     * saveConfiguration ile eklenir.
     */
    @Override
    @Transactional
    public OrderSummaryResponse createOrder(CreateOrderRequest request) {
        customerClient.getById(request.custId());

        List<CustomerAccountResponse> accounts = customerClient.getAccounts(request.custId(), 1000).content();
        basketValidationRules.ensureAccountBelongsToCustomer(request.custAcctId(), accounts);
        basketValidationRules.ensureNoDuplicateItems(request.items());

        BsnInterSpec spec = bsnInterSpecRepository.findByShrtCode(LookupCodes.BSN_INTER_SPEC_NEW_SALE)
                .orElseThrow(() -> new BsnInterSpecNotFoundException(LookupCodes.BSN_INTER_SPEC_NEW_SALE));

        BsnInter bsnInter = new BsnInter();
        bsnInter.setBsnInterSpec(spec);
        bsnInter.setCustId(request.custId());
        bsnInter.setDescr("New sales order");
        bsnInter = bsnInterRepository.save(bsnInter);

        Long waitStatusId = lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING);

        CustOrd custOrd = new CustOrd();
        custOrd.setCustId(request.custId());
        custOrd.setOrdStId(waitStatusId);
        custOrd.setBsnInter(bsnInter);
        custOrd.setBsnInterSpec(spec);
        custOrd = custOrdRepository.save(custOrd);

        for (BasketItemRequest itemRequest : request.items()) {
            CustOrdItem item = custOrderItemMapper.toEntity(itemRequest);
            item.setCustOrd(custOrd);
            item.setCustAcctId(request.custAcctId());
            item.setCustId(request.custId());
            // product-service tamamlanınca burada PROD instance olusturulup
            // dogan prodId/prodName/ofrName/price buraya yazilacak.
            item = custOrdItemRepository.save(item);
            // custOrd yeni persist edildigi icin Hibernate items koleksiyonunu bos baslatir ve
            // ayri bir repository cagrisiyla eklenen satirlari kendiliginden gormez - buildSummary'nin
            // dogru listeyi donebilmesi icin bidirectional iliski burada elle senkron tutulur.
            custOrd.getItems().add(item);

            BsnInterItem bsnInterItem = new BsnInterItem();
            bsnInterItem.setBsnInter(bsnInter);
            bsnInterItem.setRowId(item.getCustOrdItemId());
            bsnInterItemRepository.save(bsnInterItem);
        }

        return buildSummary(custOrd);
    }

    /**
     * FR-015: Product Configuration ekraninda kullanicinin girdigi karakteristikleri ve servis
     * adresini WAIT durumundaki siparise yazar. Sayfa yenilense de kaybolmasin diye her cagrida
     * charVals baştan yazilir (replace-all, idempotent); adres sadece gonderildiyse guncellenir.
     */
    @Override
    @Transactional
    public OrderSummaryResponse saveConfiguration(Long custOrdId, OrderConfigurationRequest request) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));
        ensureEditable(custOrd);

        custOrdCharValRepository.deleteByCustOrd_CustOrdId(custOrdId);
        if (request.charVals() != null) {
            for (ProdCharValRequest charValRequest : request.charVals()) {
                CustOrdCharVal charVal = custOrdCharValMapper.toEntity(charValRequest);
                charVal.setCustOrd(custOrd);
                custOrdCharValRepository.save(charVal);
            }
        }

        if (request.addressId() != null || request.newAddress() != null) {
            custOrd.setAddressId(resolveAddressId(request.addressId(), request.newAddress(), custOrdId));
            custOrd = custOrdRepository.save(custOrd);
        }

        return buildSummary(custOrd);
    }

    /**
     * FR-021: Review & Confirm'de Finish'e basilinca cagrilir. WAIT -> MIDLWARE ("Sipariş
     * Alındı, İşleniyor") statu gecisini yapar ve OrderSubmittedEvent'i yayinlar.
     */
    @Override
    @Transactional
    public OrderSummaryResponse finishOrder(Long custOrdId) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));
        ensureEditable(custOrd);

        if (custOrd.getAddressId() == null) {
            throw new ServiceAddressMissingException(custOrdId);
        }

        Long processingStatusId = lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.PROCESSING);
        custOrd.setOrdStId(processingStatusId);
        custOrd = custOrdRepository.save(custOrd);

        publishOrderSubmittedEvent(custOrd);

        return buildSummary(custOrd);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResponse getById(Long custOrdId) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));
        return buildSummary(custOrd);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustOrdItemResponse> getItemsByCustAcctId(Long custAcctId) {
        return custOrdItemRepository.findByCustAcctId(custAcctId).stream()
                .map(custOrderItemMapper::toItemResponse)
                .collect(Collectors.toList());
    }

    /** WAIT disindaki (MIDLWARE/FINISHED/REJECTED) bir siparis artik configure/finish edilemez. */
    private void ensureEditable(CustOrd custOrd) {
        Long waitStatusId = lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING);
        if (!waitStatusId.equals(custOrd.getOrdStId())) {
            throw new OrderNotEditableException(custOrd.getCustOrdId());
        }
    }

    private Long resolveAddressId(Long addressId, AddressInfoRequest newAddress, Long custOrdId) {
        basketValidationRules.ensureAddressProvided(addressId, newAddress);

        if (addressId != null) {
            // Var olan adres secildi - ayrica bir kayit olusturmadan sadece id'sini saklariz.
            return contactAddressClient.getById(addressId).id();
        }

        Long dataTypeId = lookupCacheService.resolveDataTypeId(TypeValueTables.ORDER);
        CreateAddressRequest addressRequest = addressMapper.toCreateAddressRequest(newAddress, custOrdId, dataTypeId, true);
        return contactAddressClient.createAddress(addressRequest).id();
    }

    private OrderSummaryResponse buildSummary(CustOrd custOrd) {
        List<OrderItemSummaryResponse> itemResponses = custOrd.getItems().stream()
                .map(custOrderItemMapper::toSummaryResponse)
                .collect(Collectors.toList());

        List<ProdCharValResponse> charValResponses = custOrd.getCharVals().stream()
                .map(custOrdCharValMapper::toResponse)
                .collect(Collectors.toList());

        AddressSummaryResponse addressSummary = custOrd.getAddressId() != null
                ? addressMapper.toSummaryResponse(contactAddressClient.getById(custOrd.getAddressId()))
                : null;

        return new OrderSummaryResponse(custOrd.getCustOrdId(), custOrd.getOrdStId(),
                itemResponses, charValResponses, addressSummary, BigDecimal.ZERO);
    }

    /**
     * Ayni transaction icinde outbox tablosuna insert eder; Debezium bu satiri
     * WAL'den okuyup "order-events" topic'ine yayinlar
     */
    private void publishOrderSubmittedEvent(CustOrd custOrd) {
        Long custAcctId = custOrd.getItems().isEmpty() ? null : custOrd.getItems().get(0).getCustAcctId();

        OrderSubmittedEvent payload = new OrderSubmittedEvent(
                UUID.randomUUID(),
                OrderEventTypes.ORDER_SUBMITTED,
                custOrd.getCustOrdId(),
                custOrd.getCustId(),
                custAcctId);

        outboxEventPublisher.publish(KafkaTopics.ORDER_AGGREGATE_TYPE, custOrd.getCustOrdId().toString(),
                OrderEventTypes.ORDER_SUBMITTED, payload);
    }

}
