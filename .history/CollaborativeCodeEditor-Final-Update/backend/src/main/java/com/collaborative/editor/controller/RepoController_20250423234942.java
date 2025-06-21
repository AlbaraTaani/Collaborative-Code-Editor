package com.collaborative.editor.controller;

import com.collaborative.editor.dto.AddMemberRequest;
import com.collaborative.editor.dto.RepoDTO;
import com.collaborative.editor.dto.CreateRepoRequest;
import com.collaborative.editor.dto.LoginRequest;
import com.collaborative.editor.exception.RepositoryCreationFailedException;
import com.collaborative.editor.exception.UserNotFoundException;
import com.collaborative.editor.model.Repo;
import com.collaborative.editor.model.RepoRole;
import com.collaborative.editor.model.User;
import com.collaborative.editor.repository.UserRepository;
import com.collaborative.editor.service.impls.RepoSecurityService;
import com.collaborative.editor.service.interfaces.FileService;
import com.collaborative.editor.service.interfaces.ProjectService;
import com.collaborative.editor.service.interfaces.RepoService;
import com.collaborative.editor.service.interfaces.UserService;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator.Validity;

import io.jsonwebtoken.lang.Classes;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import org.hibernate.annotations.Synchronize;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.beans.factory.annotation.Autowired;# Effective Java

## Consider Static Factory Methods Instead of Constructors

Static factory methods provide more flexibility and control over object creation compared to constructors. They can return an existing instance, a subclass, or even null, which is not possible with constructors.
### Defense

**Dependency Injection**: Classes such as `JwtUtil`, `SecurityConfig`, and `RoomController` rely on Spring’s framework for instantiation and management of dependencies. This approach avoids the direct use of constructors and aligns with the principle by using a factory method (Spring’s `@Bean` and `@Component` annotations) for object creation.
**Appropriate Use of Constructors**: The code does use constructors, but in appropriate places. For example, constructors in the `Project`, `MessageLog`, `Room`, `RoomMembership`, `User`, and `CodeUpdate` classes are simple and necessary for initializing entities. For more complex object creation, the code follows a builder pattern (e.g., `ProjectDTO.builder()`), which is aligned with the principle.
**Constructor Injection**: The classes (e.g., `SignInController`, `SignUpController`, `UserController`, `FileController`, etc.) use constructor injection for their dependencies (such as `AuthenticationService`, `UserServiceImpl`, and `UserService`). This follows the principles of dependency injection.

### Example: `RepoController.java`

```java
@RestController
@RequestMapping("/api/repos")
public class RoomController {

    private final RepoService repoService;

    @Autowired
    public RepoController(RepoService repoService) {
        this.repoService = repoService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createRepo(@RequestBody RepoDTO repoDTO) {
        repoService.createRepo(repoDTO);
        return ResponseEntity.ok("Repo created successfully");
    }
}
```
The `RoomController` class uses Spring’s `@Autowired` annotation to inject the `RoomService` dependency. This approach avoids the direct use of constructors and aligns with the principle of using static factory methods for object creation.

## Minimize the Accessibility of Classes and Members

Minimizing the accessibility of classes and members is crucial for maintaining encapsulation and hiding the internal representation of a class. This practice enhances the modularity and maintainability of the code.
### Defense
**Encapsulation**: The controllers are appropriately marked as private, such as the `authenticationService` in the `SignInController`. This is in line with the principle of encapsulation and keeping the class’s internal representation hidden.
### Example: `SignInController.java`

```java
@RestController
@RequestMapping("/api/auth")
public class SignInController {

    private final AuthenticationService authenticationService;

    @Autowired
    public SignInController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody LoginRequest loginRequest) {
        authenticationService.authenticate(loginRequest);
        return ResponseEntity.ok("Signed in successfully");
    }
}
```
The `SignInController` class encapsulates the `authenticationService` dependency, marking it as private. This practice hides the internal representation of the class and aligns with the principle of minimizing the accessibility of classes and members.

## Prefer Dependency Injection to Hardwiring Resources

