package com.github.timja.pkiclient

import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.cert.X509v1CertificateBuilder
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.Security
import java.util.*

/**
 */
fun main(args: Array<String>) {
    Security.addProvider(BouncyCastleProvider())

    generateECDSACertificateV1()
}

private fun generateRSACertificateV1() {
    val startDate = Date()              // time from which certificate is valid
    val expiryDate = Date()             // time after which certificate is not valid
    val serialNumber = BigInteger.ONE     // serial number for certificate
    val keyPair = generateRSAKeyPair()             // EC public/private key pair

    val subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)
    val dnName = X500Name("CN=Test RSA Certificate")
    val certGen = X509v1CertificateBuilder(dnName, serialNumber, startDate, expiryDate, dnName, subjectPublicKeyInfo)

    val contentSigner = JcaContentSignerBuilder("SHA256withRSA")
            .setProvider(BouncyCastleProvider())
            .build(keyPair.private)

    val cert = certGen.build(contentSigner)

    print(writeCertificate(cert))
}

private fun generateECDSACertificateV1() {
    val startDate = Date()              // time from which certificate is valid
    val expiryDate = Date()             // time after which certificate is not valid
    val serialNumber = BigInteger.ONE     // serial number for certificate
    val keyPair = generateECDSAKeyPair()             // EC public/private key pair

    val subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)
    val dnName = X500Name("CN=Test ECDSA Certificate")
    val certGen = X509v1CertificateBuilder(dnName, serialNumber, startDate, expiryDate, dnName, subjectPublicKeyInfo)

    val contentSigner = JcaContentSignerBuilder("SHA256withECDSA")
            .setProvider(BouncyCastleProvider())
            .build(keyPair.private)

    val cert = certGen.build(contentSigner)

    print(writeCertificate(cert))
}

