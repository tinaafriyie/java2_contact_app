package com.contact.service;

import com.contact.dao.PersonDAOImpl;
import com.contact.model.Person;
import java.sql.SQLException;
import java.util.List;

/**
 * Default implementation delegating to PersonDAO.
 * Person 2 can replace this with their full service implementation.
 */
public class PersonServiceImpl implements PersonService {
    private final PersonDAOImpl dao = new PersonDAOImpl();

    @Override
    public List<Person> findAll() throws SQLException {
        return dao.findAll();
    }
}
