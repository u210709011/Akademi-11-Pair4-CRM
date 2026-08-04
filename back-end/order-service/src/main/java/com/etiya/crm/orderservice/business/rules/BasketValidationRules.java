package com.etiya.crm.orderservice.business.rules;

import org.springframework.stereotype.Component;
import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.exceptions.AccountNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressSelectionInvalidException;
import com.etiya.crm.orderservice.business.exceptions.DuplicateBasketItemException;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;
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
        // kez olamaz. "Already Active" (BR-03) ve hizmet cakismasi (BR-04) product-service
        // olmadan kontrol edilemiyor - bkz. FR-014 ACC-007/ACC-008.
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
