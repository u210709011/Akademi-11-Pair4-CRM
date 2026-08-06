package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.Product.CreateProductRequest;
import com.etiya.crm.productservice.business.dtos.requests.Product.UpdateProductRequest;
import com.etiya.crm.productservice.business.dtos.responses.Product.CreatedProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetAllProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.GetProductResponse;
import com.etiya.crm.productservice.business.dtos.responses.Product.UpdatedProductResponse;
import com.etiya.crm.productservice.entities.concretes.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    //create
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "parentProduct", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "productSpec", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    Product toEntity(CreateProductRequest request);

    @Mapping(target = "parentProductId", source = "parentProduct.productId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    @Mapping(target = "campaignId", source = "campaign.campaignId")
    CreatedProductResponse toCreatedResponse(Product product);

    //update
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "parentProduct", ignore = true)
    @Mapping(target = "productOffering", ignore = true)
    @Mapping(target = "productSpec", ignore = true)
    @Mapping(target = "campaign", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductRequest request, @MappingTarget Product product);

    @Mapping(target = "parentProductId", source = "parentProduct.productId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    @Mapping(target = "campaignId", source = "campaign.campaignId")
    UpdatedProductResponse toUpdatedResponse(Product product);

    // GET (tekil)
    @Mapping(target = "parentProductId", source = "parentProduct.productId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    @Mapping(target = "campaignId", source = "campaign.campaignId")
    GetProductResponse toGetResponse(Product product);

    // GET ALL (tekil — nested mapping olduğu için elle yazıyoruz)
    @Mapping(target = "parentProductId", source = "parentProduct.productId")
    @Mapping(target = "productOfferingId", source = "productOffering.productOfferingId")
    @Mapping(target = "productSpecId", source = "productSpec.productSpecId")
    @Mapping(target = "campaignId", source = "campaign.campaignId")
    GetAllProductResponse toGetAllResponse(Product product);

    // GET ALL (liste — tekil metodu kullanır)
    List<GetAllProductResponse> toGetAllResponseList(List<Product> products);

}
