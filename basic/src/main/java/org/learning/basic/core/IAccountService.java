package org.learning.basic.core;

import org.learning.basic.core.domain.Account;
import org.springframework.core.Ordered;

public interface IAccountService extends Ordered {

    String SERVICE_ID = "basic.accountService";

    <A extends Account> A get(String id);
}
