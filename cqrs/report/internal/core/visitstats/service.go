package visitstats

import (
	"context"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/pkg/clock"
	"github.com/rs/zerolog/log"
	"github.com/samber/lo"
	"sort"
	"sync"
	"time"
)

type Service struct {
	mu              sync.Mutex
	clock           clock.Clock
	PassUsagesStats map[string]map[uint]time.Duration
	LastDate        time.Time
	eventStorage    Storage
}

func NewService(eventStorage Storage, clock clock.Clock) *Service {
	return &Service{
		PassUsagesStats: map[string]map[uint]time.Duration{},
		LastDate:        time.UnixMilli(0),
		eventStorage:    eventStorage,
		clock:           clock,
	}
}

func (s *Service) GetDayStats(atDate time.Time) DayVisitStats {
	key := atDate.Format("2006-01-02")
	s.mu.Lock()
	defer s.mu.Unlock()

	stats, exist := s.PassUsagesStats[key]
	if !exist {
		return DayVisitStats{
			Date:           key,
			UniqueVisitors: 0,
			AverageTime:    0,
		}
	}
	return DayVisitStats{
		Date:           key,
		UniqueVisitors: uint(len(stats)),
		AverageTime:    time.Duration(lo.Sum(lo.Values(stats)).Nanoseconds() / int64(len(stats))),
	}
}

func (s *Service) Fill(ctx context.Context) error {
	now := s.clock.NowUTC()
	s.mu.Lock()
	atDate := s.LastDate.UTC()
	s.mu.Unlock()

	for ; atDate.Before(now); atDate = atDate.Add(24 * time.Hour) {
		cacheKey := atDate.Format("2006-01-02")

		stats, err := s.CollectAvgVisitDurationByMember(ctx, atDate)
		if err != nil {
			log.Error().Err(err).Msgf("Cannot collect stats at %s", cacheKey)
			continue
		}
		s.mu.Lock()
		if stats != nil {
			s.PassUsagesStats[cacheKey] = stats
		}
		s.LastDate = atDate
		s.mu.Unlock()
	}
	return nil
}

func (s *Service) CollectAvgVisitDurationByMember(ctx context.Context, atDate time.Time) (map[uint]time.Duration, error) {
	events, err := s.eventStorage.FindPassUsageEvents(ctx, atDate)
	if err != nil {
		return nil, err
	}
	if len(events) == 0 {
		return nil, nil
	}

	stats := lo.MapValues(
		lo.GroupBy(events, func(item PassUsageEvent) uint {
			return item.MembershipID
		}),
		func(visits []PassUsageEvent, key uint) time.Duration {
			if len(visits) < 2 {
				return 0
			}

			sort.Slice(visits, func(i, j int) bool {
				return visits[i].CreatedAt.Before(visits[j].CreatedAt)
			})
			totals := int64(0)
			avgDuration := time.Duration(0)
			for idx := 1; idx < len(visits); idx, totals = idx+2, totals+1 {
				inTime := visits[idx-1].CreatedAt
				outTime := visits[idx].CreatedAt
				avgDuration += outTime.Sub(inTime)
			}
			return time.Duration(avgDuration.Nanoseconds() / totals)
		},
	)
	return stats, nil
}
