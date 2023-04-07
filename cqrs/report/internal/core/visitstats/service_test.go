package visitstats

import (
	"context"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/pkg/clock"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"testing"
	"time"
)

func TestCollectAvgVisitDurationByMember(t *testing.T) {
	constClock := clock.NewConstClock(time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC))

	events := []PassUsageEvent{
		{
			MembershipID: 1,
			Type:         Exit,
			CreatedAt:    constClock.NowUTC().Add(-30 * time.Minute),
		},
		{
			MembershipID: 1,
			Type:         Enter,
			CreatedAt:    constClock.NowUTC().Add(-60 * time.Minute),
		},
		{
			MembershipID: 2,
			Type:         Enter,
			CreatedAt:    constClock.NowUTC().Add(-45 * time.Minute),
		},
		{
			MembershipID: 2,
			Type:         Exit,
			CreatedAt:    constClock.NowUTC().Add(-15 * time.Minute),
		},
	}

	mockEventStorage := NewMockStorage(t)
	mockEventStorage.EXPECT().FindPassUsageEvents(mock.Anything, mock.Anything).Return(events, nil)

	service := NewService(mockEventStorage, constClock)
	testResults, err := service.CollectAvgVisitDurationByMember(context.Background(), constClock.NowUTC())
	assert.NoError(t, err)

	expectedTestResults := map[uint]time.Duration{
		1: 30 * time.Minute,
		2: 30 * time.Minute,
	}
	assert.Equal(t, testResults, expectedTestResults)
}

func TestFillFiltersOutZeroStats(t *testing.T) {
	constClock := clock.NewConstClock(time.Date(2023, 1, 1, 0, 0, 1, 0, time.UTC))

	mockEventStorage := NewMockStorage(t)
	mockEventStorage.EXPECT().FindPassUsageEvents(mock.Anything, mock.Anything).Return(nil, nil)

	service := NewService(mockEventStorage, constClock)
	err := service.Fill(context.Background())
	assert.NoError(t, err)
	assert.Len(t, service.PassUsagesStats, 0)
	assert.Equal(t, time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC), service.LastDate)
}

func TestGetDayStatsAndNoStatsAtGivenDate(t *testing.T) {
	constClock := clock.NewConstClock(time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC))
	mockEventStorage := NewMockStorage(t)

	service := NewService(mockEventStorage, constClock)
	stats := service.GetDayStats(constClock.NowUTC())
	expectedStats := DayVisitStats{
		Date:           "2023-01-01",
		UniqueVisitors: 0,
		AverageTime:    0,
	}
	assert.Equal(t, expectedStats, stats)
}

func TestGetDayStats(t *testing.T) {
	constClock := clock.NewConstClock(time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC))
	mockEventStorage := NewMockStorage(t)

	service := NewService(mockEventStorage, constClock)
	service.PassUsagesStats = map[string]map[uint]time.Duration{
		"2023-01-01": {
			123: 45 * time.Minute,
			321: 45 * time.Minute,
			213: 90 * time.Minute,
		},
	}
	stats := service.GetDayStats(constClock.NowUTC())
	expectedStats := DayVisitStats{
		Date:           "2023-01-01",
		UniqueVisitors: 3,
		AverageTime:    60 * time.Minute,
	}
	assert.Equal(t, expectedStats, stats)
}
