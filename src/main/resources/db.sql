
drop  table `t_user` IF  EXISTS ;

CREATE TABLE IF NOT EXISTS `t_user` (
    `id` int NOT NULL,
    `name` varchar(32) NOT NULL,
    `age` int NOT NULL,
    PRIMARY KEY (`id`)
    );

insert into t_user(id,name,age) values (1, 'aaa', 8);
insert into t_user(id,name,age) values (2, 'bbb', 9);
insert into t_user(id,name,age) values (3, 'ccc', 10);
