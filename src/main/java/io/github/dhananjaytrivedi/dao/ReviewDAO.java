package io.github.dhananjaytrivedi.dao;

import io.github.dhananjaytrivedi.exceptions.DAOException;
import io.github.dhananjaytrivedi.model.Review;

import java.util.List;

public interface ReviewDAO {

    void add(Review review) throws DAOException;    // Adds a review in the database

    List<Review> findAll();                         // Gets all reviews stored in database

    List<Review> findByCourseID(int courseID);       // Gets all review for a particular course

}
