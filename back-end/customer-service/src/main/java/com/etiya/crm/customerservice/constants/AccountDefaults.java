package com.etiya.crm.customerservice.constants;

public final class AccountDefaults {

	/** CUST_ACCT.ACCT_NO servis tarafinda uretilir - PREFIX metni YOK, sadece bu uzunlukta sifirla soldan doldurulmus bir sayi. */
	public static final int ACCOUNT_NO_LENGTH = 6;

	/** id'yi (custId ya da custAcctId) ACCOUNT_NO_LENGTH haneye sifirla soldan doldurur, ornn. 42 -> "000042". */
	public static String formatAccountNo(long id) {
		return String.format("%0" + ACCOUNT_NO_LENGTH + "d", id);
	}

	private AccountDefaults() {
	}
}
