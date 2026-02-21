package com.contact.service;

import com.contact.model.Person;
import java.sql.SQLException;
import java.util.List;

/**
 * Service interface for person data retrieval.
 * Person 2 provides the implementation; Person 3 connects the UI to this layer.
 */
public interface PersonService {
    List<Person> findAll() throws SQLException;
}
