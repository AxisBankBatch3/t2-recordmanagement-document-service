package com.delta.document.component

import org.springframework.stereotype.Component
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.xml.bind.DatatypeConverter


@Component
class Aes {

    /**
     * gets the AES encryption key. In your actual programs, this should be safely
     * stored.
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getSecretEncryptionKey(): SecretKey? {
        val generator: KeyGenerator = KeyGenerator.getInstance("AES")
        generator.init(128) // The AES key size in number of bits
        return generator.generateKey()
    }


    /**
     * Encrypts plainText in AES using the secret key
     * @param plainText
     * @param secKey
     * @return
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    fun encryptText(plainText: String, secKey: SecretKey?): ByteArray? {
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.ENCRYPT_MODE, secKey)
        return aesCipher.doFinal(plainText.toByteArray())
    }


    /**
     * Decrypts encrypted byte array using the key used for encryption.
     * @param byteCipherText
     * @param secKey
     * @return
     * @throws Exception
     */
    @Throws(java.lang.Exception::class)
    fun decryptText(byteCipherText: ByteArray?, secKey: SecretKey?): String? {
        // AES defaults to AES/ECB/PKCS5Padding in Java 7
        val aesCipher = Cipher.getInstance("AES")
        aesCipher.init(Cipher.DECRYPT_MODE, secKey)
        val bytePlainText = aesCipher.doFinal(byteCipherText)
        return String(bytePlainText)
    }

    /**
     * Convert a binary byte array into readable hex form
     * @param hash
     * @return
     */
    private fun bytesToHex(hash: ByteArray): String? {
        return DatatypeConverter.printHexBinary(hash)
    }
}