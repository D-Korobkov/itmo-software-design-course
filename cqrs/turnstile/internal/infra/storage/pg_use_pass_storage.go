package storage

import (
	"context"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/turnstile"
	"github.com/jackc/pgx/v4/pgxpool"
	"github.com/rs/zerolog/log"
)

type pgCustomerUsePassEventStorage struct {
	db *pgxpool.Pool
}

func NewPgTurnstileUsePassStorage(db *pgxpool.Pool) turnstile.UsePassStorage {
	return &pgCustomerUsePassEventStorage{
		db: db,
	}
}

func (storage pgCustomerUsePassEventStorage) CreateUsePassEvent(ctx context.Context, membershipID uint, eventType string) error {
	sqlQuery := `INSERT INTO customer_use_pass_event (id, event_type, created_at) VALUES ($1, $2, now())`

	rs, err := storage.db.Exec(ctx, sqlQuery, membershipID, eventType)
	if err != nil || rs.RowsAffected() == 0 {
		log.Error().Err(err).Msg("Use pass event hasn't been created")
		return turnstile.ErrEventNotCreated
	}
	return nil
}
