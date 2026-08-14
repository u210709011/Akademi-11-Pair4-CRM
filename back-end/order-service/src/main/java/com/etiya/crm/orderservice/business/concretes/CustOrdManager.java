package com.etiya.crm.orderservice.business.concretes;

import com.etiya.crm.orderservice.business.abstracts.CustOrdService;
import com.etiya.crm.orderservice.business.abstracts.LookupCacheService;
import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.requests.CreateOrderRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ItemCharValsRequest;
import com.etiya.crm.orderservice.business.dtos.requests.OrderConfigurationRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ProdCharValRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ValidateBasketRequest;
import com.etiya.crm.orderservice.business.dtos.responses.ActiveOfferResponse;
import com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.CustOrdItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderItemSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderListItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.ProdCharValResponse;
import com.etiya.crm.orderservice.business.exceptions.BsnInterSpecNotFoundException;
import com.etiya.crm.orderservice.business.exceptions.CampaignNotAppliedToOfferingException;
import com.etiya.crm.orderservice.business.exceptions.CharacteristicValueMismatchException;
import com.etiya.crm.orderservice.business.exceptions.CharacteristicValueMissingException;
import com.etiya.crm.orderservice.business.exceptions.OfferAlreadyActiveException;
import com.etiya.crm.orderservice.business.exceptions.OrderItemNotFoundException;
import com.etiya.crm.orderservice.business.exceptions.OrderNotEditableException;
import com.etiya.crm.orderservice.business.exceptions.OrderNotFoundException;
import com.etiya.crm.orderservice.business.exceptions.ServiceAddressMissingException;
import com.etiya.crm.orderservice.business.rules.BasketValidationRules;
import com.etiya.crm.orderservice.constants.LookupCodes;
import com.etiya.crm.orderservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.orderservice.clients.controllers.CustomerClient;
import com.etiya.crm.orderservice.clients.controllers.ProductClient;
import com.etiya.crm.orderservice.clients.requests.CreateProductCharacteristicValueRequest;
import com.etiya.crm.orderservice.clients.requests.CreateProductRequest;
import com.etiya.crm.orderservice.clients.responses.CampaignOfferingResponse;
import com.etiya.crm.orderservice.clients.responses.CampaignResponse;
import com.etiya.crm.orderservice.clients.responses.CreatedProductResponse;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.clients.responses.ProductCatalogOfferingResponse;
import com.etiya.crm.orderservice.clients.responses.ProductOfferingRelationResponse;
import com.etiya.crm.orderservice.clients.responses.ProductOfferingResponse;
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
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.contracts.typevalue.TypeValueTables;
import com.etiya.crm.shared.events.KafkaTopics;
import com.etiya.crm.shared.events.order.OrderEventTypes;
import com.etiya.crm.shared.events.order.OrderSubmittedEvent;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
    private final ProductClient productClient;
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
        basketValidationRules.ensureNoConflictingItems(request.items(), productClient.getOfferingRelations());
        request.items().forEach(item -> {
            ensureOfferNotAlreadyActive(request.custAcctId(), item.prodOfrId());
            ensureOfferingExists(item.prodOfrId(), item.cmpgId());
        });
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
        List<ProductOfferingRelationResponse> relations = productClient.getOfferingRelations();
        basketValidationRules.ensureNoConflictingItems(request.items(), relations);
        request.items().forEach(item -> ensureOfferNotAlreadyActive(request.custAcctId(), item.prodOfrId()));

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

        // FR-014 ACC-003/004: zorunlu (mandatory) iliskili urunler onceden sadece front-end
        // tarafindan ekleniyordu (bkz. offer-selection.component.ts) - API'yi dogrudan cagiran
        // bir istemci bu korumayi atlayabiliyordu. Sunucu artik zaten sepette olmayanlari
        // kendisi tamamliyor; front-end'in onceden eklediklerini tekrar eklemez (idempotent).
        List<BasketItemRequest> itemsToCreate = expandWithMandatoryOfferings(request.items(), relations, Set.of());

        for (BasketItemRequest itemRequest : itemsToCreate) {
            // product-service tamamlanınca burada ayrica PROD instance olusturulup
            // dogan prodId buraya yazilacak (subscription provisioning, henuz yok).
            addOrderItem(custOrd, request.custAcctId(), request.custId(), itemRequest);
        }

        return buildSummary(custOrd);
    }

    /**
     * Offer Selection'da "Add to Basket" - WAIT durumundaki siparise (createOrder ile acilmis)
     * yeni bir item ekler. Ilk item createOrder'da gelir, buradan itibaren sepete eklenen her
     * urun bu endpoint'ten geçer.
     */
    @Override
    @Transactional
    public OrderSummaryResponse addItem(Long custOrdId, BasketItemRequest request) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));
        ensureEditable(custOrd);
        basketValidationRules.ensureItemNotAlreadyInBasket(request, custOrd.getItems());
        List<ProductOfferingRelationResponse> relations = productClient.getOfferingRelations();
        basketValidationRules.ensureItemNotConflicting(request, custOrd.getItems(), relations);

        Long custAcctId = custOrd.getItems().get(0).getCustAcctId();
        ensureOfferNotAlreadyActive(custAcctId, request.prodOfrId());

        // FR-014 ACC-003/004: bkz. createOrder - alreadyPresent, custOrd'a zaten eklenmis
        // item'lari dikkate alarak ayni zorunlu urunun iki kez eklenmesini engeller.
        Set<Long> alreadyPresent = custOrd.getItems().stream()
                .map(CustOrdItem::getProdOfrId).collect(Collectors.toSet());
        List<BasketItemRequest> itemsToAdd = expandWithMandatoryOfferings(List.of(request), relations, alreadyPresent);

        for (BasketItemRequest itemRequest : itemsToAdd) {
            addOrderItem(custOrd, custAcctId, custOrd.getCustId(), itemRequest);
        }

        return buildSummary(custOrd);
    }

    /**
     * FR-014 ACC-003/004: relations icindeki mandatory=true+active=true iliskilere gore, items
     * icindeki her offering'in zorunlu companion'larini (relation.productOfferingId1 -> id2 yonu,
     * bkz. offer-selection.component.ts buildRequiredOfferingsMap ile ayni yon) alreadyPresent'e
     * (ve birbirlerine) gore idempotent sekilde ekler - front-end zaten eklediyse tekrar eklemez.
     */
    private List<BasketItemRequest> expandWithMandatoryOfferings(List<BasketItemRequest> items,
            List<ProductOfferingRelationResponse> relations, Set<Long> alreadyPresentOfferingIds) {
        List<BasketItemRequest> expanded = new ArrayList<>(items);
        Set<Long> present = new HashSet<>(alreadyPresentOfferingIds);
        items.forEach(item -> present.add(item.prodOfrId()));

        for (BasketItemRequest item : items) {
            for (ProductOfferingRelationResponse relation : relations) {
                if (Boolean.TRUE.equals(relation.mandatory()) && Boolean.TRUE.equals(relation.active())
                        && item.prodOfrId().equals(relation.productOfferingId1())
                        && present.add(relation.productOfferingId2())) {
                    expanded.add(new BasketItemRequest(relation.productOfferingId2(), null, null));
                }
            }
        }
        return expanded;
    }

    /** createOrder/addItem'daki tekrarlanan "item olustur+kaydet+siparise ekle" bloğu. */
    private CustOrdItem addOrderItem(CustOrd custOrd, Long custAcctId, Long custId, BasketItemRequest itemRequest) {
        CustOrdItem item = custOrderItemMapper.toEntity(itemRequest);
        item.setCustOrd(custOrd);
        item.setCustAcctId(custAcctId);
        item.setCustId(custId);
        applyProductOffering(item, itemRequest.prodOfrId(), itemRequest.cmpgId());
        item = custOrdItemRepository.save(item);
        // custOrd yeni persist edildigi icin Hibernate items koleksiyonunu bos baslatir ve
        // ayri bir repository cagrisiyla eklenen satirlari kendiliginden gormez - buildSummary'nin
        // dogru listeyi donebilmesi icin bidirectional iliski burada elle senkron tutulur.
        custOrd.getItems().add(item);

        BsnInterItem bsnInterItem = new BsnInterItem();
        bsnInterItem.setBsnInter(custOrd.getBsnInter());
        bsnInterItem.setRowId(item.getCustOrdItemId());
        bsnInterItemRepository.save(bsnInterItem);
        return item;
    }

    /**
     * FR-014 "In Basket": sepetten cop kutusuyla item cikarma. Item bir kampanyaya
     * (cmpgId) baglıysa, o kampanyayla sepete birlikte eklenmis butun item'lar birlikte
     * cikarilir - kampanyali bagli urunler sepette tek tek degil, hep bir arada
     * yasar/gider.
     */
    @Override
    @Transactional
    public OrderSummaryResponse removeItem(Long custOrdId, Long custOrdItemId) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));
        ensureEditable(custOrd);

        CustOrdItem item = custOrd.getItems().stream()
                .filter(i -> i.getCustOrdItemId().equals(custOrdItemId))
                .findFirst()
                .orElseThrow(() -> new OrderItemNotFoundException(custOrdItemId, custOrdId));

        List<CustOrdItem> itemsToRemove = item.getCmpgId() == null
                ? List.of(item)
                : custOrd.getItems().stream()
                        .filter(i -> item.getCmpgId().equals(i.getCmpgId()))
                        .collect(Collectors.toList());

        for (CustOrdItem toRemove : itemsToRemove) {
            custOrd.getItems().remove(toRemove);
            bsnInterItemRepository.deleteByRowId(toRemove.getCustOrdItemId());
            custOrdItemRepository.delete(toRemove);
        }

        return buildSummary(custOrd);
    }

    /**
     * FR-015: Product Configuration ekraninda basket item basina girilen karakteristikleri ve
     * siparisin servis adresini WAIT durumundaki siparise yazar. Sayfa yenilense de kaybolmasin
     * diye her cagrida gonderilen item'in karakteristikleri bastan yazilir (replace-all,
     * idempotent); adres sadece gonderildiyse guncellenir.
     */
    @Override
    @Transactional
    public OrderSummaryResponse saveConfiguration(Long custOrdId, OrderConfigurationRequest request) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));
        ensureEditable(custOrd);

        if (request.items() != null) {
            for (ItemCharValsRequest itemRequest : request.items()) {
                CustOrdItem item = custOrd.getItems().stream()
                        .filter(i -> i.getCustOrdItemId().equals(itemRequest.custOrdItemId()))
                        .findFirst()
                        .orElseThrow(() -> new OrderItemNotFoundException(itemRequest.custOrdItemId(), custOrdId));

                custOrdCharValRepository.deleteByCustOrdItem_CustOrdItemId(item.getCustOrdItemId());
                if (itemRequest.charVals() != null) {
                    for (ProdCharValRequest charValRequest : itemRequest.charVals()) {
                        validateCharacteristic(charValRequest);
                        CustOrdCharVal charVal = custOrdCharValMapper.toEntity(charValRequest);
                        charVal.setCustOrdItem(item);
                        custOrdCharValRepository.save(charVal);
                    }
                }
            }
        }

        if (request.addressId() != null || request.newAddress() != null) {
            custOrd.setAddressId(resolveAddressId(request.addressId(), request.newAddress(), custOrd));
            custOrd = custOrdRepository.save(custOrd);
        }

        return buildSummary(custOrd);
    }

    /**
     * FR-021: Review & Confirm'de Finish'e basilinca cagrilir. WAIT -> MIDLWARE ("Sipariş
     * Alındı, İşleniyor") gecisini yapar, product-service'te gercek Product'lari senkron
     * olarak provizyonlar (musteri detay ekranindaki fatura hesabi listesinde urun gorunur
     * hale gelir) ve provizyon basariyla bittiginde MIDLWARE -> FINISHED'e gecirip
     * OrderSubmittedEvent'i yayinlar. Provizyon herhangi bir item'da patlarsa @Transactional
     * tum metodu (PROCESSING kaydi dahil) geri alir - siparis gercekte urun olusmadan asla
     * FINISHED'e dusmez.
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

        provisionProducts(custOrd);

        Long finishedStatusId = lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.FINISHED);
        custOrd.setOrdStId(finishedStatusId);
        custOrd = custOrdRepository.save(custOrd);

        publishOrderSubmittedEvent(custOrd);

        return buildSummary(custOrd);
    }

    /**
     * FR-021: Finish'te her item icin product-service'te gercek Product instance'ini
     * (subscription provisioning) olusturur ve donen productId'yi item'a yazar.
     */
    private void provisionProducts(CustOrd custOrd) {
        for (CustOrdItem item : custOrd.getItems()) {
            CreateProductRequest request = new CreateProductRequest(null, item.getProdOfrId(),
                    item.getProdSpecId(), item.getOfrName(), null, item.getCmpgId(), GnlStCodes.ACTIVE);
            CreatedProductResponse created = productClient.createProduct(request);
            item.setProdId(created.productId());
            item.setProdName(created.name());
            custOrdItemRepository.save(item);
            provisionCharacteristics(item);
        }
    }

    /**
     * FR-021: Configuration'da item icin secilmis karakteristikleri (CustOrdCharVal), az once
     * provizyon edilen gercek Product'a (item.getProdId()) product-service'te islemek icin.
     */
    private void provisionCharacteristics(CustOrdItem item) {
        List<CustOrdCharVal> charVals = custOrdCharValRepository
                .findByCustOrdItem_CustOrdItemId(item.getCustOrdItemId());
        for (CustOrdCharVal charVal : charVals) {
            CreateProductCharacteristicValueRequest request = new CreateProductCharacteristicValueRequest(
                    item.getProdId(), charVal.getCharId(), charVal.getCharValId(), charVal.getVal(), null);
            productClient.createProductCharacteristicValue(request);
        }
    }

    /** Review & Confirm'de "Cancel" - WAIT durumundaki siparisi REJECTED'e cevirir. */
    @Override
    @Transactional
    public OrderSummaryResponse cancelOrder(Long custOrdId) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));
        ensureEditable(custOrd);
        
        Long rejectedStatusId = lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.REJECTED);
        custOrd.setOrdStId(rejectedStatusId);
        custOrd = custOrdRepository.save(custOrd);

        return buildSummary(custOrd);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderSummaryResponse getById(Long custOrdId) {
        CustOrd custOrd = custOrdRepository.findById(custOrdId)
                .orElseThrow(() -> new OrderNotFoundException(custOrdId));
        return buildSummary(custOrd);
    }

    // Musteri detay ekranindaki fatura hesabi urun tablosu icin - KASITLI olarak findActiveItems
    // ile AYNI (PROCESSING/FINISHED) durum filtresini kullanir. Onceden filtresiz findByCustAcctId
    // kullaniliyordu: Offer Selection'da createOrder WAIT durumunda bir siparis acar (bkz.
    // createOrder javadoc'u), kullanici Finish'e basmadan sihirbazdan geri donup FARKLI bir
    // sepetle tekrar Next'e basarsa (validateBasketThenCreateOrder her seferinde YENIDEN
    // createOrder cagirir), ilk siparis WAIT durumunda sahipsiz kalir - hicbir zaman
    // FINISHED'e ulasmadigi icin item'larinin prodId/prodName'i de hic set edilmez (bkz.
    // provisionProducts, sadece finishOrder icinde calisir), ama cmpgId/cmpgName createOrder
    // aninda zaten yazilir. Filtresiz sorgu bu terk edilmis WAIT siparisinin item'larini da
    // donduruyordu - urun tablosunda id/adi bos ama kampanya adi/id'si dolu "hayalet" satirlar
    // olarak goruluyordu.
    @Override
    @Transactional(readOnly = true)
    public List<CustOrdItemResponse> getItemsByCustAcctId(Long custAcctId) {
        return findActiveItems(custAcctId).stream()
                .map(custOrderItemMapper::toItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * BR-03 "Already Active": Offer Selection'da bir teklifin bu hesap icin zaten aktif olup
     * olmadigini gostermek icin. product-service'e gitmeye gerek yok - hesabin gercekten
     * tamamlanmis (PROCESSING/FINISHED) siparislerindeki item'lar zaten bu bilgiyi tasir.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ActiveOfferResponse> getActiveOffersByCustAcctId(Long custAcctId) {
        return findActiveItems(custAcctId).stream()
                .map(item -> new ActiveOfferResponse(item.getProdOfrId(), item.getCustOrdItemId(), item.getProdId()))
                .collect(Collectors.toList());
    }

    private List<CustOrdItem> findActiveItems(Long custAcctId) {
        Long processingStatusId = lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.PROCESSING);
        Long finishedStatusId = lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.FINISHED);
        return custOrdItemRepository
                .findByCustAcctIdAndCustOrd_OrdStIdIn(custAcctId, List.of(processingStatusId, finishedStatusId));
    }

    /**
     * FR-014 IK-05: hesapta zaten aktif (PROCESSING/FINISHED) olan bir teklif tekrar sepete
     * eklenemez - aynisi degilse de, ayni katalog kategorisinden (Internet/Mobile/TV) FARKLI bir
     * teklif zaten aktifse yenisi de eklenemez (ör. musteride TV urunu varken ikinci bir TV urunu).
     */
    private void ensureOfferNotAlreadyActive(Long custAcctId, Long prodOfrId) {
        List<CustOrdItem> activeItems = findActiveItems(custAcctId);
        boolean exactMatch = activeItems.stream().anyMatch(item -> prodOfrId.equals(item.getProdOfrId()));
        if (exactMatch) {
            throw new OfferAlreadyActiveException(custAcctId, prodOfrId);
        }

        Map<Long, Long> catalogIdByOfferingId = productClient.getCatalogOfferings().stream()
                .collect(Collectors.toMap(ProductCatalogOfferingResponse::productOfferingId,
                        ProductCatalogOfferingResponse::productCatalogId, (first, second) -> first));

        Long candidateCatalogId = catalogIdByOfferingId.get(prodOfrId);
        boolean sameCategoryActive = candidateCatalogId != null && activeItems.stream()
                .anyMatch(item -> candidateCatalogId.equals(catalogIdByOfferingId.get(item.getProdOfrId())));
        if (sameCategoryActive) {
            throw new OfferAlreadyActiveException(custAcctId, prodOfrId);
        }
    }

    /** Musteri siparis gecmisi (order list ekrani) - tahmini alanlarla eklendi, gerekirse revize edilir. */
    @Override
    @Transactional(readOnly = true)
    public List<OrderListItemResponse> getOrdersByCustId(Long custId) {
        return custOrdRepository.findByCustIdOrderByCdateDesc(custId).stream()
                .map(custOrd -> new OrderListItemResponse(custOrd.getCustOrdId(), custOrd.getOrdStId(),
                        custOrd.getItems().size(), calculateTotalAmount(custOrd), custOrd.getCdate()))
                .collect(Collectors.toList());
    }

    /** WAIT disindaki (MIDLWARE/FINISHED/REJECTED) bir siparis artik configure/finish edilemez. */
    private void ensureEditable(CustOrd custOrd) {
        Long waitStatusId = lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING);
        if (!waitStatusId.equals(custOrd.getOrdStId())) {
            throw new OrderNotEditableException(custOrd.getCustOrdId());
        }
    }

    /**
     * FR-014: validateBasket, createOrder'in aksine item'lari kalici olarak kaydetmedigi icin
     * applyProductOffering'i hic cagirmiyordu - var olmayan bir prodOfrId ile validate-basket
     * 200 donup hata ancak sonraki createOrder adiminda ortaya cikiyordu. Ayni dogrulamayi
     * (offering var mi, cmpgId varsa o offering'e gercekten uygulaniyor mu) burada da yapip
     * sonucu atarak Next adiminda erken geri bildirim verir.
     */
    private void ensureOfferingExists(Long prodOfrId, Long cmpgId) {
        productClient.getById(prodOfrId);
        if (cmpgId != null) {
            resolveCampaignPrice(cmpgId, prodOfrId);
        }
    }

    /**
     * prodOfrId'nin (ve verildiyse cmpgId'nin) product-service'te gercekten var oldugunu
     * dogrular; ofrName/prodSpecId/fiyat/cmpgName snapshot'ini alir.
     */
    private void applyProductOffering(CustOrdItem item, Long prodOfrId, Long cmpgId) {
        ProductOfferingResponse offering = productClient.getById(prodOfrId);
        item.setOfrName(offering.name());
        item.setProdSpecId(offering.productSpecId());
        item.setPrice(offering.totalPrice());

        if (cmpgId != null) {
            CampaignResponse campaign = productClient.getCampaignById(cmpgId);
            item.setCmpgName(campaign.name());
            item.setPrice(resolveCampaignPrice(cmpgId, prodOfrId));
        }
    }

    /** cmpgId'nin bu prodOfrId'ye gercekten uygulandigini dogrular ve indirimli fiyatini doner. */
    private BigDecimal resolveCampaignPrice(Long cmpgId, Long prodOfrId) {
        return productClient.getCampaignOfferingsByCampaignId(cmpgId).stream()
                .filter(campaignOffering -> prodOfrId.equals(campaignOffering.productOfferingId()))
                .map(CampaignOfferingResponse::discountedPrice)
                .findFirst()
                .orElseThrow(() -> new CampaignNotAppliedToOfferingException(cmpgId, prodOfrId));
    }

    /**
     * charId'nin var oldugunu, verildiyse charValId'nin de o charId'ye ait oldugunu dogrular.
     * charValId (listeden secim) ve val (serbest metin) en az birinin dolu olmasini zorunlu kilar
     * - ikisi de bos kalirsa DB'deki chk_cust_ord_char_val_has_value constraint'ine gitmeden
     * once burada 400 donduruluyor (bkz. B-20).
     */
    private void validateCharacteristic(ProdCharValRequest request) {
        lookupCacheService.getCharacteristic(request.charId());

        if (request.charValId() == null && (request.val() == null || request.val().isBlank())) {
            throw new CharacteristicValueMissingException(request.charId());
        }

        if (request.charValId() != null) {
            GnlCharValResponse charVal = lookupCacheService.getCharacteristicValue(request.charValId());
            if (!charVal.charId().equals(request.charId())) {
                throw new CharacteristicValueMismatchException(request.charValId(), request.charId());
            }
        }
    }

    private Long resolveAddressId(Long addressId, AddressInfoRequest newAddress, CustOrd custOrd) {
        basketValidationRules.ensureAddressProvided(addressId, newAddress);

        if (addressId != null) {
            // Var olan adres secildi - musteriye ya da hesaplarindan birine ait oldugunu dogrulayip
            // ayrica bir kayit olusturmadan sadece id'sini saklariz.
            AddressResponse address = contactAddressClient.getById(addressId);
            List<CustomerAccountResponse> accounts = customerClient.getAccounts(custOrd.getCustId(), 1000).content();
            Long custDataTypeId = lookupCacheService.resolveDataTypeId(TypeValueTables.CUSTOMER);
            basketValidationRules.ensureAddressBelongsToCustomer(address, custOrd.getCustId(), custDataTypeId, accounts);
            return address.id();
        }

        // musterinin adres listesine kaydedilsin diye CUSTOMER/custId sahipliginde olusturulur
        // (ORDER/custOrdId degil) - boylece musteri detay ekraninda da gorunur ve ileride tekrar
        // secilebilir. primary=false: mevcut ana adresi sessizce degistirmemek icin (bkz. contact-info-service
        // unsetOtherPrimaryAddresses - primary=true her diger primary'yi false yapardi).
        Long dataTypeId = lookupCacheService.resolveDataTypeId(TypeValueTables.CUSTOMER);
        CreateAddressRequest addressRequest = addressMapper.toCreateAddressRequest(newAddress, custOrd.getCustId(),
                dataTypeId, false);
        return contactAddressClient.createAddress(addressRequest).id();
    }

    private OrderSummaryResponse buildSummary(CustOrd custOrd) {
        Map<Long, List<ProdCharValResponse>> charValsByItem = custOrdCharValRepository
                .findByCustOrdItem_CustOrd_CustOrdId(custOrd.getCustOrdId()).stream()
                .collect(Collectors.groupingBy(cv -> cv.getCustOrdItem().getCustOrdItemId(),
                        Collectors.mapping(custOrdCharValMapper::toResponse, Collectors.toList())));

        List<OrderItemSummaryResponse> itemResponses = custOrd.getItems().stream()
                .map(item -> custOrderItemMapper.toSummaryResponse(item,
                        charValsByItem.getOrDefault(item.getCustOrdItemId(), List.of())))
                .collect(Collectors.toList());

        AddressSummaryResponse addressSummary = custOrd.getAddressId() != null
                ? buildAddressSummary(contactAddressClient.getById(custOrd.getAddressId()))
                : null;

        return new OrderSummaryResponse(custOrd.getCustOrdId(), custOrd.getBsnInter().getBsnInterId(), custOrd.getOrdStId(),
                itemResponses, addressSummary, calculateTotalAmount(custOrd));
    }

    // cityName AddressResponse'ta yok (sadece cityId) - "Ürün Teklifi Detayları" ekraninda
    // sehir adinin gosterilebilmesi icin lookup-service'ten ayrica cekilip eklenir.
    private AddressSummaryResponse buildAddressSummary(AddressResponse address) {
        AddressSummaryResponse base = addressMapper.toSummaryResponse(address);
        return new AddressSummaryResponse(base.addressId(), base.cityId(), resolveCityName(address.cityId()),
                base.streetName(), base.buildingName(), base.addressDesc());
    }

    // cityName sadece goruntuleme icin bir zenginlestirme - cityId lookup-service'te bulunamazsa
    // (eski/tutarsiz veri) tum siparis cagrisini dusurmek yerine sessizce null'a dusulur.
    // Hicbir Feign client'ta fallback tanimli olmadigi icin (bkz. AbstractDownstreamExceptionHandler
    // sinif-ustu yorumu) resilience4j 404'u dahi NoFallbackAvailableException'a sarar; gercek
    // FeignException zincirde .getCause() ile gelir, o yuzden burada da ayni unwrap deseni kullanilir.
    private String resolveCityName(Long cityId) {
        try {
            return lookupCacheService.getGeneralType(cityId).name();
        } catch (RuntimeException ex) {
            if (isNotFound(ex)) {
                return null;
            }
            throw ex;
        }
    }

    private boolean isNotFound(Throwable ex) {
        Throwable current = ex;
        for (int depth = 0; current != null && depth < 10; depth++) {
            if (current instanceof FeignException.NotFound) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private BigDecimal calculateTotalAmount(CustOrd custOrd) {
        return custOrd.getItems().stream()
                .map(CustOrdItem::getPrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
