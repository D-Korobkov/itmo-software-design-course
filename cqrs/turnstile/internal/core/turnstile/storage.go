package turnstile

import "context"

//go:generate mockery --name UsePassStorage
type UsePassStorage interface {
	CreateUsePassEvent(ctx context.Context, membershipID uint, eventType string) error
}
