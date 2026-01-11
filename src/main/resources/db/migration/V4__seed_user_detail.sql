INSERT INTO user_detail (
	user_id, 
	first_name, 
	last_name
) 
select 
	u.id,
	d.first_name,
	d.last_name
from _user u
join (
	VALUES
		('admin', 'adminis', 'trador'),
		('user', 'usu', 'ario')
) as d(username, first_name, last_name)
on u.username = d.username;