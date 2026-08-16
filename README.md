# Moewsic 音乐网站

基于 Spring Boot + Vue 2 的全栈音乐网站，包含用户端、管理后台与后端服务。

## 功能

- 用户端：首页、个性化推荐（协同过滤 / 基于内容 / 热门）、歌单、歌手、歌词、搜索、我的音乐、设置、登录注册、AI 音乐对话
- 管理后台：用户 / 歌曲 / 歌手 / 歌单 / 评论 / 收藏管理，数据统计（ECharts）
- 后端：RESTful API（MyBatis + MySQL），AI 对话（DeepSeek，SiliconFlow）

## 目录结构

```
├── music-client/    # 用户端前端 (Vue2 + Element UI)
├── music-manage/    # 管理后台前端 (Vue2 + Element UI + ECharts)
├── music-server/    # 后端 (Spring Boot 2 + MyBatis + MySQL)
└── 基于springboot的音乐系统.sql   # 数据库建表脚本
```

## 快速开始

1. 导入 `基于springboot的音乐系统.sql` 到 MySQL，库名 `lt_music`
2. 修改 `music-server/src/main/resources/application.properties` 中的数据库账号密码
3. 启动后端：`cd music-server && mvnw spring-boot:run`（端口 8888）
4. 启动用户端：`cd music-client && npm install && npm run dev`
5. 启动管理后台：`cd music-manage && npm install && npm run dev`

## 配置说明

- AI 对话需要设置环境变量 `AI_API_KEY`（SiliconFlow），默认 base-url 为 `https://api.siliconflow.cn`
- 后端默认端口 `8888`

## 默认账号（部署后请立即修改密码）

- 管理员：`admin` / `123`
- 测试用户：`Yin` / `123`

> 密码在数据库中均以 PBKDF2 哈希存储；历史明文密码首次登录成功后会升级为哈希。普通浏览接口无需登录，写操作（评论、收藏、评分、聊天、资料修改）需要登录，管理端接口需要管理员登录。
