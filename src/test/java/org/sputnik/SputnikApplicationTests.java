package org.sputnik;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.sputnik.config.SputnikProperties;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public abstract class SputnikApplicationTests {
    @Value("${java.io.tmpdir}")
    File tmpDirectory;

    @Autowired
    protected SputnikProperties sputnikProperties;

}
