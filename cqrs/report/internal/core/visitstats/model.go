package visitstats

import "time"

type UsageType string

const (
	Enter UsageType = "in"
	Exit  UsageType = "out"
)

type PassUsageEvent struct {
	MembershipID uint
	Type         UsageType
	CreatedAt    time.Time
}

type DayVisitStats struct {
	Date           string
	UniqueVisitors uint
	AverageTime    time.Duration
}

type PeriodVisitStats struct {
	AvgUniqueVisitors float64
	AverageTime       time.Duration
}
