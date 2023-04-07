package membership

import "time"

type GymMembershipCreatedEvent struct {
	ID            uint
	Owner         string
	ExpiresInDays uint
	CreatedAt     time.Time
}

type GymMembershipProlongedEvent struct {
	ID        uint
	ExtraDays uint
}

type GymMembershipInfo struct {
	ID         uint
	Owner      string
	ActiveTill time.Time
}
