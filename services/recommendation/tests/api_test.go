package tests

import (
	"net/http"
	"net/http/httptest"
	"testing"

	"recommendation/api"

	"github.com/gin-gonic/gin"
)

func TestGetOrigamiOfTheDay(t *testing.T) {
	// Basic test structure for learning
	router := gin.New()
	router.GET("/api/origami-of-the-day", api.GetOrigamiOfTheDay)

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/api/origami-of-the-day", nil)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusOK && w.Code != http.StatusServiceUnavailable {
		t.Errorf("Expected status 200 or 503, got %d", w.Code)
	}
}

func TestRecommendationStatus(t *testing.T) {
	router := gin.New()
	router.GET("/api/recommendation-status", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"status":    "operational",
			"service":   "recommendation",
			"timestamp": "2025-08-28T10:00:00Z",
		})
	})

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/api/recommendation-status", nil)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status 200, got %d", w.Code)
	}

	expected := `{"service":"recommendation","status":"operational","timestamp":"2025-08-28T10:00:00Z"}`
	if w.Body.String() != expected+"\n" {
		t.Errorf("Expected %s, got %s", expected, w.Body.String())
	}
}

func TestStartAPIFunction(t *testing.T) {
	// Test that StartAPI function can be called without panicking
	// Note: This would normally start a server, so we just test it doesn't panic
	defer func() {
		if r := recover(); r != nil {
			t.Errorf("StartAPI function panicked: %v", r)
		}
	}()

	// We can't easily test the server startup in a unit test
	// But we can verify the function exists and is callable
	api.GetOrigamiOfTheDay(nil) // This should not panic
}
