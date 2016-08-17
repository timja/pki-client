package com.github.timja.pkiclient

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.jcajce.JcaPEMWriter
import java.io.StringWriter
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Security

/**
 * Created by tim on 17/08/16.
 */
fun main(args: Array<String>) {
    Security.addProvider(BouncyCastleProvider())

    val ecdsaKeyPair = generateECDSAKeyPair()

    val ecdsaPrivateKey = writeKey(ecdsaKeyPair.private)
    val ecdsaPublicKey = writeKey(ecdsaKeyPair.public)

    val rsaKeyPair = generateRSAKeyPair()

    val rsaPrivateKey = writeKey(rsaKeyPair.private)
    val rsaPublicKey = writeKey(rsaKeyPair.public)


    print(ecdsaPrivateKey)
    print(ecdsaPublicKey)

    print(rsaPrivateKey)
    print(rsaPublicKey)

}

fun writeKey(key: Key): String {
    val sw = StringWriter()
    val pw = JcaPEMWriter(sw)

    pw.writeObject(key)
    pw.close()

    val data = sw.toString()
    return data
}

fun writeCertificate(key: Any): String {
    val sw = StringWriter()
    val pw = JcaPEMWriter(sw)

    pw.writeObject(key)
    pw.close()

    val data = sw.toString()
    return data
}

private val BOUNCY_CASTLE_PROVIDER = "BC"

fun generateECDSAKeyPair(): KeyPair {
    val kpGen = KeyPairGenerator.getInstance("ECDSA", BOUNCY_CASTLE_PROVIDER)
    kpGen.initialize(239)

    return kpGen.generateKeyPair()
}

fun generateRSAKeyPair(): KeyPair {
    val kpGen = KeyPairGenerator.getInstance("RSA", BOUNCY_CASTLE_PROVIDER)
    kpGen.initialize(2048)

    return kpGen.generateKeyPair()
}