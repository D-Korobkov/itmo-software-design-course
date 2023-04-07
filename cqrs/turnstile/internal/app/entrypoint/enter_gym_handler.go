package entrypoint

import (
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/membership"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/turnstile"
	"github.com/rs/zerolog/log"
	"net/http"
	"strconv"
)

func (s Server) EnterGymHandler(writer http.ResponseWriter, request *http.Request) {
	queryParams := request.URL.Query()
	rawMembershipID := queryParams.Get("membershipId")
	membershipID, err := strconv.ParseUint(rawMembershipID, 10, 64)
	if err != nil {
		log.Warn().Err(err).Msg("EnterGym: invalid query params")
		writer.WriteHeader(http.StatusBadRequest)
		return
	}

	err = s.turnstileService.ProcessEnter(request.Context(), uint(membershipID))
	if err != nil {
		switch {
		case errors.Is(err, membership.ErrNotFound):
			writer.WriteHeader(http.StatusBadRequest)
		case errors.Is(err, turnstile.ErrNoActiveMembership):
			writer.WriteHeader(http.StatusUnprocessableEntity)
		default:
			log.Error().Err(err).Msg("EnterGym: unexpected error")
			writer.WriteHeader(http.StatusInternalServerError)
		}
		return
	}

	writer.WriteHeader(http.StatusOK)
}
