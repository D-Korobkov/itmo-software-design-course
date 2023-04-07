package membership

import "time"

type GymMembershipCreatedEvent struct {
	ID            uint
	ExpiresInDays uint
	CreatedAt     time.Time
}

type GymMembershipProlongedEvent struct {
	ID        uint
	ExtraDays uint
}
