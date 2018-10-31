package io.github.dhananjaytrivedi;

import com.google.gson.Gson;
import io.github.dhananjaytrivedi.API_Testing.ApiClient;
import io.github.dhananjaytrivedi.API_Testing.ApiResponse;
import io.github.dhananjaytrivedi.dao.CourseDAOImplementation;
import io.github.dhananjaytrivedi.dao.ReviewDAOImplementation;
import io.github.dhananjaytrivedi.exceptions.DAOException;
import io.github.dhananjaytrivedi.model.Course;
import io.github.dhananjaytrivedi.model.Review;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

// Functional Testing Class

public class APITest {
    public static final String TEST_PORT = "4568";
    public static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection connection;
    private ApiClient client;
    private Gson gson;
    private CourseDAOImplementation courseDAO;
    private ReviewDAOImplementation reviewDAO;

    /* Problems :
            - If we run the main method from here, it will create a database connection and the test operations will be performed on our database. We don't want that.
            - Hence, we need a way to avoid that
     */

    @BeforeClass        // Runs once before everything
    public static void startServer() {
        String[] args = {TEST_PORT, TEST_DATASOURCE};   // We will call our main method with some data so that main method knows the request is for testing
        API.main(args);
    }

    @AfterClass         // Runs once after everything
    public static void stopServer() {
        Spark.stop();
    }

    @Before             // Runs before each test
    public void setUp() throws Exception {

        // Setting up database if not done already using the init.sql file
        Sql2o sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        // Creating the database connection
        connection = sql2o.open();
        gson = new Gson();
        courseDAO = new CourseDAOImplementation(sql2o);
        reviewDAO = new ReviewDAOImplementation(sql2o);

        client = new ApiClient("http://localhost:" + TEST_PORT);
    }

    @After              // Runs after each test
    public void tearDown() throws Exception {
        connection.close();
    }

    // ============================================ COURSE API TESTS =============================================

    @Test
    public void addingCourseReturnsCreatedStatus() {
        Map<String, String> values = new HashMap<>();         // Creating a Key-value pair Map with required values
        values.put("name", "Test");
        values.put("url", "http://test.com");
        ApiResponse response = client.request("POST", "/courses", gson.toJson(values)); // Sending back response
        assertEquals(201, response.getStatus());
    }

    @Test
    public void coursesCanBeAccessedById() throws Exception {
        Course course = newTestCourse();        // Creating a dummy course
        courseDAO.add(course);                  // Adding the course to database

        ApiResponse res = client.request("GET", "/courses/" + course.getId());     // Requesting API to get course
        Course retrievedCourse = gson.fromJson(res.getBody(), Course.class);        // Deserialize the retrieved course
        assertEquals(course, retrievedCourse);  // Checking if equal
    }

    @Test
    public void missingCourseReturnStatusNotFound() {
        ApiResponse response = client.request("GET", "/courses/42");
        assertEquals(404, response.getStatus());
    }

    private Course newTestCourse() {
        return new Course("Test", "http://test.com");

    }

    // ============================================ REVIEW API TESTS =============================================

    @Test
    public void addingReviewGivesCreatedStatus() throws DAOException {

        Course course = newTestCourse();
        courseDAO.add(course);

        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Awesome Course it was");

        ApiResponse response = client.request(
                "POST",                                     // Request Type
                "/courses/" + course.getId() + "/addReview",    // Request URL
                gson.toJson(values)                                 // Return serialized JSON
        );

        assertEquals(201, response.getStatus());
    }

    @Test
    public void addingReviewToUnknownCourseThrowsError() throws DAOException {

        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Awesome Course it was");

        ApiResponse response = client.request(
                "POST",                                     // Request Type
                "/courses/" + 1234 + "/addReview",              // Request URL
                gson.toJson(values)                                 // Return serialized JSON
        );

        assertEquals(500, response.getStatus());
    }

    @Test
    public void multipleReviewsReturnedForTheCourse() throws DAOException {

        Course course = newTestCourse();
        courseDAO.add(course);
        reviewDAO.add(new Review(course.getId(), 5, "Test comment 1"));
        reviewDAO.add(new Review(course.getId(), 4, "Test comment 1"));
        reviewDAO.add(new Review(course.getId(), 1, "Test comment 1"));

        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Awesome Course it was");

        ApiResponse response = client.request(
                "GET",                                      // Request Type
                "/courses/" + course.getId() + "/reviews");     // Request URL

        Review[] reviews = gson.fromJson(response.getBody(), Review[].class);

        assertEquals(3, reviews.length);
    }

}