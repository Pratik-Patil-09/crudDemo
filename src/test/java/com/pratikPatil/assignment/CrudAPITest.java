package com.pratikPatil.assignment;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class CrudAPITest {

    private static final Logger log = LoggerFactory.getLogger(CrudAPITest.class);

    private static String baseURL = "https://reqres.in/api";
    private static int empId;
    private static String createdName;

    @BeforeClass
    public void setup() {
        log.info("Base URI set to {}", baseURL);
        RestAssured.baseURI = baseURL;
    }

    @Test(priority = 1)
    public void testCreateUser() {
        JSONObject requestBody = new JSONObject();
        requestBody.put("name", "morpheus");
        requestBody.put("empId", "100");
        requestBody.put("job", "leader");

        log.info("Creating user with payload: {}", requestBody);

        Response response = RestAssured.
                given().
                contentType(ContentType.JSON).
                body(requestBody.toString()).
                when().
                post("/users").
                then().
                extract().response();

        Assert.assertEquals(response.getStatusCode(), 201, "Invalid status code");
        empId = response.jsonPath().getInt("empId");
        createdName = response.jsonPath().getString("name");

        log.info("User created. ID: {}, Name: {}", empId, createdName);
    }

    @Test(priority = 2, dependsOnMethods = "testCreateUser")
    public void testGetUserDetails() {
        Response response = RestAssured.given()
                .when()
                .get(String.valueOf(empId))
                .then()
                .extract().response();

        log.info("Getting user details with empId: {} ", empId);
        Assert.assertEquals(response.getStatusCode(), 200, "Failed to fetch users");
    }

    @Test(priority = 3, dependsOnMethods = "testGetUserDetails")
    public void testUpdateUser() {

        JSONObject requestBody = new JSONObject();
        requestBody.put("name", "Pratik Patil");
        requestBody.put("job", "QA Engineer");

        Response response = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody.toString())
                .when()
                .put("/users/" + empId)
                .then()
                .extract().response();

        Assert.assertEquals(response.getStatusCode(), 200, "Status Code mismatch");

        log.info("User updating with empID: {}", empId);
        String updatedName = response.jsonPath().getString("name");
        String updatedJob = response.jsonPath().getString("job");

        Assert.assertEquals(updatedName, "Pratik Patil", "Name update failed");
        Assert.assertEquals(updatedJob, "QA Engineer", "Job update failed");
        log.info("Updated user with payload: {}", response.asPrettyString());

    }
}
