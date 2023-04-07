package storage

import (
	"context"
	"database/sql"
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/membership"
	"github.com/jackc/pgx/v4/pgxpool"
)

type pgGymMembershipEventStorage struct {
	pgGymMembershipCreatedEventStorage
	pgGymMembershipProlongedEventStorage
}

type pgGymMembershipCreatedEventStorage struct {
	db *pgxpool.Pool
}

type pgGymMembershipProlongedEventStorage struct {
	db *pgxpool.Pool
}

func NewPgGymMembershipEventStorage(db *pgxpool.Pool) membership.EventStorage {
	return &pgGymMembershipEventStorage{
		pgGymMembershipCreatedEventStorage: pgGymMembershipCreatedEventStorage{
			db: db,
		},
		pgGymMembershipProlongedEventStorage: pgGymMembershipProlongedEventStorage{
			db: db,
		},
	}
}

func (storage pgGymMembershipCreatedEventStorage) FindGymMembershipCreatedEvent(ctx context.Context, membershipID uint) (*membership.GymMembershipCreatedEvent, error) {
	sqlQuery := `SELECT id, expires_in_days, created_at FROM new_gym_membership_event WHERE id = $1`

	var event membership.GymMembershipCreatedEvent
	err := storage.db.QueryRow(ctx, sqlQuery, membershipID).Scan(&event.ID, &event.ExpiresInDays, &event.CreatedAt)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, membership.ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	return &event, nil
}

func (storage pgGymMembershipProlongedEventStorage) FindGymMembershipProlongedEvents(ctx context.Context, membershipID uint) ([]membership.GymMembershipProlongedEvent, error) {
	sqlQuery := `SELECT id, extra_days FROM prolong_gym_membership_event WHERE id = $1`

	rs, err := storage.db.Query(ctx, sqlQuery, membershipID)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	defer rs.Close()

	var events []membership.GymMembershipProlongedEvent
	for rs.Next() {
		var singleEvent membership.GymMembershipProlongedEvent
		if err = rs.Scan(&singleEvent.ID, &singleEvent.ExtraDays); err != nil {
			return nil, err
		}
		events = append(events, singleEvent)
	}
	return events, nil
}
