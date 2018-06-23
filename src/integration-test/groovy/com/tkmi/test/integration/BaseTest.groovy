package com.tkmi.test.integration

import com.tkmi.test.integration.service.RestService
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
class BaseTest extends AbstractTestNGSpringContextTests {

    @Autowired RestService restService

}