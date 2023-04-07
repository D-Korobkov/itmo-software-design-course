package membership

import (
	"context"
)

//go:generate mockery --name GymMembershipEventStorage
type GymMembershipEventStorage interface {
	CreateGymMembership(ctx context.Context, owner string, expiresInDays uint) error
	ProlongGymMembership(ctx context.Context, membershipID uint, extraDays uint) error

	FindGymMembershipCreatedEvent(ctx context.Context, id uint) (*GymMembershipCreatedEvent, error)
	FindGymMembershipProlongedEvents(ctx context.Context, id uint) ([]GymMembershipProlongedEvent, error)
}
