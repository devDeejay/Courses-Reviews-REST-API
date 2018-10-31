package io.github.dhananjaytrivedi.dao;

import io.github.dhananjaytrivedi.exceptions.DAOException;
import io.github.dhananjaytrivedi.model.Course;

import java.util.List;

public interface CourseDAO {
    void add(Course course) throws DAOException;

    List<Course> findAll();

    Course findById(int id);
}
