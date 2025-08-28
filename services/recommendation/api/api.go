package api

import (
	"encoding/json"
	"math/rand"
	"net"
	"net/http"
	"os"
	"recommendation/data"
	"time"

	"github.com/gin-gonic/gin"
)

// SystemInfo represents system information
type SystemInfo struct {
	Hostname     string
	IPAddress    string
	IsContainer  bool
	IsKubernetes bool
}

// GetSystemInfo returns system information
func GetSystemInfo() SystemInfo {
	hostname, _ := os.Hostname()
	addrs, _ := net.InterfaceAddrs()
	ip := ""
	for _, addr := range addrs {
		if ipnet, ok := addr.(*net.IPNet); ok && !ipnet.IP.IsLoopback() {
			if ipnet.IP.To4() != nil {
				ip = ipnet.IP.String()
				break
			}
		}
	}
	isContainer := false
	if _, err := os.Stat("/.dockerenv"); err == nil {
		isContainer = true
	}
	isKubernetes := false

	return SystemInfo{
		Hostname:     hostname,
		IPAddress:    ip,
		IsContainer:  isContainer,
		IsKubernetes: isKubernetes,
	}
}

// Config represents the structure of our configuration file
type Config struct {
	Version string `json:"version"`
}

// loadConfig reads the configuration file and returns a Config struct
func loadConfig() (Config, error) {
	file, err := os.Open("config.json")
	if err != nil {
		return Config{}, err
	}
	defer file.Close()

	config := Config{}
	decoder := json.NewDecoder(file)
	err = decoder.Decode(&config)
	return config, err
}

func GetOrigamiOfTheDay(c *gin.Context) {
	origamis := data.GetDailyOrigami()

	// Handle empty response
	if len(origamis) == 0 {
		c.JSON(http.StatusServiceUnavailable, gin.H{
			"error": "No origami data available. Please check catalogue service.",
		})
		return
	}

	r := rand.New(rand.NewSource(time.Now().UnixNano()))
	selectedOrigami := origamis[r.Intn(len(origamis))]

	c.JSON(http.StatusOK, selectedOrigami)
}

// StartAPI initializes and starts the recommendation service API
// This function provides an alternative way to start the service with all routes configured
// It can be used as an alternative to the main function in main.go
func StartAPI() {
	r := gin.Default()

	// Load templates and static files
	r.LoadHTMLGlob("templates/*")
	r.Static("/static", "./static")

	// API routes
	r.GET("/api/origami-of-the-day", GetOrigamiOfTheDay)
	r.GET("/api/recommendation-status", func(c *gin.Context) {
		c.JSON(http.StatusOK, gin.H{
			"status":    "operational",
			"service":   "recommendation",
			"timestamp": time.Now().Format(time.RFC3339),
		})
	})

	// Web routes
	r.GET("/", func(c *gin.Context) {
		config, err := loadConfig()
		if err != nil {
			c.String(http.StatusInternalServerError, "Internal Server Error")
			return
		}

		systemInfo := GetSystemInfo()
		c.HTML(http.StatusOK, "index.html", gin.H{
			"Version":    config.Version,
			"SystemInfo": systemInfo,
		})
	})

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	r.Run(":" + port)
}
