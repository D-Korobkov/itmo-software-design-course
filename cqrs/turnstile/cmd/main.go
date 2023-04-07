package main

import (
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/app/entrypoint"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/membership"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/turnstile"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/infra/storage"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/pkg/clock"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/pkg/eventstore"
	"github.com/rs/zerolog/log"
	"os"
)

func main() {
	realClock := clock.NewRealClock()
	db := eventstore.InitDB()

	membershipEventStorage := storage.NewPgGymMembershipEventStorage(db)
	turnstileEventStorage := storage.NewPgTurnstileUsePassStorage(db)

	membershipService := membership.NewService(membershipEventStorage, realClock)
	turnstileService := turnstile.NewService(turnstileEventStorage, membershipService)

	server := entrypoint.NewServer(turnstileService)
	err := server.Start(":" + os.Getenv("HTTP_BIND"))
	if err != nil {
		log.Error().Err(err).Msg("Server hasn't been started")
		os.Exit(1)
	}
}
