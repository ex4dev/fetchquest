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

// The same as gorm's `gorm.Model` struct, but with camelCase JSON serialized names
type Model struct {
	ID        uint           `gorm:"primarykey" json:"id"`
	CreatedAt time.Time      `json:"createdAt"`
	UpdatedAt time.Time      `json:"updatedAt"`
	DeletedAt gorm.DeletedAt `gorm:"index" json:"deletedAt"`
}

type User struct {
	Model
	Name         string          `json:"name"`
	Email        string          `json:"email"`
	Picture      string          `json:"picture"`
	GoogleUserId string          `json:"googleUserId"`
	Events       []*Registration `json:"registeredEvents"`
}

type Registration struct {
	Model
	UserId      uint         `gorm:"uniqueIndex:user_event" json:"userId"`
	User        *User        `gorm:"foreignKey:UserId" json:"user"`
	EventId     uint         `gorm:"uniqueIndex:user_event" json:"eventId"`
	Event       *Event       `gorm:"foreignKey:EventId" json:"event"`
	CompletedAt sql.NullTime `json:"completedAt"`
}

type Event struct {
	Model
	Lat         float32   `json:"lat"`
	Long        float32   `json:"long"`
	Title       string    `json:"title"`
	Description string    `json:"description"`
	Date        time.Time `json:"date"`
	Hours       int       `json:"hours"`
	CreatedBy   uint      `json:"creatorId"`
	Creator     *User     `gorm:"foreignKey:CreatedBy" json:"createdBy"`
}

type Metrics struct {
	CurrentStreak  int `json:"currentStreak"`
	HoursPastWeek  int `json:"hoursPastWeek"`
	HoursPastMonth int `json:"hoursPastMonth"`
	HoursPastYear  int `json:"hoursPastYear"`
	HoursAllTime   int `json:"hoursAllTime"`
}

func main() {
	db, err := gorm.Open(sqlite.Open("data.db"), &gorm.Config{})
	if err != nil {
		panic("failed to initialize database: " + err.Error())
	}

	// Migrate the schema
	err = db.AutoMigrate(&User{}, &Event{}, &Registration{})
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

	e.GET("/events/:event/attendees", func(c echo.Context) error {
		eventID, err := strconv.ParseInt(c.Param("event"), 10, 64)
		if err != nil {
			return echo.ErrBadRequest
		}

		registrations := []Registration{}
		result := db.Preload("User").Find(&registrations, "event_id = ? AND deleted_at IS NOT NULL", eventID)
		if result.Error != nil {
			return c.String(http.StatusInternalServerError, "Error")
		}

		return c.JSON(http.StatusOK, registrations)
	})

	e.GET("/my-events", func(c echo.Context) error {
		user, err := getUser(db, c)
		if err != nil {
			return err
		}
		events := make([]Event, 0)
		result := db.Find(&events, "id IN (SELECT id FROM registrations WHERE deleted_at IS NULL AND user_id = ?)", user.ID)
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
		user, err := getUser(db, c)
		if err != nil {
			return err
		}

		result := db.Unscoped().Delete(&Registration{}, "user_id = ? AND event_id = ?", user.ID, eventID)

		if result.RowsAffected > 0 {
			return c.JSON(200, map[string]any{"success": true})
		} else {
			return c.JSON(404, map[string]any{"success": false})
		}
	})

	e.POST("/events/:event/sign-off", func(c echo.Context) error {
		eventID, err := strconv.ParseInt(c.Param("event"), 10, 64)
		if err != nil {
			return echo.ErrBadRequest
		}
		user, err := getUser(db, c)
		if err != nil {
			return err
		}

		var attendeeID uint
		var signedOff bool

		err = echo.FormFieldBinder(c).Uint("attendee", &attendeeID).Bool("signedOff", &signedOff).BindError()
		if err != nil {
			return err
		}

		var completionTime sql.NullTime

		if signedOff {
			completionTime.Valid = true
			completionTime.Time = time.Now()
		}

		result := db.Model(&Registration{}).Where("user_id = ? AND event_id = ? AND (SELECT creatorId FROM events WHERE id = ?) = ?", attendeeID, eventID, eventID, user.ID).Update("completed_at", completionTime)

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

		err = echo.FormFieldBinder(c).
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

	e.GET("/stats", func(c echo.Context) error {
		user, err := getUser(db, c)
		if err != nil {
			return err
		}

		var allTime, pastYear, pastMonth, pastWeek int
		result := db.Table("events").Where("id IN (SELECT id FROM registrations WHERE userId = ? AND deleted_at IS NOT NULL AND completed_at IS NOT NULL").Select("sum(hours)").Scan(&allTime)
		if result.Error != nil {
			return result.Error
		}

		result = db.Table("events").Where("id IN (SELECT id FROM registrations WHERE userId = ? AND deleted_at IS NOT NULL AND completed_at IS NOT NULL AND completed_at > date('now', '-1 year'))").Select("sum(hours)").Scan(&pastYear)
		if result.Error != nil {
			return result.Error
		}

		result = db.Table("events").Where("id IN (SELECT id FROM registrations WHERE userId = ? AND deleted_at IS NOT NULL AND completed_at IS NOT NULL AND completed_at > date('now', '-30 days'))").Select("sum(hours)").Scan(&pastMonth)
		if result.Error != nil {
			return result.Error
		}

		result = db.Table("events").Where("id IN (SELECT id FROM registrations WHERE userId = ? AND deleted_at IS NOT NULL AND completed_at IS NOT NULL AND completed_at > date('now', '-7 days'))").Select("sum(hours)").Scan(&pastWeek)
		if result.Error != nil {
			return result.Error
		}

		rows, err := db.Raw("SELECT completed_at FROM registrations WHERE userId = ? AND deleted_at IS NOT NULL AND completed_at IS NOT NULL ORDER BY completed_at DESC", user.ID).Rows()
		if err != nil {
			return err
		}

		defer rows.Close()
		var lastDate *time.Time
		streak := 0
		for rows.Next() {
			var currentDate time.Time
			rows.Scan(&currentDate)
			if lastDate != nil {
				if truncateToDay(*lastDate).Sub(truncateToDay(currentDate)).Hours() > 24 {
					break
				}
				streak++
			}
			lastDate = &currentDate
		}
		if result.Error != nil {
			return result.Error
		}

		return c.JSON(200, Metrics{
			CurrentStreak:  streak,
			HoursPastWeek:  pastWeek,
			HoursPastMonth: pastMonth,
			HoursPastYear:  pastYear,
			HoursAllTime:   allTime,
		})
	})

	authMiddleware := func(next echo.HandlerFunc) echo.HandlerFunc {
		return func(c echo.Context) error {
			if c.Path() == "/events" {
				return next(c)
			}
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

func truncateToDay(t time.Time) time.Time {
	return time.Date(t.Year(), t.Month(), t.Day(), 0, 0, 0, 0, t.Location())
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
