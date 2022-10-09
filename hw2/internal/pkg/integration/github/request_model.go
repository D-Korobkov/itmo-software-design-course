package github

import (
	"fmt"
	"time"
)

type SearchRepositoriesRequest struct {
	Topic        Topic
	CreatedSince time.Time
	CreatedTill  time.Time
}

func (request SearchRepositoriesRequest) ToRawQuery(repositoriesPerPage int, page int) string {
	searchQuery := fmt.Sprintf("topic:%s+created:%s..%s",
		request.Topic,
		request.CreatedSince.UTC().Format(time.RFC3339),
		request.CreatedTill.UTC().Format(time.RFC3339),
	)

	return fmt.Sprintf("q=%s&per_page=%d&page=%d", searchQuery, repositoriesPerPage, page)
}
