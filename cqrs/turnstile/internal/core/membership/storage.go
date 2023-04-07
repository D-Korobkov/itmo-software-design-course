package membership

import "context"

//go:generate mockery --name EventStorage
type EventStorage interface {
	FindGymMembershipCreatedEvent(ctx context.Context, id uint) (*GymMembershipCreatedEvent, error)
	FindGymMembershipProlongedEvents(ctx context.Context, id uint) ([]GymMembershipProlongedEvent, error)
}
