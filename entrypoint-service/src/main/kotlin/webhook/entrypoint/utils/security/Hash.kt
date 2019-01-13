package webhook.entrypoint.utils.security

import java.security.MessageDigest

/**
 * Function to calculate a String's SHA-1
 * @input: String which will be processed
 */
fun sha1(input: String?): String{
    var result = ""
    val bytes = input?.toByteArray()
    val md = MessageDigest.getInstance("SHA-1")
    val digest = md.digest(bytes)
    for (byte in digest) result += "%02x".format(byte)
    return result
}