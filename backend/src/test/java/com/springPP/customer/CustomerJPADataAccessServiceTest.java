package com.springPP.customer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomers() {
        underTest.selectAllCustomers();
        Mockito.verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerById() {
        int id = 1;
        underTest.selectCustomerById(1);
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        String email = "test@test.com";
        Customer customer = new Customer(
                "Bob",
                email,
                20
        );

        underTest.insertCustomer(customer);
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void deleteCustomerById() {
        int id = 1;

        underTest.deleteCustomerById(id);

        Mockito.verify(customerRepository).deleteById(id);
    }

    @Test
    void updateCustomer() {
        String email = "test@test.com";
        Customer customer = new Customer(
                "Alex",
                email,
                20
        );
        customer.setId(1);
        underTest.updateCustomer(customer);

        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        String email = "test@test.com";
        underTest.existsPersonWithEmail(email);

        Mockito.verify(customerRepository).existsCustomerByEmail(email);
    }

    @Test
    void existsCustomerWithId() {
        int id = 1;
        underTest.existsCustomerWithId(id);
        Mockito.verify(customerRepository).existsCustomerById(id);
    }
}