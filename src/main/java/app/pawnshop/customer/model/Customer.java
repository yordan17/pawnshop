package app.pawnshop.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 5, max = 20)
    private String phoneNumber;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Size(min = 5, max = 20)
    private String personalId;

    @Column(nullable = false)
    @NotBlank
    @Size(max = 255)
    private String address;

    @Column(nullable = false)
    private boolean active;
}
