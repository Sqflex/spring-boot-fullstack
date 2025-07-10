package com.springPP.customer;

import com.springPP.exception.DuplicateResourceException;
import com.springPP.exception.NoCustomerDataEditedException;
import com.springPP.exception.NoCustomerException;
import com.springPP.exception.ResourceNotFound;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        underTest.getAllCustomers();
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        int id = 1;
        Customer customer = new Customer(
                id,
                "Alex",
                "test@mail.by",
                20
        );
        Mockito.when(customerDAO.selectCustomerById(id))
                .thenReturn(Optional.of(customer));
        Customer actual = underTest.getCustomer(id);
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void WillThrowWhenGetCustomerNotFound() {
        int id = -1;
        Mockito.when(customerDAO.selectCustomerById(id))
                .thenReturn(Optional.empty());
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage(
                        "Customer with id " + id + " not found"
                );
    }

    @Test
    void addCustomer() {

        String email = "test@mail.by";

        when(customerDAO.existsPersonWithEmail(email))
                .thenReturn(false);
        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(
                "Alex", email, 20
        );
        underTest.addCustomer(customerRegistrationRequest);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).insertCustomer(customerCaptor.capture());

        Customer capturedCustomer = customerCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).
                isEqualTo(customerRegistrationRequest.name());
        assertThat(capturedCustomer.getEmail()).
                isEqualTo(customerRegistrationRequest.email());
        assertThat(capturedCustomer.getAge())
                .isEqualTo(customerRegistrationRequest.age());
    }

    @Test
    void willThrowWhenEmailExistWhileAddingCustomer() {

        String email = "test@mail.by";

        when(customerDAO.existsPersonWithEmail(email))
                .thenReturn(true);

        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(
                        "Alex", email, 20
                );

        assertThatThrownBy(() -> underTest.addCustomer(customerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Customer with email " + email + " already exists");

        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        int id = 1;

        when(customerDAO.existsCustomerWithId(id))
                .thenReturn(true);

        underTest.deleteCustomerById(id);
        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenIdDoesNotExistInDeleteCustomerById() {
        int id = -1;

        when(customerDAO.existsCustomerWithId(id))
                .thenReturn(false);

        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(NoCustomerException.class)
                .hasMessage("Customer with id " + id + " not found");

        verify(customerDAO, never()).deleteCustomerById(any());
    }

    @Test
    void canUpdateAllCustomerProperties() {
        int id = 1;
        String oldName = "Alex";
        String newName = "Bob";
        String oldEmail = "test@mail.by";
        String newEmail = "111@bb.us";
        int oldAge = 20;
        int newAge = 99;

        when(customerDAO.existsPersonWithEmail(oldEmail))
                .thenReturn(false);

        CustomerRegistrationRequest customerRegistrationRequest =
                new CustomerRegistrationRequest(
                        oldName, oldEmail, oldAge
                );

        underTest.addCustomer(customerRegistrationRequest);

        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).insertCustomer(customerCaptor.capture());

        Customer capturedCustomer = customerCaptor.getValue();

        Mockito.when(customerDAO.selectCustomerById(id))
                .thenReturn(Optional.of(capturedCustomer));

        CustomerEditRequest newCustomerRegistrationRequest = new CustomerEditRequest(
                newName, newEmail, newAge
        );

        underTest.updateCustomer(id, newCustomerRegistrationRequest);

        ArgumentCaptor<Customer> updatedCustomerCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomer(updatedCustomerCaptor.capture());

        Customer updatedCustomer = updatedCustomerCaptor.getValue();

        assertThat(updatedCustomer.getId()).isNull();
        assertThat(updatedCustomer.getName()).
                isEqualTo(newName);
        assertThat(updatedCustomer.getEmail()).
                isEqualTo(newEmail);
        assertThat(updatedCustomer.getAge())
                .isEqualTo(newAge);
    }

    @Test
    void updateCustomerName() {
        int id = 1;
        String oldName = "Alex";
        String newName = "Bob";
        String email = "test@mail.by";

        Customer customer = new Customer(
                oldName, email, 20
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerEditRequest newCustomerRegistrationRequest = new CustomerEditRequest(
                newName, null, null
        );

        underTest.updateCustomer(id, newCustomerRegistrationRequest);

        ArgumentCaptor<Customer> updatedCustomerCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDAO).updateCustomer(updatedCustomerCaptor.capture());

        Customer updatedCustomer = updatedCustomerCaptor.getValue();

        assertThat(updatedCustomer.getId()).isNull();
        assertThat(updatedCustomer.getName()).
                isEqualTo(newName);
        assertThat(updatedCustomer.getEmail()).
                isEqualTo(customer.getEmail());
        assertThat(updatedCustomer.getAge())
                .isEqualTo(customer.getAge());
    }

    @Test
    void updateCustomerEmail() {
        int id = 1;
        String oldName = "Alex";
        String newName = "Bob";
        String email = "test@mail.by";
        String newEmail = "bbb@i.co";

        Customer customer = new Customer(
                oldName, email, 20
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerEditRequest newCustomerRegistrationRequest = new CustomerEditRequest(
                null, newEmail, null
        );

        underTest.updateCustomer(id, newCustomerRegistrationRequest);

        ArgumentCaptor<Customer> updatedCustomerCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDAO).updateCustomer(updatedCustomerCaptor.capture());

        Customer updatedCustomer = updatedCustomerCaptor.getValue();

        assertThat(updatedCustomer.getId()).isNull();
        assertThat(updatedCustomer.getName()).
                isEqualTo(customer.getName());
        assertThat(updatedCustomer.getEmail()).
                isEqualTo(newEmail);
        assertThat(updatedCustomer.getAge())
                .isEqualTo(customer.getAge());
    }

    @Test
    void updateCustomerAge() {
        int id = 1;
        String name = "Alex";
        String email = "test@mail.by";
        int age = 20;
        int newAge = 99;

        Customer customer = new Customer(
                name, email, age
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerEditRequest newCustomerRegistrationRequest = new CustomerEditRequest(
                null, null, newAge
        );

        underTest.updateCustomer(id, newCustomerRegistrationRequest);

        ArgumentCaptor<Customer> updatedCustomerCaptor = ArgumentCaptor.forClass(
                Customer.class
        );

        verify(customerDAO).updateCustomer(updatedCustomerCaptor.capture());

        Customer updatedCustomer = updatedCustomerCaptor.getValue();

        assertThat(updatedCustomer.getId()).isNull();
        assertThat(updatedCustomer.getName()).
                isEqualTo(customer.getName());
        assertThat(updatedCustomer.getEmail()).
                isEqualTo(customer.getEmail());
        assertThat(updatedCustomer.getAge())
                .isEqualTo(newAge);
    }

    @Test
    void willThrowWhenTryingUpdatingEmailAlreadyTaken() {
        int id = 1;
        String oldName = "Alex";
        String email = "test@mail.by";
        String newEmail = "nomore@k.us";

        Customer customer = new Customer(
                id, oldName, email, 20
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerEditRequest newCustomerRegistrationRequest = new CustomerEditRequest(
                null, newEmail, null
        );

        when(customerDAO.existsPersonWithEmail(newEmail)).thenReturn(true);

        assertThatThrownBy(() -> underTest.updateCustomer(id, newCustomerRegistrationRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email already taken");

        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerHasNoChanges() {
        int id = 1;
        String oldName = "Alex";
        String oldEmail = "test@mail.by";
        int oldAge = 20;

        Customer customer = new Customer(
                id, oldName, oldEmail, oldAge
        );

        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        CustomerEditRequest newCustomerRegistrationRequest = new CustomerEditRequest(
                customer.getName(), customer.getEmail(), customer.getAge()
        );

        assertThatThrownBy(() -> underTest.updateCustomer(id, newCustomerRegistrationRequest))
                .isInstanceOf(NoCustomerDataEditedException.class)
                .hasMessage("no data changes found");

        verify(customerDAO, never()).updateCustomer(any());
    }

}