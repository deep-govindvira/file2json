
create table departments (
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    id uuid not null,
    department_name varchar(255),
    primary key (id)
);

create table exam_boards (
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    id uuid not null,
    board_short_name varchar(50),
    board_city varchar(255),
    board_full_name varchar(255),
    board_state varchar(255),
    primary key (id)
);

create table marksheet_marks (
    subject_obtained_marks integer,
    subject_out_of_marks integer,
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    id uuid not null,
    student_marksheets_id uuid,
    corrected TEXT,
    subject_code varchar(255),
    subject_grade varchar(255),
    subject_name varchar(255),
    subject_obtained_marks_in_words varchar(255),
    primary key (id)
);

create table marksheet_processing_projects (
    processed_marksheets integer,
    processing_failed_marksheets integer,
    total_marksheets integer,
    year integer,
    created_at timestamp(6) not null,
    project_processing_duration bigint,
    updated_at timestamp(6),
    id uuid not null,
    project_creator uuid not null,
    project_description TEXT,
    project_name varchar(255),
    project_status varchar(255) check ((project_status in ('UNPROCESSED','PROCESSING','COMPLETED','PARTIALLY_COMPLETED'))),
    primary key (id)
);

create table marksheet_summary (
    obtained_percentage float(53),
    obtained_percentile float(53),
    total_obtained_marks integer,
    total_out_of_marks integer,
    year_of_passing integer,
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    id varchar(255) not null,
    obtained_grade varchar(255),
    result_status varchar(255),
    primary key (id)
);

create table refresh_tokens (
    revoked boolean not null,
    expiry_date timestamp(6) with time zone,
    id uuid not null,
    user_id uuid,
    token varchar(255) not null unique,
    primary key (id)
);

create table student_marksheets (
    created_at timestamp(6) not null,
    processing_duration bigint,
    processing_started_at timestamp(6),
    updated_at timestamp(6),
    year bigint,
    assigned_to_users_id uuid,
    exam_boards_id uuid,
    id uuid not null,
    marksheet_processing_projects_id uuid not null,
    verified_by_users_id uuid,
    corrected TEXT,
    father_name varchar(255),
    group_name varchar(255),
    marksheet_summary_id varchar(255) unique,
    mother_name varchar(255),
    processing_status varchar(255) check ((processing_status in ('UNPROCESSED','QUEUED','PROCESSING','COMPLETED','FAILED'))),
    school_centre_no varchar(255),
    school_index_no varchar(255),
    seat_no varchar(255),
    student_name varchar(255),
    url varchar(255),
    verification_status varchar(255) check ((verification_status in ('UNVERIFIED','IN_PROGRESS','VERIFIED'))),
    primary key (id)
);

create table users (
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    department_id uuid,
    id uuid not null,
    email varchar(255) unique,
    password varchar(255),
    role varchar(255) check ((role in ('SUPER_ADMIN','ADMIN','VERIFIER'))),
    user_name varchar(255),
    primary key (id)
);

create table users_projects (
    created_at timestamp(6) not null,
    updated_at timestamp(6),
    marksheet_processing_projects_id uuid not null,
    user_id uuid not null,
    primary key (marksheet_processing_projects_id, user_id)
);

create index idx_marksheet_project_id
   on student_marksheets (marksheet_processing_projects_id);

create index idx_marksheet_project_status
   on student_marksheets (marksheet_processing_projects_id, processing_status);

create index idx_users_projects_user_id
   on users_projects (user_id);

create index idx_users_projects_project_id
   on users_projects (marksheet_processing_projects_id);

alter table if exists marksheet_marks
   add constraint FKhhbb5pcpes9qur0ahg5iio88m
   foreign key (student_marksheets_id)
   references student_marksheets;

alter table if exists marksheet_processing_projects
   add constraint FK3b0ae6epqmmoooqto3fusrb93
   foreign key (project_creator)
   references users;

alter table if exists refresh_tokens
   add constraint FK1lih5y2npsf8u5o3vhdb9y0os
   foreign key (user_id)
   references users;

alter table if exists student_marksheets
   add constraint FKdsner30pm2y0q58xkrlgucxgl
   foreign key (assigned_to_users_id)
   references users;

alter table if exists student_marksheets
   add constraint FKfwt4aofmnjelsyxsbr96lifb7
   foreign key (exam_boards_id)
   references exam_boards;

alter table if exists student_marksheets
   add constraint FKrpjpv6gh73ytu69tufuiqle73
   foreign key (marksheet_summary_id)
   references marksheet_summary;

alter table if exists student_marksheets
   add constraint FKih1rhnprba0o2imqtfg86rr8h
   foreign key (marksheet_processing_projects_id)
   references marksheet_processing_projects;

alter table if exists student_marksheets
   add constraint FK8m9vls17btyysuhpdjwsa5ogk
   foreign key (verified_by_users_id)
   references users;

alter table if exists users
   add constraint FKsbg59w8q63i0oo53rlgvlcnjq
   foreign key (department_id)
   references departments;

alter table if exists users_projects
   add constraint FKbq2krui85w0kr70eqra1m4vgd
   foreign key (marksheet_processing_projects_id)
   references marksheet_processing_projects;

alter table if exists users_projects
   add constraint FKen924y69h6d6chaojjgqfaow8
   foreign key (user_id)
   references users;
