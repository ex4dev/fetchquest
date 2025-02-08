# FetchQuest API Specification

## Authentication

When making any authenticated requests, provide the token in the Authorization header:

```
Authorization: Bearer <google-token-here>
```

To fetch the user's information, make a POST request to `/me`. This will return a JSON object that looks something like this:

```json
{
  "name": "Brendan Swanson",
  "googleUserId": "1209381209381209311",
  "email": "abc.xyz@gmail.com",
  "picture": "https://lh3.google.com/..."
}
```

## Events

### `Event` spec

| Field       | Data type      |
| ----------- | -------------- |
| lat         | float          |
| long        | float          |
| title       | string         |
| description | string         |
| hours       | string         |
| createdBy   | long (User ID) |

### GET `/events`

Lists all events. Returns a JSON array of `Event` objects.

### POST `/events`

Creates a new event. Accepts an `Event` JSON object in the request body.

## Users

### `User` spec

| Field   | Data type |
| ------- | --------- |
| name    | string    |
| email   | string    |
| picture | string    |
