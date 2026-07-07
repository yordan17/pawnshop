package app.pawnshop.user.service;

import app.pawnshop.user.dto.RegisterForm;
import app.pawnshop.user.exception.UserAlreadyExistsException;
import app.pawnshop.user.exception.UserNotFoundException;
import app.pawnshop.user.model.User;
import app.pawnshop.user.model.UserRole;
import app.pawnshop.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken: " + form.getUsername());
        }
        if (userRepository.existsByEmail(form.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered: " + form.getEmail());
        }

        User user = User.builder()
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .email(form.getEmail())
                .role(UserRole.EMPLOYEE)
                .active(true)
                .build();

        userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
    }
}
