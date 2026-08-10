package com.etiya.crm.productservice.business.concretes;

import com.etiya.crm.productservice.business.abstracts.LookupCacheService;
import com.etiya.crm.productservice.business.abstracts.ProductOfferingCharUseService;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.CreateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductOfferingCharUse.UpdateProductOfferingCharUseRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductOfferingCharUse.*;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingCharUseDuplicateException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingCharUseNotFoundException;
import com.etiya.crm.productservice.business.exceptions.ProductOfferingNotFoundException;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingCharUseRepository;
import com.etiya.crm.productservice.dataAccess.abstracts.ProductOfferingRepository;
import com.etiya.crm.productservice.entities.concretes.ProductOffering;
import com.etiya.crm.productservice.entities.concretes.ProductOfferingCharUse;
import com.etiya.crm.productservice.mapper.ProductOfferingCharUseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductOfferingCharUseManager implements ProductOfferingCharUseService {

    private final ProductOfferingCharUseRepository productOfferingCharUseRepository;
    private final ProductOfferingRepository productOfferingRepository;
    private final ProductOfferingCharUseMapper productOfferingCharUseMapper;
    private final LookupCacheService lookupCacheService;

    public ProductOfferingCharUseManager(ProductOfferingCharUseRepository productOfferingCharUseRepository,
                                         ProductOfferingRepository productOfferingRepository,
                                         ProductOfferingCharUseMapper productOfferingCharUseMapper,
                                         LookupCacheService lookupCacheService) {
        this.productOfferingCharUseRepository = productOfferingCharUseRepository;
        this.productOfferingRepository = productOfferingRepository;
        this.productOfferingCharUseMapper = productOfferingCharUseMapper;
        this.lookupCacheService = lookupCacheService;
    }

    @Override
    public CreatedProductOfferingCharUseResponse create(CreateProductOfferingCharUseRequest request) {
        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId()));

        lookupCacheService.validateCharacteristicId(request.getCharacteristicId());

        if (productOfferingCharUseRepository.existsByProductOffering_ProductOfferingIdAndCharacteristicId(
                request.getProductOfferingId(), request.getCharacteristicId())) {
            throw new ProductOfferingCharUseDuplicateException(request.getProductOfferingId(), request.getCharacteristicId());
        }

        ProductOfferingCharUse entity = productOfferingCharUseMapper.toEntity(request);
        entity.setProductOffering(productOffering);

        ProductOfferingCharUse saved = productOfferingCharUseRepository.save(entity);

        CreatedProductOfferingCharUseResponse response = productOfferingCharUseMapper.toCreatedResponse(saved);
        response.setCharacteristicName(lookupCacheService.getCharacteristicName(saved.getCharacteristicId()));
        return response;
    }

    @Override
    public UpdatedProductOfferingCharUseResponse update(Long id, UpdateProductOfferingCharUseRequest request) {
        ProductOfferingCharUse entity = productOfferingCharUseRepository.findById(id)
                .orElseThrow(() -> new ProductOfferingCharUseNotFoundException(id));

        ProductOffering productOffering = productOfferingRepository.findById(request.getProductOfferingId())
                .orElseThrow(() -> new ProductOfferingNotFoundException(request.getProductOfferingId()));

        lookupCacheService.validateCharacteristicId(request.getCharacteristicId());

        if (productOfferingCharUseRepository.existsByProductOffering_ProductOfferingIdAndCharacteristicIdAndProductOfferingCharUseIdNot(
                request.getProductOfferingId(), request.getCharacteristicId(), id)) {
            throw new ProductOfferingCharUseDuplicateException(request.getProductOfferingId(), request.getCharacteristicId());
        }

        productOfferingCharUseMapper.updateEntityFromRequest(request, entity);
        entity.setProductOffering(productOffering);

        ProductOfferingCharUse saved = productOfferingCharUseRepository.save(entity);

        UpdatedProductOfferingCharUseResponse response = productOfferingCharUseMapper.toUpdatedResponse(saved);
        response.setCharacteristicName(lookupCacheService.getCharacteristicName(saved.getCharacteristicId()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public GetProductOfferingCharUseResponse getById(Long id) {
        ProductOfferingCharUse entity = productOfferingCharUseRepository.findById(id)
                .orElseThrow(() -> new ProductOfferingCharUseNotFoundException(id));

        GetProductOfferingCharUseResponse response = productOfferingCharUseMapper.toGetResponse(entity);
        response.setCharacteristicName(lookupCacheService.getCharacteristicName(entity.getCharacteristicId()));
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetAllProductOfferingCharUseResponse> getAll() {
        List<ProductOfferingCharUse> entities = productOfferingCharUseRepository.findAll();
        List<GetAllProductOfferingCharUseResponse> responses = productOfferingCharUseMapper.toGetAllResponseList(entities);

        for (int i = 0; i < entities.size(); i++) {
            responses.get(i).setCharacteristicName(
                    lookupCacheService.getCharacteristicName(entities.get(i).getCharacteristicId()));
        }
        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GetAllProductOfferingCharUseResponse> getByProductOfferingId(Long productOfferingId) {
        productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new ProductOfferingNotFoundException(productOfferingId));

        List<ProductOfferingCharUse> entities =
                productOfferingCharUseRepository.findByProductOffering_ProductOfferingId(productOfferingId);
        List<GetAllProductOfferingCharUseResponse> responses = productOfferingCharUseMapper.toGetAllResponseList(entities);

        for (int i = 0; i < entities.size(); i++) {
            responses.get(i).setCharacteristicName(
                    lookupCacheService.getCharacteristicName(entities.get(i).getCharacteristicId()));
        }
        return responses;
    }

    @Override
    public void delete(Long id) {
        ProductOfferingCharUse entity = productOfferingCharUseRepository.findById(id)
                .orElseThrow(() -> new ProductOfferingCharUseNotFoundException(id));
        entity.setActive(false);
        productOfferingCharUseRepository.save(entity);
    }
}