package eventstore

import (
	"context"
	"os"
	"strconv"

	"github.com/jackc/pgx/v4/pgxpool"
	"github.com/rs/zerolog/log"
)

const (
	pgHost     = "HOST_DB"
	pgPort     = "PORT_DB"
	pgUser     = "POSTGRES_USER"
	pgPassword = "POSTGRES_PASSWORD"
	pgDatabase = "POSTGRES_DB"
)

func InitDB() *pgxpool.Pool {
	cfg, _ := pgxpool.ParseConfig("")
	cfg.ConnConfig.Host = os.Getenv(pgHost)
	portInt, err := strconv.ParseInt(os.Getenv(pgPort), 0, 16)
	cfg.ConnConfig.Port = uint16(portInt)
	cfg.ConnConfig.User = os.Getenv(pgUser)
	cfg.ConnConfig.Password = os.Getenv(pgPassword)
	cfg.ConnConfig.Database = os.Getenv(pgDatabase)
	cfg.ConnConfig.PreferSimpleProtocol = true
	cfg.MaxConns = 20

	dbPool, err := pgxpool.ConnectConfig(context.Background(), cfg)
	if err != nil {
		log.Fatal().Err(err).Msg("DB error")
		os.Exit(1)
	}

	return dbPool
}
