package com.etiya.crm.contactinfoservice.business.rules;

import com.etiya.crm.contactinfoservice.business.exceptions.AddressLimitExceededException;
import com.etiya.crm.contactinfoservice.business.exceptions.AddressLinkedToAccountException;
import com.etiya.crm.contactinfoservice.business.exceptions.AddressNotFoundException;
import com.etiya.crm.contactinfoservice.business.exceptions.PrimaryAddressDeletionException;
import com.etiya.crm.contactinfoservice.dataAccess.abstracts.AddressRepository;
import com.etiya.crm.contactinfoservice.entities.concretes.Address;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Pure adres kurallari: repository disina (baska bir servise) HTTP cagrisi
 * yapmaz, entity yazmaz - sadece var olan veri/olgu uzerinden dogrular ve
 * gerekirse istisna firlatir. Musteri hesabina baglilik kontrolu (external
 * call) ve primary adres yeniden atama (write) AddressServiceImpl'de yapilir,
 * sonucu/olgusu buraya parametre olarak gelir.
 */
@Component
public class AddressBusinessRules {

    private static final int MAX_ADDRESS_COUNT = 5;

    private final AddressRepository addressRepository;

    public AddressBusinessRules(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address checkIfAddressExists(Long id) {
        return addressRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new AddressNotFoundException(id));
    }

    public void checkAddressLimitNotExceeded(List<Address> existingAddresses) {
        if (existingAddresses.size() >= MAX_ADDRESS_COUNT) {
            throw new AddressLimitExceededException();
        }
    }

    public void checkIfNotPrimary(Address address) {
        if (address.isPrimary()) {
            throw new PrimaryAddressDeletionException();
        }
    }

    // customer-service'teki ayni kural (CustomerBusinessRules) ile birebir ayni mesaj key'i
    // kullanilir - iki servis ayni hatayi farkli sozcuklerle anlatmasin.
    public void ensureNotLinkedToAccount(boolean linkedToAccount) {
        if (linkedToAccount) {
            throw new AddressLinkedToAccountException();
        }
    }

}
