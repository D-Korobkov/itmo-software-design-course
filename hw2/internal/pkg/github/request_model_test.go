package github_test

import (
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/github"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/clock"
	"github.com/stretchr/testify/assert"
	"testing"
	"time"
)

func TestToRawQueryTransformer(t *testing.T) {
	request := github.SearchRepositoriesRequest{
		Topic:        "jekyll",
		CreatedSince: time.Date(2022, 1, 1, 3, 0, 0, 0, clock.Msk),
		CreatedTill:  time.Date(2022, 1, 2, 3, 0, 0, 0, clock.Msk),
	}
	perPage := uint(42)
	page := uint(24)

	rawQuery := request.ToRawQuery(perPage, page)
	expectedRawQuery := "q=topic:jekyll+created:2022-01-01T00:00:00Z..2022-01-02T00:00:00Z&per_page=42&page=24"
	assert.Equal(t, expectedRawQuery, rawQuery)
}
