package entrypoint

import (
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/turnstile/internal/core/turnstile"
	"github.com/rs/zerolog/log"
	"net/http"
	"strconv"
)

func (s Server) ExitGymHandler(writer http.ResponseWriter, request *http.Request) {
	queryParams := request.URL.Query()
	rawMembershipID := queryParams.Get("membershipId")
	membershipID, err := strconv.ParseUint(rawMembershipID, 10, 64)
	if err != nil {
		log.Warn().Err(err).Msg("ExitGym: invalid query params")
		writer.WriteHeader(http.StatusBadRequest)
		return
	}

	err = s.turnstileService.ProcessExit(request.Context(), uint(membershipID))
	if err != nil {
		switch {
		case errors.Is(err, turnstile.ErrNoMembership):
			writer.WriteHeader(http.StatusBadRequest)
		default:
			log.Error().Err(err).Msg("ExitGym: unexpected error")
			writer.WriteHeader(http.StatusInternalServerError)
		}
		return
	}

	writer.WriteHeader(http.StatusOK)
}
