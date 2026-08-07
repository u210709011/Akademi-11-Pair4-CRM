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
