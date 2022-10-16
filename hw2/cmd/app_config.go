package main

import (
	"github.com/D-Korobkov/itmo-software-design-course/hw2/internal/pkg/integration/github"
	"gopkg.in/yaml.v3"
	"os"
)

type AppConfig struct {
	TestRun struct {
		Topic       github.Topic `yaml:"topic"`
		PeriodHours int          `yaml:"period-hours"`
	} `yaml:"test-run"`

	GithubApiConfig github.ApiConfig `yaml:"github-api"`
}

func (cfg *AppConfig) ReadFromFile(fileName string) (*AppConfig, error) {
	rawAppConfig, err := os.ReadFile(fileName)
	if err != nil {
		return nil, err
	}
	rawAppConfig = []byte(os.ExpandEnv(string(rawAppConfig)))

	err = yaml.Unmarshal(rawAppConfig, cfg)
	if err != nil {
		return nil, err
	}

	return cfg, err
}
