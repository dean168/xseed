package org.learning.basic.shiro.service.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.learning.basic.shiro.domain.ShiroAccount;
import org.learning.basic.shiro.service.IShiroAccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

@Service(IShiroAccountService.SERVICE_ID)
public class ShiroAccountServiceImpl extends ShiroBasicServiceImpl implements IShiroAccountService {

    @Override
    public ShiroAccount get(String id) {
        return hibernateOperations.get(ShiroAccount.class, id);
    }

    @Override
    @Transactional
    public <A extends ShiroAccount> A create(A account) {
        return create(account, accountToUse -> {
            accountToUse.setPassword(new Sha256Hash(accountToUse.getPassword(), accountToUse.getId()).toBase64());
            if (StringUtils.isEmpty(accountToUse.getName())) {
                accountToUse.setName(accountToUse.getId());
            }
            return accountToUse;
        });
    }

    @Override
    @Transactional
    public <A extends ShiroAccount> A update(A account) {
        return update(account, accountToUse -> {
            accountToUse = (A) hibernateOperations.load(ClassUtils.getUserClass(account), account.getId());
            BeanUtils.copyProperties(account, accountToUse, "password", "createdAt", "createdBy");
            if (StringUtils.isNotEmpty(account.getPassword())) {
                accountToUse.setPassword(new Sha256Hash(account.getPassword(), account.getId()).toBase64());
            }
            return accountToUse;
        });
    }

    @Override
    @Transactional
    public void delete(String id) {
        ShiroAccount account = hibernateOperations.get(ShiroAccount.class, id);
        if (account != null) {
            hibernateOperations.delete(account);
        }
    }
}
