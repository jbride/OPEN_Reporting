/* mysql -u gpte_collection -p gpte_collection < create-tables.sql */

USE gpte_collection;

drop table if exists git_activity;
drop table if exists git_name_map;

CREATE TABLE `git_activity` (
    `repo_name` varchar(64) NOT NULL,
    `commit_count` int(11) NOT NULL,
    `contributor_email` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/* Add index on contributor_name so as to query by this field across all git repos */
ALTER TABLE `git_activity`
ADD INDEX `GIT_ACTIVITY_CONTRIBUTOR_idx` (`contributor_email` ASC);

CREATE TABLE `git_name_map` (
    `git_email` varchar(32) NOT NULL,
    `coninical_email` varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

