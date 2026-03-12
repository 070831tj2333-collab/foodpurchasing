package com.campus.food.repository;

import com.campus.food.model.Food;
import com.campus.food.model.FoodLike;
import com.campus.food.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodLikeRepository extends JpaRepository<FoodLike, Long> {

    Optional<FoodLike> findByStudentAndFood(Student student, Food food);

    List<FoodLike> findByStudent(Student student);
}

