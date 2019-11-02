package org.learning.basic.core;

import org.learning.basic.core.domain.Account;

public interface IAccountService {

    String SERVICE_ID = "basic.accountService";

    <A extends Account> A get(String id);
}
