package org.sputnik.util;

import org.junit.Test;

public class NameUtilsTest {
    @Test
    public void ok() throws Exception {
        NameUtils.validateIdentifier("1Az-_", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void empty() throws Exception {
        NameUtils.validateIdentifier("", "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void empty2() throws Exception {
        NameUtils.validateIdentifier(null, "name");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalid() throws Exception {
        NameUtils.validateIdentifier("a+b", "name");
    }
}
