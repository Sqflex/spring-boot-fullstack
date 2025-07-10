package com.springPP.customer;

import com.springPP.exception.DuplicateResourceException;
import com.springPP.exception.NoCustomerDataEditedException;
import com.springPP.exception.NoCustomerException;
import com.springPP.exception.ResourceNotFound;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(Integer customerId) {
        return customerDAO.selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFound(
                        "Customer with id %s not found".formatted(customerId)
                ));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        // check if email exists
        if (customerDAO.existsPersonWithEmail(customerRegistrationRequest.email())){
            throw new DuplicateResourceException("Customer with email %s already exists"
                    .formatted(customerRegistrationRequest.email()));
        }
        // add
        Customer customer = new Customer(customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age());
        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomerById(Integer customerId) {
        if (!customerDAO.existsCustomerWithId(customerId)) {
            throw new NoCustomerException("Customer with id %s not found".formatted(customerId));
        }
        customerDAO.deleteCustomerById(customerId);
    }

    public void updateCustomer(Integer customerId, CustomerEditRequest customerEditRequest) {
        Customer customer = getCustomer(customerId);

        boolean changes = false;

        if (customerEditRequest.name() != null && !customerEditRequest.name().equals(customer.getName())) {
            customer.setName(customerEditRequest.name());
            changes = true;
        }

        if (customerEditRequest.email() != null && !customerEditRequest.email().equals(customer.getEmail())) {
            if (customerDAO.existsPersonWithEmail(customerEditRequest.email())) {
                throw new DuplicateResourceException(
                        "email already taken"
                );
            }
            customer.setEmail(customerEditRequest.email());
            changes = true;
        }

        if (customerEditRequest.age() != null && !customerEditRequest.age().equals(customer.getAge())) {
            customer.setAge(customerEditRequest.age());
            changes = true;
        }

        if (!changes) {
            throw new NoCustomerDataEditedException("no data changes found");
        }

        customerDAO.updateCustomer(customer);
    }
}
