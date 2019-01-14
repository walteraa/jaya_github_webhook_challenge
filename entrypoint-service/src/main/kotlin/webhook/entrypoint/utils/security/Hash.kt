package webhook.entrypoint.utils.security

import java.security.MessageDigest
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

val HMAC_SHA1_ALGORITHM = "HmacSHA1"



fun toHexString(data: ByteArray): String{
    val formatter = Formatter();

    for (b in data) {
        formatter.format("%02x", b);
    }

    return formatter.toString()
}

/**
 * Function to calculate the data signature based on secret
 * @data: Data to calculate the signature
 * @key: key used in sign
 */
fun calculateSignature(data: String, key: String): String{
    val signingKey = SecretKeySpec(key.toByteArray(), HMAC_SHA1_ALGORITHM)

    val mac = Mac.getInstance(HMAC_SHA1_ALGORITHM)
    mac.init(signingKey)

    return toHexString(mac.doFinal(data.toByteArray()))
}