package com.example.backend.department;

import java.util.List;
import java.util.UUID;

public interface DepartmentService {

    Department createDepartment(Department department);

    List<Department> getAllDepartments();

    Department getDepartmentById(UUID id);

    Department updateDepartment(UUID id, Department updatedDepartment);

    void deleteDepartment(UUID id);
}