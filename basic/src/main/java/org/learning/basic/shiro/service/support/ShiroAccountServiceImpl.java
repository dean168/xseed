package org.learning.basic.shiro.service.support;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.learning.basic.core.domain.Account;
import org.learning.basic.core.domain.SessionContext;
import org.learning.basic.dao.IHibernateOperations;
import org.learning.basic.dao.support.SQLSupport;
import org.learning.basic.shiro.domain.ShiroAccount;
import org.learning.basic.shiro.domain.ShiroRole;
import org.learning.basic.shiro.service.IShiroAccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

@Service(IShiroAccountService.SERVICE_ID)
public class ShiroAccountServiceImpl implements IShiroAccountService {

    @Autowired
    @Qualifier(IHibernateOperations.SERVICE_ID)
    protected IHibernateOperations hibernateOperations;

    protected Map<String, ShiroRole> cached = new HashMap<>();

    @PostConstruct
    public void init() {
        // 初始化角色
        hibernateOperations.xtx((hibernateOperations) -> {
            cached.put(ADMIN_ROLE_CODE, loadRole(ADMIN_ROLE_CODE));
            cached.put(ACCOUNT_ROLE_CODE, loadRole(ACCOUNT_ROLE_CODE));
        });
    }

    protected ShiroRole loadRole(String code) {
        ShiroRole role = getRoleByCode(code);
        return role == null ? hibernateOperations.merge(createRole(code)) : role;
    }

    protected ShiroRole createRole(String code) {
        ShiroRole role = new ShiroRole();
        role.setId(code);
        role.setCode(code);
        return role;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U extends Account> U getAccountById(String id) {
        return (U) hibernateOperations.get(ShiroAccount.class, id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U extends Account> U getAccountByEmail(String email) {
        SQLSupport.SQL sql = new SQLSupport.SQL();
        sql.append("from ").append(ShiroAccount.class);
        sql.append(" where email = ?", email);
        List<?> list = hibernateOperations.find(sql.getSQL(), sql.getParams());
        return !list.isEmpty() ? (U) list.get(0) : null;
    }

    @Override
    @Transactional
    public void register(ShiroAccount account, String... roles) {
        if (StringUtils.isEmpty(account.getName())) {
            account.setName(account.getEmail());
        }
        store(account, roles);
    }

    @Override
    @Transactional
    public ShiroAccount store(ShiroAccount account, String... codes) {
        SessionContext context = SessionContext.get();
        ShiroAccount accountToUse = null;
        if (StringUtils.isNotEmpty(account.getId())) {
            accountToUse = hibernateOperations.load(ShiroAccount.class, account.getId());
            // 记住是否修改了密码
            boolean needep = !StringUtils.equals(accountToUse.getPassword(), account.getPassword());
            // 拷贝属性
            BeanUtils.copyProperties(account, accountToUse, "roles");
            // 如果密码修改了，设置新密码
            if (needep) {
                accountToUse.setPassword(new Sha256Hash(account.getPassword(), account.getEmail()).toBase64());
            }
            // 添加角色
            for (int i = 0; i < ArrayUtils.getLength(codes); i++) {
                if (!hasRole(accountToUse.getRoles(), codes[i])) {
                    ShiroRole role = cached.get(codes[i]);
                    Assert.notNull(role, "role not found by code: " + codes[i]);
                    accountToUse.getRoles().add(role);
                }
            }
        } else {
            accountToUse = account;
            if (context != null) {
                accountToUse.setCreatedBy(context.accountId());
            }
            accountToUse.setPassword(new Sha256Hash(account.getPassword(), account.getEmail()).toBase64());
            if (ArrayUtils.isNotEmpty(codes)) {
                ShiroRole[] roles = new ShiroRole[codes.length];
                for (int i = 0; i < roles.length; i++) {
                    roles[i] = cached.get(codes[i]);
                    Assert.notNull(roles[i], "role not found by code: " + codes[i]);
                }
                accountToUse.getRoles().addAll(Arrays.asList(roles));
            }
            accountToUse.setCreatedAt(new Date());
        }
        if (context != null) {
            accountToUse.setUpdatedBy(context.accountId());
        }
        accountToUse.setUpdatedAt(new Date());
        return hibernateOperations.merge(accountToUse);
    }

    private boolean hasRole(Set<ShiroRole> roles, String code) {
        for (ShiroRole role : roles) {
            if (StringUtils.equals(role.getCode(), code)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional
    public void delete(String id) {
        ShiroAccount user = hibernateOperations.get(ShiroAccount.class, id);
        if (user != null) {
            hibernateOperations.delete(user);
        }
    }

    @Override
    public ShiroRole getRoleByCode(String code) {
        SQLSupport.SQL sql = new SQLSupport.SQL();
        sql.append("from ").append(ShiroRole.class);
        sql.append(" where code = ?", code);
        List<?> list = hibernateOperations.find(sql.getSQL(), sql.getParams());
        return !list.isEmpty() ? (ShiroRole) list.get(0) : null;
    }
}
