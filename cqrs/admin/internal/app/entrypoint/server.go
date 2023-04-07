package entrypoint

import (
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/internal/core/membership"
	"github.com/gorilla/mux"
	"github.com/rs/zerolog/log"
	"net/http"
)

type Server struct {
	router           *mux.Router
	membershipEditor *membership.Service
}

func NewServer(membershipEditor *membership.Service) Server {
	s := Server{
		router:           mux.NewRouter(),
		membershipEditor: membershipEditor,
	}

	s.router.HandleFunc("/gym/membership", s.CreateGymMembershipHandler).Methods(http.MethodPost)
	s.router.HandleFunc("/gym/membership", s.ProlongGymMembershipHandler).Methods(http.MethodPatch)
	s.router.HandleFunc("/gym/membership", s.GetGymMembershipHandler).Methods(http.MethodGet)

	return s
}

func (s Server) Start(addr string) error {
	log.Info().Msg("Starting...")
	return http.ListenAndServe(addr, s.router)
}
