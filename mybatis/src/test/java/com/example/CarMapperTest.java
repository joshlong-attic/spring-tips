package com.example;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CarMapperTest {

	private List<Car> cars;

	@Autowired
	private CarMapper carMapper;

	@Before
	public void before() throws Exception {

		this.carMapper.deleteAll();

		this.cars = Arrays.asList(
				new Car("Focus", 2004, null),
				new Car("Civic", 1984, null));

		this.cars.forEach(carMapper::insert);
	}

	@Test
	public void test_selectById() {
		Car car = this.carMapper.selectById(this.cars.iterator().next().getId());
		Assert.assertNotNull(car);
	}

	@Test
	public void test_selectAll() {
		int size = this.carMapper.selectAll().size();
		Assert.assertEquals(size, this.cars.size());
	}

	@Test
	public void test_insert() throws Throwable {
		Car car = new Car("Accord", 2010, null);
		this.carMapper.insert(car);
		Assert.assertNotNull(car.getId());
	}

	@Test
	public void test_update() throws Throwable {
		Car next = this.cars.iterator().next();
		String og = next.getModel();
		String toUpperCase = og.toUpperCase();
		Assert.assertNotEquals(toUpperCase, og);
		next.setModel(toUpperCase);
		this.carMapper.update(next);
		Assert.assertEquals(this.carMapper.selectById(next.getId()).getModel(), toUpperCase);
	}

	@Test
	public void test_delete() throws Throwable {
		Assert.assertEquals(this.carMapper.selectAll().size(), this.cars.size());
		this.carMapper.delete(this.cars.iterator().next());
		Assert.assertEquals(this.carMapper.selectAll().size(), this.cars.size() - 1);
	}

	@Test
	public void test_deleteById() throws Throwable {
		Assert.assertEquals(this.carMapper.selectAll().size(), this.cars.size());
		this.carMapper.deleteById(this.cars.iterator().next().getId());
		Assert.assertEquals(this.carMapper.selectAll().size(), this.cars.size() - 1);
	}
}