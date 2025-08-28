package data

import (
	"encoding/json"
	"log"
	"net/http"
	"os"
	"strings"
	"time"
)

type Origami struct {
	ID               int      `json:"id"`
	Name             string   `json:"name"`
	Category         string   `json:"category"`
	Difficulty       string   `json:"difficulty"`
	Tags             []string `json:"tags"`
	ShortDescription string   `json:"short_description"`
	Description      string   `json:"description"`
	ImageUrl         string   `json:"image_url"`
	CreatedAt        string   `json:"created_at"`
}

func GetDailyOrigami() []Origami {
	catalogueURL := os.Getenv("CATALOGUE_API_URL")
	if catalogueURL == "" {
		catalogueURL = "http://localhost:5000/api/products" // default fallback
	}

	// Create HTTP client with timeout
	client := &http.Client{
		Timeout: 10 * time.Second,
	}

	log.Printf("Fetching data from catalogue service: %s", catalogueURL)

	resp, err := client.Get(catalogueURL)
	if err != nil {
		log.Printf("Error fetching catalogue data: %v", err)
		return []Origami{}
	}
	defer resp.Body.Close()

	// Check response status
	if resp.StatusCode != http.StatusOK {
		log.Printf("Catalogue service returned status: %d", resp.StatusCode)
		return []Origami{}
	}

	// Check content type
	contentType := resp.Header.Get("Content-Type")
	if contentType != "" && !strings.Contains(contentType, "application/json") {
		log.Printf("Unexpected content type from catalogue service: %s", contentType)
		return []Origami{}
	}

	var products []Origami
	if err := json.NewDecoder(resp.Body).Decode(&products); err != nil {
		log.Printf("Error decoding catalogue response: %v", err)
		return []Origami{}
	}

	if len(products) == 0 {
		log.Printf("No products received from catalogue service")
		return []Origami{}
	}

	log.Printf("Successfully fetched %d products from catalogue service", len(products))
	return products
}
