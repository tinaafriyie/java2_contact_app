package com.contact.service;

import com.contact.dao.PersonDAO;
import com.contact.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PersonServiceImplTest {

    private FakePersonDAO fakeDao;
    private PersonService service;

    @BeforeEach
    void setUp() {
        fakeDao = new FakePersonDAO();
        service = new PersonServiceImpl(fakeDao);
    }


    @Test
    void create_shouldThrow_whenFirstnameMissing() {
        Person p = new Person();
        p.setLastname("Doe");
        p.setNickname("JD");

        assertThrows(IllegalArgumentException.class, () -> service.create(p));
    }

  
    @Test
    void create_shouldThrow_whenEmailInvalid() {
        Person p = basePerson("Doe", "John", "JD");
        p.setEmailAddress("not-an-email");

        assertThrows(IllegalArgumentException.class, () -> service.create(p));
    }

    
    @Test
    void create_shouldThrow_whenPhoneInvalid() {
        Person p = basePerson("Doe", "John", "JD");
        p.setPhoneNumber("abc###"); 

        assertThrows(IllegalArgumentException.class, () -> service.create(p));
    }

    @Test
    void create_shouldThrow_whenDuplicateFirstnameLastname() throws SQLException {
        Person p1 = basePerson("Doe", "John", "JD");
        service.create(p1);

        Person p2 = basePerson("Doe", "John", "Johnny");
        assertThrows(IllegalStateException.class, () -> service.create(p2));
    }

    @Test
    void update_shouldThrow_whenIdMissing() {
        Person p = basePerson("Doe", "John", "JD");
        p.setIdperson(null);

        assertThrows(IllegalArgumentException.class, () -> service.update(p));
    }

    @Test
    void delete_shouldThrow_whenIdInvalid() {
        assertThrows(IllegalArgumentException.class, () -> service.delete(0));
        assertThrows(IllegalArgumentException.class, () -> service.delete(-1));
        assertThrows(IllegalArgumentException.class, () -> service.delete(null));
    }

    @Test
    void searchByName_whenEmpty_returnsAll() throws SQLException {
        service.create(basePerson("Doe", "John", "JD"));
        service.create(basePerson("Smith", "Ana", "AS"));

        List<Person> result = service.searchByName("   ");
        assertEquals(2, result.size());
    }

    @Test
    void searchByName_delegatesToDao() throws SQLException {
        service.create(basePerson("Doe", "John", "JD"));
        service.create(basePerson("Smith", "Ana", "AS"));

        List<Person> result = service.searchByName("doe");
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getLastname());
    }

    /**
     * Creates a valid Person instance for testing purposes,
     *with customization of lastname, firstname, and nickname.
     * Other fields are completed  with default valid values.
     *
     * @param last the lastname to set
     * @param first the firstname to set
     * @param nick the nickname to set
     * @return a fully initialized Person instance
     */
    private static Person basePerson(String last, String first, String nick) {
        Person p = new Person();
        p.setLastname(last);
        p.setFirstname(first);
        p.setNickname(nick);
        p.setAddress("Somewhere 123");
        p.setEmailAddress("test@example.com");
        p.setPhoneNumber("+32 123 456 789");
        p.setBirthDate(LocalDate.of(2000, 1, 1));
        return p;
    }

    
    private static class FakePersonDAO implements PersonDAO {

        private final AtomicInteger idGen = new AtomicInteger(1);
        private final Map<Integer, Person> store = new LinkedHashMap<>();

        /**
         * Creates a new person, simulates storing it in memory, and returns a copy of the entity.
         *
         * @param person the person to create
         * @return a copy of the created person with an assigned id
         */
        @Override
        public Person create(Person person) {
            int id = idGen.getAndIncrement();
            Person copy = copyOf(person);
            copy.setIdperson(id);
            store.put(id, copy);
            return copyOf(copy);
        }

        /**
         * Returns person sing the identifier, if there is not a person with that id returns empty optional
         *
         * @param id the identifier of the person to retrieve
         * @return an Optional containing a copy of the found person,
         *         or empty if no person exists with the given id
         */
        
        @Override
        public Optional<Person> findById(Integer id) {
            if (id == null) return Optional.empty();
            Person p = store.get(id);
            return Optional.ofNullable(p == null ? null : copyOf(p));
        }
        
        /**
         * Gets all person from stored in-mempry
         * 
         * @returns a list of copies of all stored persons
         */
        @Override
        public List<Person> findAll() {
            return store.values().stream().map(FakePersonDAO::copyOf).toList();
        }

        
        /**
         * Updates a person in memory store, returns false if person or id are null, or if no person with given id exists
         * 
         * @param person to update 
         * @returns True if update was successful or false if not
         */
        @Override
        public boolean update(Person person) {
            if (person == null || person.getIdperson() == null) return false;
            if (!store.containsKey(person.getIdperson())) return false;
            store.put(person.getIdperson(), copyOf(person));
            return true;
        }

        /**
         * Deletes a person form the in-memory store using the id
         * 
         * @Param id of the person to delete
         * @return true if the delete was successful or false if not
         */
        @Override
        public boolean delete(Integer id) {
            if (id == null) return false;
            return store.remove(id) != null;
        }

        
        /**
         * Searches for a person in the in memory store, if the name, lastname of nickname contains the search term
         * 
         * @param searchTerm the string that we are searching for
         * @return a list of matching persons as defensive copies
         */
        @Override
        public List<Person> searchByName(String searchTerm) {
            String q = (searchTerm == null) ? "" : searchTerm.trim().toLowerCase();
            if (q.isEmpty()) return findAll();

            return store.values().stream()
                    .filter(p -> contains(p.getFirstname(), q)
                              || contains(p.getLastname(), q)
                              || contains(p.getNickname(), q))
                    .map(FakePersonDAO::copyOf)
                    .toList();
        }

        /**
         * Checks whether the string contains the specified 
         * lowercase search term in a case-insensitive manner.
         * @param s the source string to search in 
         * @param qLower the lowercase search term
         * @return true if the source string is not null and contains the search term,
         * false otherwise
         */
        private static boolean contains(String s, String qLower) {
            return s != null && s.toLowerCase().contains(qLower);
        }

        
        /**
         * Creates a defensive copy of the given Person instance, this prevents external code from modifying the internal
         * state of the in-memory store.
         *
         * @param p the person to copy
         * @return a new Person instance with the same field values
         */
        private static Person copyOf(Person p) {
            Person c = new Person();
            c.setIdperson(p.getIdperson());
            c.setLastname(p.getLastname());
            c.setFirstname(p.getFirstname());
            c.setNickname(p.getNickname());
            c.setPhoneNumber(p.getPhoneNumber());
            c.setAddress(p.getAddress());
            c.setEmailAddress(p.getEmailAddress());
            c.setBirthDate(p.getBirthDate());
            return c;
        }
    }
}