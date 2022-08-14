/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * File Name : PasswordEncryptionProviderUsingSHA.java 
 * PasswordEncryptionProviderUsingSHA Class is used generate the secured hashed string
 *
 * @author (Goush Basha – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MOD HISTORY
 * *****************************************************************************
 * DATE				USER             		COMMENTS
 * *****************************************************************************
 * 03-08-2016		Goush Basha 			File Created
 *******************************************************************************
 */
public class PasswordEncryptionProviderUsingSHA {

	// Declare defalut_salt
	private static final String default_Salt = "12345678900987654321";
	private static final Logger logger = Logger.getLogger(PasswordEncryptionProviderUsingSHA.class);

	/**
	 * Method Name: get_SHA_1_HashedString This method is used to generate SHA-1
	 * Hashed String
	 * 
	 * @param stringToHash
	 * @param salt
	 * @return hashedString
	 * @throws Exception 
	 */
	public static String get_SHA_1_HashedString(String stringToHash, String salt) throws Exception {

		// Declare String securedHash to hold the secured hash string
		String securedHash = null;
		try {
			// validating salt is a null or empty string
			// if salt is null or empty string throws an exception
			if(salt == null || salt.trim().isEmpty()){
				logger.error("Salt should not be null or empty");
			}
			// Getting the MessageDigest Instance by passing the Algorithm type
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			// Calling generateSecureHashedString to generate the secured hash
			// string.
			securedHash = generateSecureHashedString(stringToHash, salt, md);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating the secured hash using get_SHA_1_HashedString" ,  e);
		}
		return securedHash;
	}

	/**
	 * Method Name: get_SHA_256_HashedString This method is used to generate
	 * SHA-256 Hashed String
	 * 
	 * @param stringToHash
	 * @param salt
	 * @return hashedString
	 * @throws Exception 
	 */
	public static String get_SHA_256_HashedString(String stringToHash, String salt) throws Exception {

		// Declare String securedHash to hold the secured hash string
		String securedHash = null;
		try {
			// validating salt is a null or empty string
			// if salt is null or empty string throws an exception
			if (salt == null || salt.trim().isEmpty()) {
				logger.error("Salt should not be null or empty");
			}
			// Getting the MessageDigest Instance by passing the Algorithm type
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			// Calling generateSecureHashedString to generate the secured hash
			// string.
			securedHash = generateSecureHashedString(stringToHash, salt, md);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating the secured hash using get_SHA_256_HashedString" ,  e);
		}
		return securedHash;
	}

	/**
	 * Method Name: get_SHA_348_HashedString This method is used to generate
	 * SHA-348 Hashed String
	 * 
	 * @param stringToHash
	 * @param salt
	 * @return hashedString
	 * @throws Exception 
	 */
	public static String get_SHA_384_HashedString(String stringToHash, String salt) throws Exception {
		// Declare String securedHash to hold the secured hash string
		String securedHash = null;
		try {
			// validating salt is a null or empty string
			// if salt is null or empty string throws an exception
			if (salt == null || salt.trim().isEmpty()) {
				logger.error("Salt should not be null or empty");
			}
			// Getting the MessageDigest Instance by passing the Algorithm type
			MessageDigest md = MessageDigest.getInstance("SHA-384");
			// Calling generateSecureHashedString to generate the secured hash
			// string.
			securedHash = generateSecureHashedString(stringToHash, salt, md);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating the secured hash using get_SHA_384_HashedString" ,  e);
		}
		return securedHash;
	}

	/**
	 * Method Name: get_SHA_512_HashedString This method is used to generate
	 * SHA-512 Hashed String
	 * 
	 * @param stringToHash
	 * @param salt
	 * @return hashedString
	 * @throws Exception 
	 */
	public static String get_SHA_512_HashedString(String stringToHash, String salt) throws Exception {
		// Declare String securedHash to hold the secured hash string
		String securedHash = null;
		try {
			// validating salt is a null or empty string
			// if salt is null or empty string throws an exception
			if (salt == null || salt.trim().isEmpty()) {
				logger.error("Salt should not be null or empty");
			}
			// Getting the MessageDigest Instance by passing the Algorithm type
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			// Calling generateSecureHashedString to generate the secured hash
			// string.
			securedHash = generateSecureHashedString(stringToHash, salt, md);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating the secured hash using get_SHA_512_HashedString" ,  e);
		}
		return securedHash;
	}

	/**
	 * Method Name: get_SHA_1_HashedString This method is used to generate SHA-1
	 * Hashed String using default salt
	 * 
	 * @param stringToHash
	 * @return hashedString
	 * @throws Exception 
	 */
	public static String get_SHA_1_HashedString(String stringToHash) throws Exception {
		// Declare String securedHash to hold the secured hash string
		String securedHash = null;
		try {
			// Getting the MessageDigest Instance by passing the Algorithm type
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			// Calling generateSecureHashedString to generate the secured hash
			// string.
			securedHash = generateSecureHashedString(stringToHash, default_Salt, md);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating the secured hash using get_SHA_1_HashedString" ,  e);
		}
		return securedHash;
	}

	/**
	 * Method Name: get_SHA_256_HashedString This method is used to generate
	 * SHA-256 Hashed String using default salt
	 * 
	 * @param stringToHash
	 * @return hashedString
	 * @throws Exception 
	 */
	public static String get_SHA_256_HashedString(String stringToHash) throws Exception {
		// Declare String securedHash to hold the secured hash string
		String securedHash = null;
		try {
			// Getting the MessageDigest Instance by passing the Algorithm type
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			// Calling generateSecureHashedString to generate the secured hash
			// string.
			securedHash = generateSecureHashedString(stringToHash, default_Salt, md);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating the secured hash using get_SHA_256_HashedString" ,  e);
		}
		return securedHash;
	}

	/**
	 * Method Name: get_SHA_348_HashedString This method is used to generate
	 * SHA-348 Hashed String using default salt
	 * 
	 * @param stringToHash
	 * @return hashedString
	 * @throws Exception 
	 */
	public static String get_SHA_384_HashedString(String stringToHash) throws Exception {
		// Declare String securedHash to hold the secured hash string
		String securedHash = null;
		try {
			// Getting the MessageDigest Instance by passing the Algorithm type
			MessageDigest md = MessageDigest.getInstance("SHA-384");
			// Calling generateSecureHashedString to generate the secured hash
			// string.
			securedHash = generateSecureHashedString(stringToHash, default_Salt, md);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating the secured hash using get_SHA_384_HashedString" ,  e);
		}
		return securedHash;
	}

	/**
	 * Method Name: get_SHA_512_HashedString This method is used to generate
	 * SHA-512 Hashed String using default salt
	 * 
	 * @param stringToHash
	 * @return hashedString
	 * @throws Exception 
	 */
	public static String get_SHA_512_HashedString(String stringToHash) throws Exception {
		// Declare String securedHash to hold the secured hash string
		String securedHash = null;
		try {
			// Getting the MessageDigest Instance by passing the Algorithm type
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			// Calling generateSecureHashedString to generate the secured hash
			// string.
			securedHash = generateSecureHashedString(stringToHash, default_Salt, md);
		} catch (NoSuchAlgorithmException e) {
			logger.error("Exception while generating the secured hash using get_SHA_512_HashedString" ,  e);
		}
		return securedHash;
	}

	/**
	 * Method Name: getSalt This method is used to generate default salt
	 * 
	 * @return saltString
	 */
	public static String getSalt() throws NoSuchAlgorithmException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy_hh:mm:ss.SSS");
		String currentTimeStamp1 = sdf.format(new Date());
		return currentTimeStamp1;
	}

	/**
	 * Method Name: generateSecureHashedString This method is used to generate
	 * the hashed String using the different Salts and MessageDigests.
	 * 
	 * @param stringToHash
	 * @param salt
	 * @param md
	 * @return hashedString
	 * @throws Exception 
	 */
	private static String generateSecureHashedString(String stringToHash, String salt, MessageDigest md) throws Exception {
		String generatedSecureHash = null;
		try {
			md.update(salt.getBytes());
			byte[] bytes = md.digest(stringToHash.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; ++i) {
				sb.append(Integer.toString((bytes[i] & 255) + 256, 16).substring(1));
			}
			generatedSecureHash = sb.toString();
		} catch (Exception e) {
			logger.error("Exception while generating the secured hash using generateSecureHashedString" ,  e);
		}
		return generatedSecureHash;
	}
	
//	public static void main(String[] args) throws Exception {
//		String stringToHash = "password";
//		SHA sha = new SHA();
//		String salt = sha.getSalt();
//		System.out.println("======salt==" + salt);
//		String secureHash;
//		secureHash = sha.get_SHA_1_HashedString(stringToHash, null);
//		System.out.println(secureHash);
//		secureHash = sha.get_SHA_256_HashedString(stringToHash, salt);
//		System.out.println(secureHash);
//		secureHash = sha.get_SHA_384_HashedString(stringToHash, salt);
//		System.out.println(secureHash);
//		secureHash = sha.get_SHA_512_HashedString(stringToHash, salt);
//		System.out.println(secureHash);
//		
//		secureHash = sha.get_SHA_1_HashedString(stringToHash);
//		System.out.println(secureHash);
//		secureHash = sha.get_SHA_256_HashedString(stringToHash);
//		System.out.println(secureHash);
//		secureHash = sha.get_SHA_384_HashedString(stringToHash);
//		System.out.println(secureHash);
//		secureHash = sha.get_SHA_512_HashedString(stringToHash);
//		System.out.println(secureHash);
//	}

}