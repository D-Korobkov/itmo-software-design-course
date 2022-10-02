package github

//go:generate mockgen -destination mocks.go -package github github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/github RepositoryFinder

import (
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"net/http"
	"net/url"
)

const (
	DefaultRepositoriesPerPage    = uint(30)
	MaxSearchedRepositoriesNumber = uint(1000)
)

type RepositoryFinder interface {
	SearchRepositories(request SearchRepositoriesRequest, repositoriesPerPage uint, page uint) (*SearchRepositoriesResponse, error)
}

type repositoryFinderImpl struct {
	httpClient *http.Client
	baseUrl    *url.URL
	authToken  string
}

func NewRepositoryFinder(config ApiConfig, httpClient *http.Client) (RepositoryFinder, error) {
	baseUrl, err := url.Parse(config.BaseUrl)
	if err != nil {
		return nil, err
	}

	finder := repositoryFinderImpl{
		httpClient: httpClient,
		baseUrl:    baseUrl,
		authToken:  config.AuthToken,
	}
	return &finder, nil
}

func (finder *repositoryFinderImpl) SearchRepositories(searchRequest SearchRepositoriesRequest, repositoriesPerPage uint, page uint) (*SearchRepositoriesResponse, error) {
	requestUrl := finder.baseUrl.JoinPath("search", "repositories")
	requestUrl.RawQuery = searchRequest.ToRawQuery(repositoriesPerPage, page)

	request, err := http.NewRequest(http.MethodGet, requestUrl.String(), nil)
	if err != nil {
		return nil, fmt.Errorf("failed to create an HTTP request: %w", err)
	}

	request.Header.Set("Accept", "application/vnd.github+json")
	request.Header.Set("Authorization", fmt.Sprintf("Bearer %s", finder.authToken))

	response, err := finder.httpClient.Do(request)
	if err != nil {
		return nil, fmt.Errorf("failed to send an HTTP request: %w", err)
	}

	defer response.Body.Close()
	if response.StatusCode != http.StatusOK {
		errorResponse, err := readBody[ErrorResponse](response)
		if err != nil {
			return nil, err
		}
		return nil, errors.New(errorResponse.Message)
	}

	return readBody[SearchRepositoriesResponse](response)
}

func readBody[R any](response *http.Response) (*R, error) {
	responseBody, err := io.ReadAll(response.Body)
	if err != nil {
		return nil, fmt.Errorf("failed to read response responseBody: %w", err)
	}

	var parsedResponseBody R
	err = json.Unmarshal(responseBody, &parsedResponseBody)
	if err != nil {
		return nil, fmt.Errorf("failed to parse resonse body: %w", err)
	}

	return &parsedResponseBody, nil
}
