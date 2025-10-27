--liquibase formate

--changeset zhv:29-09-2025-insert-users
INSERT INTO users(email,firstname,lastname,password,role)
VALUES
    ('user@gmail.com','First','Firsts','secret','USER'),
    ('admin@gmail.com','Zero','Zeros','secret','ADMIN');


--changeset zhv: tasks-for-user-pending-done
INSERT INTO tasks(title,description,status,user_id)
VALUES
    --pending
    ('Run 100 miles','soon','PENDING',1),
    ('task2','soon','PENDING',1),
    --done
    ('Task tracker project','already done','DONE',1),
    ('secret task','secret','DONE',1);

--changeset zhv: tasks-for-admin-pending-done
INSERT INTO tasks(title,description,status,user_id)
VALUES
    --pending
    ('What task?','unknown','PENDING',2),
    ('Secret task','unknown','PENDING',2),
    --done
    ('Soon winter','i know','DONE',2),
    ('Secret task','secret','DONE',2);
