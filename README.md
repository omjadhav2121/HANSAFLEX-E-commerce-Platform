# HANSAFLEX E-commerce Platform

A comprehensive full-stack e-commerce solution designed for global industrial equipment suppliers, featuring multi-regional support, dynamic pricing, modern authentication, and advanced order management capabilities.

## 🏢 Project Overview

HANSAFLEX E-commerce Platform is a sophisticated B2B e-commerce solution built for industrial equipment suppliers operating across multiple regions (US, EU, APAC). The platform provides seamless product management, dynamic pricing with VAT calculations, secure authentication, and comprehensive order processing capabilities.

### Key Business Features
- **Multi-Regional Operations**: Support for US, EU, and APAC markets
- **Dynamic Pricing**: Region-specific VAT calculations and pricing strategies
- **Role-Based Access**: Separate interfaces for administrators and customers
- **Stock Management**: Real-time inventory tracking and updates
- **Order Processing**: Single and bulk order capabilities with SAP integration
- **Product Catalog**: Advanced filtering, search, and categorization

## 🏗️ System Architecture

### High-Level Architecture
```
┌─────────────────────────────────────────────────────────────────┐
│                    HANSAFLEX E-commerce Platform                │
├─────────────────────────────────────────────────────────────────┤
│  Frontend (Next.js)          │  Backend (Spring Boot)          │
│  ├── Customer Dashboard      │  ├── REST API Layer             │
│  ├── Admin Dashboard         │  ├── Business Logic Layer       │
│  ├── Authentication UI       │  ├── Data Access Layer          │
│  └── Product Catalog         │  └── Security Layer             │
├─────────────────────────────────────────────────────────────────┤
│  External Systems                                             │
│  ├── H2 Database (Development)                                │
│  ├── SAP Integration (Mock)                                   │
│  └── Caching Layer (Caffeine)                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Technology Stack

#### Backend Stack
- **Framework**: Spring Boot 3.3.0
- **Language**: Java 23
- **Database**: H2 (File-based, development)
- **ORM**: Spring Data JPA with Hibernate 6.5.2
- **Security**: Spring Security with JWT
- **Caching**: Spring Cache with Caffeine
- **Build Tool**: Maven 3.11.0
- **Testing**: JUnit 5 with Mockito 5.8.0

#### Frontend Stack
- **Framework**: Next.js 15.5.4 (App Router)
- **Language**: TypeScript 5
- **Styling**: Tailwind CSS 3.4.17
- **Icons**: Lucide React 0.544.0
- **HTTP Client**: Built-in fetch API
- **State Management**: React Context API
- **Build Tool**: Turbopack (Next.js built-in)

## 📁 Project Structure

```
hansaflex-ecommerce-platform/
├── hansaflex-ecommerce-backend/          # Spring Boot Backend
│   ├── src/main/java/com/hansaflex/ecommerce/
│   │   ├── controller/                   # REST Controllers
│   │   ├── service/                      # Business Logic Layer
│   │   ├── entity/                       # JPA Entities
│   │   ├── repository/                   # Data Access Layer
│   │   ├── dto/                          # Data Transfer Objects
│   │   ├── security/                     # Security Configuration
│   │   ├── config/                       # Application Configuration
│   │   ├── exception/                    # Custom Exceptions
│   │   └── enums/                        # Enumerations
│   ├── src/main/resources/
│   │   └── application.yml               # Application Configuration
│   ├── src/test/java/                    # Unit Tests
│   ├── data/                             # H2 Database Files
│   ├── postman/                          # API Documentation
│   ├── pom.xml                           # Maven Configuration
│   └── start-backend.sh                  # Backend Startup Script
│
├── hansaflex-frontend/                   # Next.js Frontend
│   ├── src/app/                          # Next.js App Router
│   │   ├── admin/                        # Admin Dashboard Pages
│   │   │   ├── dashboard/
│   │   │   ├── products/
│   │   │   ├── orders/
│   │   │   ├── customers/
│   │   │   ├── region-products/
│   │   │   └── vat-rules/
│   │   ├── customer/                     # Customer Dashboard Pages
│   │   │   ├── dashboard/
│   │   │   ├── cart/
│   │   │   └── orders/
│   │   ├── login/                        # Authentication Pages
│   │   ├── signup/
│   │   ├── layout.tsx                    # Root Layout
│   │   └── page.tsx                      # Homepage
│   ├── src/components/                   # Reusable Components
│   │   ├── ProductCard.tsx
│   │   ├── DashboardLayout.tsx
│   │   ├── Navbar.tsx
│   │   ├── ProtectedRoute.tsx
│   │   └── BackendStatus.tsx
│   ├── src/contexts/                     # React Context Providers
│   │   ├── AuthContext.tsx
│   │   └── WishlistContext.tsx
│   ├── src/lib/                          # Utility Libraries
│   │   ├── catalog-api.ts
│   │   ├── auth.ts
│   │   ├── validation.ts
│   │   ├── config.ts
│   │   ├── port-detector.ts
│   │   ├── simple-api.ts
│   │   ├── api.ts
│   │   └── vat-api.ts
│   ├── public/images/products/           # Product Images
│   ├── package.json                      # Node.js Dependencies
│   ├── tailwind.config.ts                # Tailwind CSS Configuration
│   ├── next.config.ts                    # Next.js Configuration
│   └── start-frontend.sh                 # Frontend Startup Script
│
├── screenshots/                          # UI Screenshots
│   ├── Admin/                            # Admin Interface Screenshots
│   ├── Auth & Homepage/                  # Authentication Screenshots
│   └── Custmomer/                        # Customer Interface Screenshots
├── public/images/products/               # Shared Product Images
├── README.md                             # Main Documentation
├── UI_GUIDE.md                           # UI Documentation
└── CLEANUP_SUMMARY.md                    # Cleanup Documentation
```

## 🚀 Quick Start Guide

### Prerequisites

- **Java**: 23 or higher
- **Node.js**: 18 or higher
- **Maven**: 3.6 or higher
- **npm**: 8 or higher

### 1. Clone the Repository

```bash
git clone <repository-url>
cd hansaflex-ecommerce-platform
```

### 2. Backend Setup

```bash
cd hansaflex-ecommerce-backend

