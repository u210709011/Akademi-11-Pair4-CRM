// GET /api/v1/general-types?entCodeName=... - lookup-service GNL_TP kaydi. Numerik id'ler asla
// hardcode edilmez (bkz. CLAUDE.md), her zaman bu grup adiyla dinamik cekilir.
export interface GnlType {
  gnlTpId: number;
  name: string;
  shrtCode: string;
  active: boolean;
}

export const LOOKUP_GROUPS = {
  CITY: 'CITY',
  GENDER: 'GENDER',
  ACCOUNT_TYPE: 'ACCOUNT_TYPE'
} as const;

// GET /api/v1/characteristics - karakteristik tanimi (orn. "Baglanti Hizi").
export interface Characteristic {
  charId: number;
  name: string;
  descr: string;
  shrtCode: string;
  active: boolean;
}

// GET /api/v1/characteristic-values - bir karakteristigin alabilecegi degerlerden biri
// (orn. charId=1/CONN_SPEED icin val="100 Mbps").
export interface CharacteristicValue {
  charValId: number;
  charId: number;
  dflt: boolean;
  val: string;
  shrtCode: string;
  active: boolean;
}
