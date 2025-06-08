#  ecom - E-commerce Backend

##  專案簡介

使用 Java + Spring Boot 開發的電商後端 API  
支援使用者認證、商品管理、購物車、訂單、地址管理等功能  
目前已整合 PostgreSQL 作為資料庫，並透過 Swagger 提供互動式 API 文件

## 技術棧

- Java 21
- Spring Boot 3.5.0-M1
- Spring Data JPA
- Spring Security + JWT
- PostgreSQL
- Swagger
- Maven
- Lombok

## 快速開始

### 環境需求

- JDK 17 或以上版本
- Maven 3.x
- PostgreSQL 資料庫

### 專案啟動

1️. Clone 此專案：

```bash
git clone https://github.com/lovewinds090609/ecom.git
cd ecom
```
2️. 建置並啟動專案
```bash
./mvnw spring-boot:run
```
3️.預設運行在：
```bash
http://localhost:5000
```

Swagger API 文件
啟動後可透過 Swagger 介面查看與測試 API
```bash
http://localhost:5000/swagger-ui/index.html
```

 資料庫設定
於 application.properties 設定 PostgreSQL 資料庫連線
```bash
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db_name
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

 認證與授權
採用 JWT 作為 Token 驗證機制

流程：

1️.用戶登入或註冊取得 JWT Token  
2.登入成功後端會設定 Header並回傳給前端  
 ```bash
 Authorization: Bearer <your-jwt-token>
 ```
3.登出後會把 Token 紀錄在 Redis上

API 一覽  
Auth
| 方法 | 路徑 | 說明 |
| ---- | ---- | ---- |
| POST | /api/auth/signin | 用戶登入 |
| POST | /api/auth/signout | 用戶登出 |
| POST | /api/auth/signup | 用戶註冊 |
| GET  | /api/auth/username | 取得用戶名稱 |
| GET  | /api/auth/user | 取得用戶詳細資料 |

Category
| 方法 | 路徑 | 說明 |
| ---- | ---- | ---- |
| POST | /api/public/categories | 建立類別 |
| GET  | /api/public/categories | 取得所有類別 |
| DELETE | /api/admin/categories/{id} | 刪除類別 |
| PUT  | /api/public/categories/{categoryId} | 更新類別 |

Product
| 方法 | 路徑 | 說明 |
| ---- | ---- | ---- |
| POST | /api/admin/categories/{categoryId}/product | 建立商品 |
| GET  | /api/public/products | 取得所有商品 |
| DELETE | /api/admin/products/{productId} | 刪除商品 |
| PUT  | /api/admin/products/{productId} | 更新商品 |
| PUT  | /api/products/{productId}/image | 更新商品圖片 |
| GET  | /api/public/products/keyword/{keyword} | 依關鍵字搜尋商品 |
| GET  | /api/public/categories/{categoryId}/products | 依類別取得商品 |

Cart
| 方法 | 路徑 | 說明 |
| ---- | ---- | ---- |
| POST | /api/carts/products/{productId}/quantity/{quantity} | 加入商品至購物車 |
| GET  | /api/carts | 取得所有購物車 |
| GET  | /api/carts/user/cart | 取得指定購物車 |
| PUT  | /api/cart/products/{productId}/quantity/{add/delete} | 更新購物車內商品數量 |
| DELETE | /api/carts/{cartId}/product/{productId} | 從購物車刪除商品 |

Address
| 方法 | 路徑 | 說明 |
| ---- | ---- | ---- |
| POST | /api/addresses | 建立地址 |
| GET  | /api/addresses | 取得所有地址 |
| GET  | /api/addresses/users/{userId} | 依用戶取得地址 |
| GET  | /api/addresses/{addressId} | 取得單一地址 |
| PUT  | /api/addresses/{addressId} | 更新地址 |
| DELETE | /api/addresses/{addressId} | 刪除地址 |

Order
| 方法 | 路徑 | 說明 |
| ---- | ---- | ---- |
| POST | /api/order/users/payments/{付款方式（visa/mastercard/...）} | 建立訂單 |

TODO
```bash
 完善單元測試 / 整合測試

 加入訂單歷史查詢功能

 整合支付功能（如 Stripe）

 實作 Email 通知

 實作前台管理介面
```
