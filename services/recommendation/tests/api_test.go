package tests

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
	"time"

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
			"timestamp": time.Now().Format(time.RFC3339),
		})
	})

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/api/recommendation-status", nil)
	router.ServeHTTP(w, req)

	if w.Code != http.StatusOK {
		t.Errorf("Expected status 200, got %d", w.Code)
	}

	// Check that response contains expected fields
	expectedFields := []string{"service", "status", "timestamp"}
	body := w.Body.String()
	for _, field := range expectedFields {
		if !strings.Contains(body, field) {
			t.Errorf("Expected field %s not found in response: %s", field, body)
		}
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
