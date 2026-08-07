package com.etiya.crm.orderservice.business.concretes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.etiya.crm.orderservice.business.abstracts.LookupCacheService;
import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.dtos.requests.CreateOrderRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ItemCharValsRequest;
import com.etiya.crm.orderservice.business.dtos.requests.OrderConfigurationRequest;
import com.etiya.crm.orderservice.business.dtos.requests.ProdCharValRequest;
import com.etiya.crm.orderservice.business.dtos.responses.ActiveOfferResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderListItemResponse;
import com.etiya.crm.orderservice.business.dtos.responses.OrderSummaryResponse;
import com.etiya.crm.orderservice.business.dtos.responses.ProdCharValResponse;
import com.etiya.crm.orderservice.business.exceptions.AccountNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressSelectionInvalidException;
import com.etiya.crm.orderservice.business.exceptions.CharacteristicValueMismatchException;
import com.etiya.crm.orderservice.business.exceptions.DuplicateBasketItemException;
import com.etiya.crm.orderservice.business.exceptions.OrderItemNotFoundException;
import com.etiya.crm.orderservice.business.exceptions.OrderNotEditableException;
import com.etiya.crm.orderservice.business.exceptions.OrderNotFoundException;
import com.etiya.crm.orderservice.business.exceptions.ServiceAddressMissingException;
import com.etiya.crm.orderservice.business.rules.BasketValidationRules;
import com.etiya.crm.orderservice.clients.controllers.ContactAddressClient;
import com.etiya.crm.orderservice.clients.controllers.CustomerClient;
import com.etiya.crm.orderservice.clients.controllers.ProductClient;
import com.etiya.crm.orderservice.clients.requests.CreateProductRequest;
import com.etiya.crm.orderservice.clients.responses.CampaignResponse;
import com.etiya.crm.orderservice.clients.responses.CreatedProductResponse;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountPageResponse;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.clients.responses.CustomerResponse;
import com.etiya.crm.orderservice.clients.responses.ProductOfferingResponse;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterItemRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.BsnInterSpecRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.CustOrdCharValRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.CustOrdItemRepository;
import com.etiya.crm.orderservice.dataAccess.abstracts.CustOrdRepository;
import com.etiya.crm.orderservice.entities.concretes.BsnInter;
import com.etiya.crm.orderservice.entities.concretes.BsnInterSpec;
import com.etiya.crm.orderservice.entities.concretes.CustOrd;
import com.etiya.crm.orderservice.entities.concretes.CustOrdCharVal;
import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;
import com.etiya.crm.orderservice.mapper.AddressMapper;
import com.etiya.crm.orderservice.mapper.CustOrdCharValMapper;
import com.etiya.crm.orderservice.mapper.CustOrderItemMapper;
import org.mapstruct.factory.Mappers;
import com.etiya.crm.shared.contracts.address.AddressResponse;
import com.etiya.crm.shared.contracts.address.CreateAddressRequest;
import com.etiya.crm.shared.contracts.gnlchar.GnlCharResponse;
import com.etiya.crm.shared.contracts.gnlcharval.GnlCharValResponse;
import com.etiya.crm.shared.contracts.gnlst.GnlStCodes;
import com.etiya.crm.shared.contracts.gnlst.GnlStGroups;
import com.etiya.crm.shared.events.outbox.OutboxEventPublisher;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * createOrder/saveConfiguration/finishOrder ucgeninin (WAIT -> configure -> MIDLWARE) durum
 * gecislerini ve guard'larini dogrular. Repository'ler mock oldugu icin CustOrd.items/charVals
 * gibi lazy koleksiyonlarin gercek Hibernate'te otomatik dolmasi (persist + auto-flush) burada
 * elle simule edilir (bkz. stubSaveAddsToParentCollection).
 */
@ExtendWith(MockitoExtension.class)
class CustOrdManagerTest {

	private static final Long CUST_ID = 1L;
	private static final Long CUST_ACCT_ID = 10L;
	private static final Long CUST_ORD_ID = 100L;
	private static final Long WAIT_STATUS_ID = 51L;
	private static final Long PROCESSING_STATUS_ID = 52L;

