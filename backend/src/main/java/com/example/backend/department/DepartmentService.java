package com.example.backend.department;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    // Create Department
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(UUID id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public Department updateDepartment(UUID id, Department updatedDepartment) {
        Department department = getDepartmentById(id);
        department.setName(updatedDepartment.getName());
        return departmentRepository.save(department);
    }

    public void deleteDepartment(UUID id) {
        departmentRepository.deleteById(id);
    }
}