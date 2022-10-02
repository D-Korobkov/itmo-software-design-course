package main

import (
	"fmt"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/app"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/github"
	"github.com/D-Korobkov/itmo-software-design-course/hw2/pkg/clock"
	"log"
	"net/http"
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

	githubCrawler := app.NewGithubStatisticsCollector(finder, clock.NewRealClock())

	topic := "python"
	res, err := githubCrawler.CountRecentlyCreatedRepositories(topic, 24)
	if err != nil {
		log.Fatal(err.Error())
		return
	}

	for idx := range res {
		fmt.Printf("%d-%d hours ago: %d repositories with topic '#%s' were created", idx, idx+1, res[idx], topic)
		fmt.Println()
	}
}
