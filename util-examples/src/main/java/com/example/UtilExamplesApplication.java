package com.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.*;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EnableAspectJAutoProxy
@SpringBootApplication
public class UtilExamplesApplication {

	@Aspect
	@Component
	public static class SimpleBeforeAspect {

		@Before("execution(* begin(..))")
		public void before(JoinPoint joinPoint) {
			log.info("-------------");
			log.info("before()");
			log.info("signature: " + joinPoint.getSignature());
		}
	}

	@Data
	@AllArgsConstructor
	public static class DemoClass {

		@PostConstruct
		public void begin() {
			log.info("begin()");
		}

		private final List<Map<String, Object>> list = new ArrayList<>();
	}

	@Bean
	DemoClass demoClass() {
		return new DemoClass();
	}

	@Bean
	CommandLineRunner demo(DemoClass demo) {
		return args -> {

			Assert.notNull(demo.getList(), "the list can't be null");

			beansUtils(demo);
			classUtils();
			systemPropertyUtils();
			fileCopyUtils();
			web();
			aop(demo);
			reflection();
		};
	}

	private void reflection() {

		ReflectionUtils.doWithFields(DemoClass.class, field -> log.info("field = " + field.toString()));
		ReflectionUtils.doWithMethods(DemoClass.class, method -> log.info("method = " + method.toString()));

		Field list = ReflectionUtils.findField(DemoClass.class, "list");
		log.info(list.toString());

		ResolvableType rt = ResolvableType.forField(list);
		log.info(rt.toString());

	}

	private void aop(DemoClass demoClass) {
		Class<?> targetClass = AopUtils.getTargetClass(demoClass);
		log.info("Class<?> is " + targetClass);
		log.info("is AOP proxy? " + AopUtils.isAopProxy(demoClass));
		log.info("is CGlib proxy? " + AopUtils.isCglibProxy(demoClass));
	}

	private void web() {
		RestTemplate rt = new RestTemplate();
		rt.getForEntity("http://localhost:8080/hi", Void.class);
	}

	@RestController
	public static class SimpleRestController {

		@GetMapping("/hi")
		void hi(HttpServletRequest request) {
			long age = ServletRequestUtils.getIntParameter(request, "age", -1);
			log.info("age is " + age);

			File tempDir = WebUtils.getTempDir(request.getServletContext());
			log.info("temporary directory for Apache Tomcat is " + tempDir.getAbsolutePath());

			ApplicationContext webApplicationContext = RequestContextUtils.findWebApplicationContext(request);
			Environment bean = webApplicationContext.getBean(Environment.class);
			log.info("webApplicationContext resolved property = " + bean.getProperty("user.home"));
		}
	}


	private void fileCopyUtils() {
		File file = new File(SystemPropertyUtils.resolvePlaceholders("${user.home}"), "/Desktop/content.txt");
		try (Reader r = new FileReader(file)) {
			log.info("contents of file " + FileCopyUtils.copyToString(r));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private void systemPropertyUtils() {
		String resolvedText = SystemPropertyUtils.resolvePlaceholders("my home directory is ${user.home}");
		log.info("resolved text: " + resolvedText);
	}

	private void classUtils() {
		Constructor<DemoClass> demoClassConstructor = ClassUtils.getConstructorIfAvailable(DemoClass.class);
		log.info("demoClassConstructor: " + demoClassConstructor);
		try {
			DemoClass demoClass = demoClassConstructor.newInstance();
			log.info("newInstance'd demoClass: " + demoClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void beansUtils(DemoClass demo) {
		PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(demo.getClass());
		for (PropertyDescriptor pd : descriptors) {
			log.info("pd: " + pd.getName());
			//log.info("pd.readMethod: " + pd.getReadMethod().getName());
		}
	}

	private static final Log log = LogFactory.getLog(UtilExamplesApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UtilExamplesApplication.class, args);
	}
}
