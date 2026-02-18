package main.java.com.contact.dao;

import main.java.com.contact.model.Person;
import main.java.com.contact.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonDAOImpl implements PersonDAO {
    private final DatabaseConnection dbConnection;

    public PersonDAOImpl() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Person create(Person person) throws SQLException {
        String sql = "INSERT INTO person (lastname, firstname, nickname, phone_number, address, email_address, birth_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, person.getLastname());
            pstmt.setString(2, person.getFirstname());
            pstmt.setString(3, person.getNickname());
            pstmt.setString(4, person.getPhoneNumber());
            pstmt.setString(5, person.getAddress());
            pstmt.setString(6, person.getEmailAddress());
            pstmt.setDate(7, person.getBirthDate() != null ? Date.valueOf(person.getBirthDate()) : null);
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    person.setIdperson(generatedKeys.getInt(1));
                }
            }
            System.out.println("✅ Person created: " + person.getFullName());
            return person;
        }
    }

    @Override
    public Optional<Person> findById(Integer id) throws SQLException {
        String sql = "SELECT * FROM person WHERE idperson = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToPerson(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Person> findAll() throws SQLException {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM person ORDER BY lastname, firstname";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                persons.add(mapResultSetToPerson(rs));
            }
            System.out.println("✅ Retrieved " + persons.size() + " persons");
        }
        return persons;
    }

    @Override
    public boolean update(Person person) throws SQLException {
        String sql = "UPDATE person SET lastname=?, firstname=?, nickname=?, phone_number=?, address=?, email_address=?, birth_date=? WHERE idperson=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, person.getLastname());
            pstmt.setString(2, person.getFirstname());
            pstmt.setString(3, person.getNickname());
            pstmt.setString(4, person.getPhoneNumber());
            pstmt.setString(5, person.getAddress());
            pstmt.setString(6, person.getEmailAddress());
            pstmt.setDate(7, person.getBirthDate() != null ? Date.valueOf(person.getBirthDate()) : null);
            pstmt.setInt(8, person.getIdperson());
            
            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "✅ Person updated" : "⚠️ Person not found");
            return rows > 0;
        }
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        String sql = "DELETE FROM person WHERE idperson = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            System.out.println(rows > 0 ? "✅ Person deleted" : "⚠️ Person not found");
            return rows > 0;
        }
    }

    @Override
    public List<Person> searchByName(String searchTerm) throws SQLException {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM person WHERE LOWER(firstname) LIKE LOWER(?) OR LOWER(lastname) LIKE LOWER(?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    persons.add(mapResultSetToPerson(rs));
                }
            }
            System.out.println("✅ Found " + persons.size() + " matching persons");
        }
        return persons;
    }

    private Person mapResultSetToPerson(ResultSet rs) throws SQLException {
        Person person = new Person();
        person.setIdperson(rs.getInt("idperson"));
        person.setLastname(rs.getString("lastname"));
        person.setFirstname(rs.getString("firstname"));
        person.setNickname(rs.getString("nickname"));
        person.setPhoneNumber(rs.getString("phone_number"));
        person.setAddress(rs.getString("address"));
        person.setEmailAddress(rs.getString("email_address"));
        
        Date birthDate = rs.getDate("birth_date");
        if (birthDate != null) {
            person.setBirthDate(birthDate.toLocalDate());
        }
        return person;
    }
}