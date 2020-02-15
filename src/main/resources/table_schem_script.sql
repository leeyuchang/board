-- Table作成
create table tbl_access_token (
  id integer primary key,
  access_token varchar(300) not null,
  expires_in varchar(5),
  scope varchar(50),
  token_type varchar(10),
  reg_date timestamp
);

create table tbl_board( 
  bno integer primary key
  , title varchar (200) not null
  , content varchar (2000) not null
  , writer varchar (50) not null
  , replycnt varchar (50)
  , regdate timestamp default current_timestamp
  , updatedate timestamp default current_timestamp
);

create table tbl_attach( 
  file_id varchar (50) primary key
  , file_name varchar (80) not null
  , file_type varchar (20)
  , folder_name varchar (50) not null
  , bno integer references tbl_board(bno)
);

create table tbl_upload_path( 
  id timestamp primary key
  , root varchar (50) not null
  , year varchar (50) not null
  , month varchar (50) not null
  , day varchar (50) not null
);

create table tbl_member( 
  userid varchar(50) not null primary key
  , userpw varchar(100) not null
  , username varchar(100) not null
  , regdate date default current_date
  , updatedate date default current_date
  , enabled char (1) default '0'
);

create table tbl_member_auth( 
  userid varchar(50) not null
  , auth varchar(50) not null
  , constraint fk_member_auth foreign key (userid) references tbl_member(userid)
); 

create table tbl_reply( 
  rno integer primary key
  , bno integer not null
  , reply varchar (1000) not null
  , replyer varchar (50) not null
  , replydate timestamp default current_timestamp
  , updatedate timestamp default current_timestamp
);

-- Sequenc作成
create sequence access_token_seq;
create sequence seq_board;
create sequence seq_reply;