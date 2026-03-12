package com.example.backend.department;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
//
//    // Create Department
//    @PostMapping
//    public Department createDepartment(@RequestBody Department department) {
//        return departmentService.createDepartment(department);
//    }

    // Get All Departments
    @GetMapping
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }
//
//    // Get Department By Id
//    @GetMapping("/{id}")
//    public Department getDepartmentById(@PathVariable UUID id) {
//        return departmentService.getDepartmentById(id);
//    }
//
//    // Update Department
//    @PutMapping("/{id}")
//    public Department updateDepartment(
//            @PathVariable UUID id,
//            @RequestBody Department department) {
//        return departmentService.updateDepartment(id, department);
//    }
//
//    // Delete Department
//    @DeleteMapping("/{id}")
//    public void deleteDepartment(@PathVariable UUID id) {
//        departmentService.deleteDepartment(id);
//    }
}