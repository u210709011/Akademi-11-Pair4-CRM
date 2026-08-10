import { PRODUCT_DETAIL_MOCK_MODE } from './product-detail-mock.config';

// GECICI MOCK VERI - backend product-service Product Spec ID / Service Start Date / Product
// Characteristics alanlarini donene kadar. prodId'ye gore eslenir (new-sale/mock ile ayni
// productOfferingId'ler); eslesme yoksa veya PRODUCT_DETAIL_MOCK_MODE kapaliysa placeholder doner.

export interface ProductCharacteristic {
  label: string;
  value: string;
}

export interface ProductDetailMock {
  productSpecId: string;
  serviceStartDate: string;
  characteristics: ProductCharacteristic[];
}

const PLACEHOLDER_PRODUCT_DETAIL: ProductDetailMock = {
  productSpecId: '—',
  serviceStartDate: '—',
  characteristics: []
};

const MOCK_PRODUCT_DETAILS: Record<string, ProductDetailMock> = {
  '70010': {
    productSpecId: 'SPEC-5521',
    serviceStartDate: '12/01/2026',
    characteristics: [
      { label: 'Data Quota', value: '20 GB' },
      { label: 'Voice Minutes', value: '1000 min' },
      { label: 'Contract Term', value: '12 months' }
    ]
  },
  '70146': {
    productSpecId: 'SPEC-5533',
    serviceStartDate: '03/02/2026',
    characteristics: [
      { label: 'Download Speed', value: '100 Mbps' },
      { label: 'Upload Speed', value: '20 Mbps' },
      { label: 'Contract Term', value: '24 months' }
    ]
  }
};

export function getMockProductDetail(prodId: string): ProductDetailMock {
  if (!PRODUCT_DETAIL_MOCK_MODE) {
    return PLACEHOLDER_PRODUCT_DETAIL;
  }
  return MOCK_PRODUCT_DETAILS[prodId] ?? PLACEHOLDER_PRODUCT_DETAIL;
}
