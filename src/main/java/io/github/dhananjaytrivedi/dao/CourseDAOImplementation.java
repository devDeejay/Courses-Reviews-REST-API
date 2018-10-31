package io.github.dhananjaytrivedi.dao;

import io.github.dhananjaytrivedi.exceptions.DAOException;
import io.github.dhananjaytrivedi.model.Course;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

public class CourseDAOImplementation implements CourseDAO{

    // Sql2o is wrap around version of JDBC, removed a lot of boiler plate code which was required with JDBC

    // Creating a common reference variable for Sql2o
    private final Sql2o sql2o;

    // Constructor to initialize the variable for a particular client
    public CourseDAOImplementation(Sql2o sql2o) {                 // Constructor Dependency Injection - SQL2o is an abstraction for JDBC, so all the database which are supported by JDBC should would work
        this.sql2o = sql2o;
    }

    // Add course functionality, takes input course object, serializes it and then stores it in the database
    @Override
    public void add(Course course) throws DAOException {

        String sql = "INSERT INTO courses (name, url) VALUES (:name, :url)";
        try (Connection connection = sql2o.open()) {
            int id = (int) connection.createQuery(sql)  // Create Statement
                    .bind(course)                       // Binding POJO Class which will getName and getURL and populate the query
                    .executeUpdate()                    // Executing the sql statement
                    .getKey();                          // Some value is returned once inserted into database, we will capture that in an int

            course.setId(id);
        }

        catch (Sql2oException exception) {              // Throwing an exception if required
            throw new DAOException(exception, "Problem while adding course " + exception.getMessage());
        }
    }

    // Find all courses functionality, returns a list of all the courses
    @Override
    public List<Course> findAll() {
        try (Connection connection = sql2o.open()) {                          // Try with resources
            return connection.createQuery("SELECT * FROM courses")   // Creating Query
                    .executeAndFetch(Course.class);                            // Executing the query and returning the fetched courses
        }
    }

    // Find a course by input ID, takes int input ID and then searches the database for any matching courses, returns that course if found any
    @Override
    public Course findById(int id) {
        try(Connection connection = sql2o.open()) {
            return connection.createQuery("SELECT * FROM courses WHERE id = :id")   // Create Query
                    .addParameter("id", id)                                            // Adding the input parameter value
                    .executeAndFetchFirst(Course.class);                                     // Executing Query
        }
    }

}
