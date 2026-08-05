package com.etiya.crm.productservice.mapper;

import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.CreateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.requests.ProductCatalog.UpdateProductCatalogRequest;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.CreatedProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetAllProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.GetProductCatalogResponse;
import com.etiya.crm.productservice.business.dtos.responses.ProductCatalog.UpdatedProductCatalogResponse;
import com.etiya.crm.productservice.entities.concretes.ProductCatalog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductCatalogMapper {

    // ---------- CREATE ----------

    /** Gelen istekten yeni entity uretir. Id ve audit alanlari disarida birakilir. */
    @Mapping(target = "productCatalogId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    ProductCatalog toEntity(CreateProductCatalogRequest request);

    CreatedProductCatalogResponse toCreatedResponse(ProductCatalog productCatalog);

    // ---------- UPDATE ----------

    /**
     * Var olan entity'nin uzerine yazar (yeni nesne uretmez).
     * @MappingTarget sayesinde productSpecId ve audit alanlari korunur.
     */
    @Mapping(target = "productCatalogId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "cdate", ignore = true)
    @Mapping(target = "cuser", ignore = true)
    @Mapping(target = "udate", ignore = true)
    @Mapping(target = "uuser", ignore = true)
    void updateEntityFromRequest(UpdateProductCatalogRequest request, @MappingTarget ProductCatalog productCatalog);

    UpdatedProductCatalogResponse toUpdatedResponse(ProductCatalog productCatalog);

    // ---------- GET ----------

    GetProductCatalogResponse toGetResponse(ProductCatalog productCatalog);

    /** Liste donusumu: MapStruct tekil metodu bulup her eleman icin calistirir. */
    List<GetAllProductCatalogResponse> toGetAllResponseList(List<ProductCatalog> productCatalogs);
}