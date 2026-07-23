package com.etiya.crm.orderservice.business.rules;

import org.springframework.stereotype.Component;
import com.etiya.crm.orderservice.business.dtos.requests.SubmitOrderRequest;
import com.etiya.crm.orderservice.business.exceptions.BusinessException;
import com.etiya.crm.orderservice.clients.responses.CustomerAccountResponse;

import java.util.List;

@Component
public class BasketValidationRules {

        public void ensureAddressProvided(SubmitOrderRequest request) {
            boolean hasExisting = request.addressId() != null;
            boolean hasNew = request.newAddress() != null;

            if (hasExisting == hasNew) {
                    throw new BusinessException("either addressId or newAddress must be full");
            }
        }

        public void ensureAccountBelongsToCustomer(Long custAcctId, List<CustomerAccountResponse> accounts) {
                boolean belongs = accounts.stream().anyMatch(acc -> acc.custAcctId().equals(custAcctId));
                if (!belongs) {
                        throw new BusinessException("account doesn't belong to this customer: " + custAcctId);
                }
        }
}
