/*
 Navicat Premium Data Transfer

 Source Server         : 阿里云
 Source Server Type    : MySQL
 Source Server Version : 50743
 Source Host           : 120.26.44.124:3306
 Source Schema         : wechat

 Target Server Type    : MySQL
 Target Server Version : 50743
 File Encoding         : 65001

 Date: 05/09/2023 10:25:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for push_message
-- ----------------------------
DROP TABLE IF EXISTS `push_message`;
CREATE TABLE `push_message`  (
  `ID` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `REGISTER_WXID` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '注册者微信id',
  `ROOM_WXID` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '房间微信id',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of push_message
-- ----------------------------
INSERT INTO `push_message` VALUES (1, 'wxid_bunp0hfj6n1p22', '19456686024@chatroom');
INSERT INTO `push_message` VALUES (3, 'wxid_bunp0hfj6n1p22', '19470967495@chatroom');

SET FOREIGN_KEY_CHECKS = 1;
