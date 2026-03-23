-- super admin user (no department)
insert into users
(id, created_at, department_id, email, user_name, password, role, updated_at)
values
(gen_random_uuid(), now(), NULL, 'superadmin@gmail.com', 'Super Admin', '$2a$10$no0H9Y/DQ/9Vd7nsninz6On30kFEMTHEvAu6U.BOphLqF6RTLAvay', 'SUPER_ADMIN', now());