# Install dependencies and start the backend
mvn clean compile
mvn spring-boot:run

# Alternative: Use the startup script
chmod +x start-backend.sh
./start-backend.sh
```

The backend will start on `http://localhost:8080`

### 3. Frontend Setup

```bash
cd hansaflex-frontend

# Install dependencies
npm install

# Start the development server
npm run dev

# Alternative: Use the startup script
chmod +x start-frontend.sh
./start-frontend.sh
```

The frontend will start on `http://localhost:3000`

### 4. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console
- **API Documentation**: Import `postman/HANSAFLEX-Ecommerce-Complete-Final.postman_collection.json` into Postman


## 🔧 Core Features

### Backend Features

#### Product Management
- **CRUD Operations**: Complete product lifecycle management
- **Multi-Regional Support**: Products can be configured for specific regions (US, EU, APAC)
- **Advanced Filtering**: Filter by category, price range, region, and name
- **Pagination & Sorting**: Efficient data retrieval with configurable page sizes
- **Stock Management**: Real-time inventory tracking and updates
- **Image Support**: Product images with intelligent fallback system

#### Order Management
- **Single Orders**: Individual order processing with stock validation
- **Bulk Orders**: Multiple item orders with batch processing
- **Stock Validation**: Automatic stock checking and deduction
- **Order Tracking**: Complete order lifecycle management
- **SAP Integration**: Mock SAP service for order confirmation

#### Authentication & Security
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access**: Admin and Customer role separation
- **Password Security**: Encrypted password storage
- **Session Management**: Configurable token expiration
- **CORS Configuration**: Secure cross-origin request handling

#### Caching & Performance
- **Spring Cache**: Intelligent caching with Caffeine
- **Product Caching**: Cached product catalog for improved performance
- **Cache Eviction**: Automatic cache invalidation on data updates
- **Performance Monitoring**: Built-in metrics and health checks

#### Pricing & VAT
- **Dynamic Pricing**: Region-specific pricing strategies
- **VAT Calculation**: Automatic VAT calculation based on region
- **Configurable Rates**: Admin-configurable VAT rates per region
- **Currency Support**: Multi-currency pricing support

