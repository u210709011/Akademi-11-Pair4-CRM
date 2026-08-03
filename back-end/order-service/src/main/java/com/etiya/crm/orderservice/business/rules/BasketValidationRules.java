package com.etiya.crm.orderservice.business.rules;

import org.springframework.stereotype.Component;
import com.etiya.crm.orderservice.business.dtos.requests.AddressInfoRequest;
import com.etiya.crm.orderservice.business.dtos.requests.BasketItemRequest;
import com.etiya.crm.orderservice.business.exceptions.AccountNotBelongToCustomerException;
import com.etiya.crm.orderservice.business.exceptions.AddressSelectionInvalidException;
import com.etiya.crm.orderservice.business.exceptions.DuplicateBasketItemException;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;

import java.util.HashSet;
import java.util.List;
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
}
