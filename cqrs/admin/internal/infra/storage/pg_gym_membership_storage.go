package storage

import (
	"context"
	"database/sql"
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/internal/core/membership"
	"github.com/jackc/pgx/v4/pgxpool"
	"github.com/rs/zerolog/log"
)

type pgGymMembershipEventStorage struct {
	pgNewGymMembershipEventStorage
	pgProlongGymMembershipEventStorage
}

type pgNewGymMembershipEventStorage struct {
	db *pgxpool.Pool
}

type pgProlongGymMembershipEventStorage struct {
	db *pgxpool.Pool
}

func NewPgGymMembershipEventStorage(db *pgxpool.Pool) membership.GymMembershipEventStorage {
	return &pgGymMembershipEventStorage{
		pgNewGymMembershipEventStorage: pgNewGymMembershipEventStorage{
			db: db,
		},
		pgProlongGymMembershipEventStorage: pgProlongGymMembershipEventStorage{
			db: db,
		},
	}
}

func (storage pgNewGymMembershipEventStorage) CreateGymMembership(ctx context.Context, owner string, expiresInDays uint) error {
	sqlQuery := `INSERT INTO new_gym_membership_event (owner, expires_in_days, created_at) VALUES ($1, $2, now())`

	rs, err := storage.db.Exec(ctx, sqlQuery, owner, expiresInDays)
	if err != nil || rs.RowsAffected() == 0 {
		log.Error().Err(err).Msg("New gym membership hasn't been created")
		return membership.ErrNotCreated
	}
	return nil
}

func (storage pgProlongGymMembershipEventStorage) ProlongGymMembership(ctx context.Context, membershipID uint, extraDays uint) error {
	sqlQuery := `INSERT INTO prolong_gym_membership_event (id, extra_days) VALUES ($1, $2)`

	rs, err := storage.db.Exec(ctx, sqlQuery, membershipID, extraDays)
	if err != nil || rs.RowsAffected() == 0 {
		log.Error().Err(err).Msg("Gym membership hasn't been prolonged")
		return membership.ErrNotProlonged
	}
	return nil
}

func (storage pgNewGymMembershipEventStorage) FindGymMembershipCreatedEvent(ctx context.Context, membershipID uint) (*membership.GymMembershipCreatedEvent, error) {
	sqlQuery := `SELECT id, owner, expires_in_days, created_at FROM new_gym_membership_event WHERE id = $1`

	var event membership.GymMembershipCreatedEvent
	err := storage.db.QueryRow(ctx, sqlQuery, membershipID).Scan(&event.ID, &event.Owner, &event.ExpiresInDays, &event.CreatedAt)
	if errors.Is(err, sql.ErrNoRows) {
		return nil, membership.ErrNotFound
	}
	if err != nil {
		return nil, err
	}
	return &event, nil
}

func (storage pgProlongGymMembershipEventStorage) FindGymMembershipProlongedEvents(ctx context.Context, membershipID uint) ([]membership.GymMembershipProlongedEvent, error) {
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
