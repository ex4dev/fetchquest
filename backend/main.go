package main

import (
	"database/sql"
	"net/http"
	"strings"

	"github.com/labstack/echo/v4"
	"google.golang.org/api/idtoken"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

// Used as a key in echo.Context to store the user ID after validating the user's JWT
var userId string = "userId"

type User struct {
	gorm.Model
	Name   string
	Email  string
	Events []*UserEvent
}

type UserEvent struct {
	UserId      int64
	User        *User `gorm:"foreignKey:UserId"`
	EventId     int64
	Event       *Event `gorm:"foreignKey:EventId"`
	CompletedAt sql.NullTime
}

type Event struct {
	gorm.Model
	Lat         float32
	Long        float32
	Title       string
	Description string
	Hours       int
	CreatedBy   int64
	Creator     *User `gorm:"foreignKey:CreatedBy"`
}

func main() {
	db, err := gorm.Open(sqlite.Open("data.db"), &gorm.Config{})
	if err != nil {
		panic("failed to initialize database: " + err.Error())
	}

	// Migrate the schema
	err = db.AutoMigrate(&User{}, &Event{})
	if err != nil {
		panic("error running migrations: " + err.Error())
	}

	e := echo.New()
	e.GET("/", func(c echo.Context) error {
		return c.String(http.StatusOK, "Hello, World!")
	})

	e.GET("/events", func(c echo.Context) error {
		events := make([]Event, 0)
		result := db.Find(&events)
		if result.Error != nil {
			return c.String(http.StatusInternalServerError, "Error")
		}
		return c.JSON(http.StatusOK, events)
	})

	e.POST("/events", func(c echo.Context) error {
		event := Event{}
		err := echo.QueryParamsBinder(c).
			Float32("lat", &event.Lat).
			Float32("long", &event.Long).
			String("title", &event.Title).
			String("description", &event.Description).
			Int("hours", &event.Hours).
			BindError()

		if err != nil {
			return c.String(http.StatusBadRequest, "Bad request")
		}

		result := db.Create(&event)
		if result.Error != nil {
			return c.String(http.StatusInternalServerError, "Error")
		}
		return c.JSON(http.StatusOK, map[string]any{"success": true})
	})

	e.POST("/token-auth", func(c echo.Context) error {
		var token string

		payload, err := idtoken.Validate(c.Request().Context(), token, "")
		if err != nil {
			return err
		}

		return c.JSON(200, payload)
	})

	authMiddleware := func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			authn := c.Request().Header.Get("Authorization")
			if authn == "" {
				return echo.ErrUnauthorized
			}

			token := strings.TrimPrefix(authn, "Bearer ")

			payload, err := idtoken.Validate(c.Request().Context(), token, "")

			c.Set(userId, payload.Claims["id"])

			if err != nil {
				return echo.ErrUnauthorized
			}

			return next(c)
		}
	}

	e.Use(authMiddleware)

	e.Logger.Fatal(e.Start(":8080"))
}
