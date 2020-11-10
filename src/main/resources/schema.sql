CREATE TABLE `big_local` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `big_local` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `small_local` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `small_local` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `tmp_library` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `big_local` varchar(45) DEFAULT NULL,
  `library_nm` varchar(45) DEFAULT NULL,
  `library_type` varchar(45) DEFAULT NULL,
  `small_local` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13273 DEFAULT CHARSET=utf8;

CREATE TABLE `library` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `library_nm` int(11) DEFAULT NULL,
  `big_local_id` int(11) DEFAULT NULL,
  `libaray_type_id` int(11) DEFAULT NULL,
  `small_local_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `library_type` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `libaray_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
