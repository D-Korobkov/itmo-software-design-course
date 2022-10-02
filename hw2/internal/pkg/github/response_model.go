package github

import "time"

type SearchRepositoriesResponse struct {
	TotalCount uint                             `json:"total_count"`
	Items      []SearchRepositoriesResponseItem `json:"items"`
}

type SearchRepositoriesResponseItem struct {
	Id        int       `json:"id"`
	CreatedAt time.Time `json:"created_at"`
}

type ErrorResponse struct {
	Message string `json:"message"`
}
