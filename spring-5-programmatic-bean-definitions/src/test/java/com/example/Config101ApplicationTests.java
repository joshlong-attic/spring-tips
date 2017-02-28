package com.example;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Config101ApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void contextLoads() {
		Assert.assertNotNull("the BarService should not be null!",
				this.applicationContext.getBean(BarService.class));
		Assert.assertNotNull("the FooService should not be null!",
				this.applicationContext.getBean(FooService.class));
	}

}
