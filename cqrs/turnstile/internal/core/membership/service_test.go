package membership

import (
	"context"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/pkg/clock"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"testing"
	"time"
)

func TestGymMembershipDoesNotExist(t *testing.T) {
	membershipID := uint(1)

	constClock := clock.NewConstClock(time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC))
	mockEventStorage := NewMockEventStorage(t)
	mockEventStorage.EXPECT().FindGymMembershipCreatedEvent(mock.Anything, mock.Anything).Return(nil, ErrNotFound)

	service := NewService(mockEventStorage, constClock)
	exists, err := service.Exists(context.Background(), 1)
	assert.NoError(t, err)
	assert.False(t, exists)
	mockEventStorage.AssertCalled(t, "FindGymMembershipCreatedEvent", mock.Anything, membershipID)
}

func TestGymMembershipExists(t *testing.T) {
	membership := GymMembershipCreatedEvent{}

	constClock := clock.NewConstClock(time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC))
	mockEventStorage := NewMockEventStorage(t)
	mockEventStorage.EXPECT().FindGymMembershipCreatedEvent(mock.Anything, mock.Anything).Return(&membership, nil)

	service := NewService(mockEventStorage, constClock)
	exists, err := service.Exists(context.Background(), membership.ID)
	assert.NoError(t, err)
	assert.True(t, exists)
	mockEventStorage.AssertCalled(t, "FindGymMembershipCreatedEvent", mock.Anything, membership.ID)
}

func TestGymMembershipIsNotActive(t *testing.T) {
	constClock := clock.NewConstClock(time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC))

	createdEvent := GymMembershipCreatedEvent{
		CreatedAt:     constClock.NowUTC().Add(-300 * 24 * time.Hour),
		ExpiresInDays: 10,
	}
	prolongedEvents := []GymMembershipProlongedEvent{
		{ExtraDays: 100},
		{ExtraDays: 100},
		{ExtraDays: 90},
	}

	mockEventStorage := NewMockEventStorage(t)
	mockEventStorage.EXPECT().FindGymMembershipCreatedEvent(mock.Anything, mock.Anything).Return(&createdEvent, nil)
	mockEventStorage.EXPECT().FindGymMembershipProlongedEvents(mock.Anything, mock.Anything).Return(prolongedEvents, nil)

	service := NewService(mockEventStorage, constClock)
	active, err := service.IsActive(context.Background(), createdEvent.ID)

	assert.NoError(t, err)
	assert.False(t, active)
	mockEventStorage.AssertCalled(t, "FindGymMembershipCreatedEvent", mock.Anything, createdEvent.ID)
	mockEventStorage.AssertCalled(t, "FindGymMembershipProlongedEvents", mock.Anything, createdEvent.ID)
}

func TestGymMembershipIsActive(t *testing.T) {
	constClock := clock.NewConstClock(time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC))

	createdEvent := GymMembershipCreatedEvent{
		CreatedAt:     constClock.NowUTC().Add(-300 * 24 * time.Hour),
		ExpiresInDays: 10,
	}
	prolongedEvents := []GymMembershipProlongedEvent{
		{ExtraDays: 100},
		{ExtraDays: 100},
		{ExtraDays: 91},
	}

	mockEventStorage := NewMockEventStorage(t)
	mockEventStorage.EXPECT().FindGymMembershipCreatedEvent(mock.Anything, mock.Anything).Return(&createdEvent, nil)
	mockEventStorage.EXPECT().FindGymMembershipProlongedEvents(mock.Anything, mock.Anything).Return(prolongedEvents, nil)

	service := NewService(mockEventStorage, constClock)
	active, err := service.IsActive(context.Background(), createdEvent.ID)

	assert.NoError(t, err)
	assert.True(t, active)
	mockEventStorage.AssertCalled(t, "FindGymMembershipCreatedEvent", mock.Anything, createdEvent.ID)
	mockEventStorage.AssertCalled(t, "FindGymMembershipProlongedEvents", mock.Anything, createdEvent.ID)
}