	@Mock
	private AddressMapper addressMapper;
	@Mock
	private CustOrdCharValMapper custOrdCharValMapper;
	// gercek MapStruct implementasyonu kullanilir - duz alan kopyalama oldugu icin mock'lamaya gerek yok.
	private final CustOrderItemMapper custOrderItemMapper = Mappers.getMapper(CustOrderItemMapper.class);
	@Mock
	private CustOrdRepository custOrdRepository;
	@Mock
	private CustOrdItemRepository custOrdItemRepository;
	@Mock
	private CustOrdCharValRepository custOrdCharValRepository;
	@Mock
	private BsnInterRepository bsnInterRepository;
	@Mock
	private BsnInterItemRepository bsnInterItemRepository;
	@Mock
	private BsnInterSpecRepository bsnInterSpecRepository;
	@Mock
	private CustomerClient customerClient;
	@Mock
	private ContactAddressClient contactAddressClient;
	@Mock
	private ProductClient productClient;
	@Mock
	private LookupCacheService lookupCacheService;
	@Mock
	private OutboxEventPublisher outboxEventPublisher;

	private CustOrdManager custOrdManager;

	@BeforeEach
	void setUp() {
		custOrdManager = new CustOrdManager(addressMapper, custOrdCharValMapper, custOrderItemMapper,
				custOrdRepository, custOrdItemRepository, custOrdCharValRepository, bsnInterRepository,
				bsnInterItemRepository, bsnInterSpecRepository, customerClient, contactAddressClient,
				productClient, lookupCacheService, new BasketValidationRules(), outboxEventPublisher);
	}

	// ---- createOrder ----

	@Test
	void createOrder_opensOrderInWaitStatus_withItemsAndNoAddressOrCharVals() {
		stubCustomerAndAccount();
		stubBsnInterSpec();
		stubBsnInterSave();
		stubCustOrdSave();
		stubCustOrdItemSaveAddsToParent();
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);
		when(productClient.getById(200L)).thenReturn(
				new ProductOfferingResponse(200L, 9L, "Mobile Prepaid 5GB", "descr", null, 1L, new BigDecimal("89.90")));

		CreateOrderRequest request = new CreateOrderRequest(CUST_ID, CUST_ACCT_ID,
				List.of(new BasketItemRequest(200L, null, null)));

		OrderSummaryResponse response = custOrdManager.createOrder(request);

