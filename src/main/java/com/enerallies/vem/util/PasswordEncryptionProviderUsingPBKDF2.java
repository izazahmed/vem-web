/*
 * This is an unpublished work containing EnerAllies confidential and proprietary information.  
 * Disclosure, use or reproduction without the written authorization of EnerAllies is prohibited.  
 * If publication occurs, the following notice applies:
 *
 * Copyright (C) 2016-2017, EnerAllies All rights reserved.
 */
package com.enerallies.vem.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.enerallies.vem.exceptions.VEMAppException;

/**
 * File Name : PasswordEncryptionProviderUsingPBKDF2.java 
 * PasswordEncryptionProviderUsingPBKDF2 is used to generate an encrypted value for user password.
 *
 * @author (Goush Basha – CTE).
 * 
 * Contact (Umang)
 * 
 * @version     VEM2-1.0
 * @date        03-08-2016
 *
 * MOD HISTORY
 * DATE				USER             	COMMENTS
 * 03-08-2016		Goush Basha			File Created
 *
 */

@Component
public class PasswordEncryptionProviderUsingPBKDF2 {
	/*  PasswordEncryptionProviderUsingPBKDF2 class takes plain password and salt key by using 
	 *  PBKDF2 algorithm it will generate an encrypted value with 
	 *  the combination of both plain password and salt key as a encrypted password. */
	
	private static final Logger logger = Logger.getLogger(PasswordEncryptionProviderUsingPBKDF2.class);
	
	PasswordEncryptionProviderUsingPBKDF2(){
		//Constructor
	}
	/**
	 * Method Name: generateStorngPasswordHash, This method is used to generate
	 * the hashed String of combination of salt key and plain password 
	 * using “PBKDF2” algorithm.
	 * 
	 * @param saltKey
	 * @param password
	 * @return hexaDecimalString
	 * @throws VEMAppException 
	 */
	public static String generateStorngPasswordHash(String saltKey, String password) throws VEMAppException {
		
		int iterations 		= 	1000;	//This is used to specify the no.of iterations.
		char[] chars 		= 	null;	//This is used to hold the password as character array.
		byte[] salt 		= 	null;	//This is used to hold the saltKey as character array.
		String encryptedPd	=	"";		//This is used to hold the resulted hashedString. 
		PBEKeySpec spec		=	null;	//This is used to hold the object of PBEKeySpec.
		SecretKeyFactory skf=	null;	//This is used to hold the object of SecretKeyFactory.
		byte[] hash			=	null;	//This is used to hold the byte of generated hashed string.
		
		try{
			
			chars = password.toCharArray();
			salt = saltKey.getBytes();
			
			// Getting the PBEKeySpec Instance by passing the password,salt and iterations.
			spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
			// Getting the SecretKeyFactory Instance by passing the Algorithm type
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			// Generating the strong password hash by passing the PBEKeySpec Instance.
			hash = skf.generateSecret(spec).getEncoded();
			// Making the hashvalue more strong by appending the hashed value of salt key 
			// and iterations
			encryptedPd = convertToHex(hash) + iterations + convertToHex(salt);
			
		}catch(NoSuchAlgorithmException | InvalidKeySpecException e){
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ENCRYPTPD_ERROR, logger, e);
		}
		return encryptedPd;
	}
	
	/**
	 * Method Name: convertToHex, This method is used to generate
	 * the hexadecimal value for salt or password hashed value.
	 * 
	 * @param array
	 * @return hexaDecimalString
	 * @throws VEMAppException
	 */
	private static String convertToHex(byte[] array) throws VEMAppException
	{
		String strResult	=	"";	//This is used to hold the resulted hexadecimal value.
		BigInteger bi 		=	null;
		String hex 			= 	"";
		int paddingLength	=	0;
		
		try{
		
			bi = new BigInteger(1, array);
			hex = bi.toString(16);
			paddingLength = (array.length * 2) - hex.length();	//Getting the padding length.
			
			if(paddingLength > 0)
			{
				strResult = String.format("%0"  +paddingLength + "d", 0) + hex;
			}else{
				strResult = hex;
			}

		}catch(Exception e){
			throw new VEMAppException(ErrorCodes.GENERAL_APP_ERROR, ErrorCodes.ENCRYPTPD_ERROR, logger, e);
		}
		
		return strResult;
	}

}
