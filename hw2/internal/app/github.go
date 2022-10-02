package app

import (
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/github"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/clock"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/gmath"
	"time"
)

type GithubStatisticsCollector interface {
	CountRecentlyCreatedRepositories(topic string, periodHours int) ([]int, error)
}

type collectorImpl struct {
	finder github.RepositoryFinder
	clock  clock.Clock
}

func NewGithubStatisticsCollector(finder github.RepositoryFinder, clock clock.Clock) GithubStatisticsCollector {
	return &collectorImpl{
		finder: finder,
		clock:  clock,
	}
}

func (collector *collectorImpl) CountRecentlyCreatedRepositories(topic string, periodHours int) ([]int, error) {
	now := collector.clock.Now()
	nHoursAgo := now.Add(-time.Duration(periodHours) * time.Hour)

	request := github.SearchRepositoriesRequest{
		Topic:        topic,
		CreatedSince: nHoursAgo,
		CreatedTill:  now,
	}

	statistics := make([]int, periodHours)
	var searchedRepositoriesCount uint
	for page := uint(1); ; page++ {
		repositoriesPerPage := gmath.Min(
			github.DefaultRepositoriesPerPage,
			github.MaxSearchedRepositoriesNumber-searchedRepositoriesCount,
		)
		repositories, err := collector.finder.SearchRepositories(request, repositoriesPerPage, page)
		if err != nil {
			return nil, err
		}

		for idx := range repositories.Items {
			item := &repositories.Items[idx]

			createdAtUntilNowHours := int(now.Sub(item.CreatedAt).Hours())
			if createdAtUntilNowHours == periodHours {
				statistics[createdAtUntilNowHours-1]++
			} else {
				statistics[createdAtUntilNowHours]++
			}
		}

		searchedRepositoriesCount += uint(len(repositories.Items))
		if searchedRepositoriesCount == gmath.Min(repositories.TotalCount, github.MaxSearchedRepositoriesNumber) {
			break
		}
	}

	return statistics, nil
}
