package org.learning.basic.utils;

import org.junit.Test;

import java.security.Key;
import java.security.KeyPair;

public class RSAUtilsTest {

    @Test
    public void test1() {
        KeyPair pair = RSAUtils.keys(1024);
        Key publicKey = pair.getPublic();
        System.out.println(RSAUtils.format(publicKey));
        Key privateKey = pair.getPrivate();
        System.out.println(RSAUtils.format(privateKey));
        String content = RSAUtils.encrypt(RSAUtils.format(publicKey), "admin@qq.com");
        System.out.println(content);
        System.out.println(RSAUtils.decrypt(RSAUtils.format(privateKey), content));
    }
}
