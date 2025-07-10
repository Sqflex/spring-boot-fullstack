package com.springPP.customer;

import com.springPP.AbsractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CustomerJDBCDataAccessServiceTest extends AbsractTestContainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomers() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20
        );
        underTest.insertCustomer(customer);

        List<Customer> customers = underTest.selectAllCustomers();
        assertThat(customers).isNotEmpty();
    }

    @Test
    void selectCustomerById() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> actual = underTest.selectCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getEmail()).isEqualTo(email);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        int id = -1;

        var actual = underTest.selectCustomerById(id);

        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        assertThat(id).isGreaterThan(0);
    }

    @Test
    void deleteCustomerById() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        Optional<Customer> actual = underTest.selectCustomerById(id);

        underTest.deleteCustomerById(id);

        assertThat(actual).isNotIn(underTest.selectAllCustomers());
    }

    @Test
    void updateCustomerName() {
        String oldName = faker.name().fullName();
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                oldName,
                email,
                20
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newName = "Bob";
        customer.setName(newName);
        customer.setId(id);

        underTest.updateCustomer(customer);

        Optional<Customer> actualCustomer = underTest.selectCustomerById(id);
//        System.out.println(actualCustomer);
//        System.out.println(oldName);
//        System.out.println(newName);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getName()).isEqualTo(newName);
        });
    }

    @Test
    void updateCustomerEmail() {
        String oldEmail = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                oldEmail,
                20
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(oldEmail))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        String newEmail = "Test@mail.by";
        customer.setEmail(newEmail);
        customer.setId(id);

        underTest.updateCustomer(customer);

        Optional<Customer> actualCustomer = underTest.selectCustomerById(id);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getEmail()).isEqualTo(newEmail);
        });
    }

    @Test
    void updateCustomerAge() {
        int oldAge = 20;
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                oldAge
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        int newAge = -1;
        customer.setAge(newAge);
        customer.setId(id);

        underTest.updateCustomer(customer);

        Optional<Customer> actualCustomer = underTest.selectCustomerById(id);
//        System.out.println(actualCustomer);
//        System.out.println(oldName);
//        System.out.println(newName);

        assertThat(actualCustomer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getAge()).isEqualTo(newAge);
        });
    }

    @Test
    void existsPersonWithEmail() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);
        boolean result = underTest.existsPersonWithEmail(email);

        assertThat(result).isTrue();
    }

    @Test
    void existsCustomerWithId() {
        String email = faker.internet().safeEmailAddress() + " " + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                20
        );

        underTest.insertCustomer(customer);

        int id = underTest.selectAllCustomers().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        boolean result = underTest.existsCustomerWithId(id);

        assertThat(result).isTrue();
    }
}