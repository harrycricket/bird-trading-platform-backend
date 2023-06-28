terraform {
  cloud {
    organization = "thientryhard"

    workspaces {
      name = "bird-trading-platform"
    }
  }
}