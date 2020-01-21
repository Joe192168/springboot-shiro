package com.joe;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootShiroApplicationTests {

    //用户加密方式
    @Test
    void contextLoads() {
        SimpleHash simpleHash = new SimpleHash("md5", "admin".getBytes(), "1", 1);
        System.out.println(simpleHash.toHex());
    }

}
