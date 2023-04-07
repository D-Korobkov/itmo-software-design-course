// Code generated by mockery v2.20.0. DO NOT EDIT.

package visitstats

import (
	context "context"
	time "time"

	mock "github.com/stretchr/testify/mock"
)

// MockStorage is an autogenerated mock type for the Storage type
type MockStorage struct {
	mock.Mock
}

type MockStorage_Expecter struct {
	mock *mock.Mock
}

func (_m *MockStorage) EXPECT() *MockStorage_Expecter {
	return &MockStorage_Expecter{mock: &_m.Mock}
}

// FindPassUsageEvents provides a mock function with given fields: ctx, atDate
func (_m *MockStorage) FindPassUsageEvents(ctx context.Context, atDate time.Time) ([]PassUsageEvent, error) {
	ret := _m.Called(ctx, atDate)

	var r0 []PassUsageEvent
	var r1 error
	if rf, ok := ret.Get(0).(func(context.Context, time.Time) ([]PassUsageEvent, error)); ok {
		return rf(ctx, atDate)
	}
	if rf, ok := ret.Get(0).(func(context.Context, time.Time) []PassUsageEvent); ok {
		r0 = rf(ctx, atDate)
	} else {
		if ret.Get(0) != nil {
			r0 = ret.Get(0).([]PassUsageEvent)
		}
	}

	if rf, ok := ret.Get(1).(func(context.Context, time.Time) error); ok {
		r1 = rf(ctx, atDate)
	} else {
		r1 = ret.Error(1)
	}

	return r0, r1
}

// MockStorage_FindPassUsageEvents_Call is a *mock.Call that shadows Run/Return methods with type explicit version for method 'FindPassUsageEvents'
type MockStorage_FindPassUsageEvents_Call struct {
	*mock.Call
}

// FindPassUsageEvents is a helper method to define mock.On call
//   - ctx context.Context
//   - atDate time.Time
func (_e *MockStorage_Expecter) FindPassUsageEvents(ctx interface{}, atDate interface{}) *MockStorage_FindPassUsageEvents_Call {
	return &MockStorage_FindPassUsageEvents_Call{Call: _e.mock.On("FindPassUsageEvents", ctx, atDate)}
}

func (_c *MockStorage_FindPassUsageEvents_Call) Run(run func(ctx context.Context, atDate time.Time)) *MockStorage_FindPassUsageEvents_Call {
	_c.Call.Run(func(args mock.Arguments) {
		run(args[0].(context.Context), args[1].(time.Time))
	})
	return _c
}

func (_c *MockStorage_FindPassUsageEvents_Call) Return(_a0 []PassUsageEvent, _a1 error) *MockStorage_FindPassUsageEvents_Call {
	_c.Call.Return(_a0, _a1)
	return _c
}

func (_c *MockStorage_FindPassUsageEvents_Call) RunAndReturn(run func(context.Context, time.Time) ([]PassUsageEvent, error)) *MockStorage_FindPassUsageEvents_Call {
	_c.Call.Return(run)
	return _c
}

type mockConstructorTestingTNewMockStorage interface {
	mock.TestingT
	Cleanup(func())
}

// NewMockStorage creates a new instance of MockStorage. It also registers a testing interface on the mock and a cleanup function to assert the mocks expectations.
func NewMockStorage(t mockConstructorTestingTNewMockStorage) *MockStorage {
	mock := &MockStorage{}
	mock.Mock.Test(t)

	t.Cleanup(func() { mock.AssertExpectations(t) })

	return mock
}