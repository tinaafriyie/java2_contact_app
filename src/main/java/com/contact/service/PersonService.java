package com.contact.service;

<<<<<<< HEAD
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.contact.model.Person;

public interface PersonService {

    Person create(Person person) throws SQLException;

    Optional<Person> findById(Integer id) throws SQLException;

    List<Person> findAll() throws SQLException;

    boolean update(Person person) throws SQLException;

    boolean delete(Integer id) throws SQLException;

    List<Person> searchByName(String searchTerm) throws SQLException;
=======
import com.contact.model.Person;
import java.sql.SQLException;
import java.util.List;

/**
 * Service interface for person data retrieval.
 * Person 2 provides the implementation; Person 3 connects the UI to this layer.
 */
public interface PersonService {
    List<Person> findAll() throws SQLException;
>>>>>>> origin/main
}
