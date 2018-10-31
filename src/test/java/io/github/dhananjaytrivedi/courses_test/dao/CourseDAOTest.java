package io.github.dhananjaytrivedi.courses_test.dao;

import io.github.dhananjaytrivedi.dao.CourseDAOImplementation;
import io.github.dhananjaytrivedi.model.Course;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import static org.junit.Assert.*;

public class CourseDAOTest {

    private CourseDAOImplementation dao;
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        dao = new CourseDAOImplementation(sql2o);

        // Keep connection open throughout the test
        connection = sql2o.open();

    }

    @After
    public void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void addingCourseSetID() throws Exception{
        Course course = newTestCourse();
        int originalCourseID = course.getId();
        dao.add(course);

        assertNotEquals(originalCourseID, course.getId());
    }

    @Test
    public void addedCoursesAreReturnedFromFindAll() throws Exception{
        Course course = newTestCourse();
        dao.add(course);
        assertEquals(1, dao.findAll().size());
    }

    @Test
    public void noCoursesReuturnsEmptyList() {
        assertEquals(0, dao.findAll().size());
    }

    @Test
    public void existingCoursesCanBeFoundById() throws Exception{
        Course course = newTestCourse();
        dao.add(course);

        Course foundCourse = dao.findById(course.getId());
        assertEquals(course, foundCourse);
    }

    private Course newTestCourse() {
        return new Course("Test", "http://test.com");
    }
}