package entrypoint

import (
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/report/internal/core/visitstats"
	"github.com/gorilla/mux"
	"github.com/rs/zerolog/log"
	"net/http"
)

type Server struct {
	router            *mux.Router
	visitStatsService *visitstats.Service
}

func NewServer(visitStatsService *visitstats.Service) Server {
	s := Server{
		router:            mux.NewRouter(),
		visitStatsService: visitStatsService,
	}

	s.router.HandleFunc("/stats/day", s.GetDayStatsHandler).Methods(http.MethodGet)

	return s
}

func (s Server) Start(addr string) error {
	log.Info().Msg("Starting...")
	return http.ListenAndServe(addr, s.router)
}
