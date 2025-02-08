# FetchQuest API Specification

## Events

### `Event` spec

| Field       | Data type      |
| ----------- | -------------- |
| lat         | float          |
| long        | float          |
| title       | string         |
| description | string         |
| points      | string         |
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
