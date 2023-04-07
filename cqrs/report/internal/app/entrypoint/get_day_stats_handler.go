package entrypoint

import (
	"encoding/json"
	"github.com/rs/zerolog/log"
	"net/http"
	"time"
)

type GetDayStatsResponseBody struct {
	Date                    string  `json:"date"`
	UniqueVisitors          uint    `json:"uniqueVisitors"`
	AvgVisitDurationMinutes float64 `json:"avgVisitDurationMinutes"`
}

func (s Server) GetDayStatsHandler(writer http.ResponseWriter, request *http.Request) {
	queryParams := request.URL.Query()
	rawAtDate := queryParams.Get("atDate")
	atDate, err := time.ParseInLocation("2006-01-02", rawAtDate, time.UTC)
	if err != nil {
		log.Warn().Err(err).Msg("GetDayStats: invalid query params")
		writer.WriteHeader(http.StatusBadRequest)
		return
	}

	stats := s.visitStatsService.GetDayStats(atDate)
	responseBody, err := json.Marshal(GetDayStatsResponseBody{
		Date:                    stats.Date,
		UniqueVisitors:          stats.UniqueVisitors,
		AvgVisitDurationMinutes: stats.AverageTime.Minutes(),
	})
	if err != nil {
		log.Warn().Err(err).Msg("GetDayStats: unexpected error")
		writer.WriteHeader(http.StatusInternalServerError)
		return
	}

	writer.WriteHeader(http.StatusOK)
	writer.Write(responseBody)
}
