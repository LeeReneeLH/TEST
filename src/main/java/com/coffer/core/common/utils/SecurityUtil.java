package com.coffer.core.common.utils;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SecurityUtil {
	public static String DES = "AES"; // optional value AES/DES/DESede

	public static String CIPHER_ALGORITHM = "AES"; // optional value AES/DES/DESede

	private static String KEY = "JULONG";

	public static Key getSecretKey(String key) throws Exception {
		SecretKey securekey = null;
		if (key == null) {
			key = "";
		}
		KeyGenerator keyGenerator = KeyGenerator.getInstance(DES);
		keyGenerator.init(new SecureRandom(key.getBytes()));
		securekey = keyGenerator.generateKey();
		return securekey;
	}

	public static String encrypt(String data) throws Exception {
		SecureRandom sr = new SecureRandom();
		Key securekey = getSecretKey(KEY);
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		byte[] bt = cipher.doFinal(data.getBytes());
		String strs = Encodes.encodeBase64(bt);
		return strs;
	}

	public static String detrypt(String message) throws Exception {
		SecureRandom sr = new SecureRandom();
		Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
		Key securekey = getSecretKey(KEY);
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		byte[] res = Encodes.decodeBase64(message);
		res = cipher.doFinal(res);
		return new String(res);
	}

	public static void main(String[] args) throws Exception {
		String message = "";
		if (args != null && args.length > 0) {
			message = args[0];
		}
		for (String temp : args) {
			System.out.println(temp);
		}
		String entryptedMsg = encrypt(message);
		System.out.println("encrypted message is below :");
		System.out.println(entryptedMsg);

		String decryptedMsg = detrypt(entryptedMsg);
		System.out.println("decrypted message is below :");
		System.out.println(decryptedMsg);
	}
}