package com.etiya.crm.orderservice.business.rules;

import org.springframework.stereotype.Component;
import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.exceptions.AccountNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressSelectionInvalidException;
import com.etiya.crm.orderservice.business.exceptions.ConflictingBasketItemException;
import com.etiya.crm.orderservice.business.exceptions.DuplicateBasketItemException;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
import com.etiya.crm.orderservice.clients.responses.ProductOfferingRelationResponse;
import com.etiya.crm.orderservice.entities.concretes.CustOrdItem;
import com.etiya.crm.shared.contracts.address.AddressResponse;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class BasketValidationRules {

        public void ensureAddressProvided(Long addressId, AddressInfoRequest newAddress) {
            boolean hasExisting = addressId != null;
            boolean hasNew = newAddress != null;

            if (hasExisting == hasNew) {
                    throw new AddressSelectionInvalidException();
            }
        }

        public void ensureAccountBelongsToCustomer(Long custAcctId, List<CustomerAccountResponse> accounts) {
                boolean belongs = accounts.stream().anyMatch(acc -> acc.custAcctId().equals(custAcctId));
                if (!belongs) {
                        throw new AccountNotBelongToCustomerException(custAcctId);
                }
        }

        // FR-017/BR-02 (varsayim): ayni prodOfrId+cmpgId kombinasyonu sepette birden fazla
        // kez olamaz. "Already Active" (BR-03, bkz. CustOrdManager.ensureOfferNotAlreadyActive)
        // ve hizmet cakismasi (BR-04, bkz. ensureNoConflictingItems/ensureItemNotConflicting
        // asagida) artik ayri yerlerde kontrol ediliyor.
        public void ensureNoDuplicateItems(List<BasketItemRequest> items) {
                Set<String> seen = new HashSet<>();
                for (BasketItemRequest item : items) {
                        String key = item.prodOfrId() + ":" + item.cmpgId();
                        if (!seen.add(key)) {
                                throw new DuplicateBasketItemException(item.prodOfrId());
                        }
                }
        }

        // addItem: yeni item zaten WAIT durumundaki siparise eklenmis item'lar arasinda mi kontrol eder.
        public void ensureItemNotAlreadyInBasket(BasketItemRequest newItem, List<CustOrdItem> existingItems) {
                boolean duplicate = existingItems.stream()
                        .anyMatch(i -> i.getProdOfrId().equals(newItem.prodOfrId())
                                && Objects.equals(i.getCmpgId(), newItem.cmpgId()));
                if (duplicate) {
                        throw new DuplicateBasketItemException(newItem.prodOfrId());
                }
        }

        // FR-014 ACC-012/BR-04: validateBasket/createOrder - gonderilen sepetin kendi icinde
        // birbiriyle cakisan (EXCL) iki teklif var mi kontrol eder.
        public void ensureNoConflictingItems(List<BasketItemRequest> items, List<ProductOfferingRelationResponse> relations) {
                for (int i = 0; i < items.size(); i++) {
                        for (int j = i + 1; j < items.size(); j++) {
                                Long prodOfrId1 = items.get(i).prodOfrId();
                                Long prodOfrId2 = items.get(j).prodOfrId();
                                if (isExclusive(prodOfrId1, prodOfrId2, relations)) {
                                        throw new ConflictingBasketItemException(prodOfrId1, prodOfrId2);
                                }
                        }
                }
        }

        // addItem: yeni item, siparise zaten eklenmis item'lardan biriyle cakisiyor mu kontrol eder.
        public void ensureItemNotConflicting(BasketItemRequest newItem, List<CustOrdItem> existingItems,
                        List<ProductOfferingRelationResponse> relations) {
                for (CustOrdItem existing : existingItems) {
                        if (isExclusive(newItem.prodOfrId(), existing.getProdOfrId(), relations)) {
                                throw new ConflictingBasketItemException(newItem.prodOfrId(), existing.getProdOfrId());
                        }
                }
        }

        private boolean isExclusive(Long prodOfrId1, Long prodOfrId2, List<ProductOfferingRelationResponse> relations) {
                return relations.stream().anyMatch(r -> Boolean.TRUE.equals(r.exclusive()) && Boolean.TRUE.equals(r.active())
                                && ((prodOfrId1.equals(r.productOfferingId1()) && prodOfrId2.equals(r.productOfferingId2()))
                                        || (prodOfrId1.equals(r.productOfferingId2()) && prodOfrId2.equals(r.productOfferingId1()))));
        }

        // saveConfiguration: var olan bir adres secildiginde, o adresin gercekten bu musteriye
        // (dogrudan CUST tipinde) ya da musterinin hesaplarindan birine ait olup olmadigini
        // dogrular - aksi halde baskasinin adresi siparise baglanabilir.
        public void ensureAddressBelongsToCustomer(AddressResponse address, Long custId, Long custDataTypeId,
                        List<CustomerAccountResponse> accounts) {
                boolean ownedByCustomer = custDataTypeId.equals(address.dataTypeId()) && custId.equals(address.rowId());
                boolean ownedByAnyAccount = accounts.stream().anyMatch(acc -> address.id().equals(acc.addressId()));
                if (!ownedByCustomer && !ownedByAnyAccount) {
                        throw new AddressNotBelongToCustomerException(address.id());
                }
        }
}