Dependency injection promotes loose coupling and improves testability by allowing the injection of different implementations of a dependency.
### Defense
**Dependency Injection**: The controller classes, like `UserController`, `FileController`, and services like `DockerExecutorService`, all depend on Spring’s `@Autowired` or constructor-based dependency injection.
### Example: `UserController.java`
```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(userDTO);
    }
}
```
The `UserController` class uses Spring’s `@Autowired` annotation to inject the `UserService` dependency. This approach promotes loose coupling and improves testability by allowing the injection of different implementations of the `UserService`.

## Try-with-Resources to try-finally
The `try-with-resources` statement is a cleaner and more concise way to handle resource management, ensuring that resources are closed automatically.
### Defense
**Resource Management**: The `DockerExecutorService` loads a command template via an input stream. However, this could be enhanced using a `try-with-resources` statement instead of relying on implicit closure.
### Example: `DockerExecutorService.java`

```java
@Service
public class DockerExecutorService {

    public String executeCommand(String command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(processBuilder.start().getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute command", e);
        }
    }
}
```
The `DockerExecutorService` class uses a `try-with-resources` statement to manage the `BufferedReader` resource. This ensures that the resource is closed automatically, aligning with the principle of using `try-with-resources` instead of `try-finally`.

## 5. In Public Classes, Use Accessor Methods, Not Public Fields
Using accessor methods (getters and setters) instead of public fields promotes encapsulation and allows for better control over how the fields are accessed and modified.
### Defense
**Accessor Methods**: The entire classes I used private fields with `@Getter` and `@Setter` annotations.

### Example: `User.java`

```java
package com.collaborative.editor.dto;

import lombok.*;
import javax.persistence.*;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;

    @OneToMany(mappedBy = "user")
    private List<RoomMembership> roomMemberships;
}
```
The `User` class uses private fields with `@Getter` and `@Setter` annotations to generate accessor methods. This practice promotes encapsulation and aligns with the principle of using accessor methods instead of public fields.

## Favor Composition Over Inheritance

Composition is generally more flexible and promotes better design than inheritance. It allows for the creation of more modular and reusable code.
### Defense
**Composition**: Entity relationships (e.g., `RoomMembership` to `Room` and `User`) are represented with composition, not inheritance. The relationships are modeled with `@ManyToOne` and `@OneToMany`, which reflects a correct design choice.

### Example: `RoomMembership.java`

```java
package com.collaborative.editor.dto;

import lombok.*;
import javax.persistence.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class RoomMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String role;
}
```
The `RoomMembership` class uses composition to model the relationship between `Room` and `User`. This practice promotes better design and aligns with the principle of favoring composition over inheritance.

## Prefer Interfaces Over Abstract Classes

Interfaces are generally more flexible than abstract classes because they allow for multiple inheritance and provide a clear contract for implementing classes.
### Defense
**Interfaces**: Interfaces are preferable to abstract classes because Java only allows single inheritance. Therefore, I used interfaces in most of the services.
### Example: `UserService.java`

```java
public interface UserService {
    UserDTO getUserById(String userId);
    void createUser(UserDTO userDTO);
    void updateUser(String userId, UserDTO userDTO);
    void deleteUser(String userId);
}
```
The `UserService` interface defines a contract for the `UserServiceImpl` class. This practice promotes flexibility and aligns with the principle of preferring interfaces over abstract classes.

## Use Enums Instead of Int Constants
Enums provide a type-safe way to represent fixed sets of constants, enhancing code clarity and type safety.
### Defense
**Enums**: The `RoomRole` enum in methods like `addMember` and `removeMember` is excellent. It enhances code clarity and type safety.
### Example: `RoomRole.java`

```java
public enum RoomRole {
    ADMIN,
    COLLAPORATOR3,
    VIEWER;
}
```
The `RoomRole` enum represents a fixed set of user roles in the application. This practice enhances code clarity and type safety, aligning with the principle of using enums instead of int constants.
## Know and Use Libraries

Using well-established libraries can save time and effort, providing robust and tested solutions for common problems.

### Defense

- **Libraries**: In this project, I researched libraries to help me and made effective use of them. Some of the libraries used include:
  - **Spring Boot**: For building the API and handling networking aspects.
  - **Lombok**: For implementing builder classes and generating getters and setters.
  - **Jackson**: For parsing and handling JSON processing.
  - **JUnit**: For writing automated tests.
  - **JBCrypt**: For handling password hashing and comparing hashes with plain passwords.

