package entrypoint

import (
	"encoding/json"
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/internal/core/membership"
	"github.com/rs/zerolog/log"
	"net/http"
	"strconv"
)

type ProlongGymMembershipRequestBody struct {
	ExtraDays uint `json:"extraDays"`
}

func (s Server) ProlongGymMembershipHandler(writer http.ResponseWriter, request *http.Request) {
	queryParams := request.URL.Query()
	rawMembershipID := queryParams.Get("membershipId")
	membershipID, err := strconv.ParseUint(rawMembershipID, 10, 64)
	if err != nil {
		log.Warn().Err(err).Msg("ProlongGymMembership: invalid query params")
		writer.WriteHeader(http.StatusBadRequest)
		return
	}

	var requestBody ProlongGymMembershipRequestBody
	if err = json.NewDecoder(request.Body).Decode(&requestBody); err != nil {
		log.Warn().Err(err).Msg("ProlongGymMembership: invalid request body")
		writer.WriteHeader(http.StatusBadRequest)
		return
	}

	err = s.membershipEditor.Prolong(request.Context(), uint(membershipID), requestBody.ExtraDays)
	if err != nil {
		switch {
		case errors.Is(err, membership.ErrNotProlonged):
			writer.WriteHeader(http.StatusInternalServerError)
		default:
			log.Error().Err(err).Msg("ProlongGymMembership: unexpected error")
			writer.WriteHeader(http.StatusInternalServerError)
		}
		return
	}

	writer.WriteHeader(http.StatusOK)
}
