import { Campaign, CampaignOffering, ProductCatalog, ProductCatalogOffering, ProductOffering } from '../../../../core/product';

// GECICI MOCK VERI - backend product-service hazir olunca bu dosya tamamen silinecek.
// Mockup ekran goruntulerindeki urun/kampanya adlarina birebir uyacak sekilde hazirlandi.

export const MOCK_CATALOGS: ProductCatalog[] = [
  { productCatalogId: 1, name: 'Mobile', descr: 'Mobile products', statusId: 1, shortCode: 'MOB' },
  { productCatalogId: 2, name: 'Internet', descr: 'Internet products', statusId: 1, shortCode: 'INT' },
  { productCatalogId: 3, name: 'TV', statusId: 1, descr: 'TV products', shortCode: 'TV' }
];

export const MOCK_OFFERINGS: ProductOffering[] = [
  { productOfferingId: 70010, productSpecId: 1, name: 'Mobile Postpaid 30GB', descr: 'Mobile postpaid line with 30GB data.', parentOfferingId: null, statusId: 1, totalPrice: 249.9 },
  { productOfferingId: 70021, productSpecId: 2, name: 'Home Fiber 200Mbps', descr: 'Fiber broadband with free router. 24-month commitment.', parentOfferingId: null, statusId: 1, totalPrice: 399.9 },
  { productOfferingId: 70035, productSpecId: 1, name: 'Mobile Prepaid 5GB', descr: 'Pay-as-you-go data pack. 30-day validity.', parentOfferingId: null, statusId: 1, totalPrice: 89.9 },
  { productOfferingId: 70048, productSpecId: 1, name: 'International Roaming Pack', descr: '2GB data + 60 min for EU travel. 7-day validity.', parentOfferingId: null, statusId: 1, totalPrice: 149.9 },
  { productOfferingId: 70055, productSpecId: 1, name: 'Mobile Postpaid 50GB', descr: 'Unlimited calls + 50GB data. 12-month commitment.', parentOfferingId: null, statusId: 1, totalPrice: 329.9 },
  { productOfferingId: 70062, productSpecId: 1, name: 'Mobile Postpaid 20GB', descr: 'Unlimited calls + 20GB data. 12-month commitment.', parentOfferingId: null, statusId: 1, totalPrice: 199.9 },
  { productOfferingId: 70146, productSpecId: 2, name: 'Home Fiber 100Mbps', descr: 'Fiber broadband with free router. 24-month commitment.', parentOfferingId: null, statusId: 1, totalPrice: 299.9 },
  { productOfferingId: 70153, productSpecId: 2, name: 'Home Fiber 500Mbps', descr: 'High-speed fiber broadband with free router. 24-month commitment.', parentOfferingId: null, statusId: 1, totalPrice: 549.9 },
  { productOfferingId: 70160, productSpecId: 2, name: 'Home Fiber 1Gbps', descr: 'Gigabit fiber broadband with free router. 24-month commitment.', parentOfferingId: null, statusId: 1, totalPrice: 799.9 },
  { productOfferingId: 70300, productSpecId: 3, name: 'Wi-Fi Router Purchase', descr: 'One-time Wi-Fi router purchase.', parentOfferingId: null, statusId: 1, totalPrice: 2.49 },
  { productOfferingId: 70500, productSpecId: 4, name: 'TV Basic', descr: 'Basic TV package, 80+ channels.', parentOfferingId: null, statusId: 1, totalPrice: 99.9 },
  { productOfferingId: 70510, productSpecId: 4, name: 'TV Premium', descr: 'Premium TV package, 150+ channels + sports.', parentOfferingId: null, statusId: 1, totalPrice: 149.9 },
  { productOfferingId: 70520, productSpecId: 1, name: 'Business Mobile Line', descr: 'Unlimited calls + 100GB data for business use.', parentOfferingId: null, statusId: 1, totalPrice: 399.9 },
  { productOfferingId: 70530, productSpecId: 5, name: 'Smart Plug', descr: 'Wi-Fi enabled smart plug.', parentOfferingId: null, statusId: 1, totalPrice: 29.9 },
  { productOfferingId: 70540, productSpecId: 5, name: 'Smart Camera', descr: 'Wi-Fi enabled indoor security camera.', parentOfferingId: null, statusId: 1, totalPrice: 89.9 }
];

