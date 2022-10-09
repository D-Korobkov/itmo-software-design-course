package app

import (
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/integration/github"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/clock"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/gmath"
	"time"
)

type GithubStatisticsCollector interface {
	CountRecentlyCreatedRepositories(topic github.Topic, periodHours int) ([]int, error)
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

func (collector *collectorImpl) CountRecentlyCreatedRepositories(topic github.Topic, periodHours int) ([]int, error) {
	if periodHours < 1 || periodHours > 24 {
		return nil, errors.New("`periodHours` must be between 1 and 24")
	}

	now := collector.clock.Now()
	nHoursAgo := now.Add(-time.Duration(periodHours) * time.Hour)

	request := github.SearchRepositoriesRequest{
		Topic:        topic,
		CreatedSince: nHoursAgo,
		CreatedTill:  now,
	}

	statistics := make([]int, periodHours)
	for alreadyFetchedRepos, page := 0, 1; ; page++ {
		repositoriesPerPage := gmath.Min(github.DefaultReposPerPage, github.MaxFetchedRepos-alreadyFetchedRepos)
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

		alreadyFetchedRepos += len(repositories.Items)
		if alreadyFetchedRepos == gmath.Min(repositories.TotalCount, github.MaxFetchedRepos) {
			break
		}
	}

	return statistics, nil
}
