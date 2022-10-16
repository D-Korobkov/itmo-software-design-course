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

const appConfigFile = "cmd/resources/config.yaml"

func main() {
	appConfig, err := (&AppConfig{}).ReadFromFile(appConfigFile)
	if err != nil {
		log.Fatal(err.Error())
		return
	}

	finder, err := github.NewRepositoryFinder(appConfig.GithubApiConfig, http.DefaultClient)
	if err != nil {
		log.Fatal(err.Error())
		return
	}

	staticClock := clock.NewStaticClock(time.Now())
	githubStatisticsCollector := app.NewGithubStatisticsCollector(finder, staticClock)
	topic := appConfig.TestRun.Topic
	periodHours := appConfig.TestRun.PeriodHours
	stats, err := githubStatisticsCollector.CountRecentlyCreatedRepositories(topic, periodHours)
	if err != nil {
		log.Fatal(err.Error())
		return
	}

	period := time.Duration(periodHours) * time.Hour
	for idx := periodHours - 1; idx >= 0; idx-- {
		since := staticClock.Now().Add(-period).Format(time.RFC822)
		till := staticClock.Now().Add(-period + time.Hour).Format(time.RFC822)
		fmt.Printf("с %s до %s\nрепозиториев с топиком '#%s' было создано %d шт.\n\n", since, till, topic, stats[idx])

		period -= time.Hour
	}
}
