package com.etiya.crm.customerservice.business.exceptions;

import com.etiya.crm.customerservice.constants.MessageKeys;

/**
 * B-06: acct_no'nun UNIQUE oldugu halde iki farkli akisin (onboarding varsayilan hesabi custId'den,
 * billing account custAcctId'den) ayni degeri uretmesiyle ortaya cikan gercek bir cakisma yasandi.
 * Kok neden duzeltildi (ikisi de artik custAcctId kullanir, ayni tablonun PK'si oldugu icin
 * kendi icinde asla cakismaz) - bu exception artik sadece savunma amacli: existsByAccountNo hala
 * true donerse (beklenmedik veri bozulmasi), sessizce UNIQUE constraint ihlaline duşup ham 500
 * almak yerine acikca ve okunakli sekilde patlar.
 */
public class AccountNumberCollisionException extends BusinessException {

	public AccountNumberCollisionException(String accountNo) {
		super(MessageKeys.ACCOUNT_NUMBER_COLLISION, accountNo);
	}
}
