package com.springPP.journey;

import com.github.javafaker.Faker;
import com.springPP.customer.Customer;
import com.springPP.customer.CustomerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient client;
    @Autowired
    private WebTestClient webTestClient;


    @Test
    void canRegisterCustomer() {
        // create registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@checken123.com";
        int age = faker.number().numberBetween(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );
        // send a pos request
        webTestClient.post()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                name, email, age
        );

        // make sure that customer is present

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        // get customer by id

        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);

        webTestClient.get()
                .uri("/api/v1/customers" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@checken123.com";
        int age = faker.number().numberBetween(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );
        // send a post request
        webTestClient.post()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present


        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        webTestClient.delete()
                .uri("/api/v1/customers" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by id

        webTestClient.get()
                .uri("/api/v1/customers" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create registration request
        Faker faker = new Faker();
        String name = faker.name().fullName();
        String newName = "Random Bob";
        String email = faker.name().lastName() + "-" + UUID.randomUUID() + "@gm.com";
        String newEmail = faker.name().lastName() + "-" + UUID.randomUUID() + "@checken123.com";
        int age = faker.number().numberBetween(1, 100);
        int newAge = faker.number().numberBetween(1, 100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );
        // send a post request
        webTestClient.post()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri("/api/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present


        int id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        //update customer

        Customer customerToUpdate = new Customer();
        customerToUpdate.setName(newName);
        customerToUpdate.setEmail(newEmail);
        customerToUpdate.setAge(newAge);

        webTestClient.put()
                .uri("/api/v1/customers" + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerToUpdate)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        // get customer by id

        Customer updatedCustomer = webTestClient.get()
                .uri("/api/v1/customers" + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer customer = new Customer(
                id, newName, newEmail, newAge
        );

        assertThat(updatedCustomer).isEqualTo(customer);
    }
}