export const MOCK_CATALOG_OFFERINGS: ProductCatalogOffering[] = [
  { productCatalogOfferingId: 1, productCatalogId: 1, productOfferingId: 70010, statusId: 1 },
  { productCatalogOfferingId: 2, productCatalogId: 2, productOfferingId: 70021, statusId: 1 },
  { productCatalogOfferingId: 3, productCatalogId: 1, productOfferingId: 70035, statusId: 1 },
  { productCatalogOfferingId: 4, productCatalogId: 1, productOfferingId: 70048, statusId: 1 },
  { productCatalogOfferingId: 5, productCatalogId: 1, productOfferingId: 70055, statusId: 1 },
  { productCatalogOfferingId: 6, productCatalogId: 1, productOfferingId: 70062, statusId: 1 },
  { productCatalogOfferingId: 7, productCatalogId: 2, productOfferingId: 70146, statusId: 1 },
  { productCatalogOfferingId: 8, productCatalogId: 2, productOfferingId: 70153, statusId: 1 },
  { productCatalogOfferingId: 9, productCatalogId: 2, productOfferingId: 70160, statusId: 1 },
  { productCatalogOfferingId: 10, productCatalogId: 2, productOfferingId: 70300, statusId: 1 },
  { productCatalogOfferingId: 11, productCatalogId: 3, productOfferingId: 70500, statusId: 1 },
  { productCatalogOfferingId: 12, productCatalogId: 3, productOfferingId: 70510, statusId: 1 }
];

export const MOCK_CAMPAIGNS: Campaign[] = [
  { campaignId: 9010, name: 'Back to School Bundle', descr: 'Back to school campaign bundle.', campaignCode: 'CMP-9010', activityEndDate: null, statusId: 1, penalty: false },
  { campaignId: 2077, name: 'Fiber Loyalty', descr: 'Fiber loyalty campaign.', campaignCode: 'CMP-2077', activityEndDate: null, statusId: 1, penalty: false },
  { campaignId: 9022, name: 'Home Starter Bundle', descr: 'Home fiber + mobile data starter bundle.', campaignCode: 'CMP-9022', activityEndDate: null, statusId: 1, penalty: false },
  { campaignId: 9030, name: 'Fiber + Mobile + TV Combo', descr: 'Fiber, mobile line and TV basic combo.', campaignCode: 'CMP-9030', activityEndDate: null, statusId: 1, penalty: false },
  { campaignId: 9100, name: 'Work From Home Bundle', descr: 'High-speed fiber + router + business mobile line.', campaignCode: 'CMP-9100', activityEndDate: null, statusId: 1, penalty: false },
  { campaignId: 9130, name: 'Small Business Fiber Bundle', descr: 'Gigabit fiber + business mobile line.', campaignCode: 'CMP-9130', activityEndDate: null, statusId: 1, penalty: false },
  { campaignId: 9170, name: 'Smart Home Bundle', descr: 'Fiber + Wi-Fi router + smart home devices.', campaignCode: 'CMP-9170', activityEndDate: null, statusId: 1, penalty: false },
  { campaignId: 9190, name: 'Loyalty Reward Bundle', descr: 'Fiber + TV premium loyalty reward.', campaignCode: 'CMP-9190', activityEndDate: null, statusId: 1, penalty: false }
];

