package com.springPP.customer;

import com.springPP.AbsractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbsractTestContainers {

    @Autowired
    private CustomerRepository underTestJPA;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTestJPA.deleteAll();
    }

    @Test
    void existsCustomerByEmail() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20
        );

        underTestJPA.save(customer);

        boolean result = underTestJPA.existsCustomerByEmail(email);

        assertThat(result).isTrue();
    }

    @Test
    void existsCustomerByEmailFailsWhenEmailDoesNotExist() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();

        boolean result = underTestJPA.existsCustomerByEmail(email);

        assertThat(result).isFalse();
    }

    @Test
    void existsCustomerById() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20
        );

        underTestJPA.save(customer);

        int id = underTestJPA.findAll().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean actual = underTestJPA.existsCustomerById(id);
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerByIdWhenIdDoesNotExist() {

        int id = -1;

        var actual = underTestJPA.existsCustomerById(id);
        assertThat(actual).isFalse();
    }
}