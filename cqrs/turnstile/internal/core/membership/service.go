package membership

import (
	"context"
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/pkg/clock"
	"time"
)

var (
	ErrNotFound = errors.New("membership not found")
)

//go:generate mockery --name Service --exported=true
type Service interface {
	Exists(ctx context.Context, membershipID uint) (bool, error)
	IsActive(ctx context.Context, membershipID uint) (bool, error)
}

type serviceImpl struct {
	eventStorage EventStorage
	clock        clock.Clock
}

func NewService(eventStorage EventStorage, clock clock.Clock) Service {
	return &serviceImpl{
		eventStorage: eventStorage,
		clock:        clock,
	}
}

func (s serviceImpl) Exists(ctx context.Context, membershipID uint) (bool, error) {
	_, err := s.eventStorage.FindGymMembershipCreatedEvent(ctx, membershipID)
	if errors.Is(err, ErrNotFound) {
		return false, nil
	}
	if err != nil {
		return false, err
	}
	return true, nil
}

func (s serviceImpl) IsActive(ctx context.Context, membershipID uint) (bool, error) {
	createdEvent, err := s.eventStorage.FindGymMembershipCreatedEvent(ctx, membershipID)
	if err != nil {
		return false, err
	}

	prolongedEvents, err := s.eventStorage.FindGymMembershipProlongedEvents(ctx, membershipID)
	if err != nil {
		return false, err
	}

	activeTill := createdEvent.CreatedAt.UTC().Add(time.Duration(createdEvent.ExpiresInDays) * 24 * time.Hour)
	for idx := range prolongedEvents {
		extraDays := time.Duration(prolongedEvents[idx].ExtraDays) * 24 * time.Hour
		activeTill = activeTill.Add(extraDays)
	}

	return activeTill.After(s.clock.NowUTC()), nil
}
