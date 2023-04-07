package turnstile

import (
	"context"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/membership"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"testing"
)

func TestProcessEnterAndMembershipDoesNotExist(t *testing.T) {
	membershipService := membership.NewMockService(t)
	membershipService.EXPECT().IsActive(mock.Anything, mock.Anything).Return(false, membership.ErrNotFound)

	eventStorage := NewMockUsePassStorage(t)

	service := NewService(eventStorage, membershipService)
	err := service.ProcessEnter(context.Background(), 1)
	assert.ErrorIs(t, err, membership.ErrNotFound)
}

func TestProcessEnterAndMembershipIsNotActive(t *testing.T) {
	membershipService := membership.NewMockService(t)
	membershipService.EXPECT().IsActive(mock.Anything, mock.Anything).Return(false, nil)

	eventStorage := NewMockUsePassStorage(t)

	service := NewService(eventStorage, membershipService)
	err := service.ProcessEnter(context.Background(), 1)
	assert.ErrorIs(t, err, ErrNoActiveMembership)
}

func TestProcessEnterAndMembershipIsActive(t *testing.T) {
	membershipID := uint(1)
	eventType := "in"

	membershipService := membership.NewMockService(t)
	membershipService.EXPECT().IsActive(mock.Anything, mock.Anything).Return(true, nil)

	eventStorage := NewMockUsePassStorage(t)
	eventStorage.EXPECT().CreateUsePassEvent(mock.Anything, mock.Anything, mock.Anything).Return(nil)

	service := NewService(eventStorage, membershipService)
	err := service.ProcessEnter(context.Background(), membershipID)
	assert.NoError(t, err)

	membershipService.AssertCalled(t, "IsActive", mock.Anything, membershipID)
	eventStorage.AssertCalled(t, "CreateUsePassEvent", mock.Anything, membershipID, eventType)
}

func TestProcessExitAndMembershipDoesNotExist(t *testing.T) {
	membershipService := membership.NewMockService(t)
	membershipService.EXPECT().Exists(mock.Anything, mock.Anything).Return(false, nil)

	eventStorage := NewMockUsePassStorage(t)

	service := NewService(eventStorage, membershipService)
	err := service.ProcessExit(context.Background(), 1)
	assert.ErrorIs(t, err, ErrNoMembership)
}

func TestProcessExitAndMembershipExists(t *testing.T) {
	membershipID := uint(1)
	eventType := "out"

	membershipService := membership.NewMockService(t)
	membershipService.EXPECT().Exists(mock.Anything, mock.Anything).Return(true, nil)

	eventStorage := NewMockUsePassStorage(t)
	eventStorage.EXPECT().CreateUsePassEvent(mock.Anything, mock.Anything, mock.Anything).Return(nil)

	service := NewService(eventStorage, membershipService)
	err := service.ProcessExit(context.Background(), 1)
	assert.NoError(t, err)

	membershipService.AssertCalled(t, "Exists", mock.Anything, membershipID)
	eventStorage.AssertCalled(t, "CreateUsePassEvent", mock.Anything, membershipID, eventType)
}
