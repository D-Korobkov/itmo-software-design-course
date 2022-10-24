// Code generated by MockGen. DO NOT EDIT.
// Source: github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/integration/github (interfaces: RepositoryFinder)

// Package github is a generated GoMock package.
package github

import (
	reflect "reflect"

	gomock "github.com/golang/mock/gomock"
)

// MockRepositoryFinder is a mock of RepositoryFinder interface.
type MockRepositoryFinder struct {
	ctrl     *gomock.Controller
	recorder *MockRepositoryFinderMockRecorder
}

// MockRepositoryFinderMockRecorder is the mock recorder for MockRepositoryFinder.
type MockRepositoryFinderMockRecorder struct {
	mock *MockRepositoryFinder
}

// NewMockRepositoryFinder creates a new mock instance.
func NewMockRepositoryFinder(ctrl *gomock.Controller) *MockRepositoryFinder {
	mock := &MockRepositoryFinder{ctrl: ctrl}
	mock.recorder = &MockRepositoryFinderMockRecorder{mock}
	return mock
}

// EXPECT returns an object that allows the caller to indicate expected use.
func (m *MockRepositoryFinder) EXPECT() *MockRepositoryFinderMockRecorder {
	return m.recorder
}

// SearchRepositories mocks base method.
func (m *MockRepositoryFinder) SearchRepositories(arg0 SearchRepositoriesRequest, arg1 int) (*SearchRepositoriesResponse, error) {
	m.ctrl.T.Helper()
	ret := m.ctrl.Call(m, "SearchRepositories", arg0, arg1)
	ret0, _ := ret[0].(*SearchRepositoriesResponse)
	ret1, _ := ret[1].(error)
	return ret0, ret1
}

// SearchRepositories indicates an expected call of SearchRepositories.
func (mr *MockRepositoryFinderMockRecorder) SearchRepositories(arg0, arg1 interface{}) *gomock.Call {
	mr.mock.ctrl.T.Helper()
	return mr.mock.ctrl.RecordCallWithMethodType(mr.mock, "SearchRepositories", reflect.TypeOf((*MockRepositoryFinder)(nil).SearchRepositories), arg0, arg1)
}
