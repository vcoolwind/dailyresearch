drop table  phonenum_location ;
CREATE TABLE phonenum_location (
	id  serial8 not null,
	num_segment varchar(16) NOT NULL,
	location varchar(64) NULL,
	inserttimestamp timestamp NULL DEFAULT now(),
	updatetimestamp timestamp NULL DEFAULT now()
)
WITH (
	OIDS=FALSE
) ;
CREATE UNIQUE INDEX phonenum_location_id_idx ON public.phonenum_location (id int8_ops) ;
CREATE INDEX phonenum_location_num_segment_idx ON public.phonenum_location (num_segment text_ops) ;
comment on table  phonenum_location is  '电话号码段(前七位)归属地表' ;
comment on column  phonenum_location.num_segment  is  '电话号码段' ;
comment on column  phonenum_location.location  is  '电话号码段归属地' ;
