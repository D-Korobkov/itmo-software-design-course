package visitstats

import (
	"context"
	"time"
)

//go:generate mockery --name Storage
type Storage interface {
	FindPassUsageEvents(ctx context.Context, atDate time.Time) ([]PassUsageEvent, error)
}
