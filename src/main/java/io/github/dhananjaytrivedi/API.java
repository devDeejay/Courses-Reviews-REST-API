package io.github.dhananjaytrivedi;

import com.google.gson.Gson;
import io.github.dhananjaytrivedi.dao.CourseDAO;
import io.github.dhananjaytrivedi.dao.CourseDAOImplementation;
import io.github.dhananjaytrivedi.dao.ReviewDAO;
import io.github.dhananjaytrivedi.dao.ReviewDAOImplementation;
import io.github.dhananjaytrivedi.exceptions.APIError;
import io.github.dhananjaytrivedi.exceptions.DAOException;
import io.github.dhananjaytrivedi.model.Course;
import io.github.dhananjaytrivedi.model.Review;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class API {
    public static void main(String[] args) {

        String datasouce = "jdbc:h2:~/reviews.db";

        if (args.length == 2) {                     // For testing Requests
            port(Integer.parseInt(args[0]));        // Set the port
            datasouce = args[1];                    // Set data source
        }
        else if (args.length > 0){
            System.exit(0);
        }

        // Database connection
        Sql2o sql2o = new Sql2o(datasouce + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");

        // Creating a reference variable for our DAOs
        CourseDAO courseDAO = new CourseDAOImplementation(sql2o);
        ReviewDAO reviewDAO = new ReviewDAOImplementation(sql2o);

        // Gson is for Serialization and deserialization
        Gson gson = new Gson();

        // ===================================== COURSE API ===========================================

        // Defining for post request - We do it as follows
        // post(Define Path, Content Type, (req, res) Lambdas, lambda method reference)

        // POST is for incomming messages to get the params and then process it.
        post("/courses", "application/json", (req, res) -> {

            // Creating a course object from incoming JSON Request
            Course course = gson.fromJson(req.body(), Course.class);            // Converting incoming request body JSON to Course object

            // Calling the add method in DAO to store the incoming serialized course object into the database
            courseDAO.add(course);

            // A good practice to set the response header, so that client can know about the status of response / request
            res.status(201);

            // Returning the serialized object
            return course;

        }, gson::toJson);

        // For simple GET Request, here we will return all the courses in the database
        get("/courses", "application/json", (req, res) -> courseDAO.findAll(), gson::toJson);

        // For simple GET Request, here we will return the courses in the database with matching input param
        get("/courses/:id", "application/json", (req, res) -> {
            int id = Integer.parseInt(req.params("id"));                        // Fetching the parameter value
            // TODO : csd - What if this is not found ?
            Course course = courseDAO.findById(id);

            if (course == null) {
                res.status(404);
                throw new APIError(404, "Could not find the course with id " + id);
            }

            res.status(201);
            return course;

        }, gson::toJson);


        // ===================================== REVIEW API ===========================================

        post("/courses/:course_id/addReview", "application/json", (req, res) -> {

            int course_id = Integer.parseInt(req.params("course_id"));
            Review review = gson.fromJson(req.body(), Review.class);
            review.setCourse_id(course_id);

            try {
                reviewDAO.add(review);
            }catch (DAOException e) {
                throw new APIError(500, e.getMessage());
            }

            res.status(201);
            return review;

        }, gson::toJson);

        // Get all reviews from database
        get("/courses/allreviews", "application/json", (req, res) -> reviewDAO.findAll(), gson::toJson);

        // Get all reviews for a particular course
        get("/courses/:course_id/reviews", "application/json", (req, res) -> {
            int courseID = Integer.parseInt(req.params("course_id"));
            return reviewDAO.findByCourseID(courseID);

        }, gson::toJson);

        // ===================================== EXCEPTIONS IN API ===========================================

        // Following code generates the json when exception occurs for the client to see
        exception(APIError.class, (exception, request, response) -> {
            APIError error = (APIError) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", error.getStatus());
            jsonMap.put("errorMessage", error.getMessage());
            response.type("application/json");
            response.status(error.getStatus());
            response.body(gson.toJson(jsonMap));
        });

        // Just adding a filter (DRY Code) for response type so that response type or format is the same for all incoming request
        after ((req, res) -> {
            res.type("application/json");
        });
    }
}