		assertThat(response.custOrdId()).isEqualTo(CUST_ORD_ID);
		assertThat(response.ordStId()).isEqualTo(WAIT_STATUS_ID);
		assertThat(response.items()).hasSize(1);
		assertThat(response.items().get(0).prodOfrId()).isEqualTo(200L);
		assertThat(response.items().get(0).ofrName()).isEqualTo("Mobile Prepaid 5GB");
		assertThat(response.totalAmount()).isEqualByComparingTo("89.90");
		assertThat(response.items().get(0).charVals()).isEmpty();
		assertThat(response.serviceAddress()).isNull();
	}

	@Test
	void createOrder_throws_whenAccountDoesNotBelongToCustomer() {
		when(customerClient.getById(CUST_ID)).thenReturn(new CustomerResponse(CUST_ID, 2L, 3L, true, List.of()));
		when(customerClient.getAccounts(CUST_ID, 1000)).thenReturn(new CustomerAccountPageResponse(
				List.of(new CustomerAccountResponse(999L, "AC-999", "n", "d", null, 1L, 1L, true))));

		CreateOrderRequest request = new CreateOrderRequest(CUST_ID, CUST_ACCT_ID,
				List.of(new BasketItemRequest(200L, null, null)));

		assertThatThrownBy(() -> custOrdManager.createOrder(request))
				.isInstanceOf(AccountNotBelongToCustomerException.class);
	}

	// ---- addItem ----

	@Test
	void addItem_appendsToExistingWaitOrder() {
		CustOrd custOrd = waitingOrder();
		CustOrdItem existingItem = new CustOrdItem();
		existingItem.setCustOrdItemId(900L);
		existingItem.setCustOrd(custOrd);
		existingItem.setCustAcctId(CUST_ACCT_ID);
		existingItem.setProdOfrId(200L);
		custOrd.getItems().add(existingItem);

		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);
		when(productClient.getById(300L)).thenReturn(
				new ProductOfferingResponse(300L, 9L, "International Roaming Pack", "descr", null, 1L, new BigDecimal("149.90")));
		when(custOrdItemRepository.save(any(CustOrdItem.class))).thenAnswer(inv -> {
			CustOrdItem item = inv.getArgument(0);
			item.setCustOrdItemId(901L);
			return item;
		});

		OrderSummaryResponse response = custOrdManager.addItem(CUST_ORD_ID, new BasketItemRequest(300L, null, null));

		assertThat(response.items()).hasSize(2);
		assertThat(response.items().get(1).prodOfrId()).isEqualTo(300L);
		verify(bsnInterItemRepository).save(any());
	}

	@Test
	void addItem_throws_whenOrderNotEditable() {
		CustOrd custOrd = waitingOrder();
		custOrd.setOrdStId(PROCESSING_STATUS_ID);
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		assertThatThrownBy(() -> custOrdManager.addItem(CUST_ORD_ID, new BasketItemRequest(300L, null, null)))
				.isInstanceOf(OrderNotEditableException.class);
	}

	@Test
	void addItem_throws_whenAlreadyInBasket() {
		CustOrd custOrd = waitingOrder();
		CustOrdItem existingItem = new CustOrdItem();
		existingItem.setCustOrdItemId(900L);
		existingItem.setCustOrd(custOrd);
		existingItem.setCustAcctId(CUST_ACCT_ID);
		existingItem.setProdOfrId(200L);
		custOrd.getItems().add(existingItem);

		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		assertThatThrownBy(() -> custOrdManager.addItem(CUST_ORD_ID, new BasketItemRequest(200L, null, null)))
				.isInstanceOf(DuplicateBasketItemException.class);
	}

	@Test
	void addItem_resolvesCampaignName_whenCmpgIdProvided() {
		CustOrd custOrd = waitingOrder();
		CustOrdItem existingItem = new CustOrdItem();
		existingItem.setCustOrdItemId(900L);
		existingItem.setCustOrd(custOrd);
		existingItem.setCustAcctId(CUST_ACCT_ID);
		existingItem.setProdOfrId(200L);
		custOrd.getItems().add(existingItem);

		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);
		when(productClient.getById(300L)).thenReturn(
				new ProductOfferingResponse(300L, 9L, "International Roaming Pack", "descr", null, 1L, new BigDecimal("149.90")));
		when(productClient.getCampaignById(40L))
				.thenReturn(new CampaignResponse(40L, "Summer Discount", "descr", "CMP-40", null, 1L, false));
		when(custOrdItemRepository.save(any(CustOrdItem.class))).thenAnswer(inv -> {
			CustOrdItem item = inv.getArgument(0);
			item.setCustOrdItemId(901L);
			return item;
		});

		OrderSummaryResponse response = custOrdManager.addItem(CUST_ORD_ID, new BasketItemRequest(300L, 40L, null));

		assertThat(response.items().get(1).cmpgName()).isEqualTo("Summer Discount");
	}

	// ---- removeItem ----

	@Test
	void removeItem_removesItem_keepsOthers() {
		CustOrd custOrd = waitingOrder();
		CustOrdItem item1 = new CustOrdItem();
		item1.setCustOrdItemId(900L);
		item1.setCustOrd(custOrd);
		item1.setProdOfrId(200L);
		CustOrdItem item2 = new CustOrdItem();
		item2.setCustOrdItemId(901L);
		item2.setCustOrd(custOrd);
		item2.setProdOfrId(300L);
		custOrd.getItems().add(item1);
		custOrd.getItems().add(item2);

		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		OrderSummaryResponse response = custOrdManager.removeItem(CUST_ORD_ID, 900L);

		assertThat(response.items()).hasSize(1);
		assertThat(response.items().get(0).prodOfrId()).isEqualTo(300L);
		verify(bsnInterItemRepository).deleteByRowId(900L);
		verify(custOrdItemRepository).delete(item1);
	}

	@Test
	void removeItem_throws_whenItemNotFound() {
		CustOrd custOrd = waitingOrder();
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		assertThatThrownBy(() -> custOrdManager.removeItem(CUST_ORD_ID, 999L))
				.isInstanceOf(OrderItemNotFoundException.class);
	}

	@Test
	void removeItem_throws_whenOrderNotEditable() {
		CustOrd custOrd = waitingOrder();
		custOrd.setOrdStId(PROCESSING_STATUS_ID);
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		assertThatThrownBy(() -> custOrdManager.removeItem(CUST_ORD_ID, 900L))
				.isInstanceOf(OrderNotEditableException.class);
	}

	// ---- saveConfiguration ----

	@Test
	void saveConfiguration_throws_whenOrderNotFound() {
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.empty());

		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(), null, null);

		assertThatThrownBy(() -> custOrdManager.saveConfiguration(CUST_ORD_ID, request))
				.isInstanceOf(OrderNotFoundException.class);
	}

	@Test
	void saveConfiguration_throws_whenOrderAlreadyProcessing() {
		CustOrd custOrd = waitingOrder();
		custOrd.setOrdStId(PROCESSING_STATUS_ID);
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(), 77L, null);

		assertThatThrownBy(() -> custOrdManager.saveConfiguration(CUST_ORD_ID, request))
				.isInstanceOf(OrderNotEditableException.class);
	}

	@Test
	void saveConfiguration_throws_whenBothAddressIdAndNewAddressGiven() {
		CustOrd custOrd = waitingOrder();
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		AddressInfoRequest newAddress = new AddressInfoRequest(1L, "Street", "12", "Desc");
		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(), 77L, newAddress);

		assertThatThrownBy(() -> custOrdManager.saveConfiguration(CUST_ORD_ID, request))
				.isInstanceOf(AddressSelectionInvalidException.class);
	}

	@Test
	void saveConfiguration_replacesItemCharVals_andStoresExistingAddressId() {
		CustOrd custOrd = waitingOrder();
		CustOrdItem item = new CustOrdItem();
		item.setCustOrdItemId(900L);
		item.setCustOrd(custOrd);
		custOrd.getItems().add(item);

		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		ProdCharValRequest charValRequest = new ProdCharValRequest(1L, 2L, "200Mbps");
		when(lookupCacheService.getCharacteristic(1L))
				.thenReturn(new GnlCharResponse(1L, "Speed", "descr", null, "SPEED", true, null, null, null, null));
		when(lookupCacheService.getCharacteristicValue(2L)).thenReturn(
				new GnlCharValResponse(2L, 1L, false, "200Mbps", "200M", null, null, true, null, null, null, null));
		when(custOrdCharValMapper.toEntity(charValRequest)).thenAnswer(inv -> {
			CustOrdCharVal entity = new CustOrdCharVal();
			entity.setCharId(1L);
			entity.setCharValId(2L);
			entity.setVal("200Mbps");
			return entity;
		});
		CustOrdCharVal savedCharVal = new CustOrdCharVal();
		savedCharVal.setCharId(1L);
		savedCharVal.setCharValId(2L);
		savedCharVal.setVal("200Mbps");
		savedCharVal.setCustOrdItem(item);
		when(custOrdCharValRepository.save(any(CustOrdCharVal.class))).thenReturn(savedCharVal);
		when(custOrdCharValRepository.findByCustOrdItem_CustOrd_CustOrdId(CUST_ORD_ID)).thenReturn(List.of(savedCharVal));
		when(custOrdCharValMapper.toResponse(savedCharVal)).thenReturn(new ProdCharValResponse(1L, 2L, "200Mbps"));

		AddressResponse existingAddress = new AddressResponse(77L, CUST_ID, 5L, 1L, "Street", "12", "Desc", true,
				null, null, null, null);
		when(contactAddressClient.getById(77L)).thenReturn(existingAddress);
		when(addressMapper.toSummaryResponse(existingAddress))
				.thenReturn(new com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse(77L, 1L,
						null, "Street", "12", "Desc"));
		when(lookupCacheService.getGeneralType(1L)).thenReturn(new com.etiya.crm.shared.contracts.gnltp.GnlTpResponse(
				1L, "TestCity", null, "TESTCITY", "CITY", "CITY", true, null, null, null, null));
		when(customerClient.getAccounts(CUST_ID, 1000)).thenReturn(new CustomerAccountPageResponse(
				List.of(new CustomerAccountResponse(CUST_ACCT_ID, "AC-1", "n", "d", null, 1L, 1L, true))));
		when(lookupCacheService.resolveDataTypeId("CUST")).thenReturn(5L);
		when(custOrdRepository.save(custOrd)).thenReturn(custOrd);

		ItemCharValsRequest itemRequest = new ItemCharValsRequest(900L, List.of(charValRequest));
		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(itemRequest), 77L, null);

		OrderSummaryResponse response = custOrdManager.saveConfiguration(CUST_ORD_ID, request);

		assertThat(response.items().get(0).charVals()).hasSize(1);
		assertThat(custOrd.getAddressId()).isEqualTo(77L);
		verify(custOrdCharValRepository).deleteByCustOrdItem_CustOrdItemId(900L);
		verify(contactAddressClient, never()).createAddress(any());
	}

	@Test
	void saveConfiguration_throws_whenItemDoesNotBelongToOrder() {
		CustOrd custOrd = waitingOrder();
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		ItemCharValsRequest itemRequest = new ItemCharValsRequest(999L, List.of());
		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(itemRequest), null, null);

		assertThatThrownBy(() -> custOrdManager.saveConfiguration(CUST_ORD_ID, request))
				.isInstanceOf(OrderItemNotFoundException.class);
	}

	@Test
	void saveConfiguration_throws_whenCharValDoesNotBelongToChar() {
		CustOrd custOrd = waitingOrder();
		CustOrdItem item = new CustOrdItem();
		item.setCustOrdItemId(900L);
		item.setCustOrd(custOrd);
		custOrd.getItems().add(item);

		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		ProdCharValRequest charValRequest = new ProdCharValRequest(1L, 2L, "200Mbps");
		when(lookupCacheService.getCharacteristic(1L))
				.thenReturn(new GnlCharResponse(1L, "Speed", "descr", null, "SPEED", true, null, null, null, null));
		// charValId=2, ama gercekte charId=9'a ait - siparisin gonderdigi charId=1 ile eslesmiyor
		when(lookupCacheService.getCharacteristicValue(2L)).thenReturn(
				new GnlCharValResponse(2L, 9L, false, "200Mbps", "200M", null, null, true, null, null, null, null));

		ItemCharValsRequest itemRequest = new ItemCharValsRequest(900L, List.of(charValRequest));
		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(itemRequest), null, null);

		assertThatThrownBy(() -> custOrdManager.saveConfiguration(CUST_ORD_ID, request))
				.isInstanceOf(CharacteristicValueMismatchException.class);
	}

	@Test
	void saveConfiguration_throws_whenAddressDoesNotBelongToCustomer() {
		CustOrd custOrd = waitingOrder();
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		AddressResponse someoneElsesAddress = new AddressResponse(77L, 999L, 5L, 1L, "Street", "12", "Desc", true,
				null, null, null, null);
		when(contactAddressClient.getById(77L)).thenReturn(someoneElsesAddress);
		when(customerClient.getAccounts(CUST_ID, 1000)).thenReturn(new CustomerAccountPageResponse(
				List.of(new CustomerAccountResponse(CUST_ACCT_ID, "AC-1", "n", "d", null, 1L, 1L, true))));
		when(lookupCacheService.resolveDataTypeId("CUST")).thenReturn(5L);

		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(), 77L, null);

		assertThatThrownBy(() -> custOrdManager.saveConfiguration(CUST_ORD_ID, request))
				.isInstanceOf(AddressNotBelongToCustomerException.class);
	}

	@Test
	void saveConfiguration_createsNewAddress_whenNewAddressProvided() {
		CustOrd custOrd = waitingOrder();
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);
		when(lookupCacheService.resolveDataTypeId("ORDER")).thenReturn(21L);

		AddressInfoRequest newAddress = new AddressInfoRequest(5L, "Street", "12", "Desc");
		CreateAddressRequest createAddressRequest = new CreateAddressRequest(CUST_ORD_ID, 21L, 5L, "Street", "12",
				"Desc", true);
		when(addressMapper.toCreateAddressRequest(newAddress, CUST_ORD_ID, 21L, true)).thenReturn(createAddressRequest);

		AddressResponse createdAddress = new AddressResponse(88L, CUST_ORD_ID, 21L, 5L, "Street", "12", "Desc", true,
				null, null, null, null);
		when(contactAddressClient.createAddress(createAddressRequest)).thenReturn(createdAddress);
		when(contactAddressClient.getById(88L)).thenReturn(createdAddress);
		when(addressMapper.toSummaryResponse(createdAddress))
				.thenReturn(new com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse(88L, 5L,
						null, "Street", "12", "Desc"));
		when(lookupCacheService.getGeneralType(5L)).thenReturn(new com.etiya.crm.shared.contracts.gnltp.GnlTpResponse(
				5L, "Ankara", null, "ANKARA", "CITY", "CITY", true, null, null, null, null));
		when(custOrdRepository.save(custOrd)).thenReturn(custOrd);

		OrderConfigurationRequest request = new OrderConfigurationRequest(List.of(), null, newAddress);

		custOrdManager.saveConfiguration(CUST_ORD_ID, request);

		assertThat(custOrd.getAddressId()).isEqualTo(88L);
	}

	// ---- finishOrder ----

	@Test
	void finishOrder_throws_whenServiceAddressMissing() {
		CustOrd custOrd = waitingOrder(); // addressId hic set edilmemis
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		assertThatThrownBy(() -> custOrdManager.finishOrder(CUST_ORD_ID))
				.isInstanceOf(ServiceAddressMissingException.class);

		verify(outboxEventPublisher, never()).publish(any(), any(), any(), any());
	}

	@Test
	void finishOrder_throws_whenOrderNotInWaitStatus() {
		CustOrd custOrd = waitingOrder();
		custOrd.setAddressId(77L);
		custOrd.setOrdStId(PROCESSING_STATUS_ID); // zaten finish edilmis
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		assertThatThrownBy(() -> custOrdManager.finishOrder(CUST_ORD_ID))
				.isInstanceOf(OrderNotEditableException.class);
	}

	@Test
	void finishOrder_transitionsToProcessing_andPublishesEvent() {
		CustOrd custOrd = waitingOrder();
		custOrd.setAddressId(77L);
		CustOrdItem item = new CustOrdItem();
		item.setCustOrd(custOrd);
		item.setCustAcctId(CUST_ACCT_ID);
		item.setProdOfrId(200L);
		item.setProdSpecId(9L);
		custOrd.getItems().add(item);

		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.PROCESSING))
				.thenReturn(PROCESSING_STATUS_ID);
		when(custOrdRepository.save(custOrd)).thenReturn(custOrd);
		AddressResponse address = new AddressResponse(77L, CUST_ORD_ID, 21L, 5L, "Street", "12", "Desc", true, null,
				null, null, null);
		when(contactAddressClient.getById(77L)).thenReturn(address);
		when(addressMapper.toSummaryResponse(address))
				.thenReturn(new com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse(77L, 5L,
						null, "Street", "12", "Desc"));
		when(lookupCacheService.getGeneralType(5L)).thenReturn(new com.etiya.crm.shared.contracts.gnltp.GnlTpResponse(
				5L, "Ankara", null, "ANKARA", "CITY", "CITY", true, null, null, null, null));
		when(productClient.createProduct(any()))
				.thenReturn(new CreatedProductResponse(500L, null, 200L, 9L, "Mobile Prepaid 5GB", null, null, 1L));

		OrderSummaryResponse response = custOrdManager.finishOrder(CUST_ORD_ID);

		assertThat(response.ordStId()).isEqualTo(PROCESSING_STATUS_ID);
		assertThat(item.getProdId()).isEqualTo(500L);
		verify(outboxEventPublisher).publish(eq("order"), eq(CUST_ORD_ID.toString()), eq("OrderSubmitted"), any());
	}

	@Test
	void finishOrder_provisionsProduct_withOfferAndSpecFromItem() {
		CustOrd custOrd = waitingOrder();
		custOrd.setAddressId(77L);
		CustOrdItem item = new CustOrdItem();
		item.setCustOrd(custOrd);
		item.setCustAcctId(CUST_ACCT_ID);
		item.setProdOfrId(200L);
		item.setProdSpecId(9L);
		item.setOfrName("Mobile Prepaid 5GB");
		item.setCmpgId(40L);
		custOrd.getItems().add(item);

		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.PROCESSING))
				.thenReturn(PROCESSING_STATUS_ID);
		when(custOrdRepository.save(custOrd)).thenReturn(custOrd);
		AddressResponse address = new AddressResponse(77L, CUST_ORD_ID, 21L, 5L, "Street", "12", "Desc", true, null,
				null, null, null);
		when(contactAddressClient.getById(77L)).thenReturn(address);
		when(addressMapper.toSummaryResponse(address))
				.thenReturn(new com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse(77L, 5L,
						null, "Street", "12", "Desc"));
		when(lookupCacheService.getGeneralType(5L)).thenReturn(new com.etiya.crm.shared.contracts.gnltp.GnlTpResponse(
				5L, "Ankara", null, "ANKARA", "CITY", "CITY", true, null, null, null, null));
		when(productClient.createProduct(new CreateProductRequest(null, 200L, 9L, "Mobile Prepaid 5GB", null, 40L,
				GnlStCodes.ACTIVE)))
				.thenReturn(new CreatedProductResponse(500L, null, 200L, 9L, "Mobile Prepaid 5GB", null, 40L, 1L));

		custOrdManager.finishOrder(CUST_ORD_ID);

		assertThat(item.getProdId()).isEqualTo(500L);
		assertThat(item.getProdName()).isEqualTo("Mobile Prepaid 5GB");
		verify(custOrdItemRepository).save(item);
	}

	// ---- cancelOrder ----

	@Test
	void cancelOrder_transitionsToRejected() {
		CustOrd custOrd = waitingOrder();
		Long rejectedStatusId = 53L;
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.REJECTED)).thenReturn(rejectedStatusId);
		when(custOrdRepository.save(custOrd)).thenReturn(custOrd);

		OrderSummaryResponse response = custOrdManager.cancelOrder(CUST_ORD_ID);

		assertThat(response.ordStId()).isEqualTo(rejectedStatusId);
	}

	@Test
	void cancelOrder_throws_whenOrderNotEditable() {
		CustOrd custOrd = waitingOrder();
		custOrd.setOrdStId(PROCESSING_STATUS_ID);
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.WAITING)).thenReturn(WAIT_STATUS_ID);

		assertThatThrownBy(() -> custOrdManager.cancelOrder(CUST_ORD_ID))
				.isInstanceOf(OrderNotEditableException.class);
	}

	// ---- getById ----

	@Test
	void getById_throws_whenOrderNotFound() {
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> custOrdManager.getById(CUST_ORD_ID)).isInstanceOf(OrderNotFoundException.class);
	}

	@Test
	void getById_resolvesServiceAddress_whenAddressIdSet() {
		CustOrd custOrd = waitingOrder();
		custOrd.setAddressId(77L);
		when(custOrdRepository.findById(CUST_ORD_ID)).thenReturn(Optional.of(custOrd));
		AddressResponse address = new AddressResponse(77L, CUST_ORD_ID, 21L, 5L, "Street", "12", "Desc", true, null,
				null, null, null);
		when(contactAddressClient.getById(77L)).thenReturn(address);
		when(addressMapper.toSummaryResponse(address))
				.thenReturn(new com.etiya.crm.orderservice.business.dtos.responses.AddressSummaryResponse(77L, 5L,
						null, "Street", "12", "Desc"));
		when(lookupCacheService.getGeneralType(5L)).thenReturn(new com.etiya.crm.shared.contracts.gnltp.GnlTpResponse(
				5L, "Ankara", null, "ANKARA", "CITY", "CITY", true, null, null, null, null));

		OrderSummaryResponse response = custOrdManager.getById(CUST_ORD_ID);

		assertThat(response.serviceAddress()).isNotNull();
		assertThat(response.serviceAddress().addressId()).isEqualTo(77L);
		assertThat(response.serviceAddress().cityName()).isEqualTo("Ankara");
	}

	// ---- getOrdersByCustId ----

	@Test
	void getOrdersByCustId_returnsOneRowPerOrder_withItemCountAndTotal() {
		CustOrd custOrd = waitingOrder();
		CustOrdItem item1 = new CustOrdItem();
		item1.setCustOrdItemId(900L);
		item1.setCustOrd(custOrd);
		item1.setPrice(new BigDecimal("89.90"));
		CustOrdItem item2 = new CustOrdItem();
		item2.setCustOrdItemId(901L);
		item2.setCustOrd(custOrd);
		item2.setPrice(new BigDecimal("149.90"));
		custOrd.getItems().add(item1);
		custOrd.getItems().add(item2);

		when(custOrdRepository.findByCustIdOrderByCdateDesc(CUST_ID)).thenReturn(List.of(custOrd));

		List<OrderListItemResponse> response = custOrdManager.getOrdersByCustId(CUST_ID);

		assertThat(response).hasSize(1);
		assertThat(response.get(0).custOrdId()).isEqualTo(CUST_ORD_ID);
		assertThat(response.get(0).itemCount()).isEqualTo(2);
		assertThat(response.get(0).totalAmount()).isEqualByComparingTo("239.80");
	}

	// ---- getActiveOffersByCustAcctId ----

	@Test
	void getActiveOffersByCustAcctId_returnsOnlyProcessingAndFinishedItems() {
		Long finishedStatusId = 54L;
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.PROCESSING))
				.thenReturn(PROCESSING_STATUS_ID);
		when(lookupCacheService.resolveStatusId(GnlStGroups.CUST_ORDER, GnlStCodes.FINISHED))
				.thenReturn(finishedStatusId);

		CustOrdItem activeItem = new CustOrdItem();
		activeItem.setCustOrdItemId(900L);
		activeItem.setProdOfrId(200L);
		activeItem.setProdId(500L);
		when(custOrdItemRepository.findByCustAcctIdAndCustOrd_OrdStIdIn(CUST_ACCT_ID,
				List.of(PROCESSING_STATUS_ID, finishedStatusId))).thenReturn(List.of(activeItem));

		List<ActiveOfferResponse> response = custOrdManager.getActiveOffersByCustAcctId(CUST_ACCT_ID);

		assertThat(response).hasSize(1);
		assertThat(response.get(0).prodOfrId()).isEqualTo(200L);
		assertThat(response.get(0).prodId()).isEqualTo(500L);
	}

	// ---- helpers ----

	private void stubCustomerAndAccount() {
		when(customerClient.getById(CUST_ID)).thenReturn(new CustomerResponse(CUST_ID, 2L, 3L, true, List.of()));
		when(customerClient.getAccounts(CUST_ID, 1000)).thenReturn(new CustomerAccountPageResponse(
				List.of(new CustomerAccountResponse(CUST_ACCT_ID, "AC-1", "n", "d", null, 1L, 1L, true))));
	}

	private void stubBsnInterSpec() {
		BsnInterSpec spec = new BsnInterSpec();
		spec.setBsnInterSpecId(5L);
		spec.setShrtCode("NEW_SALE");
		when(bsnInterSpecRepository.findByShrtCode("NEW_SALE")).thenReturn(Optional.of(spec));
	}

	private void stubBsnInterSave() {
		when(bsnInterRepository.save(any(BsnInter.class))).thenAnswer(inv -> {
			BsnInter bsnInter = inv.getArgument(0);
			bsnInter.setBsnInterId(500L);
			return bsnInter;
		});
	}

	private void stubCustOrdSave() {
		when(custOrdRepository.save(any(CustOrd.class))).thenAnswer(inv -> {
			CustOrd custOrd = inv.getArgument(0);
			if (custOrd.getCustOrdId() == null) {
				custOrd.setCustOrdId(CUST_ORD_ID);
			}
			return custOrd;
		});
	}

	private void stubCustOrdItemSaveAddsToParent() {
		// custOrd.getItems().add(item) artik CustOrdManager.createOrder icinde yapiliyor -
		// burada sadece IDENTITY id ataniyormus gibi davranmak yeterli, ikinci kez eklemeyiz.
		when(custOrdItemRepository.save(any(CustOrdItem.class))).thenAnswer(inv -> {
			CustOrdItem item = inv.getArgument(0);
			item.setCustOrdItemId(900L);
			return item;
		});
	}

	private CustOrd waitingOrder() {
		CustOrd custOrd = new CustOrd();
		custOrd.setCustOrdId(CUST_ORD_ID);
		custOrd.setCustId(CUST_ID);
		custOrd.setOrdStId(WAIT_STATUS_ID);
		return custOrd;
	}
}
