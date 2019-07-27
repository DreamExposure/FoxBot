--
-- Host: host    Database: foxbot
-- ------------------------------------------------------
-- Server version	redacted

--
-- Table structure for table `${prefix}guild_settings`
--

CREATE TABLE IF NOT EXISTS `${prefix}guild_settings`
(
    `guild_id`     long         NOT NULL,
    `lang`         varchar(255) NOT NULL,
    `prefix`       varchar(8)   NOT NULL,
    `patron_guild` tinyint(1)   NOT NULL,
    `dev_guild`    tinyint(1)   NOT NULL,
    PRIMARY KEY (`guild_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;

--
-- Table structure for table `${prefix}role_mirror`
--

CREATE TABLE IF NOT EXISTS '${prefix}role_mirror'
(
    'primary_guild_id'     long NOT NULL,
    'primary_guild_role'   long NOT NULL,
    'secondary_guild_id'   long NOT NULL,
    'secondary_guild_role' long NOT NULL,
    PRIMARY KEY ('primary_guild_id')
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1;
