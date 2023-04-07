package membership

import (
	"context"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"testing"
	"time"
)

func TestFindNoMembership(t *testing.T) {
	mockEventStorage := NewMockGymMembershipEventStorage(t)
	mockEventStorage.EXPECT().FindGymMembershipCreatedEvent(mock.Anything, mock.Anything).Return(nil, ErrNotFound)

	service := NewService(mockEventStorage)
	_, err := service.Find(context.Background(), 1)
	assert.ErrorIs(t, err, ErrNotFound)
}

func TestFindNoProlongation(t *testing.T) {
	createdEvent := GymMembershipCreatedEvent{
		ID:            1,
		Owner:         "Me",
		CreatedAt:     time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC),
		ExpiresInDays: 30,
	}
	mockEventStorage := NewMockGymMembershipEventStorage(t)
	mockEventStorage.EXPECT().FindGymMembershipCreatedEvent(mock.Anything, mock.Anything).Return(&createdEvent, nil)
	mockEventStorage.EXPECT().FindGymMembershipProlongedEvents(mock.Anything, mock.Anything).Return(nil, nil)

	service := NewService(mockEventStorage)
	membershipInfo, err := service.Find(context.Background(), createdEvent.ID)
	assert.NoError(t, err)

	expectedMembershipInfo := GymMembershipInfo{
		ID:         1,
		Owner:      "Me",
		ActiveTill: time.Date(2023, 1, 31, 0, 0, 0, 0, time.UTC),
	}
	assert.Equal(t, expectedMembershipInfo, *membershipInfo)
}

func TestFindProlongationApplied(t *testing.T) {
	createdEvent := GymMembershipCreatedEvent{
		ID:            1,
		Owner:         "Me",
		CreatedAt:     time.Date(2023, 1, 1, 0, 0, 0, 0, time.UTC),
		ExpiresInDays: 30,
	}
	prolongedEvents := []GymMembershipProlongedEvent{
		{
			ID:        1,
			ExtraDays: 10,
		},
		{
			ID:        1,
			ExtraDays: 10,
		},
		{
			ID:        1,
			ExtraDays: 8,
		},
	}
	mockEventStorage := NewMockGymMembershipEventStorage(t)
	mockEventStorage.EXPECT().FindGymMembershipCreatedEvent(mock.Anything, mock.Anything).Return(&createdEvent, nil)
	mockEventStorage.EXPECT().FindGymMembershipProlongedEvents(mock.Anything, mock.Anything).Return(prolongedEvents, nil)

	service := NewService(mockEventStorage)
	membershipInfo, err := service.Find(context.Background(), createdEvent.ID)
	assert.NoError(t, err)

	expectedMembershipInfo := GymMembershipInfo{
		ID:         1,
		Owner:      "Me",
		ActiveTill: time.Date(2023, 2, 28, 0, 0, 0, 0, time.UTC),
	}
	assert.Equal(t, expectedMembershipInfo, *membershipInfo)
}
