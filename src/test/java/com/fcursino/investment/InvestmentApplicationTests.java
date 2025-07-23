package com.fcursino.investment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InvestmentApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    void mainMethodRuns() {
        InvestmentApplication.main(new String[]{});
    }

}
