package main

import (
	"fmt"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/app"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/integration/github"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/clock"
	"log"
	"net/http"
	"time"
)

const (
	topic       = github.Topic("api")
	periodHours = 24
)

func main() {
	config := github.ApiConfig{
		BaseUrl:   "https://api.github.com",
		AuthToken: "???",
	}

	finder, err := github.NewRepositoryFinder(config, http.DefaultClient)
	if err != nil {
		log.Fatal(err.Error())
		return
	}

	staticClock := clock.NewStaticClock(time.Now())
	githubStatisticsCollector := app.NewGithubStatisticsCollector(finder, staticClock)
	stats, err := githubStatisticsCollector.CountRecentlyCreatedRepositories(topic, periodHours)
	if err != nil {
		log.Fatal(err.Error())
		return
	}

	for hoursAgo, idx := periodHours*time.Hour, periodHours-1; hoursAgo > 0; hoursAgo, idx = hoursAgo-time.Hour, idx-1 {
		since := staticClock.Now().Add(-hoursAgo).Format(time.RFC822)
		till := staticClock.Now().Add(-hoursAgo + time.Hour).Format(time.RFC822)
		fmt.Printf("с %s до %s\nрепозиториев с топиком '#%s' было создано %d шт.\n\n", since, till, topic, stats[idx])
	}
}
