
--�������ݿ�
CREATE TABLE `artists` ( 
    `id` INTEGER(11) NOT NULL AUTO_INCREMENT,
    `name` TEXT COLLATE utf8_general_ci DEFAULT '', 
    `decription` TEXT COLLATE utf8_general_ci DEFAULT '',
    `image_url` TEXT COLLATE utf8_general_ci DEFAULT '',
    `homepage_url` TEXT COLLATE utf8_general_ci DEFAULT '',
    PRIMARY KEY (`id`) ) 
    ENGINE=MyISAM 
    AUTO_INCREMENT=10000 
    COMMENT = '�������ݿ�' 
    CHARACTER SET 'utf8' 
    COLLATE 'utf8_general_ci';
    
--�������ݿ�
CREATE TABLE `songs` ( 
    `id` INTEGER(11) NOT NULL AUTO_INCREMENT,
    `title` TEXT COLLATE utf8_general_ci DEFAULT '',
    `artist` TEXT COLLATE utf8_general_ci DEFAULT '',
    `album` TEXT COLLATE utf8_general_ci DEFAULT '',
    `decription` TEXT COLLATE utf8_general_ci DEFAULT '',
    `image_url` TEXT COLLATE utf8_general_ci DEFAULT '',
    PRIMARY KEY (`id`) ) 
    ENGINE=MyISAM 
    AUTO_INCREMENT=3 
    COMMENT = '�������ݿ�' 
    CHARACTER SET 'utf8'
    COLLATE 'utf8_general_ci';