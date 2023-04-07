package entrypoint

import (
	"encoding/json"
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/internal/core/membership"
	"github.com/rs/zerolog/log"
	"net/http"
)

type CreateGymMembershipRequestBody struct {
	Owner         string `json:"owner"`
	ExpiresInDays uint   `json:"expiresInDays"`
}

func (s Server) CreateGymMembershipHandler(writer http.ResponseWriter, request *http.Request) {
	var requestBody CreateGymMembershipRequestBody
	if err := json.NewDecoder(request.Body).Decode(&requestBody); err != nil {
		log.Warn().Err(err).Msg("CreateGymMembership: invalid request body")
		writer.WriteHeader(http.StatusBadRequest)
		return
	}

	err := s.membershipEditor.CreateNew(request.Context(), requestBody.Owner, requestBody.ExpiresInDays)
	if err != nil {
		switch {
		case errors.Is(err, membership.ErrNotCreated):
			writer.WriteHeader(http.StatusInternalServerError)
		default:
			log.Error().Err(err).Msg("CreateGymMembership: unexpected error")
			writer.WriteHeader(http.StatusInternalServerError)
		}
		return
	}

	writer.WriteHeader(http.StatusOK)
}
