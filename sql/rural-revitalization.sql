/*
 Navicat Premium Dump SQL

 Source Server         : javaweb项目作业
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3308
 Source Schema         : rural-revitalization

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 19/11/2025 23:18:58
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for r_apply
-- ----------------------------
DROP TABLE IF EXISTS `r_apply`;
CREATE TABLE `r_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID（主键）',
  `user_id` int NOT NULL COMMENT '申请人ID（关联sys_user表的id）',
  `apply_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间（自动填充当前时间）',
  `auditor_id` int NULL DEFAULT NULL COMMENT '审核人ID（关联sys_user表的id，管理员ID）',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_status` tinyint NOT NULL DEFAULT 0 COMMENT '审核状态：0-待审核，1-审核通过，2-审核驳回',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '申请人ID索引，便于查询个人申请记录',
  INDEX `idx_audit_status`(`audit_status` ASC) USING BTREE COMMENT '审核状态索引，便于管理员筛选'
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商家申请记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_apply
-- ----------------------------
INSERT INTO `r_apply` VALUES (3, 3, '2025-11-19 14:52:31', 0, '2025-11-19 14:52:31', 0);

-- ----------------------------
-- Table structure for r_banner
-- ----------------------------
DROP TABLE IF EXISTS `r_banner`;
CREATE TABLE `r_banner`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID（自增）',
  `banner_img` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '轮播图图片地址',
  `create_user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_banner
-- ----------------------------
INSERT INTO `r_banner` VALUES (1, 'https://picb4.photophoto.cn/09/181/09181644_1.jpg', '管理员', '2025-01-22 18:40:54');
INSERT INTO `r_banner` VALUES (2, 'https://img95.699pic.com/photo/60085/1883.jpg_wh860.jpg', '管理员', '2025-01-22 18:41:46');
INSERT INTO `r_banner` VALUES (3, 'https://img95.699pic.com/photo/50768/8621.jpg_wh860.jpg', '管理员', '2025-01-22 18:42:37');

-- ----------------------------
-- Table structure for r_cart
-- ----------------------------
DROP TABLE IF EXISTS `r_cart`;
CREATE TABLE `r_cart`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '购物车记录ID（主键）',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品类型（如水果类、蔬菜类）',
  `price` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品单价（此处用VARCHAR，实际建议用DECIMAL(10,2)）',
  `unit` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品单位（如公斤、箱）',
  `user_id` int NOT NULL COMMENT '关联用户ID（外键）',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建时间（建议改用DATETIME类型）',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态（0：未支付/有效）',
  `update_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新时间（建议改用DATETIME类型）',
  `update_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `goods_id` int NOT NULL COMMENT '关联商品ID（外键）',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `count` int NOT NULL COMMENT '商品数量',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_goods_id`(`goods_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_cart
-- ----------------------------
INSERT INTO `r_cart` VALUES (27, '散养土鸡蛋', '蛋类', '1.8', '个', 3, '2025-11-18 12:05:16.266', '李四', 0, NULL, NULL, 13, NULL, 4, 'http://localhost:8080/file/api/f032df5b-b59d-43fe-9f2a-9d250548f2ad.jpg');
INSERT INTO `r_cart` VALUES (29, '山东烟台红富士苹果', '水果类', '8.99', '斤', 1, '2025-11-19 09:59:22.05', '1', 0, NULL, NULL, 1, NULL, 2, 'http://localhost:8080/file/api/8fa1bb97-079f-423b-8b29-e2f0aed5c585.jpg');

-- ----------------------------
-- Table structure for r_categories
-- ----------------------------
DROP TABLE IF EXISTS `r_categories`;
CREATE TABLE `r_categories`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '类别ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '是否删除，0否1是',
  `create_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建时间',
  `update_time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_categories
-- ----------------------------
INSERT INTO `r_categories` VALUES (1, '水果类', 0, '2025-11-14 21:57:36', '2025-11-14 21:57:36');
INSERT INTO `r_categories` VALUES (2, '蔬菜类', 0, '2025-11-14 21:57:36', '2025-11-14 21:57:36');
INSERT INTO `r_categories` VALUES (3, '豆类', 0, '2025-11-14 21:57:36', '2025-11-14 21:57:36');
INSERT INTO `r_categories` VALUES (4, '肉类', 0, '2025-11-14 21:57:36', '2025-11-14 21:57:36');
INSERT INTO `r_categories` VALUES (5, '蛋类', 0, '2025-11-14 21:57:36', '2025-11-14 21:57:36');

-- ----------------------------
-- Table structure for r_notice
-- ----------------------------
DROP TABLE IF EXISTS `r_notice`;
CREATE TABLE `r_notice`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标题',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '内容',
  `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布时间',
  `open` tinyint(1) NULL DEFAULT NULL COMMENT '是否公开',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_notice
-- ----------------------------
INSERT INTO `r_notice` VALUES (1, '【重要通知】平台水果上新活动', '本周六起将上架云南高山沃柑、广西砂糖橘，限时3天8折优惠，支持产地直发，坏果包赔！', '2025-11-14 10:30:00', 1, '2025-11-18 23:28:01');
INSERT INTO `r_notice` VALUES (2, '【内部通知】11月20日系统维护通知', '11月20日00:00-02:00平台将进行服务器升级，期间下单、支付功能暂停，敬请谅解', '2025-11-15 18:45:00', 1, '2025-11-18 23:28:06');
INSERT INTO `r_notice` VALUES (3, '【活动提醒】双十一优惠活动倒计时16天', '双十一水果满减活动仅剩2天，满199减50、满399减120，点击首页banner即可参与，库存有限先到先得', '2025-11-16 15:20:00', 1, '2025-11-18 23:28:12');
INSERT INTO `r_notice` VALUES (5, '好消息好消息特价买', '123456', '2025-11-19 00:58:44', 1, '2025-11-19 00:58:45');
INSERT INTO `r_notice` VALUES (6, '【重大信息】停业', '32523524', '2025-11-19 01:16:59', 1, '2025-11-19 01:16:59');

-- ----------------------------
-- Table structure for r_order
-- ----------------------------
DROP TABLE IF EXISTS `r_order`;
CREATE TABLE `r_order`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '订单ID（主键）',
  `order_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号（唯一标识，如：ORDER_20251116001）',
  `product_id` int NOT NULL COMMENT '关联产品ID（关联 r_products 表的 id 字段）',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品名称（与 r_products.name 一致）',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品图片（与 r_products.image_url 一致）',
  `count` int NOT NULL COMMENT '购买数量',
  `price` decimal(10, 2) NOT NULL COMMENT '产品单价（与 r_products.price 一致，避免浮点数精度问题）',
  `unit` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '产品单位（与 r_products.unit 一致，如：斤、盒）',
  `link_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `link_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货地址',
  `link_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '待支付' COMMENT '订单状态：待支付/已支付/已发货/已完成/已取消',
  `create_time` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建时间',
  `user_id` int NOT NULL COMMENT '下单用户ID（关联 sys_user 表的 user_id 字段）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_id`(`order_id` ASC) USING BTREE COMMENT '订单编号唯一',
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE COMMENT '用户ID索引，优化查询',
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE COMMENT '产品ID索引，优化查询',
  CONSTRAINT `fk_order_product` FOREIGN KEY (`product_id`) REFERENCES `r_products` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_order_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_order
-- ----------------------------
INSERT INTO `r_order` VALUES (1, 'ORDER_20251116001', 1, '魏县雪梨', 'http://localhost:8080/file/api/sydney.jpg', 2, 8.99, '斤', '张三', '上海市浦东新区张江高科技园区博云路2号', '18612345678', '1', '2025-11-15 22:27:06', 1);
INSERT INTO `r_order` VALUES (2, 'ORDER_20251116002', 3, '烟台苹果', 'http://localhost:8080/file/api/apple.jpg', 3, 12.99, '斤', '张三', '上海市黄浦区南京东路353号', '18612345678', '2', '2025-11-15 22:27:06', 1);
INSERT INTO `r_order` VALUES (3, 'ORDER_20251116003', 5, '沾化冬枣', 'http://localhost:8080/file/api/jujube.jpg', 5, 15.99, '斤', '张三', '上海市静安区静安寺街道南京西路1266号', '18612345678', '0', '2025-11-15 22:27:06', 1);
INSERT INTO `r_order` VALUES (4, 'ORDER_20251116004', 2, '寿光西红柿', 'http://localhost:8080/file/api/tomato.jpg', 4, 3.50, '斤', '张三', '上海市徐汇区徐家汇街道漕溪北路88号', '18612345678', '1', '2025-11-15 22:27:06', 1);
INSERT INTO `r_order` VALUES (5, 'ORDER_20251116005', 7, '宁夏枸杞', 'http://localhost:8080/file/api/wolfberry.jpg', 2, 29.99, '盒', '张三', '上海市闵行区莘庄镇莘松路958号', '18612345678', '2', '2025-11-15 22:27:06', 1);
INSERT INTO `r_order` VALUES (6, 'ORDER_20251116006', 2, '寿光西红柿', 'http://localhost:8080/file/api/tomato.jpg', 6, 3.50, '斤', '李四', '北京市朝阳区建国路88号', '13887654321', '0', '2025-11-15 22:27:06', 2);
INSERT INTO `r_order` VALUES (7, 'ORDER_20251116007', 4, '山东黄瓜', 'http://localhost:8080/file/api/cucumber.jpg', 3, 4.99, '斤', '李四', '北京市海淀区中关村大街1号', '13887654321', '2', '2025-11-15 22:27:06', 2);
INSERT INTO `r_order` VALUES (8, 'ORDER_20251116008', 6, '陕西猕猴桃', 'http://localhost:8080/file/api/kiwi.jpg', 4, 9.99, '斤', '李四', '北京市东城区王府井大街1号', '13887654321', '0', '2025-11-15 22:27:06', 2);
INSERT INTO `r_order` VALUES (9, 'ORDER_20251116009', 1, '魏县雪梨', 'http://localhost:8080/file/api/sydney.jpg', 5, 8.99, '斤', '李四', '北京市西城区金融大街15号', '13887654321', '2', '2025-11-15 22:27:06', 2);
INSERT INTO `r_order` VALUES (10, 'ORDER_20251116010', 3, '烟台苹果', 'http://localhost:8080/file/api/apple.jpg', 2, 12.99, '斤', '王五', '广州市天河区天河路385号', '13998765432', '2', '2025-11-15 22:27:06', 3);
INSERT INTO `r_order` VALUES (11, 'ORDER_20251116011', 5, '沾化冬枣', 'http://localhost:8080/file/api/jujube.jpg', 3, 15.99, '斤', '王五', '广州市越秀区北京路328号', '13998765432', '1', '2025-11-15 22:27:06', 3);
INSERT INTO `r_order` VALUES (12, '32025111723093316', 14, '柴鸡鸭蛋', 'http://localhost:8080/file/api/c5605595-c96f-49b5-803a-8518e2f3f89d.jpg', 2, 2.19, '个', '呃呃呃', '天津市市辖区和平区12223', '136454928965', '0', '2025-11-17 23:09:44', 3);
INSERT INTO `r_order` VALUES (14, 'ORDER_20251116014', 6, '陕西猕猴桃', 'http://localhost:8080/file/api/kiwi.jpg', 2, 9.99, '斤', '赵六', '深圳市福田区深南大道1003号', '13776543210', '2', '2025-11-15 22:27:06', 4);
INSERT INTO `r_order` VALUES (15, 'ORDER_20251116015', 2, '寿光西红柿', 'http://localhost:8080/file/api/tomato.jpg', 8, 3.50, '斤', '赵六', '深圳市罗湖区深南东路5002号', '13776543210', '3', '2025-11-15 22:27:06', 4);
INSERT INTO `r_order` VALUES (16, '32025111723093666', 8, '东北有机黄豆', 'http://localhost:8080/file/api/0f52210d-3835-4cf2-b506-9af4f02f74bb.jpg', 4, 4.59, '斤', '呃呃呃', '天津市市辖区和平区12223', '136454928965', '0', '2025-11-17 23:09:44', 3);
INSERT INTO `r_order` VALUES (20, '32025111723099551', 2, '云南阳光玫瑰葡萄', 'http://localhost:8080/file/api/27baa066-d2ff-4e17-8507-d1c501e2f05c.jpg', 1, 25.99, '串', '呃呃呃', '天津市市辖区和平区12223', '136454928965', '0', '2025-11-17 23:09:44', 3);
INSERT INTO `r_order` VALUES (21, '32025111723090075', 6, '四川青线椒', 'http://localhost:8080/file/api/d6072afe-48b9-422e-bab4-2ec40d51e298.jpg', 3, 5.29, '斤', '呃呃呃', '天津市市辖区和平区12223', '136454928965', '2', '2025-11-17 23:09:44', 3);
INSERT INTO `r_order` VALUES (22, '32025111723117230', 2, '云南阳光玫瑰葡萄', 'http://localhost:8080/file/api/27baa066-d2ff-4e17-8507-d1c501e2f05c.jpg', 3, 25.99, '串', '都护府', '天津市市辖区南开区大赛F', '13342', '0', '2025-11-17 23:11:29', 3);
INSERT INTO `r_order` VALUES (23, '32025111723128033', 2, '云南阳光玫瑰葡萄', 'http://localhost:8080/file/api/27baa066-d2ff-4e17-8507-d1c501e2f05c.jpg', 5, 25.99, '串', '阿大撒', '北京市市辖区东城区的政策', '3435', '0', '2025-11-17 23:12:29', 3);
INSERT INTO `r_order` VALUES (24, '32025111723127688', 3, '广西沃柑', 'http://localhost:8080/file/api/5b8e311c-f69a-4a40-9bc2-c3c973794a8a.jpg', 3, 6.59, '斤', '阿大撒', '北京市市辖区东城区的政策', '3435', '0', '2025-11-17 23:12:29', 3);
INSERT INTO `r_order` VALUES (25, '32025111812048970', 7, '福建莆田荷兰豆', 'http://localhost:8080/file/api/90898185-3291-4b04-9c33-de23127be07a.jpg', 2, 7.99, '斤', '啊啊啊从', '北京市市辖区朝阳区笑啊', '15546374658', '0', '2025-11-18 12:04:43', 3);
INSERT INTO `r_order` VALUES (26, '32025111812048509', 13, '散养土鸡蛋', 'http://localhost:8080/file/api/f032df5b-b59d-43fe-9f2a-9d250548f2ad.jpg', 3, 1.80, '个', '啊啊啊从', '北京市市辖区朝阳区笑啊', '15546374658', '1', '2025-11-18 12:04:43', 3);
INSERT INTO `r_order` VALUES (28, 'ORDER_20251116016', 10, '河北绿豆', NULL, 5, 12.99, '斤', '赵六', '四川省成都市锦江区春熙路', '13600136004', '2', '2025-11-19 01:31:53', 1);

-- ----------------------------
-- Table structure for r_product_comment
-- ----------------------------
DROP TABLE IF EXISTS `r_product_comment`;
CREATE TABLE `r_product_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID，自增',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `pid` bigint NOT NULL COMMENT '商品ID（关联r_products表的id）',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称（冗余存储，便于展示）',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_user` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '创建人',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_product_comment
-- ----------------------------
INSERT INTO `r_product_comment` VALUES (1, 1001, 1, '山东烟台红富士苹果', '苹果很甜，水分充足！', '王五', '2025-11-17 11:40:04');
INSERT INTO `r_product_comment` VALUES (2, 1002, 2, '云南阳光玫瑰葡萄', '玫瑰香味浓郁，口感绝佳！', '李斯', '2025-11-17 11:40:04');
INSERT INTO `r_product_comment` VALUES (3, 1003, 4, '山东寿光有机生菜', '新鲜脆嫩，沙拉必备！', '马力', '2025-11-17 11:40:04');
INSERT INTO `r_product_comment` VALUES (4, 1001, 1, '山东烟台红富士苹果', '脆甜多汁，烟台特产果然名不虚传！', '张三', '2025-11-18 10:30:00');
INSERT INTO `r_product_comment` VALUES (5, 1002, 1, '山东烟台红富士苹果', '个头大，水分足，全家人都爱吃', '李四', '2025-11-18 11:15:00');
INSERT INTO `r_product_comment` VALUES (6, 1003, 2, '云南阳光玫瑰葡萄', '玫瑰香味浓郁，甜度刚好，冷链运输很新鲜', '王五', '2025-11-18 09:45:00');
INSERT INTO `r_product_comment` VALUES (7, 1004, 2, '云南阳光玫瑰葡萄', '颗粒饱满，吃完还会回购', '赵六', '2025-11-18 14:20:00');
INSERT INTO `r_product_comment` VALUES (8, 1005, 3, '广西沃柑', '酸甜可口，果肉细腻，广西产的就是正宗', '孙七', '2025-11-18 10:00:00');
INSERT INTO `r_product_comment` VALUES (9, 1006, 3, '广西沃柑', '汁水丰富，皮很薄，容易剥开', '周八', '2025-11-18 15:30:00');
INSERT INTO `r_product_comment` VALUES (10, 1007, 4, '山东寿光有机生菜', '有机生菜很新鲜，做沙拉太合适了', '吴九', '2025-11-18 08:20:00');
INSERT INTO `r_product_comment` VALUES (11, 1008, 4, '山东寿光有机生菜', '口感脆嫩，产地直供就是不一样', '郑十', '2025-11-18 13:10:00');

-- ----------------------------
-- Table structure for r_products
-- ----------------------------
DROP TABLE IF EXISTS `r_products`;
CREATE TABLE `r_products`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '农产品ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '农产品名称',
  `price` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '农产品价格',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '农产品简介',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称（关联 r_categories 的 name）',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '图片地址',
  `producer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产地',
  `unit` varchar(4) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '单位',
  `sell_count` int NULL DEFAULT 0 COMMENT '售卖数量',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细介绍',
  `stock` int NULL DEFAULT 0 COMMENT '库存量',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '商品是否下架（0否1是）',
  `user_id` int NULL DEFAULT NULL COMMENT '用户id',
  `create_user` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL COMMENT '修改时间',
  `deleted` int NOT NULL DEFAULT 0 COMMENT '是否删除，0否1是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of r_products
-- ----------------------------
INSERT INTO `r_products` VALUES (1, '山东烟台红富士苹果', '8.99', '山东烟台高山红富士，自然成熟不催熟，脆嫩爽口皮薄无渣，无农残安全放心，现摘现发锁住原生风味，送礼自食皆佳', '水果类', 'http://localhost:8080/file/api/8fa1bb97-079f-423b-8b29-e2f0aed5c585.jpg', '山东烟台产区', '斤', 0, '<p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\">山东烟台红富士苹果是中国著名的水果品种，以其优良的品质和口感深受消费者喜爱。以下是关于它的详细介绍：</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\"> - **产地环境**：</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\">烟台地处北纬37°苹果生产“黄金纬度带”，是中国苹果黄金带的核心地带。烟台市下辖的栖霞市、蓬莱区、招远市和福山区是红富士苹果的核心主产区。该区域年均气温12.5℃，年日照时数达2600小时以上，昼夜温差平均在10-13℃之间，有利于糖分积累。土壤以棕壤为主，pH值在5.8-6.8之间，富含钾、钙、镁等矿物质元素，为苹果生长提供了良好条件。</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\"> - **品种来源**：</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\">红富士苹果由日本青森县藤崎町农业试验场于1939年杂交育成，亲本为国光×元帅。1982年起，烟台开始引进该品种，并通过引种选育，形成了“烟富3号”“烟富6号”“长富2号”等多个适应本地气候的优良品系。</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\"> - **外观特征**：</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\">果实呈圆或近圆形，果个大，平均单果重可达280g以上，最大可达350克。果面平滑，有果粉且有光泽，蜡质层厚，色泽艳丽，分为条红和片红两种，海拔较高地区种植的苹果颜色更为浓红。</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\"> - **口感与营养**：</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\">果肉呈黄白色，肉质致密、细脆，汁液丰富度达82%以上，糖酸比高达40:1-50:1，口感脆甜，果香浓郁。每100g果肉中含有可溶性糖12.8-15.6g、总酸0.28-0.35g、膳食纤维2.4g、维生素C4.8-6.2mg，还富含钾离子、多酚类物质等，具有抗氧化、辅助预防心血管疾病等功效。</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\"> - **上市时间**：</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\">因海拔、小气候及栽培管理差异而有所不同。福山区、蓬莱区等沿海低海拔区，一般在10月上旬开始采收，10月中旬集中上市；栖霞市、招远市等内陆丘陵区，采收期集中在10月下旬至11月上旬；莱州市、海阳市部分山区果园可延迟至11月中旬采收。</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\"> - **市场价值**：</span></p><p><span style=\"color: rgb(31, 35, 41); background-color: rgb(255, 255, 255); font-size: 16px;\">“烟台苹果”已连续多年位列中国区域品牌（地理标志产品）价值排行榜水果类第一，2023年品牌价值达166.2亿元人民币。烟台红富士苹果还远销东南亚、中东、俄罗斯、澳大利亚等地，年出口量超25万吨，深受国际市场认可。 <br></span></p>', 358, '0', 5, 'farm_shop', '2025-11-18 14:20:39.868000', '2025-11-18 14:20:39.868000', 0);
INSERT INTO `r_products` VALUES (2, '云南阳光玫瑰葡萄', '25.99', '被云南阳光喂大的玫瑰葡萄！脆甜多汁还带天然奶香，无核皮薄不涩口，现摘锁鲜，每颗都裹着日光甜。\n', '水果类', 'http://localhost:8080/file/api/27baa066-d2ff-4e17-8507-d1c501e2f05c.jpg', '云南弥勒产区', '串', 0, '单串重约500g，果肉晶莹剔透，甜度高达18-22度，冷链运输保鲜', 225, '0', 5, 'farm_shop', '2025-11-16 17:10:48.707000', '2025-11-16 17:10:48.707000', 0);
INSERT INTO `r_products` VALUES (3, '广西沃柑', '6.59', '广西沃柑来袭！黄金纬度孕育，果大饱满色泽鲜亮，皮薄无核肉嫩多汁，蜜甜回甘不腻口，无农残零添加，现摘锁鲜直达，秋冬补水润燥首选', '水果类', 'http://localhost:8080/file/api/5b8e311c-f69a-4a40-9bc2-c3c973794a8a.jpg', '广西武鸣产区', '斤', 0, '<p>当季新鲜沃柑，果径65-75mm，无核多汁，富含膳食纤维，老少皆宜</p>', 50, '0', 5, 'farm_shop', '2025-11-16 17:12:12.575000', '2025-11-16 17:12:12.575000', 0);
INSERT INTO `r_products` VALUES (4, '山东寿光有机生菜', '3.99', '寿光有机生菜，沃土精养脆嫩无渣，自带清鲜回甘，生食沙拉或清炒，一口咬出农场原生脆爽', '蔬菜类', 'http://localhost:8080/file/api/2cd5c63a-c8ab-4c6b-b9bd-71c13de8aaf8.jpg', '山东寿光蔬菜基地', '份', 0, '<p>每份约250g，新鲜采摘无损伤，适合凉拌、火锅、沙拉，全程冷链保鲜</p>', 289, '0', 14, 'fresh_fruit_shop', '2025-11-16 17:14:11.193000', '2025-11-16 17:14:11.193000', 0);
INSERT INTO `r_products` VALUES (5, '云南高山娃娃菜', '2.59', '云南高山娃娃菜，云雾滋养清甜脆嫩，芯嫩如婴脂，清炒煲汤都鲜甜，一口尝尽高山纯净感', '蔬菜类', 'http://localhost:8080/file/api/ce554c05-4e69-4853-a4b9-d9ac26c489d9.jpg', '云南曲靖产区', '棵', 0, '<p>三棵装，单棵重约300g，无农残检测合格，清炒、煮汤、涮锅均可</p>', 345, '0', 14, 'fresh_fruit_shop', '2025-11-16 17:14:29.771000', '2025-11-16 17:14:29.771000', 0);
INSERT INTO `r_products` VALUES (6, '四川青线椒', '5.29', '四川青线椒，辣香霸道脆爽带劲，川味灵魂担当，小炒凉拌都够味，颗颗裹着川蜀热辣魂', '蔬菜类', 'http://localhost:8080/file/api/d6072afe-48b9-422e-bab4-2ec40d51e298.jpg', '四川成都产区', '斤', 0, '<p>新鲜采摘青线椒，长度15-20cm，辣度适中，适合炒菜、做辣椒酱</p>', 273, '0', 62, 'shop1', '2025-11-16 17:14:48.015000', '2025-11-16 17:14:48.015000', 0);
INSERT INTO `r_products` VALUES (7, '福建莆田荷兰豆', '7.99', '莆田荷兰豆，海风湿润养出脆甜，豆荚饱满肉厚无渣，清炒配肉或清煮，一口咬出闽地鲜爽', '蔬菜类', 'http://localhost:8080/file/api/90898185-3291-4b04-9c33-de23127be07a.jpg', '福建莆田产区', '斤', 0, '<p>新鲜荷兰豆，豆荚饱满，无老筋，清炒腊肉、蒜蓉清炒均可</p>', 187, '0', 62, 'shop1', '2025-11-16 17:15:07.903000', '2025-11-16 17:15:07.903000', 0);
INSERT INTO `r_products` VALUES (8, '东北有机黄豆', '4.59', '东北有机黄豆，黑土精酿豆香浓郁，打浆煲汤都绵密，颗颗裹着黑土原生营养，有机更安心', '豆类', 'http://localhost:8080/file/api/0f52210d-3835-4cf2-b506-9af4f02f74bb.jpg', '黑龙江五常产区', '斤', 0, '<p>2025年新豆，无添加无染色，颗粒均匀饱满，出浆率高，口感香浓</p>', 528, '0', 63, 'fruit_shop2', '2025-11-16 17:15:23.847000', '2025-11-16 17:15:23.847000', 0);
INSERT INTO `r_products` VALUES (9, '云南小粒红豆', '5.99', '云南小粒红豆，日光烘焙沙糯清甜，熬粥焖饭都绵密，一口尝尽滇地杂粮香，煮粥甜到心坎里', '豆类', 'http://localhost:8080/file/api/ef51f2fe-7a50-4644-a08c-958f50742312.jpg', '云南昭通产区', '斤', 0, '<p>精选小粒红豆，无杂质无霉变，煮后沙糯，适合做红豆粥、红豆沙</p>', 318, '0', 63, 'fruit_shop2', '2025-11-16 17:15:55.595000', '2025-11-16 17:15:55.595000', 0);
INSERT INTO `r_products` VALUES (10, '河北绿豆', '4.29', '河北绿豆，平原滋养沙糯清甜，熬汤解暑沙感足，颗颗清热又解毒，夏日消暑粥品绝配', '豆类', 'http://localhost:8080/file/api/f98877fe-1c41-46d7-86b5-0e61aebfee48.jpg', '河北张家口产区', '斤', 0, '<p>新鲜绿豆，无虫蛀无杂质，可煮粥、煮汤、发绿豆芽，清热解暑</p>', 426, '0', 63, 'fruit_shop2', '2025-11-16 17:16:12.235000', '2025-11-16 17:16:12.235000', 0);
INSERT INTO `r_products` VALUES (11, '内蒙古草原羊腿肉', '59.99', '内蒙古草原羊腿肉，牧草精养不膻不腻，烤煎炖煮都鲜香，一口咬出草原自由风', '肉类', 'http://localhost:8080/file/api/f77dd085-bd99-4cf4-8d4d-f33a00d5fd8c.jpg', '内蒙古草原', '斤', 0, '<p>精选羊腿肉，去骨率90%，肉质鲜嫩无膻味，适合烤、炖、炒</p>', 156, '0', 14, 'fresh_fruit_shop', '2025-11-16 19:03:00.175000', '2025-11-16 19:03:00.175000', 0);
INSERT INTO `r_products` VALUES (12, '东北黑猪肉', '32.99', '东北黑猪肉，山林散养脂香浓郁，煎炒炖煮都入味，一口尝尽黑土猪肉原生香，吃肉就吃真本味', '肉类', 'http://localhost:8080/file/api/ce2ec9c9-871e-4406-8d02-7ff6df215d65.jpg', '吉林生态养殖场', '斤', 0, '<p>散养黑猪，喂养谷物和野菜，无激素无抗生素，肉质紧实有弹性</p>', 213, '0', 14, 'fresh_fruit_shop', '2025-11-16 19:03:11.152000', '2025-11-16 19:03:11.152000', 0);
INSERT INTO `r_products` VALUES (13, '散养土鸡蛋', '1.8', '散养土鸡蛋，林间觅食蛋香浓郁，煎炒水煮都嫩滑，蛋黄绵密蛋白 Q 弹，早餐来一颗活力满格', '蛋类', 'http://localhost:8080/file/api/f032df5b-b59d-43fe-9f2a-9d250548f2ad.jpg', '安徽黄山养鸡场', '个', 0, '<p>散养土鸡蛋，单个重约50g，蛋黄呈金黄色，蛋清粘稠，营养密度高</p>', 3, '0', 5, 'farm_shop', '2025-11-16 17:16:59.230000', '2025-11-16 17:16:59.230000', 0);
INSERT INTO `r_products` VALUES (14, '柴鸡鸭蛋', '2.19', '柴鸡鸭蛋，杂粮喂养蛋香醇厚，煎饺腌蛋都美味，蛋黄沙糯蛋白细腻，家常烹饪鲜味担当', '蛋类', 'http://localhost:8080/file/api/c5605595-c96f-49b5-803a-8518e2f3f89d.jpg', '江苏高邮产区', '个', 0, '<p>高邮湖散养鸭蛋，单个重约65g，蛋黄出油率高，口感沙糯</p>', 340, '0', 5, 'farm_shop', '2025-11-16 17:17:11.409000', '2025-11-16 17:17:11.409000', 0);
INSERT INTO `r_products` VALUES (15, '鹌鹑蛋', '0.39', '鹌鹑蛋，玲珑小巧营养密集，卤煮油炸都入味，一口一个一口鲜，追剧解馋小零嘴绝绝子', '蛋类', 'http://localhost:8080/file/api/beccfe1a-4aef-4421-b5d1-70a1ccab9e3e.jpg', '山东聊城养殖场', '个', 0, '<p>新鲜鹌鹑蛋，单个重约10g，富含蛋白质和维生素，适合卤制、红烧</p>', 43, '0', 5, 'farm_shop', '2025-11-16 17:17:25.819000', '2025-11-16 17:17:25.819000', 0);
INSERT INTO `r_products` VALUES (16, '陕西洛川红富士苹果', '9.8', '洛川红富士，黄土高坡滋养脆甜多汁，皮薄肉嫩无渣，一口咬出西北阳光甜，苹果就吃洛川产', '水果类', 'http://localhost:8080/file/api/718b82fe-47b8-4e32-b4af-4f4f554b9d16.jpg', '陕西洛川', '斤', 0, '<p>1. 洛川核心产区直供，日照充足甜度高；2. 人工挑选无坏果，果径75-80mm；3. 泡沫网套+纸箱包装，运输不易损</p>', 500, '0', 14, 'fresh_fruit_shop', '2025-11-16 17:17:41.610000', '2025-11-16 17:17:41.610000', 0);
INSERT INTO `r_products` VALUES (17, '山东寿光有机黄瓜', '5.5', '寿光有机黄瓜，沃土精养脆嫩带刺，生食凉拌都清爽，一口咬出农场脆甜，无农残吃着更放心', '蔬菜类', 'http://localhost:8080/file/api/9feb4c99-e108-40de-a609-bcf863dc3951.jpg', '山东寿光', '斤', 0, '<p>1. 有机认证种植，无化肥农药；2. 瓜身挺直，刺密均匀，口感清脆；3. 适合凉拌、清炒、做沙拉</p>', 300, '0', 14, 'fresh_fruit_shop', '2025-11-16 17:18:05.129000', '2025-11-16 17:18:05.129000', 0);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` int NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键，自增）',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号（唯一，不可重复）',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码（建议加密存储，如BCrypt）',
  `nickname` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '用户昵称',
  `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '头像URL（存储图片地址，如OSS路径）',
  `sex` tinyint(1) NULL DEFAULT 0 COMMENT '性别（0-未知，1-男，2-女）',
  `phone` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '手机号（唯一）',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '邮箱（唯一）',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '账号状态（0-正常，1-禁用，2-待审核）',
  `deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（注册时间）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间（自动更新）',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'user' COMMENT '用户类型（admin-管理员，user-普通用户，merchant-商家）',
  `rest_money` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '购物金',
  PRIMARY KEY (`user_id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 69 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '系统管理员', 'https://ts1.tc.mm.bing.net/th/id/OIP-C._EJ4gAHvtRGVBotZqOtd4gAAAA?w=190&h=211&c=8&rs=1&qlt=90&o=6&dpr=1.2&pid=3.1&rm=2', 1, '13800138000', 'admin@ruralfruits.com', 0, 0, '2025-11-12 15:33:15', '2025-11-19 15:30:27', 'admin', 54.00);
INSERT INTO `sys_user` VALUES (2, 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', '张三', 'http://localhost:8080/file/api/2b70ef65-07fc-463b-9df1-1b1ee799d302.jpg', 1, '13800138001', 'zhangsan@ruralfruits.com', 0, 0, '2025-11-12 15:33:15', '2025-11-19 15:30:33', 'user', 464.00);
INSERT INTO `sys_user` VALUES (3, 'lisi', 'e10adc3949ba59abbe56e057f20f883e', '李四', 'http://localhost:8080/file/api/cb80d4a8-f6b8-49b8-a7fc-dbac938f6bb4.jpg', 2, '13800138002', 'lisi@ruralfruits.com', 0, 0, '2025-11-12 15:33:15', '2025-11-19 15:30:39', 'user', 32.00);
INSERT INTO `sys_user` VALUES (4, 'wangwu', 'e10adc3949ba59abbe56e057f20f883e', '王五', 'http://localhost:8080/file/api/38983ebb-2525-4ad0-b730-5da202578b50.jpg', 1, '13800138003', 'wangwu@ruralfruits.com', 2, 0, '2025-11-12 15:33:15', '2025-11-19 15:30:41', 'user', 65.00);
INSERT INTO `sys_user` VALUES (5, 'farm_shop', 'e10adc3949ba59abbe56e057f20f883e', '乡村果园店', 'http://localhost:8080/file/api/51e5a228-ec97-48f9-913d-51353beaa95b.jpg', 0, '13800138004', 'farmshop@ruralfruits.com', 0, 0, '2025-11-12 15:33:15', '2025-11-19 15:30:43', 'merchant', 32.00);
INSERT INTO `sys_user` VALUES (12, 'wushi22', 'e10adc3949ba59abbe56e057f20f883e', '吴十', 'http://localhost:8080/file/api/9cae51f2-8248-428f-86bf-532da2d5134e.jpg', 2, '13100131011', 'wushi22@ruralfruits.com', 0, 0, '2025-11-13 01:37:17', '2025-11-19 15:30:45', 'user', 223.00);
INSERT INTO `sys_user` VALUES (13, 'zhengshiyi33', 'e10adc3949ba59abbe56e057f20f883e', '郑十一', 'http://localhost:8080/file/api/24db8ab1-0dfb-4650-b573-393a4a4f9622.jpg', 1, '13000130012', 'zhengshiyi33@ruralfruits.com', 2, 0, '2025-11-13 01:37:17', '2025-11-19 15:30:47', 'user', 88.00);
INSERT INTO `sys_user` VALUES (14, 'fresh_fruit_shop', 'e10adc3949ba59abbe56e057f20f883e', '鲜果直送店', 'http://localhost:8080/file/api/431b2249-0291-45b7-9250-318bc06247a6.jpg', 0, '12900129013', 'freshfruit@ruralfruits.com', 0, 0, '2025-11-13 01:37:17', '2025-11-19 15:30:49', 'merchant', 43.00);
INSERT INTO `sys_user` VALUES (17, 'name2', 'e10adc3949ba59abbe56e057f20f883e', 'xiaoname', 'http://localhost:8080/file/api/15a81abb-af25-4ce0-be84-7c8fe34bdfb9.jpg', 2, '3456787654', '345678965@qq.com', 0, 0, '2025-11-13 11:52:00', '2025-11-19 15:30:51', 'user', 4.00);
INSERT INTO `sys_user` VALUES (62, 'shop1', 'e10adc3949ba59abbe56e057f20f883e', '乡村果园店', 'http://localhost:8080/file/api/bbada043-7d43-4a31-bf9f-ef6b733e7284.jpg', 0, '1380138004', 'farmshop@rurafruits.com', 0, 0, '2025-11-12 15:33:15', '2025-11-19 15:30:53', 'merchant', 2.00);
INSERT INTO `sys_user` VALUES (63, 'fruit_shop2', 'e10adc3949ba59abbe56e057f20f883e', '鲜果直送店', 'http://localhost:8080/file/api/7d531961-8168-4be0-aaf1-81918f702cd1.jpg', 0, '1290029013', 'freshfuit@rurlfruits.com', 0, 0, '2025-11-13 01:37:17', '2025-11-19 15:30:55', 'merchant', 3.00);
INSERT INTO `sys_user` VALUES (64, 'zhao', 'e10adc3949ba59abbe56e057f20f883e', '赵', 'http://localhost:8080/file/api/b93f3684-fc2d-4e05-b6d4-d899a833f93a.jpg', 0, '16654768774', '', 0, 0, '2025-11-14 15:45:57', '2025-11-19 15:30:56', 'user', 5.00);
INSERT INTO `sys_user` VALUES (68, 'libai', 'e10adc3949ba59abbe56e057f20f883e', '李白', 'http://localhost:8080/file/api/14103067-b343-4cc4-bd5e-03900a206a53.jpg', 0, '155463547556', '', 0, 0, '2025-11-14 16:19:43', '2025-11-19 15:31:00', 'user', 324.00);

-- ----------------------------
-- Triggers structure for table r_order
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_r_order_create_time`;
delimiter ;;
CREATE TRIGGER `trg_r_order_create_time` BEFORE INSERT ON `r_order` FOR EACH ROW BEGIN
  -- 赋值格式：YYYY-MM-DD HH:MM:SS（长度19，varchar(20) 足够容纳）
  SET NEW.create_time = DATE_FORMAT(NOW(), '%Y-%m-%d %H:%i:%s');
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
