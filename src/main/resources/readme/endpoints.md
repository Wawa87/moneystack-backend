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

### GET /user/{id}

### POST /user
Access: Admin only

Payload:

`
{
    "username": <username>,
    "firstName": <firstName>,
    "lastName": <lastName>,
    "emails": [<email1>, <email2>],
    "phoneNumber": <phoneNumber>,
    "password": <password>
}
`

### PUT /user/{id}
Update user details. Restricted to admin-only - there are separate workflows for users to update key info like emails, password, and phone number.

Access: Admin only

Payload:

`
{
    "username": <username>,
    "firstName": <firstName>,
    "lastName": <lastName>,
    "emails": [<email1>, <email2>],
    "phoneNumber": <phoneNumber>,
    "password": <password>
}
`

### PUT /user/{id}/updatePassword
// TODO: Implement password change workflow.

### PUT /user/{id}/updateEmails
// TODO: Implement email update workflow.

### PUT /user/{id}/updatePhoneNumber
// TODO: Implement phone number update workflow.

### DELETE /user/{id}
// TODO: Implement user delete endpoint.

Access: Admin only

## RegistrationServlet
Servlet for User registration.

### POST /register

Payload:

`
{
    "username": <username>,
    "firstName": <firstName>,
    "lastName": <lastName>,
    "emails": [<email1>, <email2>],
    "phoneNumber": <phoneNumber>,
    "password": <password>
}
`

## AuthenicationServlet
Servlet for authentication. Sets the JWT cookie upon successful authentication.

### POST /login
Payload:
`
{
    "username": <username>,
    "password": <password>
}
`

## UsernameValidationServlet
Servlet for validating a username for availability and format.

### POST /validateNewUsername
Validate the provided new username for availability.

Payload:

`
{
    "username": <username>
}
`

Response:

`
{
    "result": <true/false>,
    "message": <message>
}
`

## CategoryServlet
Servlet for Category CRUD operations.

### GET /categories
Get the categories for the current User.

### GET /categories/{id}
Get the specific category for the current User.

### POST /categories
Create a new Category.

Payload:

`
{
    "name": <name>,
    "description": <description>
}
`

### PUT /categories/{id}
Update the category.

Payload:

`
{
    "name": <name>,
    "description": <description>
}
`

### DELETE /categories/{id}
Delete the category.