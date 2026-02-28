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

    /**
     * Creates a new person after validating the input and checking for duplicates.
     *
     * @param person the person to be created
     * @return the created person
     * @throws SQLException if a database access error occurs
     * @throws IllegalStateException if a duplicate person exists
     */
    @Override
    public Person create(Person person) throws SQLException {
        validate(person);

        if (existsDuplicate(person, null)) {
            throw new IllegalStateException(
                "Duplicate person (same firstname + lastname + phone + email)"
            );
        }

        String phone = person.getPhoneNumber();
        if (phone != null && !phone.trim().isEmpty() 
                && personDAO.existsByPhone(phone)) {
            throw new IllegalStateException("Phone number already exists.");
        }

        String email = person.getEmailAddress();
        if (email != null && !email.trim().isEmpty() 
                && personDAO.existsByEmail(email)) {
            throw new IllegalStateException("Email already exists.");
        }

        try {
            return personDAO.createPerson(person);
        } catch (SQLException e) {
            String msg = (e.getMessage() == null) ? "" : e.getMessage().toLowerCase();
            if (msg.contains("unique") || msg.contains("constraint")) {
                throw new IllegalStateException(
                    "This contact already exists.", e
                );
            }
            throw e;
        }
    }

    /**
     *  Finds a person with the identifier  
     *  @param id to be checked
     *  @return Optional with the found person or empty if 
     *  @throws SQLException if a database access error occurs
     *  @throws IllegalArgumentException if id is null
     */
    @Override
    public Optional<Person> findById(Integer id) throws SQLException {
        if (id == null || id <= 0) throw new IllegalArgumentException("Invalid id");
        return personDAO.findById(id);
    }

    /**
     * Retrieves all persons from the database.
     * @return a List with all registered persons
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<Person> findAll() throws SQLException {
        return personDAO.findAll();
    }

    /**
     * Updates a person, after validating the input data, ensuring a valid id, and checking no duplicates 
     * @param person the person to be updated
     * @return true if update was successful, false if not
     * @throws IllegalArgumentException if person or id is null 
     * @throws IllegalStateException if a duplicate person exists
     * @throws SQLException if a database access error occurs
     */
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

        return personDAO.updatePerson(person);
    }

    /**
     * Deletes person by the identifier if it is valid
     * @param id person identifier
     * @return true if delete was successful, false if not
     * @throws IllegalArgumentException if person or id is null
     * @throws SQLException if a database access error occurs 
     */
    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id == null || id <= 0) throw new IllegalArgumentException("Invalid id");
        return personDAO.deletePerson(id);
    }

    /**
     * Search for a person by its string name
     * @param searchTerm the string containing the person name
     * @return List with the names of the person with this identifier
     * @throws SQLException if a database access error occurs
     */
    @Override
    public List<Person> searchByName(String searchTerm) throws SQLException {
        String q = (searchTerm == null) ? "" : searchTerm.trim();

        if (q.isEmpty()) {
            return findAll();
        }

        return personDAO.searchPersonByName(q);
    }



    /**
     * Validates all the information a person 
     * validates fields are not blank
     * verifies phone number and email formats
     * @param person to be added
     * @throws IllegalArgumentException if the person is null,
     * required fields are blank, or the email or phone format is invalid
     * 
     */
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

    /**
     * Method to check an existing duplicate of a person 
     * @param candidate the person to be check
     * @param idToIgnore the value of the id input
     * @return true if the person already exist
     * @throws SQLException if a database access error occurs
     */
    private boolean existsDuplicate(Person candidate, Integer idToIgnore) throws SQLException {
        String fn = safeLower(candidate.getFirstname());
        String ln = safeLower(candidate.getLastname());

        return personDAO.findAll().stream().anyMatch(p ->
                (idToIgnore == null || !Objects.equals(p.getIdperson(), idToIgnore))
                        && safeLower(p.getFirstname()).equals(fn)
                        && safeLower(p.getLastname()).equals(ln)
        );
    }
    
    /**
     * check that string is not null, empty, or blank.
     * @param s the string to validate
     * @param message the exception message to use if the validation fails
     * @throws IllegalArgumentException if the string is null, empty, or contains only whitespace
     */
    private static void requireNotBlank(String s, String message) {
        if (!notBlank(s)) throw new IllegalArgumentException(message);
    }

    /**
     * Checks if a string is not null and contains at least one non-whitespace character.
     *
     * @param s the string to check
     * @return true if the string is not null and not blank, false otherwise
     */
    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    /**
     * Returns a short lowercase version of the string.
     * If the string is null, returns an empty string instead.
     * @param s the string to normalize
     * @return a lowercase trimmed string, or an empty string if null
     */
    private static String safeLower(String s) {
        return (s == null) ? "" : s.trim().toLowerCase();
    }
}