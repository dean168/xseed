package org.learning.basic.core;

import org.learning.basic.core.domain.Account;

public interface IAccountService {

	String SERVICE_ID = "basic.accountService";

	<U extends Account> U getAccountById(String id);

	<U extends Account> U getAccountByEmail(String email);
}
