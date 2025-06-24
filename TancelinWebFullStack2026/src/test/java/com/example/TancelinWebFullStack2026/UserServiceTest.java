package com.example.TancelinWebFullStack2026;

import com.example.TancelinWebFullStack2026.exception.DataIntegrityViolationException;
import com.example.TancelinWebFullStack2026.exception.ObjectNotFoundException;
import com.example.TancelinWebFullStack2026.model.User;
import com.example.TancelinWebFullStack2026.repository.UserRepository;
import com.example.TancelinWebFullStack2026.service.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void trivialTrue() {
        assertTrue(true);
    }

    @Test
    void testFindByIdExists() {
        User user = new User(1L, "Test", "password", "test@example.com", "ROLE_USER", true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> found = userService.findById(1L);

        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<User> found = userService.findById(1L);

        assertFalse(found.isPresent());
    }

    @Test
    void testCreateUserSuccess() {
        User user = new User(null, "Test", "password", "test@example.com", "ROLE_USER", true);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean created = userService.createUser(user);

        assertTrue(created);
        verify(userRepository).save(user);
    }

    @Test
    void testCreateUserEmailExists() {
        User user = new User(null, "Test", "password", "test@example.com", "ROLE_USER", true);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        assertThrows(DataIntegrityViolationException.class, () -> userService.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUserSuccess() {
        User existing = new User(1L, "Old Name", "oldPass", "old@example.com", "ROLE_USER", true);
        User update = new User(null, "New Name", "newPass", "new@example.com", "ROLE_USER", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean updated = userService.updateUser(1L, update);

        assertTrue(updated);
        assertEquals("new@example.com", existing.getEmail());
        assertEquals("New Name", existing.getName());
        verify(userRepository).save(existing);
    }

    @Test
    void testUpdateUserEmailExistsThrows() {
        User existing = new User(1L, "Old Name", "oldPass", "old@example.com", "ROLE_USER", true);
        User update = new User(null, "New Name", "newPass", "taken@example.com", "ROLE_USER", true);
        User otherUser = new User(2L, "Other", "pass", "taken@example.com", "ROLE_USER", true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("taken@example.com")).thenReturn(Optional.of(otherUser));

        assertThrows(DataIntegrityViolationException.class, () -> userService.updateUser(1L, update));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testUpdateUserNotFoundThrows() {
        User update = new User(null, "New Name", "newPass", "new@example.com", "ROLE_USER", true);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> userService.updateUser(1L, update));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        boolean deleted = userService.deleteUser(1L);

        assertTrue(deleted);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFoundThrows() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(ObjectNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void testFindAllUsers() {
        List<User> users = List.of(
                new User(1L, "User1", "pass1", "user1@example.com", "ROLE_USER", true),
                new User(2L, "User2", "pass2", "user2@example.com", "ROLE_USER", true)
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> foundUsers = userService.findAll();

        assertEquals(2, foundUsers.size());
    }
}
