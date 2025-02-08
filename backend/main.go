package main

import (
	"database/sql"
	"net/http"
	"strconv"
	"strings"
	"time"

	"github.com/labstack/echo/v4"
	"google.golang.org/api/idtoken"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
	"gorm.io/gorm/clause"
)

// Used as a key in echo.Context to store the user ID after validating the user's JWT
var jwt string = "googleJWT"

type User struct {
	gorm.Model
	Name         string
	Email        string
	Picture      string
	GoogleUserId string
	Events       []*Registration
}

type Registration struct {
	UserId      uint   `gorm:"uniqueIndex:user_event"`
	User        *User  `gorm:"foreignKey:UserId"`
	EventId     uint   `gorm:"uniqueIndex:user_event"`
	Event       *Event `gorm:"foreignKey:EventId"`
	CompletedAt sql.NullTime
}

type Event struct {
	gorm.Model
	Lat         float32
	Long        float32
	Title       string
	Description string
	Date        time.Time
	Hours       int
	CreatedBy   uint
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

	e.POST("/events/:event/join", func(c echo.Context) error {
		user, err := getUser(db, c)
		if err != nil {
			return err
		}

		eventID, err := strconv.ParseInt(c.Param("event"), 10, 64)
		if err != nil {
			return echo.ErrBadRequest
		}

		reg := Registration{UserId: user.ID, EventId: uint(eventID)}

		result := db.Create(&reg)
		if result.Error != nil {
			return c.String(http.StatusInternalServerError, "Error")
		}

		return c.JSON(http.StatusOK, reg)
	})

	e.POST("/events/:event/leave", func(c echo.Context) error {
		eventID, err := strconv.ParseInt(c.Param("event"), 10, 64)
		if err != nil {
			return echo.ErrBadRequest
		}
		uid, err := getUser(db, c)
		if err != nil {
			return err
		}
		result := db.Delete(&Registration{}, "user_id = ? AND event_id = ?", uid, eventID)
		if result.RowsAffected > 0 {
			return c.JSON(200, map[string]any{"success": true})
		} else {
			return c.JSON(404, map[string]any{"success": false})
		}
	})

	e.POST("/events", func(c echo.Context) error {
		event := Event{}

		user, err := getUser(db, c)
		if err != nil {
			return err
		}

		event.CreatedBy = user.ID

		err = echo.QueryParamsBinder(c).
			Float32("lat", &event.Lat).
			Float32("long", &event.Long).
			String("title", &event.Title).
			String("description", &event.Description).
			Int("hours", &event.Hours).
			BindError()

		if err != nil {
			return c.String(http.StatusBadRequest, "Bad request")
		}

		result := db.Model(Event{}).Clauses(clause.Returning{}).Create(&event)
		if result.Error != nil {
			return c.String(http.StatusInternalServerError, "Error")
		}
		return c.JSON(http.StatusOK, result)
	})

	e.GET("/me", func(c echo.Context) error {
		user, err := getUser(db, c)
		if err != nil {
			return err
		}
		return c.JSON(200, user)
	})

	authMiddleware := func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			authn := c.Request().Header.Get("Authorization")
			if authn == "" {
				return echo.ErrUnauthorized
			}

			token := strings.TrimPrefix(authn, "Bearer ")

			payload, err := idtoken.Validate(c.Request().Context(), token, "")

			c.Set(jwt, payload)

			if err != nil {
				return echo.ErrUnauthorized
			}

			return next(c)
		}
	}

	e.Use(authMiddleware)

	e.Logger.Fatal(e.Start(":8080"))
}

func getUser(db *gorm.DB, c echo.Context) (*User, error) {
	jwt := c.Get(jwt).(*idtoken.Payload)
	uid := jwt.Claims["sub"].(string)

	user := &User{}
	result := db.First(user, "google_user_id = ?", uid)

	if result.Error == gorm.ErrRecordNotFound {
		// The user doesn't exist. Create a new one
		user.Name = jwt.Claims["name"].(string)
		user.Email = jwt.Claims["email"].(string)
		user.Picture = jwt.Claims["picture"].(string)
		user.GoogleUserId = uid

		result := db.Model(&User{}).Clauses(clause.Returning{}).Create(user)
		if result.Error != nil {
			return nil, result.Error
		}
		return user, nil
	} else if result.Error != nil {
		return nil, result.Error
	}

	return user, nil
}
