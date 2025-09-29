--liquibase formate

--changeset zhv:29-09-2025-insert-users
INSERT INTO users(email,firstname,lastname,password,role)
VALUES
    ('user1@gmail.com','First','Firsts','secret','USER'),
    ('user2@gmail.com','Two','Twos','secret','USER'),
    ('admin1@gmail.com','Zero','Zeros','secret','ADMIN'),
    ('admin2@gmail.com','Sin','Sins','secret','ADMIN')
RETURNING id;


--changeset zhv: tasks-for-user1-pending-done
INSERT INTO tasks(title,description,status,user_id)
VALUES
    --pending
    ('Run 100 miles','soon','PENDING',1),
    ('task2','soon','PENDING',1),
    --done
    ('Task tracker project','already done','DONE',1),
    ('secret task','secret','DONE',1);

--changeset zhv: tasks-for-user2-pending-done
INSERT INTO tasks(title,description,status,user_id)
VALUES
    --pending
    ('Run 10 miles','nice','PENDING',2),
    ('Secret task','nice','PENDING',2),
    --done
    ('Task tracker project','secret','DONE',2),
    ('Secret task','secret','DONE',2);

--changeset zhv: tasks-for-user3-pending-done
INSERT INTO tasks(title,description,status,user_id)
VALUES
    --pending
    ('My task','unknown','PENDING',3),
    ('Secret task','unknown','PENDING',3),
    --done
    ('Buy old book','secret','DONE',3),
    ('Secret task','secret','DONE',3);

--changeset zhv: tasks-for-user4-pending-done
INSERT INTO tasks(title,description,status,user_id)
VALUES
    --pending
    ('What task?','unknown','PENDING',4),
    ('Secret task','unknown','PENDING',4),
    --done
    ('Soon winter','i know','DONE',4),
    ('Secret task','secret','DONE',4);
