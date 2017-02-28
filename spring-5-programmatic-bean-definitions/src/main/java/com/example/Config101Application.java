package com.example;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;


@SpringBootApplication
public class Config101Application {

	//	@Component
	public static class MyBDRPP
			implements BeanDefinitionRegistryPostProcessor {

		@Override
		public void postProcessBeanDefinitionRegistry(
				BeanDefinitionRegistry bdr) throws BeansException {

			BeanFactory beanFactory = BeanFactory.class.cast(bdr);

			bdr.registerBeanDefinition("barService",
					genericBeanDefinition(BarService.class).getBeanDefinition());

			bdr.registerBeanDefinition("fooService", genericBeanDefinition(FooService.class,
					() -> new FooService(beanFactory.getBean(BarService.class))).getBeanDefinition());

		}

		@Override
		public void postProcessBeanFactory(ConfigurableListableBeanFactory clbf) throws BeansException {
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Config101Application.class, args);
	}
}

class ProgrammaticBeanDefinitionInitializr implements
		ApplicationContextInitializer<GenericApplicationContext> {

	@Override
	public void initialize(GenericApplicationContext applicationContext) {

		BarService barService = new BarService();
		FooService fooService = new FooService(barService);

		applicationContext.registerBean(BarService.class, () -> barService);
		applicationContext.registerBean(FooService.class, () -> fooService);
	}
}

class FooService {

	private final BarService barService;

	FooService(BarService barService) {
		this.barService = barService;
	}
}

class BarService {
}