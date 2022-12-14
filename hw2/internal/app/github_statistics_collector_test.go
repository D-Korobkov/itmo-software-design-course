package app_test

import (
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/app"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/integration/github"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/clock"
	"github.com/golang/mock/gomock"
	"github.com/stretchr/testify/assert"
	"testing"
	"time"
)

type CommonData struct {
	Now                       time.Time
	Topic                     github.Topic
	PeriodHours               int
	SearchRepositoriesRequest github.SearchRepositoriesRequest
}

type Mocks struct {
	Clock  *clock.MockClock
	Finder *github.MockRepositoryFinder
}

func setupTest(t *testing.T) (CommonData, Mocks, func()) {
	now := time.Date(2022, 10, 2, 16, 0, 0, 0, clock.Msk)
	topic := github.Topic("test")
	periodHours := 3
	commonData := CommonData{
		Now:         now,
		Topic:       topic,
		PeriodHours: periodHours,
		SearchRepositoriesRequest: github.SearchRepositoriesRequest{
			Topic:        topic,
			CreatedSince: now.Add(-time.Duration(periodHours) * time.Hour),
			CreatedTill:  now,
		},
	}

	ctrl := gomock.NewController(t)

	mockClock := clock.NewMockClock(ctrl)
	mockFinder := github.NewMockRepositoryFinder(ctrl)
	mocks := Mocks{
		Clock:  mockClock,
		Finder: mockFinder,
	}

	return commonData, mocks, ctrl.Finish
}

func TestNoRepositoriesWithTheGivenTopic(t *testing.T) {
	commonData, mocks, teardownTest := setupTest(t)
	defer teardownTest()

	request := commonData.SearchRepositoriesRequest
	response := github.SearchRepositoriesResponse{TotalCount: 0}

	mocks.Clock.EXPECT().Now().Return(commonData.Now).AnyTimes()
	mocks.Finder.EXPECT().SearchRepositories(request, gomock.Any()).Return(&response, nil)

	collector := app.NewGithubStatisticsCollector(mocks.Finder, mocks.Clock)
	stats, err := collector.CountRecentlyCreatedRepositories(commonData.Topic, commonData.PeriodHours)
	assert.Nil(t, err)

	expectedStats := []int{0, 0, 0}
	assert.Equal(t, expectedStats, stats)
}

func TestRepositoriesWithTheGivenTopicAreOnTheSinglePage(t *testing.T) {
	commonData, mocks, teardownTest := setupTest(t)
	defer teardownTest()

	request := commonData.SearchRepositoriesRequest
	response := github.SearchRepositoriesResponse{
		TotalCount: 1,
		Items: []github.SearchRepositoriesResponseItem{
			{
				Id:        1,
				CreatedAt: commonData.Now.Add(-90 * time.Minute),
			},
		},
	}

	mocks.Clock.EXPECT().Now().Return(commonData.Now).AnyTimes()
	mocks.Finder.EXPECT().SearchRepositories(request, gomock.Any()).Return(&response, nil)

	collector := app.NewGithubStatisticsCollector(mocks.Finder, mocks.Clock)
	stats, err := collector.CountRecentlyCreatedRepositories(commonData.Topic, commonData.PeriodHours)
	assert.Nil(t, err)

	expectedStats := []int{0, 1, 0}
	assert.Equal(t, expectedStats, stats)
}

func TestRepositoryCreatedAtEqToCreatedTillParam(t *testing.T) {
	commonData, mocks, teardownTest := setupTest(t)
	defer teardownTest()

	request := commonData.SearchRepositoriesRequest
	response := github.SearchRepositoriesResponse{
		TotalCount: 1,
		Items: []github.SearchRepositoriesResponseItem{
			{
				Id:        1,
				CreatedAt: request.CreatedTill,
			},
		},
	}

	mocks.Clock.EXPECT().Now().Return(commonData.Now).AnyTimes()
	mocks.Finder.EXPECT().SearchRepositories(request, gomock.Any()).Return(&response, nil)

	collector := app.NewGithubStatisticsCollector(mocks.Finder, mocks.Clock)
	stats, err := collector.CountRecentlyCreatedRepositories(commonData.Topic, commonData.PeriodHours)
	assert.Nil(t, err)

	expectedStats := []int{1, 0, 0}
	assert.Equal(t, expectedStats, stats)
}

func TestRepositoryCreatedAtEqToCreatedSinceParam(t *testing.T) {
	commonData, mocks, teardownTest := setupTest(t)
	defer teardownTest()

	request := commonData.SearchRepositoriesRequest
	response := github.SearchRepositoriesResponse{
		TotalCount: 1,
		Items: []github.SearchRepositoriesResponseItem{
			{
				Id:        1,
				CreatedAt: request.CreatedSince,
			},
		},
	}

	mocks.Clock.EXPECT().Now().Return(commonData.Now).AnyTimes()
	mocks.Finder.EXPECT().SearchRepositories(request, gomock.Any()).Return(&response, nil)

	collector := app.NewGithubStatisticsCollector(mocks.Finder, mocks.Clock)
	stats, err := collector.CountRecentlyCreatedRepositories(commonData.Topic, commonData.PeriodHours)
	assert.Nil(t, err)

	expectedStats := []int{0, 0, 1}
	assert.Equal(t, expectedStats, stats)
}

func TestRepositoriesWithTheGivenTopicAreOnTheDifferentPages(t *testing.T) {
	commonData, mocks, teardownTest := setupTest(t)
	defer teardownTest()

	request := commonData.SearchRepositoriesRequest

	fstPage := 1
	responseFstPage := github.SearchRepositoriesResponse{
		TotalCount: 3,
		Items: []github.SearchRepositoriesResponseItem{
			{
				Id:        1,
				CreatedAt: commonData.Now.Add(-10 * time.Minute),
			},
		},
	}

	sndPage := 2
	responseSndPage := github.SearchRepositoriesResponse{
		TotalCount: 3,
		Items: []github.SearchRepositoriesResponseItem{
			{
				Id:        2,
				CreatedAt: commonData.Now.Add(-130 * time.Minute),
			},
		},
	}

	thdPage := 3
	responseThdPage := github.SearchRepositoriesResponse{
		TotalCount: 3,
		Items: []github.SearchRepositoriesResponseItem{
			{
				Id:        3,
				CreatedAt: commonData.Now.Add(-70 * time.Minute),
			},
		},
	}

	mocks.Clock.EXPECT().Now().Return(commonData.Now).AnyTimes()
	mocks.Finder.EXPECT().SearchRepositories(request, fstPage).Return(&responseFstPage, nil)
	mocks.Finder.EXPECT().SearchRepositories(request, sndPage).Return(&responseSndPage, nil)
	mocks.Finder.EXPECT().SearchRepositories(request, thdPage).Return(&responseThdPage, nil)

	collector := app.NewGithubStatisticsCollector(mocks.Finder, mocks.Clock)
	stats, err := collector.CountRecentlyCreatedRepositories(commonData.Topic, commonData.PeriodHours)
	assert.Nil(t, err)

	expectedStats := []int{1, 1, 1}
	assert.Equal(t, expectedStats, stats)
}
