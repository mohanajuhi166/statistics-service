package com.events.statsservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetStatsTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void test() throws JsonProcessingException, Exception {

        HttpStatus invalidRes = restTemplate.postForEntity("/event",
                String.valueOf(System.currentTimeMillis() - (60000 + 100)).concat(",")
                        .concat("0.0442672968")
                        .concat(",").concat("111212767")
                , Object.class)
                .getStatusCode();
        assertEquals(HttpStatus.NO_CONTENT, invalidRes);
        HttpStatus res = restTemplate.postForEntity("/event",
                String.valueOf(System.currentTimeMillis() - (50000)).concat(",")
                        .concat("0.0442672968")
                        .concat(",").concat("1282509067")
                , Object.class)
                .getStatusCode();
        assertEquals(HttpStatus.ACCEPTED, res);

        assertEquals("1,0.0442672968,0.0442672968,1282509067,1.282509067E9",
                restTemplate.getForEntity("/stats", String.class).getBody());

        //invalid cases
        HttpStatus invalidTimestampRes = restTemplate.postForEntity("/event",
                "AAAA".concat(",")
                        .concat("0.0442672968")
                        .concat(",").concat("111212767")
                , Object.class)
                .getStatusCode();
        assertEquals(HttpStatus.NO_CONTENT, invalidTimestampRes);


        //invalid X value > 1
        HttpStatus invalidXRes = restTemplate.postForEntity("/event",
                String.valueOf(System.currentTimeMillis() - (50000)).concat(",")
                        .concat("2.0442672968")
                        .concat(",").concat("111212767")
                , Object.class)
                .getStatusCode();
        assertEquals(HttpStatus.NO_CONTENT, invalidXRes);


        //invalid Y Value

        HttpStatus invalidYRes = restTemplate.postForEntity("/event",
                String.valueOf(System.currentTimeMillis() - (50000)).concat(",")
                        .concat("0.0442672968")
                        .concat(",").concat("AAAAA")
                , Object.class)
                .getStatusCode();
        assertEquals(HttpStatus.NO_CONTENT, invalidYRes);
    }


}
