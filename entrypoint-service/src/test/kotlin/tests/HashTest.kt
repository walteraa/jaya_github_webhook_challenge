package tests

import com.sun.crypto.provider.HmacSHA1
import junit.framework.TestCase
import webhook.entrypoint.utils.security.calculateSignature

class ConsumerTest: TestCase() {


    fun testHashFunction(){
        val secret = "testgithubsecret"
        val data = "{\"test\":\"secutiry\"}"

        assertEquals("Invalid hash was built","02fe02fd51397cc9b58f1cd8f51b05a552b363da",
            calculateSignature(data, secret)
        )

    }

}