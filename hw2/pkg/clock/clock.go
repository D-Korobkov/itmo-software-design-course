package clock

import "time"

//go:generate mockgen -destination mocks.go -package clock github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/clock Clock

type Clock interface {
	Now() time.Time
}

type realClock struct{}

func NewRealClock() Clock {
	return &realClock{}
}

func (clock *realClock) Now() time.Time {
	return time.Now()
}
