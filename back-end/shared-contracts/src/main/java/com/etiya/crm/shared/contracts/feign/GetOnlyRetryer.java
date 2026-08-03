package com.etiya.crm.shared.contracts.feign;

import feign.Request;
import feign.RetryableException;
import feign.Retryer;

/**
 * Feign, baglanti seviyesindeki hatalari (connect/read timeout, connection refused) HTTP
 * metodundan bagimsiz olarak RetryableException'a cevirir - varsayilan Retryer da metodu
 * ayirt etmeden retry eder. Bu, POST/PUT gibi non-idempotent cagrilarda (orn. customer-service
 * -> party-service POST /individuals) tehlikelidir: istek karsi tarafa ulasip islenmis ama
 * cevap kaybolmus olabilir, bu durumda retry mukerrer kayit acar. Bu yuzden retry sadece GET
 * (idempotent/read) cagrilarina izin verilir; digerleri ilk denemede oldugu gibi propagate edilir.
 */
public class GetOnlyRetryer implements Retryer {

	private final Retryer delegate;

	public GetOnlyRetryer() {
		this(new Retryer.Default(100, 1000, 3));
	}

	public GetOnlyRetryer(Retryer delegate) {
		this.delegate = delegate;
	}

	@Override
	public void continueOrPropagate(RetryableException e) {
		Request request = e.request();
		if (request == null || request.httpMethod() != Request.HttpMethod.GET) {
			throw e;
		}
		delegate.continueOrPropagate(e);
	}

	@Override
	public Retryer clone() {
		return new GetOnlyRetryer(delegate.clone());
	}
}
