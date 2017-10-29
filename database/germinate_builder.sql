/*
 *  Copyright 2017 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for instance_types
-- ----------------------------
DROP TABLE IF EXISTS `instance_types`;
CREATE TABLE `instance_types` (
  `id`          INT(11)      NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 3
  DEFAULT CHARSET = latin1;

-- ----------------------------
-- Records of instance_types
-- ----------------------------
INSERT INTO `instance_types` VALUES ('1', 'Germinate');
INSERT INTO `instance_types` VALUES ('2', 'Germinate Gatekeeper');

-- ----------------------------
-- Table structure for instances
-- ----------------------------
DROP TABLE IF EXISTS `instances`;
CREATE TABLE `instances` (
  `id`               INT(11)      NOT NULL AUTO_INCREMENT,
  `deploy_name`      VARCHAR(255) NOT NULL,
  `instance_name`    VARCHAR(255) NOT NULL,
  `display_name`     VARCHAR(255) NOT NULL,
  `properties_file`  VARCHAR(255) NOT NULL,
  `url`              VARCHAR(255)          DEFAULT NULL,
  `url_description`  VARCHAR(255)          DEFAULT NULL,
  `browser_opt`      VARCHAR(255) NOT NULL,
  `compile_opt`      TINYINT(1)   NOT NULL DEFAULT '0',
  `google_analytics` TINYINT(1)   NOT NULL DEFAULT '0',
  `tomcat_url`       VARCHAR(255) NOT NULL,
  `tomcat_username`  VARCHAR(255) NOT NULL,
  `tomcat_password`  VARCHAR(255) NOT NULL,
  `enabled`          TINYINT(1)   NOT NULL,
  `type_id`          INT(11)      NOT NULL,
  PRIMARY KEY (`id`),
  KEY `type_id` (`type_id`) USING BTREE,
  CONSTRAINT `instances_ibfk_1` FOREIGN KEY (`type_id`) REFERENCES `instance_types` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = latin1;

-- ----------------------------
-- Table structure for user_permissions
-- ----------------------------
DROP TABLE IF EXISTS `user_permissions`;
CREATE TABLE `user_permissions` (
  `id`          INT(11) NOT NULL AUTO_INCREMENT,
  `user_id`     INT(11) NOT NULL,
  `instance_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `instance_id` (`instance_id`) USING BTREE,
  CONSTRAINT `user_permissions_ibfk_1` FOREIGN KEY (`instance_id`) REFERENCES `instances` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = latin1;

SET FOREIGN_KEY_CHECKS = 1;
