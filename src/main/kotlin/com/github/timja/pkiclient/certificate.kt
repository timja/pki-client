package com.github.timja.pkiclient

import org.bouncycastle.asn1.DEROctetString
import org.bouncycastle.asn1.x500.X500Name
import org.bouncycastle.asn1.x509.*
import org.bouncycastle.cert.X509CertificateHolder
import org.bouncycastle.cert.X509v1CertificateBuilder
import org.bouncycastle.cert.X509v3CertificateBuilder
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.operator.ContentSigner
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder
import java.math.BigInteger
import java.security.KeyPair
import java.security.Security
import java.security.cert.X509Certificate
import java.util.*
import javax.security.auth.x500.X500Principal

/**
 */
fun main(args: Array<String>) {
    Security.addProvider(BouncyCastleProvider())

    val caCertificate = generateECDSACertificateV1()
    val keyPair = generateECDSAKeyPair()

    val contentSigner = JcaContentSignerBuilder("SHA256withECDSA")
            .setProvider(BouncyCastleProvider())
            .build(keyPair.private)

    val intermediateCertificate = generateCertificateV3(caCertificate, keyPair, contentSigner)

    print(writeCertificate(intermediateCertificate))


}

private fun generateCertificateV3(issuer: X509Certificate, keyPair: KeyPair, contentSigner: ContentSigner): X509Certificate {
    val subjectName = X500Name("CN=Test V3 Certificate")
    val startDate = Date()
    val expiryDate = Date()
    val serialNumber = BigInteger.ONE

    val subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)


    val x509ExtensionUtils = JcaX509ExtensionUtils()

    val authorityKeyIdentifier = Extension(Extension.authorityKeyIdentifier, false,
            DEROctetString(x509ExtensionUtils.createAuthorityKeyIdentifier(issuer)))

    val basicConstraints = Extension(Extension.basicConstraints, true, DEROctetString(BasicConstraints(0)))

    val anyKeyUsage = ExtendedKeyUsage(KeyPurposeId.anyExtendedKeyUsage)
    val extendedKeyUsage = Extension(Extension.extendedKeyUsage, false, DEROctetString(anyKeyUsage))

    val keyCertSignAndCRLSign = KeyUsage(KeyUsage.keyCertSign or KeyUsage.cRLSign)
    val keyUsage = Extension(Extension.keyUsage, true, DEROctetString(keyCertSignAndCRLSign))

    val dnsName = GeneralNames(GeneralName(GeneralName.dNSName, "example.com"))
    val subjectAlternativeName = Extension(Extension.subjectAlternativeName, false, DEROctetString(dnsName))

    val subjectKeyIdentifier = Extension(Extension.subjectKeyIdentifier, false,
            DEROctetString(x509ExtensionUtils.createSubjectKeyIdentifier(keyPair.public)))

    return X509v3CertificateBuilder(issuer.issuerX500Principal.toX500Name(), serialNumber, startDate, expiryDate, subjectName, subjectPublicKeyInfo)
            .addExtension(authorityKeyIdentifier)
            .addExtension(basicConstraints)
            .addExtension(extendedKeyUsage)
            .addExtension(keyUsage)
            .addExtension(subjectAlternativeName)
            .addExtension(subjectKeyIdentifier)
            .build(contentSigner)
            .toX509()

}

private fun generateCertificateV1(x500Name: X500Name, keyPair: KeyPair, contentSigner: ContentSigner): X509Certificate {
    val startDate = Date()
    val expiryDate = Date()
    val serialNumber = BigInteger.ONE

    val subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPair.public.encoded)

    val certGen = X509v1CertificateBuilder(x500Name, serialNumber, startDate, expiryDate, x500Name, subjectPublicKeyInfo)
    return certGen.build(contentSigner).toX509()

}

private fun generateRSACertificateV1(): X509Certificate {
    val keyPair = generateRSAKeyPair()             // EC public/private key pair
    val dnName = X500Name("CN=Test RSA Certificate")

    val contentSigner = JcaContentSignerBuilder("SHA256withRSA")
            .setProvider(BouncyCastleProvider())
            .build(keyPair.private)

    return generateCertificateV1(dnName, keyPair, contentSigner)
}

fun X509CertificateHolder.toX509(): X509Certificate {
    return JcaX509CertificateConverter()
            .setProvider(BouncyCastleProvider())
            .getCertificate(this)
}

fun X500Principal.toX500Name(): X500Name {
    return X500Name(this.getName(X500Principal.RFC1779))
}

private fun generateECDSACertificateV1(): X509Certificate {
    val keyPair = generateECDSAKeyPair()             // EC public/private key pair

    val dnName = X500Name("CN=Test ECDSA Certificate")
    val contentSigner = JcaContentSignerBuilder("SHA256withECDSA")
            .setProvider(BouncyCastleProvider())
            .build(keyPair.private)

    return generateCertificateV1(dnName, keyPair, contentSigner)
}

