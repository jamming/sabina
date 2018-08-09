package co.there4.bali

import org.testng.annotations.Test

@Test class ChecksTest implements Checks {
    @Test (expectedExceptions = IllegalArgumentException.class)
    void "a non compliant argument throws an exception" () {
        checkArgument (false)
    }

    @Test void "a compliant argument do not throw an exception" () {
        checkArgument (true)
    }

    @Test void "a non compliant argument throws an exception with the proper message" () {
        try {
            checkArgument (false, "Bad argument: %s", "arg1")
            assert false
        }
        catch (IllegalArgumentException e) {
            assert e.message == "Bad argument: arg1"
        }
    }
}
