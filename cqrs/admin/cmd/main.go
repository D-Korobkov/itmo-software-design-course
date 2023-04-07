package main

import (
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/internal/app/entrypoint"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/internal/core/membership"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/internal/infra/storage"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/pkg/eventstore"
	"github.com/rs/zerolog/log"
	"os"
)

func main() {
	db := eventstore.InitDB()

	newGymMembershipEventStorage := storage.NewPgGymMembershipEventStorage(db)
	gymMembershipEditor := membership.NewService(newGymMembershipEventStorage)

	server := entrypoint.NewServer(gymMembershipEditor)
	err := server.Start(":" + os.Getenv("HTTP_BIND"))
	if err != nil {
		log.Error().Err(err).Msg("Server hasn't been started")
		os.Exit(1)
	}
}
