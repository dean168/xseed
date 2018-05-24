package org.learning.basic.dao.support;

import static org.junit.Assert.*;

import org.apache.shiro.codec.Base64;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;

public class DefaultJdbcPasswordDecoderImplTest {

    @Test
    public void testEncode() throws NoSuchAlgorithmException {
        DefaultJdbcPasswordDecoderImpl decoder = new DefaultJdbcPasswordDecoderImpl();
        decoder.setKey("org.learning.key");
        decoder.init();
        assertEquals("XArtGjXpKTfaDBBz0T1sgQ==", decoder.encode("admin@qq.com"));
        // 生成 basic.shiro.cookie.rme.cipher.key 配置
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        // rememberMe cookie加密的密钥 建议每个项目都不一样 默认AES算法 密钥长度（128 256 512 位）
        kg.init(128);
        SecretKey sk = kg.generateKey();
        byte[] buffer = sk.getEncoded();
        String hex = Base64.encodeToString(buffer);
        System.out.println(hex);
    }

    @Test
    public void testDecode() {
        DefaultJdbcPasswordDecoderImpl decoder = new DefaultJdbcPasswordDecoderImpl();
        decoder.setKey("org.learning.key");
        decoder.init();
        assertEquals("admin@qq.com", decoder.decode("XArtGjXpKTfaDBBz0T1sgQ=="));
    }

}
