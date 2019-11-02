package org.learning.basic.dao.support;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;
import org.learning.basic.dao.IJdbcPasswordDecoder;
import org.learning.basic.dao.IJdbcPasswordEncoder;
import org.learning.basic.utils.ByteUtils;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static org.learning.basic.core.Asserts.Patterns.hasText;
import static org.learning.basic.core.Asserts.Patterns.isTrue;

public class DefaultJdbcPasswordDecoderImpl implements IJdbcPasswordEncoder, IJdbcPasswordDecoder {

    private AesCipherService cipherService;
    private byte[] key;

    @PostConstruct
    public void init() {
        isTrue(key != null && key.length > 0, "key must not be null.");
        cipherService = new AesCipherService();
        cipherService.setModeName("ECB");
    }

    @Override
    public String encode(String password) {
        if (StringUtils.isNotEmpty(password)) {
            password = ByteUtils.strings(Base64.encode(cipherService.encrypt(ByteUtils.bytes(password), key).getBytes()));
        }
        return password;
    }

    @Override
    public String decode(String password) {
        if (StringUtils.isNotEmpty(password)) {
            password = ByteUtils.strings(cipherService.decrypt(Base64.decode(password), key).getBytes());
            hasText(password, "decoded password must have text; it must not be null, empty, or blank");
        }
        return password;
    }

    public void setKey(String key) {
        this.key = ByteUtils.bytes(key);
    }
}
