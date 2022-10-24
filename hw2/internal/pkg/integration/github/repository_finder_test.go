package github_test

import (
	"context"
	"fmt"
	github2 "github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/integration/github"
	"github.com/stretchr/testify/assert"
	"net"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"
)

var apiConfig = github2.ApiConfig{
	BaseUrl:   "http://test.org",
	AuthToken: "test",
}

func TestErrorResponseProcessing(t *testing.T) {
	responseBody := []byte(`{"message": "boom"}`)
	handler := mkSearchRepositoriesRequestHandler(t, func(w http.ResponseWriter) {
		w.WriteHeader(http.StatusInternalServerError)
		w.Write(responseBody)
	})

	client, closeClient := mkStubServer(handler)
	defer closeClient()

	finder, err := github2.NewRepositoryFinder(apiConfig, client)
	assert.Nil(t, err)

	resp, err := finder.SearchRepositories(github2.SearchRepositoriesRequest{}, 1)
	assert.Nil(t, resp)

	expectedErrString := "boom"
	assert.EqualError(t, err, expectedErrString)
}

func TestSuccessResponseProcessing(t *testing.T) {
	responseBody := []byte(`{
		"total_count": 666,
		"items": [
			{
				"id": 111,
				"created_at": "2022-01-01T01:01:01Z"
			}
		]
	}`)
	handler := mkSearchRepositoriesRequestHandler(t, func(w http.ResponseWriter) {
		w.Write(responseBody)
	})

	client, closeClient := mkStubServer(handler)
	defer closeClient()

	finder, err := github2.NewRepositoryFinder(apiConfig, client)
	assert.Nil(t, err)

	resp, err := finder.SearchRepositories(github2.SearchRepositoriesRequest{}, 1)
	assert.Nil(t, err)

	expectedResponse := github2.SearchRepositoriesResponse{
		TotalCount: 666,
		Items: []github2.SearchRepositoriesResponseItem{
			{
				Id:        111,
				CreatedAt: time.Date(2022, 1, 1, 1, 1, 1, 0, time.UTC),
			},
		},
	}
	assert.Equal(t, expectedResponse, *resp)
}

func mkSearchRepositoriesRequestHandler(t *testing.T, doResponse func(w http.ResponseWriter)) http.HandlerFunc {
	expectedPath := "/search/repositories"
	expectedAcceptHeader := "application/vnd.github+json"
	expectedAuthHeader := fmt.Sprintf("Bearer %s", apiConfig.AuthToken)

	return func(w http.ResponseWriter, r *http.Request) {
		assert.Equal(t, expectedPath, r.URL.EscapedPath())
		assert.Equal(t, expectedAcceptHeader, r.Header.Get("Accept"))
		assert.Equal(t, expectedAuthHeader, r.Header.Get("Authorization"))

		doResponse(w)
	}
}

func mkStubServer(handlerFunc http.HandlerFunc) (*http.Client, func()) {
	stubServer := httptest.NewServer(handlerFunc)

	client := &http.Client{
		Transport: &http.Transport{
			DialContext: func(_ context.Context, network, _ string) (net.Conn, error) {
				return net.Dial(network, stubServer.Listener.Addr().String())
			},
		},
	}

	return client, stubServer.Close
}