export const MOCK_CAMPAIGN_OFFERINGS: CampaignOffering[] = [
  { campaignOfferingId: 1, campaignId: 9010, productOfferingId: 70010, productOfferingName: 'Mobile Postpaid 30GB', priority: 1, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 2, campaignId: 2077, productOfferingId: 70146, productOfferingName: 'Home Fiber 100Mbps', priority: 1, startDate: null, endDate: null, active: true },

  { campaignOfferingId: 3, campaignId: 9022, productOfferingId: 70021, productOfferingName: 'Home Fiber 200Mbps', priority: 1, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 4, campaignId: 9022, productOfferingId: 70035, productOfferingName: 'Mobile Prepaid 5GB', priority: 2, startDate: null, endDate: null, active: true },

  { campaignOfferingId: 5, campaignId: 9030, productOfferingId: 70146, productOfferingName: 'Home Fiber 100Mbps', priority: 1, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 6, campaignId: 9030, productOfferingId: 70010, productOfferingName: 'Mobile Postpaid 30GB', priority: 2, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 7, campaignId: 9030, productOfferingId: 70500, productOfferingName: 'TV Basic', priority: 3, startDate: null, endDate: null, active: true },

  { campaignOfferingId: 8, campaignId: 9100, productOfferingId: 70153, productOfferingName: 'Home Fiber 500Mbps', priority: 1, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 9, campaignId: 9100, productOfferingId: 70300, productOfferingName: 'Wi-Fi Router Purchase', priority: 2, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 10, campaignId: 9100, productOfferingId: 70520, productOfferingName: 'Business Mobile Line', priority: 3, startDate: null, endDate: null, active: true },

  { campaignOfferingId: 11, campaignId: 9130, productOfferingId: 70160, productOfferingName: 'Home Fiber 1Gbps', priority: 1, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 12, campaignId: 9130, productOfferingId: 70520, productOfferingName: 'Business Mobile Line', priority: 2, startDate: null, endDate: null, active: true },

  { campaignOfferingId: 13, campaignId: 9170, productOfferingId: 70021, productOfferingName: 'Home Fiber 200Mbps', priority: 1, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 14, campaignId: 9170, productOfferingId: 70300, productOfferingName: 'Wi-Fi Router Purchase', priority: 2, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 15, campaignId: 9170, productOfferingId: 70530, productOfferingName: 'Smart Plug', priority: 3, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 16, campaignId: 9170, productOfferingId: 70540, productOfferingName: 'Smart Camera', priority: 4, startDate: null, endDate: null, active: true },

  { campaignOfferingId: 17, campaignId: 9190, productOfferingId: 70146, productOfferingName: 'Home Fiber 100Mbps', priority: 1, startDate: null, endDate: null, active: true },
  { campaignOfferingId: 18, campaignId: 9190, productOfferingId: 70510, productOfferingName: 'TV Premium', priority: 2, startDate: null, endDate: null, active: true }
];

// productOfferingId -> zorunlu olarak sepete otomatik eklenecek productOfferingId listesi.
// Backend'de bu ilişkiyi veren bir endpoint yok (bkz. ProductRelation notlari), sadece mock modda.
export const MOCK_REQUIRED_PRODUCTS: Record<number, number[]> = {
  70021: [70300],
  70146: [70300],
  70153: [70300],
  70160: [70300]
};

// productOfferingId -> tek seferlik mi (aksi halde aylik/recurring sayilir). Backend'de bu ayrimi
// veren bir alan yok (ProductOffering'de sadece totalPrice var), sadece mock modda kullanilir.
export const MOCK_ONE_TIME_OFFERING_IDS: ReadonlySet<number> = new Set([70300]);

// Review & Submit'teki Price Summary icin sabit degerler - backend'de vergi/aktivasyon ucreti
// hesaplayan bir endpoint yok, sadece mock modda gosterim amacli.
export const MOCK_TAX_RATE = 0.2;
export const MOCK_ACTIVATION_FEE = 50;

export interface MockCharacteristicOption {
  value: string;
  label: string;
}

export interface MockCharacteristicField {
  key: string;
  label: string;
  type: 'select' | 'text';
  required: boolean;
  options?: MockCharacteristicOption[];
  placeholder?: string;
}

