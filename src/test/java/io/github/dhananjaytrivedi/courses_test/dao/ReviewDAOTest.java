package io.github.dhananjaytrivedi.courses_test.dao;

import io.github.dhananjaytrivedi.dao.CourseDAOImplementation;
import io.github.dhananjaytrivedi.dao.ReviewDAOImplementation;
import io.github.dhananjaytrivedi.exceptions.DAOException;
import io.github.dhananjaytrivedi.model.Course;
import io.github.dhananjaytrivedi.model.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ReviewDAOTest {

    private ReviewDAOImplementation reviewDAO;
    private CourseDAOImplementation courseDAO;
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");

        reviewDAO = new ReviewDAOImplementation(sql2o);
        courseDAO = new CourseDAOImplementation(sql2o);

        // Keep connection open throughout the test
        connection = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void addingReviewSetsNewID() throws DAOException {

        Review review = createTestReview();                 // Creating a test review
        int originalID = review.getId();

        reviewDAO.add(review);                              // Adding the review for the test course
        assertNotEquals(originalID, review.getId());        // Validating test results
    }

    @Test
    public void addedReviewsAreReturnedFromFindAll() throws Exception {
        Review review = createTestReview();
        reviewDAO.add(review);
        assertEquals(1, reviewDAO.findAll().size());
    }

    @Test
    public void noReviewReturnsEmptyList() {
        assertEquals(0, reviewDAO.findAll().size());
    }

    @Test
    public void existingReviewsCanBeFoundById() throws Exception {
        Review review = createTestReview();
        reviewDAO.add(review);
        int course_id = review.getCourse_id();
        List<Review> foundReviews = reviewDAO.findByCourseID(course_id);
        assertNotEquals(foundReviews.size(), 0);
    }

    @Test(expected = DAOException.class)
    public void addingAReviewForNonExistingCourse() throws DAOException {
        Review review = new Review(23, 5, "This should give error");
        reviewDAO.add(review);
    }

    private Review createTestReview() throws DAOException {
        Course course = createTestCourse();                 // Creating a course for this test course
        return new Review(course.getId(), 5, "Great review");
    }

    private Course createTestCourse() throws DAOException {
        Course course = new Course("Testing Course", "http://testingcourse.com");     // Adding a course for the test
        courseDAO.add(course);
        return course;
    }

}