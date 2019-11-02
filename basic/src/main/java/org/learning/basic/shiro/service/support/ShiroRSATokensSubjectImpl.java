package org.learning.basic.shiro.service.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.subject.Subject;
import org.learning.basic.core.IAccountService;
import org.learning.basic.shiro.domain.ShiroAccount;
import org.learning.basic.shiro.domain.ShiroStatus;
import org.learning.basic.shiro.service.IShiroSubject;
import org.learning.basic.utils.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service(IShiroSubject.SERVICE_ID)
public class ShiroRSATokensSubjectImpl implements IShiroSubject {

    private static final Logger logger = LoggerFactory.getLogger(ShiroRSATokensSubjectImpl.class);

    @Autowired
    @Qualifier(IAccountService.SERVICE_ID)
    private IAccountService accountService;
    @Value("${basic.shiro.tokens.secretKey}")
    private String secretKey;
    @Value("${basic.shiro.tokens.headerName}")
    private String headerName;

    @Override
    public Subject subject(HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        String token = request.getHeader(headerName);
        if (StringUtils.isNotEmpty(token)) {
            if (subject.getPrincipal() == null) {
                String accountId = RSAUtils.decrypt(secretKey, token);
                ShiroAccount account = accountService.get(accountId);
                if (account.getStatus() == null || ShiroStatus.ACTIVE.equals(account.getStatus())) {
                    UsernameHashSaltedPasswordToken tokenToUse = new UsernameHashSaltedPasswordToken(account.getId(), account.getPassword(), false);
                    try {
                        subject.login(tokenToUse);
                        if (logger.isDebugEnabled()) {
                            logger.debug("login#" + account.getId() + "(" + account.getId() + ")");
                        }
                    } catch (AuthenticationException e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(e.getMessage());
                        }
                    }
                } else if (logger.isDebugEnabled()) {
                    logger.debug("invalid account#" + account.getId() + ", status must be active.");
                }
            }
        }
        return subject;
    }
}
