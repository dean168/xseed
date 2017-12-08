package org.learning.basic.dao.support;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.crypto.AesCipherService;

import org.learning.basic.dao.IJdbcPasswordDecoder;
import org.learning.basic.dao.IJdbcPasswordEncoder;
import org.learning.basic.utils.ByteUtils;
import org.springframework.util.Assert;

public class DefaultJdbcPasswordDecoderImpl implements IJdbcPasswordEncoder, IJdbcPasswordDecoder {

	private AesCipherService cipherService;

	@PostConstruct
	public void init() {
		cipherService = new AesCipherService();
		cipherService.setModeName("ECB");
	}

	@Override
    public String encode(String password) {
	    if (StringUtils.isNotEmpty(password)) {
	        password = ByteUtils.toString(Base64.encode(cipherService.encrypt(ByteUtils.getBytes(password), createSecretKey()).getBytes()));
	    }
        return password;
    }

    @Override
	public String decode(String password) {
		if (StringUtils.isNotEmpty(password)) {
			password = ByteUtils.toString(cipherService.decrypt(Base64.decode(password), createSecretKey()).getBytes());
			Assert.hasText(password, "decoded password must have text; it must not be null, empty, or blank");
		}
		return password;
	}

	private byte[] createSecretKey() {
		return (new byte[] { 104, 117, 97, 119, 101, 105, 95, 99, 100, 115, 102, 95, 112, 97, 115, 115 });
	}
}
