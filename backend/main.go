package main

import (
	"database/sql"
	"net/http"

	"github.com/labstack/echo/v4"
	"gorm.io/driver/sqlite"
	"gorm.io/gorm"
)

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
	Points      int32
	CreatedBy   int64
	Creator     *User `gorm:"foreignKey:CreatedBy"`
}

type DB struct {
	gorm.DB
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

	e.Logger.Fatal(e.Start(":8080"))
}
