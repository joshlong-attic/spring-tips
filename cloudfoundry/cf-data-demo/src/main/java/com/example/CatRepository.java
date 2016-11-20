package com.example;

import org.springframework.data.jpa.repository.JpaRepository;


public interface CatRepository extends JpaRepository<Cat, Long> {
}
