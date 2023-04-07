package membership

import (
	"context"
	"errors"
	"time"
)

var (
	ErrNotCreated   = errors.New("gym membership not created")
	ErrNotProlonged = errors.New("gym membership not prolonged")
	ErrNotFound     = errors.New("gym membership not found")
)

type Service struct {
	eventStorage GymMembershipEventStorage
}

func NewService(eventStorage GymMembershipEventStorage) *Service {
	return &Service{
		eventStorage: eventStorage,
	}
}

func (s Service) Find(ctx context.Context, membershipID uint) (*GymMembershipInfo, error) {
	createdEvent, err := s.eventStorage.FindGymMembershipCreatedEvent(ctx, membershipID)
	if err != nil {
		return nil, err
	}

	prolongedEvents, err := s.eventStorage.FindGymMembershipProlongedEvents(ctx, membershipID)
	activeTill := createdEvent.CreatedAt.UTC().Add(time.Duration(createdEvent.ExpiresInDays) * 24 * time.Hour)
	for idx := range prolongedEvents {
		extraDays := time.Duration(prolongedEvents[idx].ExtraDays) * 24 * time.Hour
		activeTill = activeTill.Add(extraDays)
	}
	info := GymMembershipInfo{
		ID:         createdEvent.ID,
		Owner:      createdEvent.Owner,
		ActiveTill: activeTill,
	}
	return &info, nil
}

func (s Service) CreateNew(ctx context.Context, owner string, expiresInDays uint) error {
	return s.eventStorage.CreateGymMembership(ctx, owner, expiresInDays)
}

func (s Service) Prolong(ctx context.Context, membershipID uint, extraDays uint) error {
	return s.eventStorage.ProlongGymMembership(ctx, membershipID, extraDays)
}
