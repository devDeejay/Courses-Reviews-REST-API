package io.github.dhananjaytrivedi.dao;

import io.github.dhananjaytrivedi.exceptions.DAOException;
import io.github.dhananjaytrivedi.model.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class ReviewDAOImplementation implements ReviewDAO {

    private final Sql2o sql2o;

    public ReviewDAOImplementation(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Review review) throws DAOException {
        String sql = "INSERT INTO reviews (course_id, rating, comment) VALUES (:course_id, :rating, :comment)";
        try (Connection connection = sql2o.open()) {
            int storedID = (int) connection.createQuery(sql)
                    .bind(review)
                    .executeUpdate()
                    .getKey();

            review.setId(storedID);
        }
        catch (Sql2oException exception) {
            throw new DAOException(exception, "Problem while adding review : " + exception.getMessage());
        }
    }

    @Override
    public List<Review> findAll() {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM reviews")
                    .executeAndFetch(Review.class);
        }
    }

    @Override
    public List<Review> findByCourseID(int courseID) {
        try (Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM reviews WHERE course_id = :course_id")
                    .addParameter("course_id", courseID)
                    .executeAndFetch(Review.class);
        }
    }
}
