package com.example;

import org.apache.ibatis.annotations.*;

import java.util.Collection;


@Mapper
public interface CarMapper {

	@Select("select * from car")
	Collection<Car> selectAll();

	@Select("select * from car where id = #{id}")
	Car selectById(long id);

	@Options(useGeneratedKeys = true)
	@Insert("insert into car(model,year) values( #{model}, #{year})")
	void insert(Car car);

	@Update("update car set model = #{model}, year= #{year} where id = #{id}")
	void update(Car car);

	@Delete("delete from car where id =#{id}")
	void deleteById(long id);

	@Delete("delete from car where id =#{id}")
	void delete(Car id);

	@Delete("delete from car")
	void deleteAll();
}