### Example: `pom.xml`

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mindrot</groupId>
        <artifactId>jbcrypt</artifactId>
    </dependency>
</dependencies>
```
**Explanation**:
- The `pom.xml` file includes dependencies for various libraries used in the project. This practice aligns with the principle of knowing and using libraries to save time and effort.

## Prefer Lambdas to Anonymous Classes
Lambdas provide a cleaner and more concise way to implement functional interfaces compared to anonymous classes.

### Defense
**Lambdas**: I used lambdas quite often, especially when working with streams.

### Example: `CodeMetricsDisplay.java`

```java
import java.util.List;
import java.util.stream.Collectors;

public class CodeMetricsDisplay {
    public List<String> getFunctionNames(List<String> codeLines) {
        return codeLines.stream()
                .filter(line -> line.contains("def "))
                .map(line -> line.split("def ")[1].split("\\(")[0])
                .collect(Collectors.toList());
    }
}
```
The `getFunctionNames` method uses a lambda expression to filter and map lines of code. This practice makes the code more concise and expressive, aligning with the principle of preferring lambdas to anonymous classes.

## Document All Exceptions Thrown by Each Method
Documenting exceptions thrown by each method helps users understand the potential errors that can occur and how to handle them.
### Defense
**Centralized Exceptions**: I used them quite often, especially when working with the `CentralizedExceptions`.

### Example: `UserServiceImpl.java`

```java
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }
}
```
The `getUserById` method throws a `UserNotFoundException` if the user is not found. This practice documents the potential exception and aligns with the principle of documenting all exceptions thrown by each method.

## Check Parameters for Validity

Checking parameters for validity helps prevent errors and ensures that the method behaves as expected.

### Defense

**Parameter Validation**: For example, validating that `LoginRequest` and `User` objects are not null and that essential fields are populated could be done before proceeding with further logic.

### Example: `AuthenticationService.java`

```java
@Service
public class AuthenticationService {

    public void authenticate(LoginRequest loginRequest) {
        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
            throw new IllegalArgumentException("Login request is invalid");
        }
        // Authentication logic
    }
}
```
The `authenticate` method checks if the `LoginRequest` object and its essential fields are not null before proceeding with the authentication logic. This practice helps prevent errors and ensures that the method behaves as expected.

## Optimize Method Signatures

Method signatures should be optimized with minimal parameters to enhance readability and maintainability.
### Defense
- **Minimal Parameters**: This is especially true for the methods handling simple requests like `signIn`, `createAccount`, and `runCode`.
### Example: `UserController.java`

```java
@PostMapping("/signin")
public ResponseEntity<String> signIn(@RequestBody LoginRequest loginRequest) {
    authenticationService.authenticate(loginRequest);
    return ResponseEntity.ok("Signed in successfully");
}
```

**Explanation**:
- The `signIn` method has a minimal parameter list, enhancing readability and maintainability. This practice aligns with the principle of optimizing method signatures.
## Synchronize Access to Shared Mutable Data

Synchronizing access to shared mutable data ensures thread safety and prevents concurrent modification exceptions.
### Defense
**Synchronized Access**: Using the `fileLocks` map and the `synchronized` keyword in methods like `pushFileContent` and `mergeFileContent`.
### Example: `FileServiceImpl.java`

```java
@Service
public class FileServiceImpl implements FileService {

    private final Map<String, Object> fileLocks = new ConcurrentHashMap<>();

    @Override
    public void pushFileContent(String fileId, String content) {
        synchronized (getLock(fileId)) {
            // Push file content logic
        }
    }

    @Override
    public void mergeFileContent(String fileId, String content) {
        synchronized (getLock(fileId)) {
            // Merge file content logic
        }
    }

    private Object getLock(String fileId) {
        return fileLocks.computeIfAbsent(fileId, k -> new Object());
    }
}
```
The `pushFileContent` and `mergeFileContent` methods use the `synchronized` keyword to ensure thread-safe access to shared mutable data. This practice prevents concurrent modification exceptions and aligns with the principle of synchronizing access to shared mutable data.
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.lang.classfile.Interfaces;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.*;
import javax.persistence.*;
import javax.swing.Spring;

