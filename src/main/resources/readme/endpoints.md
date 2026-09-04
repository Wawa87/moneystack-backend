# Servlet and Endpoint Reference Guide

## AuthenticationFilter
Filters requests to check for the presence of the JWT cookie.

JWT cookie key: 'access_token'

If found, sets the two following request attributes:

userId: The user's Long id number.

subject: The registered username.

Exceptions for the following URL paths used for registration and login:
/register
/login

## UserServlet
Servlet for User data.

### GET /user/all
Access: Admin only

#### Response:
`
[
    {
        "id": 203,
        "username": "dev",
        "emails": [
            "dev@test.com"
        ],
        "firstName": "Dev",
        "lastName": "Use",
        "phoneNumber": "+17602221111",
        "createdAt": "2026-07-16T15:58:07.482409",
        "updatedAt": null
    }
]
`

### GET /user/{id}

#### Response:
`
{
    "id": 203,
    "username": "dev",
    "emails": [
        "dev@test.com"
    ],
    "firstName": "Dev",
    "lastName": "Use",
    "phoneNumber": "+17602221111",
    "createdAt": "2026-07-16T15:58:07.482409",
    "updatedAt": null
}
`

### POST /user
Access: Admin only

#### Request:
`
{
    "username": "michael",
    "firstName": "Michael",
    "lastName": "Scott",
    "emails": ["michael.scott@office.com"],
    "phoneNumber": "+17602220001",
    "password": "testpass"
}
`

#### Response:
`
{
    "id": 204,
    "username": "michael",
    "emails": [
        "michael.scott@office.com"
    ],
    "firstName": "Michael",
    "lastName": "Scott",
    "phoneNumber": "+17602220001",
    "createdAt": "2026-07-16T16:07:30.270018",
    "updatedAt": null
}
`

### PUT /user/{id}
Update user details. Restricted to admin-only - there are separate workflows for users to update key info like emails, password, and phone number.

Access: Admin only

#### Request:
`
{
    "username": "mscott",
    "firstName": "Boss",
    "lastName": "Man",
    "emails": [
        "mscott@office.com"
    ],
    "phoneNumber": "+17602220011",
    "password": "newPass"
}
`

#### Response:
`
{
    "id": 204,
    "username": "mscott",
    "emails": [
        "mscott@office.com"
    ],
    "firstName": "Boss",
    "lastName": "Man",
    "phoneNumber": "+17602220011",
    "createdAt": "2026-07-16T16:07:30.270018",
    "updatedAt": "2026-07-16T16:12:11.615297400"
}
`

### PUT /user/{id}/updatePassword
// TODO: Implement password change workflow.

### PUT /user/{id}/updateEmails
// TODO: Implement email update workflow.

### PUT /user/{id}/updatePhoneNumber
// TODO: Implement phone number update workflow.

### DELETE /user/{id}
User delete endpoint.

Access: Admin only

## RegistrationServlet
Servlet for User registration.

### POST /register

#### Request:
`
{
    "username": "dev",
    "emails": ["dev@test.com"],
    "firstName": "Dev",
    "lastName": "Use",
    "password": "dev",
    "phoneNumber": "+17602221111"
}
`

#### Response:
`
{
    "id": 203,
    "username": "dev",
    "emails": [
        "dev@test.com"
    ],
    "firstName": "Dev",
    "lastName": "Use",
    "phoneNumber": "+17602221111",
    "createdAt": "2026-07-16T15:58:07.482409",
    "updatedAt": null
}
`

## AuthenicationServlet
Servlet for authentication. Sets the JWT cookie upon successful authentication.

### POST /login
#### Request:
`
{
    "username": "dev",
    "password": "dev"
}
`

#### Response:
`
{
    "id": 203,
    "username": "dev",
    "emails": [
        "dev@test.com"
    ],
    "firstName": "Dev",
    "lastName": "Use",
    "phoneNumber": "+17602221111",
    "createdAt": "2026-07-16T15:58:07.482409",
    "updatedAt": null
}
`

## UsernameValidationServlet
Servlet for validating a username for availability and format.

### POST /validateNewUsername
Validate the provided new username for availability.
#### Request:
`
{
    "username": <username>
}
`

#### Response:
`
{
    "message": <message>
}
`

## CategoryServlet
Servlet for Category CRUD operations.

### GET /categories
Get the categories for the current User.

#### Response:
`
[
    {
        "id": 66,
        "userId": 276,
        "name": "Investments",
        "description": "All investment allocations."
    },
    {
        "id": 67,
        "userId": 276,
        "name": "Housing",
        "description": "All housing expenses."
    }
]
`

### GET /categories/{id}
Get the specific category for the current User.

#### Response:
`
{
    "id": 66,
    "userId": 276,
    "name": "Investments",
    "description": "All investment allocations."
}
`

### POST /categories
Create a new Category.

#### Request:
`
{
    "name": "Investments",
    "description": "All investment allocations."
}
`

#### Response:
`
{
    "id": 66,
    "userId": 276,
    "name": "Investments",
    "description": "All investment allocations."
}
`

### PUT /categories/{id}
Update the category.

#### Request:
`
{
    "name": "Investments",
    "description": "My investment allocations."
}
`

#### Response:
`
{
    "id": 66,
    "userId": 276,
    "name": "Investments",
    "description": "My investment allocations."
}
`

### DELETE /categories/{id}
Delete the category.
#### Response:
`
{
    "message": "Category deleted."
}
`

## SubcategoryServlet
Servlet for Subcategory CRUD operations.

### GET /subcategories/{id}
Get the specific subcategory for the current User.

#### Response:
`
{
    "id": 1,
    "categoryId": 2,
    "name": "Mortgage",
    "description": "House payment"
}
`

### POST /categories
Create a new Subcategory.

#### Request:
`
{
    "categoryId": 3,
    "name": "Utilities",
    "description": "Trash and water to city"
}
`

#### Response:
`
{
    "id": 3,
    "categoryId": 3,
    "name": "Utilities",
    "description": "Trash and water to city"
}
`

### PUT /subcategories/{id}
Update the subcategory.

#### Request:
`
{
    "categoryId": 3,
    "name": "Mortgage",
    "description": "Monthly house payment"
}
`

#### Response:
`
{
    "id": 1,
    "categoryId": 3,
    "name": "Mortgage",
    "description": "Monthly house payment"
}
`

### DELETE /subcategories/{id}
Delete the subcategory.

#### Response:
`
{
    "message": "Subcategory deleted."
}
`