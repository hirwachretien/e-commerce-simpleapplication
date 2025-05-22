# E-Commerce Simple Application

A simple E-Commerce web application built with Spring Boot, Thymeleaf, JPA, and MySQL. The application supports user and admin roles, product management, cart and order functionality, as well as basic sales reporting.

## Features

 **User Dashboard:** View products, add to cart, checkout, and see order history.
 **Admin Dashboard:** Manage products, view customers, and see sales reports. **Product Management:** Add, edit, delete, and view products.
 **Cart & Orders:** Users can add products to cart, update quantities, place orders, and view order confirmations.
 **Role-based Views:** "Add to Cart" button and admin features are only visible to their respective roles.
 **Static Images:** Product images are served from the `/static/images/` directory.
 **Sales Reporting:** Admins can see total orders, revenue, products sold, and recent orders.

## Tech Stack

- Java 17+ / 21+ (compatible with Java 23)
- Spring Boot 3.x
- Spring Security
- Spring Data JPA (Hibernate)
- Thymeleaf
- MySQL
- Bootstrap 5

## Getting Started

### Prerequisites

- Java 17 or higher installed
- Maven installed
- MySQL database running

### Setup

1. **Clone the repository**
   bash
   git clone https://github.com/hirwachretien/e-commerce-simpleapplication.git
   cd e-commerce-simpleapplication
   

2. **Configure the database**

   Update `src/main/resources/application.properties` with your MySQL credentials:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/ecommercedb
   spring.datasource.username=root
   spring.datasource.password=hirwa2002
   spring.jpa.hibernate.ddl-auto=update
   

3. **Add product images (optional)**

   Place your product images in:
   
   src/main/resources/static/images/
   
   Reference them in your product entities as `/images/yourimage.jpg`.

4. **Build and Run**
   bash
   mvn clean install
   mvn spring-boot:run
   The app will be available at `http://localhost:8080/` (or your configured port).

### Default Roles & Logins

By default, you should configure at least one admin user in the database.  
If you have a data.sql or initial migration, update as needed.

## Usage

- **User:** Browse `/products`, add to cart, checkout, see `/user-dashboard` and `/orders`.
- **Admin:** Access `/admin-dashboard` for management, `/admin/orders` for all orders, `/admin/reports` for reports, `/admin/customers` for customer list.

## Project Structure

```
src/
  main/
    java/
      com.example.ecommerce/
        controllers/
        entities/
        repositories/
        services/
    resources/
      static/
        images/
      templates/
        *.html
      application.properties

## Deployment

- Static images in `static/images/` will be deployed with the app if they are committed to the repository.
- To update images or static files, add/commit/push them and redeploy.
### Live Demo

The application is deployed and accessible at:
 [e-commerce-simpleapplication on Render]   (https://e-commerce-simpleapplication-3.onrender.com/ecommerce/)

### Hosted on Render using Docker

This application is deployed on **Render** with a Docker container.

- The GitHub repository is connected to Render.
- Render detects the `Dockerfile` in the root of the project to build the container.
- Render manages the deployment, running your app in a containerized environment.
- Any push to the main branch triggers an automatic rebuild and redeploy on Render.
- Environment variables and other deployment settings can be configured in Render’s dashboard.

For more details on Render, visit https://render.com

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

Made by [hirwachretien](https://github.com/hirwachretien)
