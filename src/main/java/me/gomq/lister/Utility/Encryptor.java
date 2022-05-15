package me.gomq.lister.Utility;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Encryptor {
    private static volatile Encryptor INSTANCE;

    public static String alg = "AES/CBC/PKCS5Padding";
    private final String cipherKey;
    private final String iv;

    public Encryptor(String cipherKey) {
        this.cipherKey = formatKey(cipherKey);
        this.iv = this.cipherKey.substring(0,16);
    }

    private String formatKey(String sVal) {
        StringBuilder result = new StringBuilder();
        
        for(int i=0; i<sVal.length(); i++) {
            if (String.valueOf(sVal.charAt(i)).matches("[a-zA-Z0-9!@+?]")) {
                result.append(sVal.charAt(i));
            }
        }
        result = new StringBuilder(result.toString().replaceAll(" ", ""));

        result.append("0".repeat(Math.max(0, 32 - result.length())));

        return result.toString();
    }

    public String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(this.cipherKey.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(this.iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(this.cipherKey.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(this.iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
