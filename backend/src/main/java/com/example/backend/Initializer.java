package com.example.backend;

import com.example.backend.auth.entity.Role;
import com.example.backend.board.Board;
import com.example.backend.board.BoardService;
import com.example.backend.config.AppProps;
import com.example.backend.department.Department;
import com.example.backend.department.DepartmentService;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.user_project.UserProjectRepository;
import com.example.backend.user_project.UserProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Initializer implements CommandLineRunner {

    private final AppProps props;
    private final BoardService boardService;
    private final UserService userService;
    private final UserProjectService userProjectService;
    private final UserProjectRepository userProjectRepository;
    private final PasswordEncoder encoder;
    private final DepartmentService departmentService;

    @Override
    public void run(String... args) {
        log.info("Properties: {}", props);
        log.info("Apis: {}", "http://localhost:" + props.getServer().getPort() + "/swagger-ui/index.html");
        addBoards();
//        addUsers();

        User user = User.builder()
                .name("Super Admin")
                .email("superadmin@gmail.com")
                .password(encoder.encode("superadmin"))
                .role(Role.SUPER_ADMIN)
                .build();

        userService.save(user);

        departmentService.createDepartment(Department.builder().name("Information Technology").build());
        departmentService.createDepartment(Department.builder().name("Computer Engineering").build());
        departmentService.createDepartment(Department.builder().name("Chemical Engineering").build());
        departmentService.createDepartment(Department.builder().name("Civil Engineering").build());
        departmentService.createDepartment(Department.builder().name("Electronics & Communication Engineering").build());
        departmentService.createDepartment(Department.builder().name("Information Technology").build());
        departmentService.createDepartment(Department.builder().name("Instrumentation & Control Engineering").build());
        departmentService.createDepartment(Department.builder().name("Mechanical Engineering").build());
    }

    void addBoards() {
        List<Board> boardList = new ArrayList<>();
        addGsebBoard(boardList);
        addCbseBoard(boardList);
        addIcseBoard(boardList);
        boardService.addBoards(boardList);
    }

    void addGsebBoard(List<Board> boardList) {
        Board gseb = Board.builder()
                .fullName("Gujarat Secondary and Higher Secondary Education Board")
                .city("Gandhinagar")
                .state("Gujarat")
                .shortName("GSEB")
                .build();
        boardList.add(gseb);
    }

    void addCbseBoard(List<Board> boardList) {
        Board board = Board.builder()
                .fullName("Central Board of Secondary Education")
                .city("New Delhi")
                .state("New Delhi")
                .shortName("CBSE")
                .build();
        boardList.add(board);
    }

    void addIcseBoard(List<Board> boardList) {
        Board board = Board.builder()
                .fullName("Indian Certificate of Secondary Education")
                .city("New Delhi")
                .state("New Delhi")
                .shortName("ICSE")
                .build();
        boardList.add(board);
    }

    void addUsers() {
        List<User> userList = new ArrayList<>();
        addDeep(userList);
        addYash(userList);
        userService.saveAll(userList);
    }

    void addDeep(List<User> userList) {
        User user = User.builder()
                .name("Deep Govindvira")
                .email("deepgovindvira@gmail.com")
                .password(encoder.encode("deep"))
                .role(Role.ADMIN)
                .build();

        userList.add(user);
    }

    void addYash(List<User> userList) {
        User user = User.builder()
                .name("Yash Gokulgandhi")
                .email("yashgokulgandhi6@gmail.com")
                .password(encoder.encode("yash"))
                .role(Role.ADMIN)
                .build();

        userList.add(user);
    }
}