### Frontend Features

#### User Interface
- **Modern Design**: Clean, professional interface with Tailwind CSS
- **Responsive Layout**: Mobile-first responsive design
- **Interactive Components**: Smooth animations and transitions
- **Product Cards**: Rich product displays with images and details
- **Real-time Updates**: Live data synchronization with backend

#### Authentication
- **Login/Signup**: Secure user registration and authentication
- **Role-Based UI**: Different interfaces for admins and customers
- **Protected Routes**: Secure route protection with authentication
- **Session Management**: Automatic token refresh and logout

#### Admin Dashboard
- **Product Management**: Complete product CRUD interface
- **Order Management**: Order tracking and management
- **VAT Rules**: Regional VAT configuration interface
- **Customer Management**: Customer account management
- **Analytics**: Business metrics and reporting

#### Customer Dashboard
- **Product Catalog**: Browsable product catalog with filtering
- **Shopping Cart**: Add/remove items with quantity management
- **Order History**: View past orders and status
- **Wishlist**: Save favorite products for later
- **Search & Filter**: Advanced product search capabilities

#### Data Management
- **Form Validation**: Client-side validation with real-time feedback
- **Error Handling**: Comprehensive error handling and user feedback
- **Loading States**: Proper loading indicators and states
- **Offline Support**: Basic offline functionality with local storage

## 🔗 API Architecture

### RESTful API Design

The backend follows REST principles with clear endpoint structures:

#### Authentication Endpoints
```
POST /api/auth/login          # User login
POST /api/auth/signup         # User registration
POST /api/auth/logout         # User logout
GET  /api/auth/me             # Get current user
```

#### Product Endpoints
```
GET    /api/catalog           # Get all products (public)
GET    /api/catalog/{id}      # Get product by ID (public)
GET    /api/admin/products    # Admin product management
POST   /api/admin/products    # Create new product
PUT    /api/admin/products/{id} # Update product
DELETE /api/admin/products/{id} # Delete product
```

#### Order Endpoints
```
GET  /api/orders              # Get user orders
POST /api/orders              # Create new order
POST /api/orders/bulk         # Create bulk orders
GET  /api/orders/{id}         # Get order details (public)
```

#### Pricing Endpoints
```
GET  /api/pricing/vat         # Get VAT rates (public)
GET  /api/admin/vat-rules     # Admin VAT management
POST /api/admin/vat-rules     # Create VAT rule
PUT  /api/admin/vat-rules/{id} # Update VAT rule
```

#### Cache Management
```
POST /api/admin/cache/clear-products # Clear product cache
POST /api/admin/cache/clear-all      # Clear all caches
POST /api/public/cache/clear-products # Public cache clearing
```

## 🔐 Security Implementation

### Authentication Flow
1. **User Registration**: Users register with username, email, password, and region
2. **JWT Token Generation**: Server generates JWT token upon successful login
3. **Token Validation**: All protected endpoints validate JWT tokens
4. **Role-Based Access**: Different permissions for Admin and Customer roles
5. **Session Management**: Configurable token expiration (24 hours default)

### Security Features
- **Password Encryption**: BCrypt password hashing
- **JWT Security**: Signed tokens with configurable secret
- **CORS Configuration**: Secure cross-origin request handling
- **Input Validation**: JSR-303 validation on all inputs
- **SQL Injection Protection**: JPA/Hibernate ORM protection
- **XSS Protection**: Input sanitization and output encoding

### Role-Based Access Control

#### Admin Role
- Full product CRUD operations
- Order management and tracking
- VAT rule configuration
- Customer management
- Cache management
- System analytics

#### Customer Role
- Browse product catalog
- Add items to cart
- Place orders
- View order history
- Manage wishlist
- Update profile

## 📊 Database Schema

### Entity Relationships

```
Users (1) ──────── (N) Orders
  │
  └── Role: ADMIN | CUSTOMER

Orders (1) ──────── (N) OrderItems
  │
  └── Region: US | EU | APAC

Products (N) ──────── (N) OrderItems
  │
  └── Region: US | EU | APAC

RegionPricingConfig (1) ──── (N) Regions
  │
  └── VAT Rates per Region
```

