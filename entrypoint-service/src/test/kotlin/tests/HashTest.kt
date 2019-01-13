package tests

import junit.framework.TestCase
import webhook.entrypoint.utils.security.sha1

class ConsumerTest: TestCase() {


    fun testHashFunction(){
        var passphrase = "this test was awesome"

        assertEquals("Invalid hash was built","7ee0f5e4d865ee2c03bb2783eada64317e6ffe39",
            sha1(passphrase))
    }

}