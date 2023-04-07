package storage

import (
	"context"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/internal/core/visitstats"
	"github.com/jackc/pgx/v4/pgxpool"
	"time"
)

type pgCustomerUsePassEventStorage struct {
	db *pgxpool.Pool
}

func NewPgPassUsagesStorage(db *pgxpool.Pool) visitstats.Storage {
	return &pgCustomerUsePassEventStorage{
		db: db,
	}
}

func (storage pgCustomerUsePassEventStorage) FindPassUsageEvents(ctx context.Context, atDate time.Time) ([]visitstats.PassUsageEvent, error) {
	sqlQuery := `SELECT id, event_type, created_at FROM customer_use_pass_event WHERE created_at >= $1 AND created_at < $2 ORDER BY created_at`

	rs, err := storage.db.Query(ctx, sqlQuery, atDate, atDate.Add(24*time.Hour))
	if err != nil {
		return nil, err
	}
	defer rs.Close()

	var events []visitstats.PassUsageEvent
	for rs.Next() {
		var singleEvent visitstats.PassUsageEvent
		if err = rs.Scan(&singleEvent.MembershipID, &singleEvent.Type, &singleEvent.CreatedAt); err != nil {
			return nil, err
		}
		events = append(events, singleEvent)
	}
	return events, nil
}
