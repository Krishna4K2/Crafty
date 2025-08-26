package data

import (
	"encoding/json"
	"log"
	"net/http"
	"os"
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

	resp, err := http.Get(catalogueURL)
	if err != nil {
		log.Printf("Error fetching catalogue data: %v", err)
		return []Origami{}
	}
	defer resp.Body.Close()

	var products []Origami
	if err := json.NewDecoder(resp.Body).Decode(&products); err != nil {
		log.Printf("Error decoding catalogue response: %v", err)
		return []Origami{}
	}
	return products
}
