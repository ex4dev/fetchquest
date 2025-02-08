# FetchQuest API Specification

## Authentication

1. Make a POST request to `/login` with a JSON object in the request body. The object should contain one field, `token`, which is the user's [ID Token](https://developers.google.com/identity/sign-in/android/backend-auth#send-the-id-token-to-your-server).
   The server will return the decoded content of the JWT. You can also do this on the client; it really doesn't matter. The token is verified with every API request.

2. When making any subsequent requests, provide the token in the Authorization header:
   ```
   Authorization: Bearer <google-token-here>
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

| Field | Data type |
| ----- | --------- |
| name  | string    |
| email | string    |