// productOfferingId -> Configuration ekraninda gosterilecek karakteristik alanlari.
// Backend'de "bu offering icin hangi karakteristikler gosterilecek" semasini veren bir endpoint
// yok (bkz. ProductSpecServiceSpec/ProductCharacteristicValue notlari), sadece mock modda.
const FIBER_CHARACTERISTIC_FIELDS: MockCharacteristicField[] = [
  {
    key: 'connectionType',
    label: 'Connection Type',
    type: 'select',
    required: true,
    options: [
      { value: 'fiber', label: 'Fiber' },
      { value: 'adsl', label: 'ADSL' },
      { value: 'vdsl', label: 'VDSL' }
    ]
  },
  { key: 'modemModel', label: 'Modem Model', type: 'text', required: true },
  {
    key: 'installationPreference',
    label: 'Installation Preference',
    type: 'select',
    required: true,
    options: [
      { value: 'standard', label: 'Standard' },
      { value: 'express', label: 'Express' }
    ]
  },
  {
    key: 'bandwidth',
    label: 'Bandwidth',
    type: 'select',
    required: true,
    options: [
      { value: '50', label: '50 Mbps' },
      { value: '100', label: '100 Mbps' },
      { value: '200', label: '200 Mbps' },
      { value: '500', label: '500 Mbps' },
      { value: '1000', label: '1 Gbps' }
    ]
  },
  { key: 'staticIp', label: 'Static IP', type: 'text', required: false, placeholder: 'optional' }
];

const TV_CHARACTERISTIC_FIELDS: MockCharacteristicField[] = [
  {
    key: 'package',
    label: 'Package',
    type: 'select',
    required: true,
    options: [
      { value: 'basic', label: 'Basic' },
      { value: 'premium', label: 'Premium' },
      { value: 'sports', label: 'Sports' }
    ]
  },
  {
    key: 'decoderType',
    label: 'Decoder Type',
    type: 'select',
    required: true,
    options: [
      { value: 'hd', label: 'HD Decoder' },
      { value: '4k', label: '4K Decoder' }
    ]
  },
  { key: 'smartCardNumber', label: 'Smart Card Number', type: 'text', required: true },
  {
    key: 'hdOption',
    label: 'HD Option',
    type: 'select',
    required: true,
    options: [
      { value: 'sd', label: 'SD' },
      { value: 'hd', label: 'HD' }
    ]
  }
];

const MOBILE_CHARACTERISTIC_FIELDS: MockCharacteristicField[] = [
  {
    key: 'simType',
    label: 'SIM Type',
    type: 'select',
    required: true,
    options: [
      { value: 'physical', label: 'Physical SIM' },
      { value: 'esim', label: 'eSIM' }
    ]
  },
  {
    key: 'numberType',
    label: 'Number Type',
    type: 'select',
    required: true,
    options: [
      { value: 'new', label: 'New Number' },
      { value: 'portIn', label: 'Number Transfer (Port-In)' }
    ]
  },
  { key: 'portInNumber', label: 'Port-In Number', type: 'text', required: false },
  { key: 'iccid', label: 'ICCID', type: 'text', required: true },
  { key: 'imei', label: 'IMEI (optional)', type: 'text', required: false }
];

export const MOCK_CHARACTERISTICS_BY_OFFERING: Record<number, MockCharacteristicField[]> = {
  70021: FIBER_CHARACTERISTIC_FIELDS,
  70146: FIBER_CHARACTERISTIC_FIELDS,
  70153: FIBER_CHARACTERISTIC_FIELDS,
  70160: FIBER_CHARACTERISTIC_FIELDS,
  70500: TV_CHARACTERISTIC_FIELDS,
  70510: TV_CHARACTERISTIC_FIELDS,
  70010: MOBILE_CHARACTERISTIC_FIELDS,
  70035: MOBILE_CHARACTERISTIC_FIELDS,
  70048: MOBILE_CHARACTERISTIC_FIELDS,
  70055: MOBILE_CHARACTERISTIC_FIELDS,
  70062: MOBILE_CHARACTERISTIC_FIELDS,
  70520: MOBILE_CHARACTERISTIC_FIELDS
};