### Data Models

#### Product Entity
```java
@Entity
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String currency;
    private Integer stockQty;
    private String category;
    private String region;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### Order Entity
```java
@Entity
public class Order {
    private Long id;
    private String customerId;
    private String region;
    private BigDecimal totalAmount;
    private String currency;
    private OrderStatus status;
    private String confirmationNumber;
    private List<OrderItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

#### User Entity
```java
@Entity
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Role role;
    private String region;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## 🧪 Testing Strategy

### Backend Testing

#### Unit Tests
```bash
cd hansaflex-ecommerce-backend
mvn test
```

**Test Coverage**:
- Service layer business logic
- Repository data access methods
- Controller endpoint validation
- Authentication and security
- Cache functionality
- Exception handling

#### Integration Tests
- Database integration testing
- API endpoint integration
- Security integration testing
- Cache integration testing

### Frontend Testing

#### Type Checking
```bash
cd hansaflex-frontend
npm run type-check
```

#### Build Testing
```bash
npm run build
```

#### Component Testing
- React component rendering
- User interaction testing
- Form validation testing
- API integration testing

## 📈 Performance Optimization

### Backend Performance

#### Caching Strategy
- **Product Cache**: Cached product catalog with 30-minute expiration
- **Pricing Cache**: Cached VAT rates and pricing configurations
- **User Cache**: Cached user sessions and profiles
- **Cache Eviction**: Automatic cache invalidation on data updates

#### Database Optimization
- **Indexing**: Optimized database indexes for frequently queried fields
- **Pagination**: Efficient data retrieval with configurable page sizes
- **Batch Operations**: Optimized bulk processing for orders and stock updates
- **Connection Pooling**: HikariCP connection pooling for database connections

#### API Performance
- **Response Compression**: GZIP compression for API responses
- **Async Processing**: Asynchronous order processing for better scalability
- **Request Validation**: Early request validation to prevent unnecessary processing

### Frontend Performance

#### Bundle Optimization
- **Code Splitting**: Automatic code splitting with Next.js
- **Tree Shaking**: Elimination of unused code
- **Image Optimization**: Next.js automatic image optimization
- **Static Generation**: Static site generation where possible

#### Runtime Performance
- **Lazy Loading**: Component lazy loading for better initial load times
- **Memoization**: React.memo and useMemo for expensive computations
- **Virtual Scrolling**: Efficient rendering of large lists
- **Service Worker**: Caching for offline functionality

## 🚀 Deployment Guide

### Backend Deployment

#### Development Environment
```bash
cd hansaflex-ecommerce-backend
mvn spring-boot:run
```

#### Production Build
```bash
mvn clean package
java -jar target/ecommerce-backend-0.0.1-SNAPSHOT.jar
```

#### Environment Configuration
```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hansaflex_prod
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  
server:
  port: 8080
  
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400000
```

### Frontend Deployment

#### Development Environment
```bash
cd hansaflex-frontend
npm run dev
```

#### Production Build
```bash
npm run build
npm run start
```

#### Static Export (Optional)
```bash
npm run build
npm run export
```

#### Environment Configuration
```bash
# .env.local
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_APP_NAME=HANSAFLEX E-commerce
```

## 📚 API Documentation

### Authentication API

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@hansaflex.com",
      "role": "ADMIN",
      "region": "US"
    }
  }
}
```

#### Signup
```http
POST /api/auth/signup
Content-Type: application/json

{
  "username": "customer1",
  "email": "customer@example.com",
  "password": "password123",
  "region": "US",
  "role": "CUSTOMER"
}
```

### Product API

#### Get All Products
```http
GET /api/catalog?page=0&size=10&region=US&category=Hydraulics
Authorization: Bearer <token>
```

**Response**:
```json
{
  "success": true,
  "message": "Products retrieved successfully",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Hydraulic Hose 1/4\"",
        "description": "High-pressure hydraulic hose",
        "price": 25.99,
        "currency": "USD",
        "stockQty": 100,
        "category": "Hydraulics",
        "region": "US",
        "imageUrl": "/images/products/hose.jpg"
      }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "size": 10,
    "number": 0
  }
}
```

#### Create Product
```http
POST /api/admin/products
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "name": "New Hydraulic Fitting",
  "description": "Premium hydraulic fitting",
  "price": 15.99,
  "currency": "USD",
  "stockQty": 50,
  "category": "Hydraulics",
  "region": "US",
  "imageUrl": "/images/products/fitting.jpg"
}
```

### Order API

#### Create Order
```http
POST /api/orders
Authorization: Bearer <customer-token>
Content-Type: application/json

{
  "items": [
    {
      "productId": 1,
      "quantity": 2
    }
  ],
  "region": "US"
}
```

**Response**:
```json
{
  "success": true,
  "message": "Order created successfully",
  "data": {
    "id": 123,
    "confirmationNumber": "ORD-2024-001",
    "totalAmount": 51.98,
    "currency": "USD",
    "status": "CONFIRMED",
    "items": [
      {
        "productId": 1,
        "productName": "Hydraulic Hose 1/4\"",
        "quantity": 2,
        "price": 25.99,
        "subtotal": 51.98
      }
    ]
  }
}
```

## 🛠️ Troubleshooting

### Common Issues

#### Backend Issues

**Port Already in Use**:
```bash
# Find process using port 8080
lsof -i :8080
# Kill the process
kill -9 <PID>
```

**Database Connection Issues**:
```bash
# Check if H2 database files exist
ls -la data/
# Restart backend to recreate database
mvn spring-boot:run
```

**Java Version Issues**:
```bash
# Check Java version
java -version
# Should be Java 23 or higher
```

#### Frontend Issues

**Port Already in Use**:
```bash
# Find process using port 3000
lsof -i :3000
# Kill the process
kill -9 <PID>
```

**Node Modules Issues**:
```bash
# Clear node modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

**Build Issues**:
```bash
# Clear Next.js cache
rm -rf .next
npm run build
```

### Performance Issues

#### Slow API Responses
1. Check database query performance
2. Verify cache configuration
3. Monitor memory usage
4. Check for N+1 query problems

#### Frontend Performance
1. Check bundle size with `npm run build`
2. Verify image optimization
3. Check for unnecessary re-renders
4. Monitor network requests

### Debug Mode

#### Backend Debug
```yaml
# application.yml
logging:
  level:
    com.hansaflex.ecommerce: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
```

#### Frontend Debug
```bash
# Enable Next.js debug mode
DEBUG=* npm run dev
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Commit changes: `git commit -m 'Add amazing feature'`
4. Push to branch: `git push origin feature/amazing-feature`
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🎯 Future Roadmap

### Planned Features
- **Payment Integration**: Stripe/PayPal payment processing
- **Email Notifications**: Order confirmations and updates
- **Advanced Analytics**: Business intelligence dashboard
- **Multi-language Support**: Internationalization (i18n)
- **Mobile App**: React Native mobile application
- **Microservices**: Service decomposition for scalability
- **Cloud Deployment**: AWS/Azure cloud deployment
- **Advanced Search**: Elasticsearch integration
- **Real-time Updates**: WebSocket integration
- **API Versioning**: API version management

### Technical Improvements
- **Database Migration**: PostgreSQL for production
- **Container Orchestration**: Kubernetes deployment
- **Monitoring**: Prometheus and Grafana integration
- **CI/CD Pipeline**: Automated deployment pipeline
- **Security Hardening**: Enhanced security measures
- **Performance Optimization**: Advanced caching strategies

## 📞 Support & Contact

### Development Team
- **Lead Developer**: Om Jadhav
- **Email**: omjadhav@gmail.com
- **GitHub**: [Repository URL]

### Getting Help
1. **Documentation**: Check this README and inline code documentation
2. **Issues**: Create GitHub issues for bugs and feature requests
3. **Email**: Contact omjadhav@gmail.com for urgent issues
4. **API Testing**: Use the provided Postman collection for API testing

---

**HANSAFLEX E-commerce Platform** - Empowering global industrial equipment suppliers with modern, scalable, and secure e-commerce solutions.

Built with using Spring Boot, Next.js, and modern web technologies.
