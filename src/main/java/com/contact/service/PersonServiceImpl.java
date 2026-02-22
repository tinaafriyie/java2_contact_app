package com.contact.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import com.contact.dao.PersonDAO;
import com.contact.model.Person;

public class PersonServiceImpl implements PersonService {

    private final PersonDAO personDAO;


    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+()\\-\\s]{6,20}$");

    public PersonServiceImpl(PersonDAO personDAO) {
        this.personDAO = Objects.requireNonNull(personDAO);
    }

    @Override
    public Person create(Person person) throws SQLException {
        validate(person);


        if (existsDuplicate(person, null)) {
            throw new IllegalStateException("Duplicate person (same firstname + lastname)");
        }

        return personDAO.create(person);
    }

    @Override
    public Optional<Person> findById(Integer id) throws SQLException {
        if (id == null || id <= 0) throw new IllegalArgumentException("Invalid id");
        return personDAO.findById(id);
    }

    @Override
    public List<Person> findAll() throws SQLException {
        return personDAO.findAll();
    }

    @Override
    public boolean update(Person person) throws SQLException {
        if (person == null) throw new IllegalArgumentException("Person cannot be null");

        if (person.getIdperson() == null || person.getIdperson() <= 0) {
            throw new IllegalArgumentException("Person id is required for update");
        }

        validate(person);

        if (existsDuplicate(person, person.getIdperson())) {
            throw new IllegalStateException("Duplicate person (same firstname + lastname)");
        }

        return personDAO.update(person);
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id <= 0) throw new IllegalArgumentException("Invalid id");
        return personDAO.delete(id);
    }

    @Override
    public List<Person> searchByName(String searchTerm) throws SQLException {
        String q = (searchTerm == null) ? "" : searchTerm.trim();

        if (q.isEmpty()) {
            return findAll();
        }

        return personDAO.searchByName(q);
    }



    private void validate(Person p) {
        if (p == null) throw new IllegalArgumentException("Person cannot be null");

        requireNotBlank(p.getLastname(), "Lastname is required");
        requireNotBlank(p.getFirstname(), "Firstname is required");
        requireNotBlank(p.getNickname(), "Nickname is required");

        if (notBlank(p.getEmailAddress()) && !EMAIL_PATTERN.matcher(p.getEmailAddress().trim()).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (notBlank(p.getPhoneNumber()) && !PHONE_PATTERN.matcher(p.getPhoneNumber().trim()).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    private boolean existsDuplicate(Person candidate, Integer idToIgnore) throws SQLException {
        String fn = safeLower(candidate.getFirstname());
        String ln = safeLower(candidate.getLastname());

        return personDAO.findAll().stream().anyMatch(p ->
                (idToIgnore == null || !Objects.equals(p.getIdperson(), idToIgnore))
                        && safeLower(p.getFirstname()).equals(fn)
                        && safeLower(p.getLastname()).equals(ln)
        );
    }

    private static void requireNotBlank(String s, String message) {
        if (!notBlank(s)) throw new IllegalArgumentException(message);
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static String safeLower(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }
}