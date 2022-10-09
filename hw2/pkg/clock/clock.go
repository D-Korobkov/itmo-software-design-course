package clock

import "time"

//go:generate mockgen -destination mocks.go -package clock github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/clock Clock

type Clock interface {
	Now() time.Time
}

type staticClock struct {
	now time.Time
}

func NewStaticClock(fixedTime time.Time) Clock {
	return &staticClock{now: fixedTime}
}

func (clock *staticClock) Now() time.Time {
	return clock.now
}
