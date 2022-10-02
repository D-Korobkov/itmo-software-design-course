package github_test

import (
	"context"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/github"
	"github.com/stretchr/testify/assert"
	"net"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"
)

var apiConfig = github.ApiConfig{
	BaseUrl:   "http://test.org",
	AuthToken: "test",
}

func TestErrorResponseProcessing(t *testing.T) {
	responseBody := []byte(`{"message": "boom"}`)
	client, closeClient := mkStubGithubServer(t, http.StatusInternalServerError, responseBody)
	defer closeClient()

	finder, err := github.NewRepositoryFinder(apiConfig, client)
	assert.Nil(t, err)

	resp, err := finder.SearchRepositories(github.SearchRepositoriesRequest{}, 1, 1)
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
	client, closeClient := mkStubGithubServer(t, http.StatusOK, responseBody)
	defer closeClient()

	finder, err := github.NewRepositoryFinder(apiConfig, client)
	assert.Nil(t, err)

	resp, err := finder.SearchRepositories(github.SearchRepositoriesRequest{}, 1, 1)
	assert.Nil(t, err)

	expectedResponse := github.SearchRepositoriesResponse{
		TotalCount: 666,
		Items: []github.SearchRepositoriesResponseItem{
			{
				Id:        111,
				CreatedAt: time.Date(2022, 1, 1, 1, 1, 1, 0, time.UTC),
			},
		},
	}
	assert.Equal(t, expectedResponse, *resp)
}

func mkStubGithubServer(t *testing.T, respStatusCode int, respBody []byte) (*http.Client, func()) {
	handlerF := http.HandlerFunc(
		func(w http.ResponseWriter, r *http.Request) {
			assert.Equal(t, "/search/repositories", r.URL.EscapedPath())
			assert.Equal(t, "application/vnd.github+json", r.Header.Get("Accept"))
			assert.Equal(t, "Bearer test", r.Header.Get("Authorization"))

			w.WriteHeader(respStatusCode)
			w.Write(respBody)
		},
	)

	stubServer := httptest.NewServer(handlerF)

	client := &http.Client{
		Transport: &http.Transport{
			DialContext: func(_ context.Context, network, _ string) (net.Conn, error) {
				return net.Dial(network, stubServer.Listener.Addr().String())
			},
		},
	}

	return client, stubServer.Close
}
