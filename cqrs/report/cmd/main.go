package main

import (
	"context"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/internal/app/entrypoint"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/internal/core/visitstats"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/internal/infra/storage"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/pkg/clock"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/pkg/eventstore"
	"github.com/rs/zerolog/log"
	"os"
	"time"
)

func main() {
	realClock := clock.NewRealClock()
	db := eventstore.InitDB()

	passUsageEventStorage := storage.NewPgPassUsagesStorage(db)

	visitStatsService := visitstats.NewService(passUsageEventStorage, realClock)
	timer := time.NewTicker(1 * time.Second)
	defer timer.Stop()
	go func() {
		for range timer.C {
			visitStatsService.Fill(context.Background())
		}
	}()

	server := entrypoint.NewServer(visitStatsService)
	err := server.Start(":" + os.Getenv("HTTP_BIND"))
	if err != nil {
		log.Error().Err(err).Msg("Server hasn't been started")
		os.Exit(1)
	}
}
