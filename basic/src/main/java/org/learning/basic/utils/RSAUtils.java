package org.learning.basic.utils;

import org.apache.commons.lang3.ArrayUtils;
import org.learning.basic.core.BasicException;
import org.springframework.util.Base64Utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public abstract class RSAUtils {

    private static final String ALGORITHM = "RSA";
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;

    public static KeyPair keys(int keysize) {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(ALGORITHM);
            generator.initialize(keysize);
            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new BasicException(null, "can not gen RSA keys.", e);
        }
    }

    public static String format(Key key) {
        return Base64Utils.encodeToString(key.getEncoded());
    }

    public static String encrypt(String publicKey, String text) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64Utils.decodeFromString(publicKey));
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKeyToUse = factory.generatePublic(spec);
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKeyToUse);
            byte[] bytes = ByteUtils.bytes(text);
            byte[] bytesToUse = segment(cipher, bytes, MAX_ENCRYPT_BLOCK);
            return Base64Utils.encodeToString(bytesToUse);
        } catch (NoSuchAlgorithmException e) {
            throw new BasicException(null, "encrypt failed.", e);
        } catch (InvalidKeyException e) {
            throw new BasicException(null, "encrypt failed.", e);
        } catch (NoSuchPaddingException e) {
            throw new BasicException(null, "encrypt failed.", e);
        } catch (BadPaddingException e) {
            throw new BasicException(null, "encrypt failed.", e);
        } catch (InvalidKeySpecException e) {
            throw new BasicException(null, "encrypt failed.", e);
        } catch (IllegalBlockSizeException e) {
            throw new BasicException(null, "encrypt failed.", e);
        }
    }

    public static String decrypt(String privateKey, String text) {
        try {
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKey));
            KeyFactory factory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKeyToUse = factory.generatePrivate(spec);
            Cipher cipher = Cipher.getInstance(factory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKeyToUse);
            byte[] bytes = Base64Utils.decodeFromString(text);
            byte[] bytesToUse = segment(cipher, bytes, MAX_DECRYPT_BLOCK);
            return ByteUtils.strings(bytesToUse);
        } catch (NoSuchAlgorithmException e) {
            throw new BasicException(null, "decrypt failed.", e);
        } catch (InvalidKeyException e) {
            throw new BasicException(null, "decrypt failed.", e);
        } catch (NoSuchPaddingException e) {
            throw new BasicException(null, "decrypt failed.", e);
        } catch (BadPaddingException e) {
            throw new BasicException(null, "decrypt failed.", e);
        } catch (InvalidKeySpecException e) {
            throw new BasicException(null, "decrypt failed.", e);
        } catch (IllegalBlockSizeException e) {
            throw new BasicException(null, "decrypt failed.", e);
        }
    }

    private static byte[] segment(Cipher cipher, byte[] bytes, int max) throws BadPaddingException, IllegalBlockSizeException {
        // 分段加/解密
        byte[] bytesToUse = null;
        for (int i = 1, length = bytes.length, offset = 0; length - offset > 0; i++, offset = i * max) {
            if (length - offset > max) {
                bytesToUse = ArrayUtils.addAll(bytesToUse, cipher.doFinal(bytes, offset, max));
            } else {
                bytesToUse = ArrayUtils.addAll(bytesToUse, cipher.doFinal(bytes, offset, length - offset));
            }
        }
        return bytesToUse;
    }
}
