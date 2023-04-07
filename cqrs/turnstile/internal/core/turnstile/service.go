package turnstile

import (
	"context"
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/membership"
)

var (
	ErrEventNotCreated    = errors.New("using of pass was not fixed")
	ErrNoActiveMembership = errors.New("no active membership")
	ErrNoMembership       = errors.New("no membership")
)

type Service struct {
	membershipService membership.Service
	eventStorage      UsePassStorage
}

func NewService(eventStorage UsePassStorage, membershipService membership.Service) *Service {
	return &Service{
		membershipService: membershipService,
		eventStorage:      eventStorage,
	}
}

func (s Service) ProcessEnter(ctx context.Context, membershipID uint) error {
	isActive, err := s.membershipService.IsActive(ctx, membershipID)
	if err != nil {
		return err
	}
	if !isActive {
		return ErrNoActiveMembership
	}

	return s.eventStorage.CreateUsePassEvent(ctx, membershipID, "in")
}

func (s Service) ProcessExit(ctx context.Context, membershipID uint) error {
	exists, err := s.membershipService.Exists(ctx, membershipID)
	if err != nil {
		return err
	}
	if !exists {
		return ErrNoMembership
	}

	return s.eventStorage.CreateUsePassEvent(ctx, membershipID, "out")
}
