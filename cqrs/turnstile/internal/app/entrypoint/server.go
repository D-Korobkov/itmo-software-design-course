package entrypoint

import (
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/turnstile"
	"github.com/gorilla/mux"
	"github.com/rs/zerolog/log"
	"net/http"
)

type Server struct {
	router           *mux.Router
	turnstileService *turnstile.Service
}

func NewServer(turnstileService *turnstile.Service) Server {
	s := Server{
		router:           mux.NewRouter(),
		turnstileService: turnstileService,
	}

	s.router.HandleFunc("/gym/enter", s.EnterGymHandler).Methods(http.MethodPost)
	s.router.HandleFunc("/gym/exit", s.ExitGymHandler).Methods(http.MethodPost)

	return s
}

func (s Server) Start(addr string) error {
	log.Info().Msg("Starting...")
	return http.ListenAndServe(addr, s.router)
}
