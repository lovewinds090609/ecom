# 🛒 ecom - E-commerce Backend

## 📌 專案簡介

`ecom` 是一套使用 Java + Spring Boot 開發的電商後端 API，支援使用者認證、商品管理、購物車、訂單、地址管理等功能。  
目前已整合 PostgreSQL 作為資料庫，並透過 Swagger 提供互動式 API 文件。

## 🛠 技術棧

- Java 21
- Spring Boot 3.5.0-M1
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Swagger
- Maven
- Lombok

## 🚀 快速開始

### 環境需求

- JDK 17 或以上版本
- Maven 3.x
- PostgreSQL 資料庫

### 專案啟動

1️⃣ Clone 此專案：

```bash
git clone https://github.com/lovewinds090609/ecom.git
cd ecom
2️⃣ 建置並啟動專案：

bash
複製
編輯
./mvnw spring-boot:run
3️⃣ 預設運行在：

arduino
複製
編輯
http://localhost:8080
Swagger API 文件
啟動後可透過 Swagger 介面查看與測試 API：

bash
複製
編輯
http://localhost:8080/swagger-ui/index.html
🗂 資料庫設定
於 application.properties 設定 PostgreSQL 資料庫連線：

properties
複製
編輯
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
🔐 認證與授權
採用 JWT 作為 Token 驗證機制。

流程：

1️⃣ 用戶登入或註冊取得 JWT Token
2️⃣ 於 API 工具 (Postman / Swagger) 設定 Header：

makefile
複製
編輯
Authorization: Bearer <your-jwt-token>
📚 API 一覽
Auth
方法	路徑	說明
POST	/auth/signin	用戶登入
POST	/auth/signout	用戶登出
POST	/auth/signup	用戶註冊
GET	/auth/getUserName	取得用戶名稱
GET	/auth/getUserDetails	取得用戶詳細資料

Category
方法	路徑	說明
POST	/categories	建立類別
GET	/categories	取得所有類別
DELETE	/categories/{id}	刪除類別
PUT	/categories/{id}	更新類別

Product
方法	路徑	說明
POST	/products	建立商品
GET	/products	取得所有商品
DELETE	/products/{id}	刪除商品
PUT	/products/{id}	更新商品
PUT	/products/{id}/image	更新商品圖片
GET	/products/search?keyword=xxx	依關鍵字搜尋商品
GET	/products/category/{categoryId}	依類別取得商品

Cart
方法	路徑	說明
POST	/cart	加入商品至購物車
GET	/cart	取得所有購物車
GET	/cart/{cartId}	取得指定購物車
PUT	/cart/{cartId}/product/{productId}	更新購物車內商品數量
DELETE	/cart/{cartId}/product/{productId}	從購物車刪除商品

Address
方法	路徑	說明
POST	/addresses	建立地址
GET	/addresses	取得所有地址
GET	/addresses/user/{userId}	依用戶取得地址
GET	/addresses/{addressId}	取得單一地址
PUT	/addresses/{addressId}	更新地址
DELETE	/addresses/{addressId}	刪除地址

Order
方法	路徑	說明
POST	/orders	建立訂單

📄 授權條款
MIT License

TODO
 完善單元測試 / 整合測試

 加入訂單歷史查詢功能

 整合支付功能（如 Stripe）

 實作 Email 通知

 實作前台管理介面
