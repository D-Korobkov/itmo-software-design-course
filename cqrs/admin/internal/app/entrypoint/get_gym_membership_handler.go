package entrypoint

import (
	"encoding/json"
	"errors"
	"github.com/D-Korobkov/itmo-software-design-course/cqrs/admin/internal/core/membership"
	"github.com/rs/zerolog/log"
	"net/http"
	"strconv"
	"time"
)

type GetGymMembershipResponseBody struct {
	MembershipID uint      `json:"membershipId"`
	Owner        string    `json:"owner"`
	ActiveTill   time.Time `json:"activeTill"`
}

func (s Server) GetGymMembershipHandler(writer http.ResponseWriter, request *http.Request) {
	queryParams := request.URL.Query()
	rawMembershipID := queryParams.Get("membershipId")
	membershipID, err := strconv.ParseUint(rawMembershipID, 10, 64)
	if err != nil {
		log.Warn().Err(err).Msg("GetGymMembership: invalid query params")
		writer.WriteHeader(http.StatusBadRequest)
		return
	}

	gymMembership, err := s.membershipEditor.Find(request.Context(), uint(membershipID))
	if err != nil {
		switch {
		case errors.Is(err, membership.ErrNotFound):
			writer.WriteHeader(http.StatusBadRequest)
		default:
			log.Error().Err(err).Msg("GetGymMembership: unexpected error")
			writer.WriteHeader(http.StatusInternalServerError)
		}
		return
	}
	responseBody, err := json.Marshal(GetGymMembershipResponseBody{
		MembershipID: gymMembership.ID,
		Owner:        gymMembership.Owner,
		ActiveTill:   gymMembership.ActiveTill,
	})
	if err != nil {
		log.Error().Err(err).Msg("GetGymMembership: unexpected error")
		writer.WriteHeader(http.StatusInternalServerError)
	}

	writer.WriteHeader(http.StatusOK)
	writer.Write(responseBody)
}
