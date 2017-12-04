CREATE DATABASE  IF NOT EXISTS `remote` ;
USE `remote`;

DROP TABLE IF EXISTS `account`;
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(200) NOT NULL,
  `password_hash` varchar(200) NOT NULL,
  `password_salt` varchar(200) NOT NULL,
  `admin` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=latin1;

LOCK TABLES `account` WRITE;
INSERT INTO `account` VALUES (0,'user','hPyUJkY1DNXVkQdYbhhiJWh6rANS3KuFVXy3Rqt8tPY=','LAu3qPP3TE2MOzFHI3M4mw==',0),(1,'admin','ROtAKH3G5ygb59kcRvnOIlAm1iwDoJL9XnYi4Ww2ys4=','QkoRHPEbkWnXzvgDRGxuUQ==',1);
UNLOCK TABLES;