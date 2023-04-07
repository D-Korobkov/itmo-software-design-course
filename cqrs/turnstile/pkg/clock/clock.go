package clock

import "time"

type Clock interface {
	NowUTC() time.Time
}

type realClock struct{}

func NewRealClock() Clock {
	return realClock{}
}

func (rc realClock) NowUTC() time.Time {
	return time.Now().UTC()
}

type constClock struct {
	fixedTime time.Time
}

func NewConstClock(fixedTime time.Time) Clock {
	return constClock{fixedTime: fixedTime}
}

func (cc constClock) NowUTC() time.Time {
	return cc.fixedTime.UTC()
}
